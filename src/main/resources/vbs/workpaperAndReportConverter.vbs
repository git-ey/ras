' Copyright EY 2019
' 
' Author logsin37
' Date   2018-12-31
' 
' ���ű����ڽ�xml/xls��ʽ��Excel�ļ�ת��Ϊxlsx��ʽ
' ���ű����ڽ�doc��ʽ��Word�ļ�ת��Ϊdocx��ʽ
' ����Windows����ֱ̨������ .\workpaperAndReportConverter.vbs ԴĿ¼����·�� ���Ŀ¼����·��
' ԴĿ¼�����Ŀ¼������ͬ,����ᱨ��
' ԴĿ¼�е���Ŀ¼������еݹ鴦��,�������Ŀ¼�д�����Ӧ����Ŀ¼�����ɽ���ļ�
' ����ļ��ĸ�ʽΪö������(��SaveAs�洢����ʹ��)
' Word�ļ�����ö�ٲμ� https://docs.microsoft.com/zh-CN/office/vba/api/word.wdsaveformat
' Excel�ļ�����ö�ٲμ� https://docs.microsoft.com/zh-CN/office/vba/api/excel.xlfileformat

' main
ConvertToXlsx
' msgbox "���" ' to debug

' process Arguments
Sub ConvertToXlsx()
    Dim inputFolderPathStr,outputFolderPathStr
    Dim shellArgs

    ' read arguments from shell
    Set shellArgs = WScript.Arguments
    if shellArgs.Count > 0 Then
        inputFolderPathStr = shellArgs(0)
    end if
    if shellArgs.Count > 1 Then
        outputFolderPathStr = shellArgs(1)
    end if
  
    ' Specify the folder
    if inputFolderPathStr = Empty Then
        inputFolderPathStr = inputbox("��ȡ���ݵ��ļ���·��","ȱ�ٲ���")
    end if
    if outputFolderPathStr = Empty Then
        outputFolderPathStr = inputbox("������ݵ��ļ���·��,����������·����ͬ","ȱ�ٲ���")
    end if
    
    if inputFolderPathStr = outputFolderPathStr Then
        msgbox "����·�������·��������ͬ"
    Else
        ProcessFolder inputFolderPathStr,outputFolderPathStr
    end if
End Sub
  
' process files in folder
Sub ProcessFolder(inputFolderPathStr, outputFolderPathStr)
    Dim excel
    Dim subFolder
    Dim file
    Dim fileName
    Dim workbook
    Dim fileType
    Dim folder
    Dim fileSystemObject
    Dim subInputFolderPathStr
    Dim subOutputFolderPathStr
    Dim aField
    Dim aHeader
    Dim aSection

    inputFolderPathStr = FormatFolderPath(inputFolderPathStr)
    outputFolderPathStr = FormatFolderPath(outputFolderPathStr)

    Set fileSystemObject = CreateObject("Scripting.FileSystemObject")
    Set folder = fileSystemObject.GetFolder(inputFolderPathStr)
    'Set excel = CreateObject("Excel.Application")
    'Set word = CreateObject("Word.Application")
    ' Loop through the files
    For Each file In folder.Files
        fileName = file.Name
        fileType = Right(fileName, 3)
        If  (fileType = "xls" OR fileType = "xml") Then
            ' process Excel file
            Set excel = CreateObject("Excel.Application")
            excel.Displayalerts=false
            Set workbook = excel.Workbooks.Open(file)
            fileName = outputFolderPathStr + "\" + Replace(fileName, ".xml", ".xls")
            If workbook.HasVBProject Then
                workbook.SaveAs (fileName + "m"), 52 'xlsm
            Else
                workbook.SaveAs (fileName + "x") ,51 'xlsx
            End If
            workbook.Close False
            set workbook = nothing
            excel.Displayalerts=true
            excel.Quit
            set excel = nothing
        elseif fileType = "doc" Then
            ' process Word file
            Set word = CreateObject("Word.Application")
            word.Displayalerts=False
            Set document = word.documents.open(inputFolderPathStr + "\" + fileName)
            fileName = outputFolderPathStr + "\" + Replace(fileName, ".doc", ".docx")
            ' for update fileds in word document's Headers
            document.TrackRevisions = False
            document.ShowRevisions = False
            For Each aSection in document.Sections
                for each aHeader in aSection.Headers 
                        for each aField in aHeader.Range.Fields
                            aField.Update
                        Next
                Next
            Next
            document.TrackRevisions = True
            document.ShowRevisions = True
            'document.SaveAs fileName, 16 'docx
            'document.SaveAs2 fileName,16,,,,,,,,,,,,,,,15
            document.SaveAs2 fileName,12,,,,,,,,,,,,,,,15
            document.Close False
            set document = nothing
            word.Displayalerts=true
            word.Quit
            set word = nothing
        End If
        fileName = inputFolderPathStr + "\" + file.Name
        If fileSystemObject.FileExists(fileName) Then 
            fileSystemObject.deletefile fileName,1
        END IF
    Next

    ' close Application
    'excel.Quit
    'word.Quit
  
    ' Loop through the subfolders
    For Each subFolder In folder.SubFolders
        ' Call ProcessFolder recursively
        subInputFolderPathStr = inputFolderPathStr + "\" + subFolder.name
        subOutputFolderPathStr = outputFolderPathStr + "\" + subFolder.name
        If Not fileSystemObject.FolderExists(subOutputFolderPathStr) then
            fileSystemObject.CreateFolder subOutputFolderPathStr
        end If
        ProcessFolder subInputFolderPathStr,subOutputFolderPathStr
    Next
End Sub

' drop last '\' or '/' in path string
Function FormatFolderPath(path)
    dim temp
    temp = path
    if (Right(temp, 1) = "\" OR Right(temp, 1) = "/") then
        temp = Left(temp, Len(temp) - 1)
    end if
    FormatFolderPath = temp
End Function
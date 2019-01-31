' Copyright EY 2019
' 
' Author logsin37
' Date   2018-12-31
' 
' 本脚本用于将xml/xls格式的Excel文件转换为xlsx格式
' 本脚本用于将doc格式的Word文件转换为docx格式
' 可在Windows控制台直接运行 .\workpaperAndReportConverter.vbs 源目录绝对路径 输出目录绝对路径
' 源目录和输出目录不能相同,否则会报错
' 源目录中的子目录将会进行递归处理,并在输出目录中创建对应的子目录来容纳结果文件
' 输出文件的格式为枚举类型(在SaveAs存储过程使用)
' Word文件类型枚举参见 https://docs.microsoft.com/zh-CN/office/vba/api/word.wdsaveformat
' Excel文件类型枚举参见 https://docs.microsoft.com/zh-CN/office/vba/api/excel.xlfileformat

' main
ConvertToXlsx
' msgbox "完成" ' to debug

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
        inputFolderPathStr = inputbox("读取数据的文件夹路径","缺少参数")
    end if
    if outputFolderPathStr = Empty Then
        outputFolderPathStr = inputbox("输出数据的文件夹路径,不能与输入路径相同","缺少参数")
    end if
    
    if inputFolderPathStr = outputFolderPathStr Then
        msgbox "输入路径与输出路径不能相同"
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
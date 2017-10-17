package com.ey.util.fileexport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.ey.util.Logger;

/**
 * @name FileExportUtils
 * @description 文件导出工具 
 * @author Dai Zong	2017年8月26日
 */
public class FileExportUtils {
    /**
     * 默认IO Buffer大小
     */
    private static int DEF_IO_BUF_SIZE = 1024;
    /**
     * 默认字符集
     */
    private static String DEF_CHARSET = "UTF-8";
    /**
     * 默认下载文件名
     */
    private static String DEF_FILE_NAME = "file";
    
    private static Logger logger = Logger.getLogger(FileExportUtils.class);
    
    public static void writeFileToHttpResponse(HttpServletRequest request, HttpServletResponse response, String fileName ,InputStream fileContent ){
        OutputStream outputStream = null;
        InputStream inputStream = null;
        if(StringUtils.isEmpty(fileName)) {
            fileName = DEF_FILE_NAME;
        }
        try{
            if(fileContent == null) {
                fileContent = new BufferedInputStream(new ByteArrayInputStream(StringUtils.EMPTY.getBytes(DEF_CHARSET)), DEF_IO_BUF_SIZE);
            }
            //String basePath = request.getSession().getServletContext().getRealPath("/");
            // 1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
            response.setContentType("multipart/form-data");
            // 2.设置文件头
            String userAgent = request.getHeader("User-Agent");
            if (StringUtils.isBlank(userAgent)) {
                fileName = URLEncoder.encode(fileName, DEF_CHARSET);
            } else {
                if (userAgent.indexOf("MSIE") != -1) {
                    // IE使用URLEncoder
                    fileName = URLEncoder.encode(fileName, DEF_CHARSET);
                } else {
                    // FireFox使用ISO-8859-1
                    fileName = new String(fileName.getBytes(DEF_CHARSET), "ISO-8859-1");
                }
            }
            response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setDateHeader("Expires", (System.currentTimeMillis() + 1000));
            // 3.构建输入输出流
            inputStream = fileContent;
            outputStream = new BufferedOutputStream(response.getOutputStream(), DEF_IO_BUF_SIZE);
    
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }
            outputStream.flush();
        }catch(Exception e){
            logger.error("",e);
        }finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }
    
    public static void writeFileToHttpResponse(HttpServletRequest request, HttpServletResponse response, String fileName ,String fileContent ){
        InputStream inputStream = null;
        
        if(fileContent == null) {
            fileContent = StringUtils.EMPTY;
        }
        try{
            inputStream = new BufferedInputStream(new ByteArrayInputStream(fileContent.getBytes(DEF_CHARSET)), DEF_IO_BUF_SIZE);
            writeFileToHttpResponse(request, response, fileName, inputStream);
        }catch(Exception e){
            logger.error("",e);
        }
    }
    
    public static void writeFileToDisk(String folderName, String fileName,InputStream fileContent) {
        OutputStream outputStream = null;
        
        if(StringUtils.isEmpty(folderName)) {
            folderName = ".";
        }
        if(StringUtils.isEmpty(fileName)) {
            fileName = String.valueOf(new Date().getTime());
        }
        
        File dir = new File(folderName);  
        //如果临时文件所在目录不存在，首先创建  
        if (!dir.exists()) {  
            if (!createDir(folderName)) {  
                System.out.println("创建临时文件失败，不能创建临时文件所在的目录！");  
            }  
        }  
        
        final String fileAddress = folderName + "/" + fileName;
        
        try {
            File outputFile = new File(fileAddress);
            if(!outputFile.exists()) {
                outputFile.createNewFile();
            }
            outputStream = new BufferedOutputStream(new FileOutputStream(outputFile), DEF_IO_BUF_SIZE);
            
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = fileContent.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }
            outputStream.flush();
        }catch (Exception e) {
            logger.error("",e);
        }finally {
            IOUtils.closeQuietly(fileContent);
            IOUtils.closeQuietly(outputStream);
        }
        
    }
    
    public static boolean createDir(String destDirName) {  
        File dir = new File(destDirName);  
        if (dir.exists()) {  
            System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");  
            return false;  
        }  
        if (!destDirName.endsWith(File.separator)) {  
            destDirName = destDirName + File.separator;  
        }  
        //创建目录  
        if (dir.mkdirs()) {  
            System.out.println("创建目录" + destDirName + "成功！");  
            return true;  
        } else {  
            System.out.println("创建目录" + destDirName + "失败！");  
            return false;  
        }  
    }  
}

package com.ey.util.fileexport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
    
    /**
     * 将流发送到HttpServletResponse
     * @author Dai Zong 2017年10月17日
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param fileName 浏览器端收到的文件名
     * @param inputStream 源(请自行装饰缓冲类)
     */
    public static void writeFileToHttpResponse(HttpServletRequest request, HttpServletResponse response, String fileName ,InputStream inputStream ){
        OutputStream outputStream = null;
        if(StringUtils.isEmpty(fileName)) {
            fileName = DEF_FILE_NAME;
        }
        try{
            if(inputStream == null) {
                inputStream = getInputStreamFromString(StringUtils.EMPTY);
            }
            //String basePath = request.getSession().getServletContext().getRealPath("/");
            // 1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
            response.setContentType("multipart/form-data");
            // 2.设置文件头
            String userAgent = request.getHeader("User-Agent");
            if (StringUtils.isBlank(userAgent)) {
                fileName = URLEncoder.encode(fileName, DEF_CHARSET);
            } else {
                if (userAgent.indexOf("MSIE") != -1 || userAgent.contains("like Gecko")) {
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
            outputStream = new BufferedOutputStream(response.getOutputStream(), DEF_IO_BUF_SIZE);
            
            writeDateBetweenIOStream(inputStream, outputStream, DEF_IO_BUF_SIZE);
        }catch(Exception e){
            logger.error("",e);
        }finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }
    
    /**
     * 将文本内容发送到HttpServletResponse
     * @author Dai Zong 2017年10月17日
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param fileName 浏览器端收到的文件名
     * @param fileStr 文本内容
     */
    public static void writeFileToHttpResponse(HttpServletRequest request, HttpServletResponse response, String fileName ,String fileStr ){
        InputStream inputStream = null;
        
        if(fileStr == null) {
            fileStr = StringUtils.EMPTY;
        }
        try{
            inputStream = new BufferedInputStream(getInputStreamFromString(fileStr), DEF_IO_BUF_SIZE);
            writeFileToHttpResponse(request, response, fileName, inputStream);
        }catch(Exception e){
            logger.error("",e);
        }
    }
    
    /**
     * 文件下载
     * @author Dai Zong 2017年10月17日
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param fileName 浏览器端收到的文件名
     * @param file 文件
     */
    public static void writeFileToHttpResponse(HttpServletRequest request, HttpServletResponse response, String fileName ,File file){
        InputStream inputStream = null;
        
        if(file == null) {
            writeFileToHttpResponse(request, response, fileName, StringUtils.EMPTY);
        }
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file), DEF_IO_BUF_SIZE);
            writeFileToHttpResponse(request, response, fileName, inputStream);
        }catch (Exception e) {
            logger.error("",e);
        }
    }
    
    /**
     * 将流输出到磁盘
     * @author Dai Zong 2017年10月17日
     * 
     * @param folderName 文件夹名(有效路径)
     * @param fileName 文件名
     * @param inputStream 源(请自行装饰缓冲类)
     */
    public static void writeFileToDisk(String folderName, String fileName, InputStream inputStream) {
        OutputStream outputStream = null;
        
        if(StringUtils.isEmpty(folderName)) {
            logger.error("无效的文件夹名");
            return;
        }
        if(StringUtils.isEmpty(fileName)) {
            fileName = String.valueOf(new Date().getTime());
        }
        
        createDir(folderName);
        
        String fileAddress;
        if(folderName.endsWith("/")) {
            fileAddress = folderName + fileName;
        }else {
            fileAddress = folderName + "/" + fileName;
        }
        
        try {
            File outputFile = new File(fileAddress);
            if(!outputFile.exists()) {
                outputFile.createNewFile();
            }
            outputStream = new BufferedOutputStream(new FileOutputStream(outputFile), DEF_IO_BUF_SIZE);
            
            writeDateBetweenIOStream(inputStream, outputStream, DEF_IO_BUF_SIZE);
        }catch (Exception e) {
            logger.error("",e);
        }finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
        
    }
    
    /**
     * 将文本内容输出到磁盘
     * @author Dai Zong 2017年10月17日
     * 
     * @param folderName 文件夹名(有效路径)
     * @param fileName 文件名
     * @param fileStr 文本内容
     */
    public static void writeFileToDisk(String folderName, String fileName, String fileStr) {
        if(fileStr == null) {
            fileStr = StringUtils.EMPTY;
        }
        
        InputStream inputStream = getInputStreamFromString(fileStr);
        writeFileToDisk(folderName, fileName, inputStream);
    }
    
    /**
     * 创建文件夹
     * @author Dai Zong 2017年10月17日
     * 
     * @param dirName 文件夹有效路径
     * @return
     */
    public static boolean createDir(String dirName) {
        if(StringUtils.isEmpty(dirName)) {
            logger.error("无效的目录名:" + dirName);
            return false;
        }
        File dir = new File(dirName);
        if (dir.exists()) {
            logger.error("创建目录" + dirName + "失败，目标目录已经存在");
            return false;
        }
        if (!dirName.endsWith("/")) {  
            dirName = dirName + "/";  
        }  
        //创建目录  
        if (dir.mkdirs()) {  
            logger.info("创建目录" + dirName + "成功！");  
            return true;  
        } else {  
            logger.error("创建目录" + dirName + "失败！");  
            return false;  
        }  
    }
    
    /**
     * 从String对象中获取输入流
     * @author Dai Zong 2017年10月17日
     * 
     * @param src 源
     * @param charSetName 字符集
     * @return
     * @throws UnsupportedEncodingException
     */
    public static InputStream getInputStreamFromString(String src, String charSetName) throws UnsupportedEncodingException {
        if(src == null) {
            return null;
        }
        return new ByteArrayInputStream(src.getBytes(charSetName));
    }
    
    /**
     * 从String对象中获取输入流(使用UTF-8编码)
     * @author Dai Zong 2017年10月17日
     * 
     * @param src
     * @return
     * @throws UnsupportedEncodingException
     */
    public static InputStream getInputStreamFromString(String src){
        try {
            return getInputStreamFromString(src, DEF_CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 将InputStream的数据搬运到OutputStream
     * @author Dai Zong 2017年10月17日
     * 
     * @param in 输入流
     * @param out 输出流
     * @param bufferSize 缓冲区大小
     * @throws IOException
     */
    private static void writeDateBetweenIOStream(InputStream in, OutputStream out, int bufferSize) throws IOException {
        byte[] buffer = new byte[bufferSize];
        int count = 0;
        while ((count = in.read(buffer)) != -1) {
            out.write(buffer, 0, count);
        }
        out.flush();
    }
}

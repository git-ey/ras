package com.ey.util.fileexport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

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
    
    public static void writeFileToHttpResponse(HttpServletRequest request, HttpServletResponse response, String fileName ,String fileContent ){
        OutputStream outputStream = null;
        InputStream inputStream = null;
        if(StringUtils.isEmpty(fileName)) {
            fileName = DEF_FILE_NAME;
        }
        if(fileContent == null) {
            fileContent = StringUtils.EMPTY;
        }
        try{
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
            inputStream = new BufferedInputStream(new ByteArrayInputStream(fileContent.getBytes(DEF_CHARSET)), DEF_IO_BUF_SIZE);
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
}

package com.ey.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

/**
 * 文件处理类
 */
public class FileUtil {

	/**
	 * 获取文件大小 返回 KB 保留3位小数 没有文件时返回0
	 * 
	 * @param filepath
	 *            文件完整路径，包括文件名
	 * @return
	 */
	public static Double getFilesize(String filepath) {
		File backupath = new File(filepath);
		return Double.valueOf(backupath.length()) / 1000.000;
	}

	/**
	 * 创建目录
	 * 
	 * @param destDirName目标目录名
	 * @return
	 */
	public static Boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (!dir.getParentFile().exists()) { // 判断有没有父路径，就是判断文件整个路径是否存在
			return dir.getParentFile().mkdirs(); // 不存在就全部创建
		}
		return false;
	}

	/**
	 * 删除文件
	 * 
	 * @param filePathAndName
	 *            String 文件路径及名称 如c:/fqf.txt
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFile(String filePathAndName) {
		try {
			java.io.File myDelFile = new java.io.File(filePathAndName);
			myDelFile.delete();
		} catch (Exception e) {
			System.out.println("删除文件操作出错");
		}
	}

	/**
	 * 读取到字节数组0
	 * 
	 * @param filePath
	 *            //路径
	 * @throws IOException
	 */
	public static byte[] getContent(String filePath) throws IOException {
		File file = new File(filePath);
		long fileSize = file.length();
		if (fileSize > Integer.MAX_VALUE) {
			System.out.println("file too big...");
			return null;
		}
		FileInputStream fi = new FileInputStream(file);
		byte[] buffer = new byte[(int) fileSize];
		int offset = 0;
		int numRead = 0;
		while (offset < buffer.length && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
			offset += numRead;
		}
		// 确保所有数据均被读取
		if (offset != buffer.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}
		fi.close();
		return buffer;
	}

	/**
	 * 读取到字节数组1
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray(String filePath) throws IOException {

		File f = new File(filePath);
		if (!f.exists()) {
			throw new FileNotFoundException(filePath);
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(f));
			int buf_size = 1024;
			byte[] buffer = new byte[buf_size];
			int len = 0;
			while (-1 != (len = in.read(buffer, 0, buf_size))) {
				bos.write(buffer, 0, len);
			}
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			bos.close();
		}
	}

	/**
	 * 读取到字节数组2
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray2(String filePath) throws IOException {
		File f = new File(filePath);
		if (!f.exists()) {
			throw new FileNotFoundException(filePath);
		}
		FileChannel channel = null;
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(f);
			channel = fs.getChannel();
			ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
			while ((channel.read(byteBuffer)) > 0) {
				// do nothing
				// System.out.println("reading");
			}
			return byteBuffer.array();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Mapped File way MappedByteBuffer 可以在处理大文件时，提升性能
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray3(String filePath) throws IOException {

		FileChannel fc = null;
		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(filePath, "r");
			fc = rf.getChannel();
			MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0, fc.size()).load();
			// System.out.println(byteBuffer.isLoaded());
			byte[] result = new byte[(int) fc.size()];
			if (byteBuffer.remaining() > 0) {
				// System.out.println("remain");
				byteBuffer.get(result, 0, byteBuffer.remaining());
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rf.close();
				fc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取指定目录下的所有文件
	 * 
	 * @param filePath
	 * @return
	 */
	public static List<File> getPathFile(String filePath) {
		List<File> files = Lists.newArrayList();
		File file = new File(filePath);
		if (!file.isDirectory()) {
			files.add(file);
		} else if (file.isDirectory()) {
			String[] filelist = file.list();
			for (int i = 0; i < filelist.length; i++) {
				File readfile = new File(filePath + File.separator + filelist[i]);
				if (!readfile.isDirectory()) {
					files.add(readfile);
				} else if (readfile.isDirectory()) {
					getPathFile(filePath + File.separator + filelist[i]);
				}
			}
		}
		return files;
	}

	/**
	 * 根据正则表达式过滤文件名称
	 * @param filePath
	 * @param regex
	 * @return
	 */
	public static List<File> getPathFile(String filePath, String regex) {
		List<File> list = FileUtil.getPathFile(filePath);
		Iterator<File> itFiles = list.iterator();
		while(itFiles.hasNext()){
			boolean result = Pattern.compile(regex).matcher(itFiles.next().getName()).find();
			if (!result) {
				itFiles.remove();
			}
		}
		return list;
	}

	public static void main(String[] args) {
		List<File> files = FileUtil.getPathFile("D:\\importdata",".*JE.xlsx$");
		for (File file : files) {
			System.out.println(file.getName());
		}
	}

}
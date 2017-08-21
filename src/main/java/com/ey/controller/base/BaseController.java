package com.ey.controller.base;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ey.entity.Page;
import com.ey.entity.system.ImportConfig;
import com.ey.entity.system.ImportConfig.ImportFileType;
import com.ey.util.Const;
import com.ey.util.FileUpload;
import com.ey.util.Logger;
import com.ey.util.PageData;
import com.ey.util.PathUtil;
import com.ey.util.UuidUtil;
import com.ey.util.fileimport.FileImportException;
import com.ey.util.fileimport.FileImportExecutor;
import com.ey.util.fileimport.ImportConfigParser;
import com.ey.util.fileimport.MapResult;
import com.ey.util.fileimport.XlsToCsv;
import com.ey.util.fileimport.XlsxToCsv;

/**
 * 父类控制器
 */
@Controller
public class BaseController {

	// 数据导入配置服务
	@Autowired
	@Qualifier("importConfigParser")
	private ImportConfigParser importConfigParser;
	
	protected Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * AM科目映射导入模版代码
	 */
	protected final static String AM_IMPORT_TEMPLATE_CODE = "AM";
	
	/**
	 * 交易日历导入模版代码
	 */
	protected final static String TC_IMPORT_TEMPLATE_CODE = "TC";

	/**
	 * new PageData对象
	 * 
	 * @return
	 */
	public PageData getPageData() {
		return new PageData(this.getRequest());
	}

	/**
	 * 得到ModelAndView
	 * 
	 * @return
	 */
	public ModelAndView getModelAndView() {
		return new ModelAndView();
	}

	/**
	 * 得到request对象
	 * 
	 * @return
	 */
	public HttpServletRequest getRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		return request;
	}

	/**
	 * 得到32位的uuid
	 * 
	 * @return
	 */
	public String get32UUID() {
		return UuidUtil.get32UUID();
	}

	/**
	 * 得到分页列表的信息
	 * 
	 * @return
	 */
	public Page getPage() {
		return new Page();
	}

	public static void logBefore(Logger logger, String interfaceName) {
		logger.info("");
		logger.info("start");
		logger.info(interfaceName);
	}

	public static void logAfter(Logger logger) {
		logger.info("end");
		logger.info("");
	}

	/**
	 * Excel上传数据
	 * @param file
	 * @param importFileCode
	 * @return
	 * @throws Exception
	 */
	protected MapResult readExcel(MultipartFile file, String importTempCode) throws Exception {
		MapResult mapResult = null;
		if (null != file && !file.isEmpty()) {
			String filePath = PathUtil.getClasspath() + Const.FILEPATHFILE;
			String fileNameStr = UuidUtil.get32UUID();
			String fileName = FileUpload.fileUp(file, filePath, fileNameStr);
			// 获取数据导入配置
			ImportConfig configuration = null;
			try {
				configuration = importConfigParser.getConfig(importTempCode);
			} catch (Exception e) {
				throw new Exception("获取数据导入配置失败:" + importTempCode + "," + e.getMessage());
			}
			String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
			String excelFilePath = filePath + fileName;
			String executeFilePath = "";
			if(configuration.getImportFileType() == ImportFileType.CSV){
				String csvFilePath = filePath + fileNameStr + ".csv";
				switch (extension) {
				case "xls":
					// 先执行Excel转换成CSV
					XlsToCsv xlsTocsv = new XlsToCsv(excelFilePath, csvFilePath);
					xlsTocsv.process();
					break;
				case "xlsx":
					// 先执行Excel转换成CSV
					XlsxToCsv xlsxTocsv = new XlsxToCsv(excelFilePath, csvFilePath);
					xlsxTocsv.process();
					break;
				case "csv":
					csvFilePath = excelFilePath;
					break;
				}
				executeFilePath = csvFilePath;
			}else{
				executeFilePath = excelFilePath;
			}
			
			File executeFile = new File(executeFilePath);
			if (!executeFile.exists()) {
				throw new Exception(executeFilePath + " 未找到上传文件");
			}
			try {
				mapResult = (MapResult) FileImportExecutor.importFile(configuration, executeFile, executeFile.getName());
			} catch (FileImportException e) {
				throw new Exception("导入上传文件数据失败:" + executeFile.getName() + "," + e.getMessage());
			}
		}
		return mapResult;
	}

}

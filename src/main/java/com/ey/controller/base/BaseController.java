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
	 * 基金信息导入模版代码
	 */
	protected final static String FI_IMPORT_TEMPLATE_CODE = "FI";
	
	/**
	 * 股票信息导入模版代码
	 */
	protected final static String SSI_IMPORT_TEMPLATE_CODE = "SSI";
	
	/**
	 * 资产负债表映射导入模版代码
	 */
	protected final static String BSM_IMPORT_TEMPLATE_CODE = "BSM";
	
	/**
	 * 利润表映射导入模版代码
	 */
	protected final static String PLM_IMPORT_TEMPLATE_CODE = "PLM";
	
	/**
	 * AMA科目映射属性导入模版代码
	 */
	protected final static String AMA_IMPORT_TEMPLATE_CODE = "AMA";
	
	/**
	 * BETA系数导入模版代码
	 */
	protected final static String BETA_IMPORT_TEMPLATE_CODE = "BETA";
	
	/**
	 * SCB中债估值导入模版代码
	 */
	protected final static String SCB_IMPORT_TEMPLATE_CODE = "SCB";
	
	/**
	 * SDC久期凸性导入模版代码
	 */
	protected final static String SDC_IMPORT_TEMPLATE_CODE = "SDC";
	
	/**
	 * EYI指数导入模版代码
	 */
	protected final static String EYI_IMPORT_TEMPLATE_CODE = "EYI";
	
	/**
	 * FIFR基金指数使用费率导入模版代码
	 */
	protected final static String FIFR_IMPORT_TEMPLATE_CODE = "FIFR";
	
	/**
	 * HPTS风险分析性假设导入模版代码
	 */
	protected final static String HPTS_IMPORT_TEMPLATE_CODE = "HPTS";
	
	/**
	 * SSB中证估值导入模版代码
	 */
	protected final static String SSB_IMPORT_TEMPLATE_CODE = "SSB";
	
	/**
	 * SLI股票受限信息导入模版代码
	 */
	protected final static String SLI_IMPORT_TEMPLATE_CODE = "SLI";
	
	/**
	 * SVI估值方法索引导入模版代码
	 */
	protected final static String SVI_IMPORT_TEMPLATE_CODE = "SVI";
	
	/**
	 * EFI基金投资信息导入模版代码
	 */
	protected final static String EFI_IMPORT_TEMPLATE_CODE = "EFI";
	
	/**
	 * EFUI期货投资信息导入模版代码
	 */
	protected final static String EFUI_IMPORT_TEMPLATE_CODE = "EFUI";
	
	/**
	 * EBI债券投资信息导入模版代码
	 */
	protected final static String EBI_IMPORT_TEMPLATE_CODE = "EBI";
	
	/**
	 * BLI债券流通受限信息导入模版代码
	 */
	protected final static String BLI_IMPORT_TEMPLATE_CODE = "BLI";
	
	/**
	 * SFS基金分级信息导入模版代码
	 */
	protected final static String SFS_IMPORT_TEMPLATE_CODE = "SFS";
	
	/**
	 * SFSF基金分级信息假分级导入模版代码
	 */
	protected final static String SFSF_IMPORT_TEMPLATE_CODE = "SFSF";
	
	/**
	 * SFR基金申赎规则导入模版代码
	 */
	protected final static String SFR_IMPORT_TEMPLATE_CODE = "SFR";
	
	/**
	 * SFSO基金签字人导入模版代码
	 */
	protected final static String SFSO_IMPORT_TEMPLATE_CODE = "SFSO";

	/**
	 * DSS日交割单导入模版代码
	 */
	protected final static String DSS_IMPORT_TEMPLATE_CODE = "DSS";
	
	/**
	 * SFRP基金关联方导入模版代码
	 */
	protected final static String SFRP_IMPORT_TEMPLATE_CODE = "SFRP";
	
	/**
	 * SFRI基金募集信息导入模版代码
	 */
	protected final static String SFRI_IMPORT_TEMPLATE_CODE = "SFRI";	
	
	/**
	 * SFA基金券商信息导入模版代码
	 */
	protected final static String SFA_IMPORT_TEMPLATE_CODE = "SFA";	
	
	/**
	 * TPS第三方名称导入模版代码
	 */
	protected final static String TPS_IMPORT_TEMPLATE_CODE = "TPS";		
	
	/**
	 * SBI债券信息导入模版代码
	 */
	protected final static String SBI_IMPORT_TEMPLATE_CODE = "SBI";			
	
	
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

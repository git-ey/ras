package com.ey.util.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ey.entity.system.ImportConfig;
import com.ey.entity.system.ImportConfigCell;
import com.ey.util.Logger;
import com.ey.util.fileimport.FileImportException;
import com.ey.util.fileimport.FileImportor;
import com.ey.util.fileimport.ImportResult;
import com.ey.util.fileimport.MapResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Excel文件导入处理器
 *
 */
public class ExcelImportor extends FileImportor  {

	protected Logger logger = Logger.getLogger(ExcelImportor.class);

	private ImportConfig configuration;

	@Override
	public ImportResult getImportResult(File file, String fileName) throws FileImportException {
		if (configuration == null) {
			throw new FileImportException("configuration is null");
		}
		StringBuilder stringbuilder = new StringBuilder();
		Workbook workbook = null;

		String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
//		System.out.println("fis:"+file.getPath());
		try (FileInputStream fis = new FileInputStream(file)) {
			if ("xls".equals(extension)) {
				workbook = new HSSFWorkbook(fis); // 读取 .xls 文件
			} else if ("xlsx".equals(extension)) {
				workbook = new XSSFWorkbook(new FileInputStream(file));
//				workbook = StreamingReader.builder()
//						.rowCacheSize(300)
//						.bufferSize(4096)
//						.open(fis);  // 只能打开 XLSX 格式的文件
			} else {
				throw new FileImportException("不支持的文件格式");
			}
		} catch (IOException e) {
			throw new FileImportException(e, "解析异常");
		}
		List<Map> result = readExcel(workbook, configuration, stringbuilder);
		MapResult mapResult = new MapResult();
		mapResult.setResult(result);
		mapResult.setResMsg(stringbuilder.toString());
		return mapResult;
	}

	@Override
	public ImportResult getImportResult(File file, String fileName, boolean flag) throws FileImportException {
		if (configuration == null) {
			throw new FileImportException("configuration is null");
		}
		StringBuilder stringbuilder = new StringBuilder();
		Workbook workbook = null;
		String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
		try (FileInputStream fis = new FileInputStream(file)) {
			if ("xls".equals(extension)) {
				workbook = new HSSFWorkbook(fis); // 读取 .xls 文件
			} else if ("xlsx".equals(extension)) {
				workbook = new XSSFWorkbook(new FileInputStream(file));
//				workbook = StreamingReader.builder()
//						.rowCacheSize(100)
//						.bufferSize(4096)
//						.open(fis);  // 只能打开 XLSX 格式的文件
			} else {
				throw new FileImportException("unsupport file style");
			}
		} catch (IOException e) {
			throw new FileImportException(e, "解析异常");
		}
		List<Map> result = readExcel(workbook, configuration, stringbuilder);
		MapResult mapResult = new MapResult();
		mapResult.setResult(result);
		mapResult.setResMsg(stringbuilder.toString());
		return mapResult;
	}

	private List<Map> readExcel(Workbook workbook, ImportConfig configuration, StringBuilder sb)
			throws FileImportException {
		// 选择第一个sheet
		Sheet sheet = workbook.getSheetAt(configuration.getSheetNo() == null ? 0 : configuration.getSheetNo());
		int startRow = configuration.getStartRowNo();
		List<ImportConfigCell> lists = configuration.getImportCells();
		int phyRow = sheet.getLastRowNum();
		List<Map> results = Lists.newLinkedList();
		// 引号，处理MySQL插入数据返回的Map用到
		String quotes = "";
		if (StringUtils.isNotBlank(configuration.getTableName())) {
			quotes = "'";
		}
		for (int t = startRow; t <= phyRow; t++) {
			Row row = sheet.getRow(t);
			if (row == null) {
				continue;
			}
			// poi获取正确行数很难。这里约定，前三个值都为空时，自动放弃该行
			// if (isCellEmpty(row.getCell(0)) && isCellEmpty(row.getCell(1)) &&
			// isCellEmpty(row.getCell(2))) {
			// continue;
			// }
			// 过滤行判断
			if (configuration.getIgnoreRule() != null && isIgnoreRow(configuration.getIgnoreRule(), row)) {
				continue;
			}
			Map<String, Object> maps = Maps.newLinkedHashMap();
			maps.put(MapResult.IS_LINE_LEGAL_KEY, true);
			for (ImportConfigCell importCell : lists) {
				setValue(maps, importCell, row, sb, t, startRow, quotes);
			}
			results.add(maps);
		}
		return results;
	}

	private boolean isIgnoreRow(String[] ignoreRole, Row row) {
		for (String irs : ignoreRole) {
			try {
				String[] ir = irs.split(":");
				int cellKey = Integer.parseInt(ir[0]);
				String ignoreValue = ir[1];
				Pattern p = Pattern.compile(ignoreValue);
				String matchStr = "";
				switch(row.getCell(cellKey).getCellType()){
				case Cell.CELL_TYPE_STRING :
					matchStr = row.getCell(cellKey).getStringCellValue();
					break;
				case Cell.CELL_TYPE_BLANK :
					matchStr = "";
					break;
				case Cell.CELL_TYPE_FORMULA :
					matchStr = row.getCell(cellKey).getCellFormula();
					break;
				case Cell.CELL_TYPE_NUMERIC :
					matchStr = String.valueOf(row.getCell(cellKey).getNumericCellValue());
					break;
				default:
					break;
				}
				Matcher m = p.matcher(matchStr);
				// 任意一个条件满足则过滤
				if (m.find()) {
					return true;
				}
				/*if (m.find()) {
					continue;
				} else {
					return false;
				}*/
			} catch (Exception ex) {
				logger.error("过滤条件解析异常:"+ex.getMessage());
				return true;
			}
		}
		return false;
	}

	private boolean isCellEmpty(Cell cell) {
		if (cell == null) {
			return true;
		}
		if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			return true;
		}
		if (cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isEmpty(cell.getStringCellValue())) {
			return true;
		}
		return false;
	}

	private void setErrMsg(String errMsg, Map<String, Object> maps, StringBuilder sb) {
		sb.append(errMsg);
		maps.put(MapResult.IS_LINE_LEGAL_KEY, false);
	}

	private void setValue(Map<String, Object> maps, ImportConfigCell importCell, Row row, StringBuilder sb, int line,
			int startRow, String quotes) throws FileImportException {
		int num = importCell.getNumber();
		int showLine = line + startRow;
		int showColumn = num;
		maps.put(MapResult.LINE_NUM_KEY, showLine);
		ImportConfigCell.CellType cellType = importCell.getCellType();
		ImportConfigCell.NullAble nullable = importCell.getNullAble();
		String errMsg = null;
		String key = importCell.getKey();
		Cell cell = row.getCell(num);
		int rawCellType = Cell.CELL_TYPE_BLANK;
		if (cell != null) {
			rawCellType = cell.getCellType();
		}
		if (rawCellType == Cell.CELL_TYPE_BLANK || cell == null
				|| rawCellType == Cell.CELL_TYPE_STRING && StringUtils.isEmpty(cell.getStringCellValue())) {
			if (nullable == ImportConfigCell.NullAble.NULL_ALLOWED) {
				maps.put(key, null);
			} else {
				errMsg = String.format("line:%d,column:%d is null \n", showLine, showColumn);
				setErrMsg(errMsg, maps, sb);
			}
		} else {
			switch (cellType) {
			case INT:
				if (rawCellType == Cell.CELL_TYPE_STRING) {
					String temp = cell.getStringCellValue();
					if (!StringUtils.isNumeric(temp)) {
						errMsg = String.format("line:%d,column:%d is not number \n", showLine, showColumn);
						setErrMsg(errMsg, maps, sb);
					}
					maps.put(key, Integer.valueOf(temp));
				}
				if (rawCellType == Cell.CELL_TYPE_NUMERIC) {
					Double temp = cell.getNumericCellValue();
					maps.put(key, temp.intValue());
				}
				break;
			case STRING:
				String temp = null;
				if (rawCellType == Cell.CELL_TYPE_NUMERIC || rawCellType == Cell.CELL_TYPE_FORMULA) {
					temp = String.valueOf(cell.getNumericCellValue());
					maps.put(key, StringUtils.isBlank(temp) ? "''" : quotes + temp + quotes);
					break;
				}
				if (rawCellType == Cell.CELL_TYPE_STRING) {
					temp = cell.getStringCellValue().replace("'", "\\'"); // 解析excel时将单引号转义
					maps.put(key, StringUtils.isBlank(temp) ? "''" : quotes + temp + quotes);
					break;
				}
				errMsg = String.format("line:%d,column:%d is not string\n", showLine, showColumn);
				setErrMsg(errMsg, maps, sb);
				break;
			case FLOAT:
				if (rawCellType == Cell.CELL_TYPE_NUMERIC) {
					Double temp1 = cell.getNumericCellValue();
					maps.put(key, temp1.floatValue());
				} else {
					errMsg = String.format("line:%d,column:%d is not float\n", showLine, showColumn);
					setErrMsg(errMsg, maps, sb);
				}
				break;
			case DATE:
				if (rawCellType == Cell.CELL_TYPE_NUMERIC) {
					Date date = DateUtil.getJavaDate(cell.getNumericCellValue());
					maps.put(key, quotes + com.ey.util.DateUtil.getDateTimeStr(date,importCell.getDateFormat()) + quotes); // 转换成字符串便于数据库存储
				}else if((rawCellType == Cell.CELL_TYPE_STRING)){
					maps.put(key, StringUtils.isBlank(cell.getStringCellValue()) ? "''" : quotes + cell.getStringCellValue() + quotes);
				} else {
					errMsg = String.format("line:%d,column:%d is not date\n", showLine, showColumn);
					setErrMsg(errMsg, maps, sb);
				}
				break;
			case BIGDECIMAL:
				if (rawCellType == Cell.CELL_TYPE_NUMERIC || rawCellType == Cell.CELL_TYPE_FORMULA) {
					Double temp1 = cell.getNumericCellValue();
					maps.put(key, BigDecimal.valueOf(temp1));
				} else {
					errMsg = String.format("line:%d,column:%d is not bigDecimal\n", showLine, showColumn);
					setErrMsg(errMsg, maps, sb);
				}
				break;
			case DOUBLE:
				if (rawCellType == Cell.CELL_TYPE_NUMERIC) {
					Double temp1 = cell.getNumericCellValue();
					maps.put(key, temp1);
				} else {
					errMsg = String.format("line:%d,column:%d is not double\n", showLine, showColumn);
					setErrMsg(errMsg, maps, sb);
				}
				break;
			}

		}
	}

	public ExcelImportor(ImportConfig configuration) {
		this.configuration = configuration;
	}

	// public static void main() {
	// 	String file = "C:\\Users\\EYHIVE\\Desktop\\华宝股票流通受限表-EY_STOCK_LIMIT.xlsx";
	// 	workbook = new XSSFWorkbook(new FileInputStream(file));
	// 	StringBuilder sb = new StringBuilder();
	// 	List<Map> map = this.readExcel(Workbook workbook, ImportConfig configuration, sb)
	// 	this.readExcel()
	// }

}

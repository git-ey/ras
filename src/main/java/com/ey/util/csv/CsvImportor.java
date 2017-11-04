package com.ey.util.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.ey.entity.system.ImportConfig;
import com.ey.entity.system.ImportConfigCell;
import com.ey.util.fileimport.FileImportException;
import com.ey.util.fileimport.FileImportor;
import com.ey.util.fileimport.ImportResult;
import com.ey.util.fileimport.MapResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * CSV文件导入处理器
 *
 */
public class CsvImportor extends FileImportor {

	private ImportConfig configuration;

	public CsvImportor(ImportConfig configuration) {
		this.configuration = configuration;
	}

	@Override
	public ImportResult getImportResult(File file, String fileName) throws FileImportException {
		if (configuration == null) {
			throw new FileImportException("configuration is null");
		}
		StringBuilder stringbuilder = new StringBuilder();
		List<Map> result = readCsv(file, configuration, stringbuilder);
		MapResult mapResult = new MapResult();
		mapResult.setResult(result);
		mapResult.setResMsg(stringbuilder.toString());
		return mapResult;
	}

	/**
	 * 读取CSV文件
	 * 
	 * @param csvfile
	 * @param configuration
	 * @param sb
	 * @return
	 * @throws FileImportException
	 */
	private List<Map> readCsv(File csvFile, ImportConfig configuration, StringBuilder sb) throws FileImportException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(csvFile));
		} catch (FileNotFoundException ex) {
			sb.append("读取CSV文件失败:" + ex.getMessage());
		}
		List<ImportConfigCell> lists = configuration.getImportCells();
		List<Map> results = Lists.newLinkedList();
		// 引号，处理MySQL插入数据返回的Map用到
		String quotes = "";
		if (StringUtils.isNotBlank(configuration.getTableName())) {
			quotes = "'";
		}
		String readline = "";
		String line = "";
		int startRow = configuration.getStartRowNo();
		int idx = 0;
		try {
			while ((readline = br.readLine()) != null) // 读取到的内容给line变量
			{
				if (idx >= startRow) {
					line = readline.replace("\"", quotes);
					Map<String, Object> maps = Maps.newLinkedHashMap();
					maps.put(MapResult.IS_LINE_LEGAL_KEY, true);
					String[] rowLine = line.split("`");// 按照`符号分割处理
					// 行过滤规则
					if (configuration.getIgnoreRule() != null && isIgnoreRow(configuration.getIgnoreRule(), rowLine)) {
						continue;
					}
					for (ImportConfigCell importCell : lists) {
						setValue(maps, importCell, rowLine, rowLine.length, sb, idx, startRow, quotes);
					}
					results.add(maps);
				}
				idx++;
			}
		} catch (IOException ex) {
			sb.append("读取CSV文件行出错:" + com.ey.util.StringUtil.getStringByLength(ex.getMessage(), 480));
		}
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return results;
	}

	/**
	 * 解析并组装行值
	 * 
	 * @param maps
	 * @param importCell
	 * @param rowLine
	 * @param rows
	 * @param sb
	 * @param line
	 * @param startRow
	 * @param quotes
	 * @throws FileImportException
	 */
	private void setValue(Map<String, Object> maps, ImportConfigCell importCell, String[] rowLine, int rows,
			StringBuilder sb, int line, int startRow, String quotes) throws FileImportException {
		int num = importCell.getNumber();
		int showLine = line + startRow;
		int showColumn = num + startRow;
		ImportConfigCell.CellType cellType = importCell.getCellType();
		ImportConfigCell.NullAble nullable = importCell.getNullAble();
		String key = importCell.getKey();
		String errMsg = null;
		// 验证是否为空
		if (nullable == ImportConfigCell.NullAble.NULL_ALLOWED) {
			maps.put(key, Optional.empty());
		} else {
			errMsg = String.format("line:%d,column:%d is null \n", showLine, showColumn);
			setErrMsg(errMsg, maps, sb);
		}
		switch (cellType) {
		case INT:
			if (rows > importCell.getNumber()) {
				maps.put(key,
						StringUtils.isBlank(rowLine[importCell.getNumber()]) ? null : rowLine[importCell.getNumber()]);
			} else {
				maps.put(key, null);
			}
			break;
		case STRING:
			if (rows > importCell.getNumber()) {
				maps.put(key,
						StringUtils.isBlank(rowLine[importCell.getNumber()]) ? "''" : rowLine[importCell.getNumber()].replace("\"", "'"));
			} else {
				maps.put(key, null);
			}
			break;
		case FLOAT:
			if (rows > importCell.getNumber()) {
				maps.put(key,
						StringUtils.isBlank(rowLine[importCell.getNumber()]) ? null : rowLine[importCell.getNumber()]);
			} else {
				maps.put(key, null);
			}
			break;
		case BIGDECIMAL:
			if (rows > importCell.getNumber()) {
				maps.put(key,
						StringUtils.isBlank(rowLine[importCell.getNumber()]) ? null : rowLine[importCell.getNumber()]);
			} else {
				maps.put(key, null);
			}
			break;
		case DATE:
			if (rows > importCell.getNumber()) {
				String dateStr = com.ey.util.DateUtil.getDateTimeStr(rowLine[importCell.getNumber()],importCell.getDateFormat());
				maps.put(key,quotes + dateStr + quotes);
			} else {
				maps.put(key, null);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 是否过滤行
	 * 
	 * @param ignoreRole
	 * @param readLine
	 * @return
	 */
	private boolean isIgnoreRow(String[] ignoreRole, String[] readLine) {
		for (String irs : ignoreRole) {
			try {
				String[] ir = irs.split(":");
				int cellKey = Integer.parseInt(ir[0]);
				String ignoreValue = ir[1];
				Pattern p = Pattern.compile(ignoreValue);
				Matcher m = p.matcher(StringUtils.isBlank(readLine[cellKey]) ? "" : readLine[cellKey]);
				if (m.find()) {
					continue;
				} else {
					return false;
				}
			} catch (Exception ex) {
				return false;
			}
		}
		return true;
	}

	private void setErrMsg(String errMsg, Map<String, Object> maps, StringBuilder sb) {
		sb.append(errMsg);
		maps.put(MapResult.IS_LINE_LEGAL_KEY, false);
	}

}

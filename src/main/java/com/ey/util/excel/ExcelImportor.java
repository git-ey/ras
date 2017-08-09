package com.ey.util.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
public class ExcelImportor extends FileImportor {
	
    private ImportConfig configuration;

    @Override
    public ImportResult getImportResult(File file, String fileName) throws FileImportException {
        if (configuration == null) {
            throw new FileImportException("configuration is null");
        }
        StringBuilder stringbuilder = new StringBuilder();
        Workbook workbook = null;
        String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName
                .substring(fileName.lastIndexOf(".") + 1);
        if ("xls".equals(extension)) {
            try {
                workbook = new HSSFWorkbook(new FileInputStream(file));
            } catch (IOException e) {
                throw new FileImportException(e, e.getMessage());
            }
        } else if ("xlsx".equals(extension)) {
            try {
                workbook = new XSSFWorkbook(new FileInputStream(file));
            } catch (IOException e) {
                throw new FileImportException(e, e.getMessage());
            }
        } else {
            throw new FileImportException("unsupport file style");
        }
        List<Map> result = readExcel(workbook, configuration, stringbuilder);
        MapResult mapResult = new MapResult();
        mapResult.setResult(result);
        mapResult.setResMsg(stringbuilder.toString());
        return mapResult;
    }

    private List<Map> readExcel(Workbook workbook, ImportConfig configuration, StringBuilder sb) throws FileImportException {
        //选择第一个sheet
        Sheet sheet = workbook.getSheetAt(0);
        int startRow = configuration.getStartRowNo();
        List<ImportConfigCell> lists = configuration.getImportCells();
        int phyRow = sheet.getPhysicalNumberOfRows();
        List<Map> results = Lists.newLinkedList();
        // 引号，处理MySQL插入数据返回的Map用到
        String quotes = null;
        if(StringUtils.isNotBlank(configuration.getTableName())){
        	quotes = "'";
        }
        for (int t = startRow; t < phyRow; t++) {
            Row row = sheet.getRow(t);
            if (row == null) {
                continue;
            }
            //poi获取正确行数很难。这里约定，前三个值都为空时，自动放弃该行
            //if (isCellEmpty(row.getCell(0)) && isCellEmpty(row.getCell(1)) && isCellEmpty(row.getCell(2))) {
            //    continue;
            //}
            // 过滤行判断
            if(configuration.getIgnoreRule() != null && isIgnoreRow(configuration.getIgnoreRule(),row)){
            	continue;
            }
            Map<String, Object> maps = Maps.newLinkedHashMap();
            maps.put(MapResult.IS_LINE_LEGAL_KEY, true);
            for (ImportConfigCell importCell : lists) {
                setValue(maps, importCell, row, sb, t, startRow,quotes);
            }
            results.add(maps);
        }
        return results;
    }
    
    private boolean isIgnoreRow(String[] ignoreRole,Row row){
    	for(String irs : ignoreRole){
    		String[] ir = irs.split(":");
    		int cellKey = Integer.parseInt(ir[0]);
    		String ignoreValue = ir[1];
    		if(ignoreValue.equals("null") && !isCellEmpty(row.getCell(cellKey))){
    			return false;
    		}
    		if(!ignoreValue.equals("null") && isCellEmpty(row.getCell(cellKey))){
    			return false;
    		}
    		if(ignoreValue.equals("null") && isCellEmpty(row.getCell(cellKey))){
    			continue;
    		}
    		try {
				if (row.getCell(cellKey).getStringCellValue().indexOf(ignoreValue) == -1) {
					return false;
				} 
			} catch (Exception e) {
				// 如果不是指向的字符串类型字段，则不处理
				return false;
			}
    	}
    	return true;
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

    private void setValue(Map<String, Object> maps, ImportConfigCell importCell, Row row, StringBuilder sb, int line, int startRow,String quotes) throws FileImportException {
        int num = importCell.getNumber();
        int showLine = line + startRow;
        int showColumn = num + startRow;
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
        if (rawCellType == Cell.CELL_TYPE_BLANK ||
                cell == null ||
                rawCellType == Cell.CELL_TYPE_STRING && StringUtils.isEmpty(cell.getStringCellValue())) {
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
                    if (rawCellType == Cell.CELL_TYPE_NUMERIC) {
                        temp = String.valueOf(cell.getNumericCellValue());
                        maps.put(key, quotes+temp+quotes);
                        break;
                    }
                    if (rawCellType == Cell.CELL_TYPE_STRING) {
                        temp = cell.getStringCellValue();
                        maps.put(key, quotes+temp+quotes);
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
                        maps.put(key, quotes+com.ey.util.DateUtil.getDateTimeStr(date)+quotes); // 转换成字符串便于数据库存储
                    } else {
                        errMsg = String.format("line:%d,column:%d is not date\n", showLine, showColumn);
                        setErrMsg(errMsg, maps, sb);
                    }
                    break;
                case BIGDECIMAL:
                    if (rawCellType == Cell.CELL_TYPE_NUMERIC) {
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

}

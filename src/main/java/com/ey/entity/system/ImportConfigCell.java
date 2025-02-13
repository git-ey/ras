package com.ey.entity.system;

import com.ey.util.fileimport.BaseModel;

/**
 * 数据导行配置
 * 
 * @author andyChen
 *
 */
public class ImportConfigCell extends BaseModel {

	private Integer number;// excel对应的序列，起始0
	private String key;// 存储的map对应的key
	private CellType cellType;// 0:int,1:float,2:string,3:date,4:bigDecimal
	private String dateFormat;// 时间格式掩码
	private NullAble nullAble;// 是否允许为空 0:允许 ,1:不允许

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public CellType getCellType() {
		return cellType;
	}

	public void setCellType(CellType cellType) {
		this.cellType = cellType;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public NullAble getNullAble() {
		return nullAble;
	}

	public void setNullAble(NullAble nullAble) {
		this.nullAble = nullAble;
	}

	public enum NullAble {
		NULL_ALLOWED(0), NULL_NOT_ALLOWED(1);
		NullAble(int val) {
		}

		public static NullAble getNullble(int val) {
			switch (val) {
			case 0:
				return NULL_ALLOWED;
			case 1:
				return NULL_NOT_ALLOWED;
			default:
				return NULL_ALLOWED;
			}
		}
	}

	public enum CellType {
		INT(0, "int"), FLOAT(1, "float"), STRING(2, "string"), DATE(3, "date"), BIGDECIMAL(4, "bigDecimal"), DOUBLE(5,
				"double");
		CellType(int val, String description) {
		}

		public static CellType getCellType(int val) {
			switch (val) {
			case 0:
				return INT;
			case 1:
				return FLOAT;
			case 2:
				return STRING;
			case 3:
				return DATE;
			case 4:
				return BIGDECIMAL;
			case 5:
				return DOUBLE;
			default:
				return STRING;
			}
		}

		public static CellType getCellType(String str) {
			switch (str) {
			case "Int":
				return INT;
			case "Float":
				return FLOAT;
			case "String":
				return STRING;
			case "Date":
				return DATE;
			case "BigDecimal":
				return BIGDECIMAL;
			case "Double":
				return DOUBLE;
			default:
				return STRING;
			}
		}
	}

}

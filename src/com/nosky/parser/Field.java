package com.nosky.parser;

import java.util.ArrayList;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class Field implements Cloneable{
	private String tableColName;	//���ݿ�����
	private String excelColName;	//Excel�ļ�����
	private int excelColNum = -1;		//Excel�ļ��к�
	private String tableColType;		//���ݿ�������	0�����Σ�1���ַ�����2������ʱ��
	private boolean fixValue = false;
	private String defaltValue;
	private ArrayList<String> colValue = new ArrayList<String>();
	
	public String getTableColName() {
		return tableColName;
	}
	public void setTableColName(String tableColName) {
		this.tableColName = tableColName;
	}
	public String getExcelColName() {
		return excelColName;
	}
	public void setExcelColName(String excelColName) {
		this.excelColName = excelColName;
	}
	public int getExcelColNum() {
		return excelColNum;
	}
	public void setExcelColNum(int excelColNum) {
		this.excelColNum = excelColNum;
	}
	public String getTableColType() {
		return tableColType;
	}
	public void setTableColType(String tableColType) {
		this.tableColType = tableColType;
	}	
	public boolean isFixValue() {
		return fixValue;
	}
	public void setFixValue(boolean fixValue) {
		this.fixValue = fixValue;
	}
	public String getDefaltValue() {
		return defaltValue;
	}
	public void setDefaltValue(String defaltValue) {
		this.defaltValue = defaltValue;
	}
	public String getValue(int index) {
		String value = "";
		switch (tableColType) {
		case "INT":
		case "NUMBER":
		case "SMALLINT":
			value  = fixValue?defaltValue:colValue.get(index);
			break;
		case "VARCHAR":
		case "TEXT":
			value  = fixValue?"'"+defaltValue+"'":"'"+colValue.get(index)+"'";
			break;
		default:
			break;
		}
		return value;
	}
	public void addValue(int index, String value) {
		this.colValue.add(index, value);
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}

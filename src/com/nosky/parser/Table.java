package com.nosky.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.activation.FileDataSource;

import org.apache.camel.component.file.GenericFile;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Table implements Cloneable{
	private static final Logger LOG = LoggerFactory.getLogger(Table.class);
	
	private final static String ATTRIBUTE_NAME="name";
	private final static String NODE_ATTRIBUTE="attribute";
	private final static String NODE_COLNUMBER="columnNum";
	private final static String NODE_COLTYPE="FieldType";
	private final static String NODE_FIXVALUE="fixValue";
	private final static String NODE_DEFAULTVALUE="defaultValue";
	
	private String xmlname;		//xml模板名称
	private String tableName;	//数据库表名
	private int action;			//动作，0，insert；1，update
	private HashMap<Integer,Field> fields = new HashMap<Integer,Field>();
	
	private int curRownum = 0;
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public void addField(Integer num,Field field){
		fields.put(num, field);
	}
	
	public String getXmlname() {
		return xmlname;
	}
	public void setXmlname(String xmlname) {
		this.xmlname = xmlname;
	}
	public String toSQL(int index){
		
		if (0 == fields.size()) {
			return "";
		}
		
		String sql = "";
		String values = " VALUES(";
		switch (action) {
		case 0:
			sql = "INSERT INTO " + tableName + "(";
			
			for (Field f : fields.values()) {  
				sql += f.getTableColName() + ",";
				values += f.getValue(index) + ",";			  
			}  
			
			sql=sql.replace(sql.charAt(sql.length()-1)+"",") ");
			values=values.replace(values.charAt(values.length()-1)+"",") ");
			sql += values;
			break;
		case 1:
			break;
		default:
			break;
		}
		
		return sql;
	}
	
	public boolean parseXml(File file){
		setXmlname(file.getName());
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(file);
			if (null == document) {
				LOG.error("File(%s) read failed",file.getAbsolutePath());				
				return false;
			}
			Element root = document.getRootElement();
			if(null == root){
				LOG.error("root element of file(%s) read failed",file.getAbsolutePath());				
				return false;
			}
			String tblname=root.attributeValue(Table.ATTRIBUTE_NAME);
			if(null == tblname){
				LOG.error("Attribute of root element(%s) of file(%s) read failed",root.getName(), file.getAbsolutePath());				
				return false;
			}
			setTableName(tblname);
			List list=document.selectNodes("/table/field");
			for (Object object : list) {
				Element e = (Element) object;
				Field field = new Field();
				field.setTableColName(e.attributeValue(Table.ATTRIBUTE_NAME));
				field.setTableColType(e.attributeValue(Table.NODE_COLTYPE));
				field.setExcelColName(e.elementText(Table.NODE_ATTRIBUTE));
				int colNum = Integer.parseInt(e.elementText(Table.NODE_COLNUMBER));
				field.setExcelColNum(colNum);
				field.setFixValue(Boolean.parseBoolean(e.elementText(Table.NODE_FIXVALUE)));
				field.setDefaltValue(e.elementText(Table.NODE_DEFAULTVALUE));
				
				fields.put(colNum, field);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			return false;
		}  
		return true;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		Table table = (Table) super.clone();
		for (Field f : fields.values()) {  
			table.addField(f.getExcelColNum(), f);
		}  
		return table;

	}
	
	public boolean parse(GenericFile gf) throws IOException {
		if (null == gf) {
			LOG.error("GenericFile is null");
			return false;
		}
		File file = (File) gf.getFile();
		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook(is);
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				HSSFSheet sheet = workbook.getSheetAt(i);
	            if (sheet == null) {
	                continue;
	            }
	            for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
	            	HSSFRow row = sheet.getRow(rowNum);
	            	if (row == null) {
	                    continue;
	                }
	            	for (Field f : fields.values()) {  
	            		int colNum = f.getExcelColNum();
	        			if (colNum == -1) {	//不从Excel中读取的列
							continue;
						}
	        			String cell = row.getCell(colNum).getStringCellValue();
	        			f.addValue(rowNum, cell);
	        		}
	            	curRownum = rowNum;
	            }
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			is.close();
		}
		return true;
	}
	public int getRownum() {
		return curRownum;
	}
	public void setCurRownum(int curRownum) {
		this.curRownum = curRownum;
	}
}

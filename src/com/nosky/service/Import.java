package com.nosky.service;

import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.file.GenericFile;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nosky.parser.Table;
import com.nosky.server.*;
import com.rayda.utils.CamelContextHelper;

public class Import {
	
	private final static String FILE_TYPE = "FileType";
	
	private static final Logger LOG = LoggerFactory.getLogger(Import.class);
	
	private String basePath;
	private HashMap<String, Table> template = new HashMap<String, Table>();
	private CamelContext context = CamelContextHelper.getMyCamelContext();
	
	public HashMap<String, Object> ImportExcel(Exchange exchange) throws IOException {
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		MainFrame mf = MainFrame.getInstance(false);
		String fileType = (String) exchange.getIn().getHeader(Import.FILE_TYPE);
		
		Table tbl = null;
		try {
			tbl = (Table) template.get(fileType).clone();
			GenericFile gf = (GenericFile) exchange.getIn().getBody();
			mf.getTrayicon().displayMessage("提示", "开始处理文件"+gf.getFileName(), MessageType.INFO);
			boolean b = tbl.parse(gf);
			if(b){
				int iRows = tbl.getRownum();
				for (int i = 0; i < iRows; i++) {
					String sql = tbl.toSQL(i);
					ProducerTemplate template = context.createProducerTemplate();
			        Endpoint endpoint = context.getEndpoint("direct:db");
			        Exchange ex = endpoint.createExchange();			        
			        ex.getOut().setBody(sql);
			        template.send(endpoint, ex);
				}
				mf.getTrayicon().displayMessage("提示", "文件"+gf.getFileName()+"导入完成", MessageType.INFO);
			}else {
				mf.getTrayicon().displayMessage("提示", "文件"+gf.getFileName()+"解析出错", MessageType.INFO);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	
	public String getBasePath() {
		return basePath;
	}
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}	
	
	public void BuildParser(){
		if (StringUtils.isEmpty(basePath)) {
			LOG.error("basePath is null");
			return;
		}
		File root = new File(basePath);
		FileFilter ff = new FileFilter(){

			@Override
			public boolean accept(File pathname) {
				 if (pathname.getName().endsWith(".xml")) {
		                return true;
		            }
		            return false;
		        }
			};
		File[] files = root.listFiles(ff);
		for (File file : files) {
			Table table = new Table();
			table.parseXml(file);
			template.put(file.getName(), table);
		}
	}
	
	public void Initialize()
	{
		BuildParser();
	}
	public void Finilize()
	{
		
	}
}

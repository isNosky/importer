package com.nosky.server;


import java.awt.AWTException;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class MainFrame extends JFrame implements ActionListener{

		private static WebMain wm = null;
	 	private JLabel jlPort;  
	    private JTextField jt_port;  
	    private JButton jb_enter,jb_exit;  
	    private TrayIcon trayicon;  
	    
	    private static MainFrame instance;
	    
	    public TrayIcon getTrayicon() {
			return trayicon;
		}

		public void setTrayicon(TrayIcon trayicon) {
			this.trayicon = trayicon;
		}

		private void initCompoenent()  
	    {  
	        setSize(300,200);  
	        setLayout(new GridLayout(2,2));  
	        jlPort=new JLabel("端口："); 
	        jt_port=new JTextField();  
	        jt_port.setText(getPort());
	        jb_enter=new JButton("启动");  
	        jb_exit=new JButton("退出");  
	        jb_enter.addActionListener(this);  
	        jb_exit.addActionListener(this);  
	        add(jlPort);  
	        add(jt_port);  
	        add(jb_enter);  
	        add(jb_exit);  
	        setVisible(true);  
	    }  
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MainFrame mai=MainFrame.getInstance(false);
	}
	
	public static MainFrame getInstance(boolean bService){
		if(null == instance){
			instance = new MainFrame(bService);
		}
		return instance;
	}

	@Override
	public void actionPerformed(ActionEvent ex) {
		if(ex.getSource().equals(jb_enter))  
        {  
			int nPort = 8080;
            String port=jt_port.getText().toString().trim();  
            if(null != port){
            	nPort = Integer.parseInt(port);
            	if((nPort <= 0 ) ||(nPort >= 65535)){
            		JOptionPane.showMessageDialog(null, "请输入正确的端口号(1~65534)");
                	return;
            	}else {
            		if(isPortUsed(nPort)){
            			JOptionPane.showMessageDialog(null, "端口"+nPort+"已被占用");
                    	return;
            		}else {
            			savePort(nPort);
            			
            			new Thread(new Runnable() {
							
							@Override
							public void run() {
								wm = new WebMain();
								wm.StartServer();
							}
						}).start();
            			
            			this.dispose();
					}
				}
            		
            }else{
            	JOptionPane.showMessageDialog(null, "请输入端口号");
            	return;
            }
        }  
        else if(ex.getSource().equals(jb_exit))  
        {  
            System.exit(0);  
        }  
        else if(ex.getSource().equals(trayicon))  
        {  
            if(!isVisible())  
            {  
	            setVisible(true);  
	            toFront();  
            }  
        }  
	}
	
	private boolean isPortUsed(int port){
		ServerSocket s = null;
		try {
			s = new ServerSocket(port);    
          }
          catch (IOException ex) {
        	  return true;
          }
          try {
            s.close();
          }
          catch (IOException ex) {
          }
          return false;
	}
	
	private boolean savePort(int port){
		boolean isOk = false;
		File f = null;
        try{
        	f = new File("conf/jetty.xml");
            SAXReader reader = new SAXReader();
            Document doc = reader.read(f);
            List list = doc.selectNodes("//Property[@name='jetty.port']/@*");
            Iterator iter = list.iterator();
            while(iter.hasNext()){
                Attribute attr = (Attribute)iter.next();
                if(attr.getName().equals("default"))
                    attr.setValue(String.valueOf(port));
            }
            try{
            	OutputFormat format = OutputFormat.createPrettyPrint();
            	format.setEncoding("GBK");
                FileWriter newFile = new FileWriter(f);
                XMLWriter newWriter = new XMLWriter(newFile,format);
                newWriter.write(doc);
                newFile.close();
                newWriter.close();                
            }catch(Exception e){
                e.printStackTrace();
            }
            isOk = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return isOk;
	}
	private MainFrame(boolean bService)  
    {  
		if(!bService){
			initCompoenent();
		}
		
        if(!SystemTray.isSupported())  
        {  
            return;  
        }  
        else  
        {  
            SystemTray systemTray=SystemTray.getSystemTray();  
            String title="数据采集系统";  
            String company="锐意恒达";  
            Image image=Toolkit.getDefaultToolkit().getImage("image\\systray.png");  
            trayicon=new TrayIcon(image,title+"\n"+company,createMenu());  
            trayicon.setImageAutoSize(true);
            trayicon.addActionListener(this);  
            try {  
                systemTray.add(trayicon);  
                trayicon.displayMessage(title, company, MessageType.INFO);  
            } catch (AWTException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
    private PopupMenu createMenu()  
    {  
        PopupMenu menu=new PopupMenu();  
          
        MenuItem open=new MenuItem("打开");  
        open.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent ex)  
            {  
                if(!isVisible())  
                {  
                    setVisible(true);  
                    toFront();  
                }  
                else  
                {  
                    toFront();  
                }  
            }  
        });  
        MenuItem exit=new MenuItem("关闭");  
        exit.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent ex)  
            { 
            	if(null != wm){
            		wm.StopServer();
            	}
                System.exit(0);  
            }  
        });
        
        MenuItem installservice=new MenuItem("安装服务");  
        installservice.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent ex)  
            {  
            	BufferedReader br = null;
                try {
                	String cmd = System.getProperty("user.dir") + "/service.exe --WinRun4J:RegisterService";
                    Process p = Runtime.getRuntime().exec(cmd);
                    br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
     
                    if (br != null) {
                        try {
                            br.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }  
        });
        MenuItem uninstallservice=new MenuItem("卸载服务");  
        uninstallservice.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent ex)  
            {  
            	BufferedReader br = null;
                try {
                	String cmd = System.getProperty("user.dir") + "/service.exe --WinRun4J:UnregisterService";
                    Process p = Runtime.getRuntime().exec(cmd);
                    br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
     
                    if (br != null) {
                        try {
                            br.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }  
        });
        
        menu.add(installservice);  
        menu.add(uninstallservice);  
        menu.addSeparator();  
        menu.add(open);  
        menu.addSeparator();  
        menu.add(exit);         
        
        return menu;  
    } 
    
    private String getPort(){
		File f = null;
		String strPortString = "";
        try{
        	f = new File("conf/jetty.xml");
            SAXReader reader = new SAXReader();
            Document doc = reader.read(f);
            List list = doc.selectNodes("//Property[@name='jetty.port']/@*");
            Iterator iter = list.iterator();
            while(iter.hasNext()){
                Attribute attr = (Attribute)iter.next();
                if(attr.getName().equals("default"))
                	strPortString =attr.getValue();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return strPortString;
	}
}

package com.nosky.server;

import org.apache.camel.spring.Main;
import org.apache.log4j.PropertyConfigurator;

public class WebMain {
	
	JettyCustomServer server = null;
	//private static final Logger LOG = LoggerFactory.getLogger(WebMain.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WebMain wm = new WebMain();
		wm.StartServer();
	}
	
	public void StartServer(){
		try {
			System.setProperty("WORKDIR", System.getProperty("user.dir"));
			PropertyConfigurator.configure("conf/log4j.properties");
			
			/*Main spring = new Main();
			spring.setFileApplicationContextUri("conf/camel-context.xml");
			//spring.setApplicationContextUri("META-INF/spring/camel-context.xml");
			spring.enableHangupSupport();
			spring.start();*/
			
			server = new JettyCustomServer("conf/jetty.xml", "/");
	        server.startServer();
		} catch (Exception e) {
			e.printStackTrace();
		}        
	}
	
	public void StopServer(){
		try {
	        server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}        
	}

}

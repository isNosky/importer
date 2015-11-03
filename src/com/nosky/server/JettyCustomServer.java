package com.nosky.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.xml.sax.SAXException;

//总的服务类，本质上是一个分发器
public class JettyCustomServer extends Server {
    private String xmlConfigPath;

    private String contextPath;

    private String warPath;

    private String resourceBase = "./webapp";

    private String webXmlPath = "./webapp/WEB-INF/web.xml";

    public JettyCustomServer(String xmlConfigPath, String contextPath,
            String resourceBase, String webXmlPath) {
        this(xmlConfigPath, contextPath, resourceBase, webXmlPath, null);
    }

    public JettyCustomServer(String xmlConfigPath, String contextPath) {
        this(xmlConfigPath, contextPath, null, null, null);
    }

    public JettyCustomServer(String xmlConfigPath, String contextPath,
            String warPath) {
        this(xmlConfigPath, contextPath, null, null, warPath);
    }

    public JettyCustomServer(String xmlConfigPath, String contextPath,
            String resourceBase, String webXmlPath, String warPath) {
        super();
        if (StringUtils.isNotBlank(xmlConfigPath)) {
            this.xmlConfigPath = xmlConfigPath;
            readXmlConfig();
        }

        if (StringUtils.isNotBlank(warPath)) {
            this.warPath = warPath;
            if (StringUtils.isNotBlank(contextPath)) {
                this.contextPath = contextPath;
                applyHandle(true);
            }
        } else {
            if (StringUtils.isNotBlank(resourceBase))
                this.resourceBase = resourceBase;
            if (StringUtils.isNotBlank(webXmlPath))
                this.webXmlPath = webXmlPath;
            if (StringUtils.isNotBlank(contextPath)) {
                this.contextPath = contextPath;
                applyHandle(false);
            }
        }

    }

    private void readXmlConfig() {
        try {
            XmlConfiguration configuration = new XmlConfiguration(
                    new FileInputStream(this.xmlConfigPath));
            configuration.configure(this);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (SAXException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void applyHandle(Boolean warDeployFlag) {

        ContextHandlerCollection handler = new ContextHandlerCollection();

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath(contextPath);
        webapp.setDefaultsDescriptor("./webapp/WEB-INF/web.xml");
        //webapp.setWelcomeFiles(new String[] {"index.html"});
        if (!warDeployFlag) {
            webapp.setResourceBase(resourceBase);
            webapp.setDescriptor(webXmlPath);
        } else {
            webapp.setWar(warPath);
        }

        handler.addHandler(webapp);

        super.setHandler(handler);
    }

    public void startServer() {
        try {
        	
//        	HttpConfiguration https_config = new HttpConfiguration();
//        	https_config.setSecureScheme("https");
//        	https_config.setSecurePort(8443);
//        	https_config.setOutputBufferSize(32768);
//        	https_config.addCustomizer(new SecureRequestCustomizer());
//        	 
//        	SslContextFactory sslContextFactory = new SslContextFactory();
//        	sslContextFactory.setKeyStorePath("keystore");
//        	sslContextFactory.setKeyStorePassword("OBF:1xtb1uo71wg41y0q1y7z1y101wfu1unr1xu7");
//        	sslContextFactory.setKeyManagerPassword("OBF:1xtb1uo71wg41y0q1y7z1y101wfu1unr1xu7");
//        	 
//        	ServerConnector httpsConnector = new ServerConnector(server,
//        	        new SslConnectionFactory(sslContextFactory,"http/1.1"),
//        	        new HttpConnectionFactory(https_config));
//        	httpsConnector.setPort(8443);
//        	httpsConnector.setIdleTimeout(500000);
//        	super.addConnector(httpsConnector);
       	
            super.start();
            System.out.println("current thread:"
                    + super.getThreadPool().getThreads() + "| idle thread:"
                    + super.getThreadPool().getIdleThreads());
            super.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getXmlConfigPath() {
        return xmlConfigPath;
    }

    public void setXmlConfigPath(String xmlConfigPath) {
        this.xmlConfigPath = xmlConfigPath;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getResourceBase() {
        return resourceBase;
    }

    public void setResourceBase(String resourceBase) {
        this.resourceBase = resourceBase;
    }

    public String getWebXmlPath() {
        return webXmlPath;
    }

    public void setWebXmlPath(String webXmlPath) {
        this.webXmlPath = webXmlPath;
    }

    public String getWarPath() {
        return warPath;
    }

    public void setWarPath(String warPath) {
        this.warPath = warPath;
    }
}
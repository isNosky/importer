package com.nosky.server;

import org.boris.winrun4j.AbstractService; 
import org.boris.winrun4j.EventLog; 
import org.boris.winrun4j.ServiceException; 


public class ServiceMain extends AbstractService {

	@Override
	public int serviceMain(String[] arg0) throws ServiceException {
		
		MainFrame mainFrame = MainFrame.getInstance(true);
		WebMain wm = new WebMain();
		wm.StartServer();
		
		int count = 0; 
        while (!shutdown) { 
            try { 
                Thread.sleep(1000); 
            } catch (InterruptedException e) { 
            } 
            if (++count % 10 == 0) { 
                EventLog.report("test", EventLog.INFORMATION, "test..." + count); 
                System.out.println("test log...." + count); 
            } 
        } 
        wm.StopServer();
        System.out.println("service shutdown..."); 
        return 0; 
	}

}

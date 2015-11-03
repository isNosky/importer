package com.rayda.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextHelper implements ApplicationContextAware{
	private static ApplicationContext context;
	
	public static Object getBean(String beanName)
    {
        return context.getBean(beanName);
    }
	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		context = arg0;
	}

}

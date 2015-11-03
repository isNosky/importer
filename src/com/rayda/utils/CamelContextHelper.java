package com.rayda.utils;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;

public class CamelContextHelper implements CamelContextAware{
	private static CamelContext context;
	@Override
	public CamelContext getCamelContext() {
		return context;
	}

	public static CamelContext getMyCamelContext() {
		return context;
	}
	
	@Override
	public void setCamelContext(CamelContext arg0) {
		context = arg0;		
	}

}

package com.emirates;

import java.util.HashMap;

import org.jboss.soa.esb.actions.AbstractActionLifecycle;
import org.jboss.soa.esb.helpers.ConfigTree;
import org.jboss.soa.esb.message.Message;

public class PrepareRequestMessage extends AbstractActionLifecycle {
	
	protected ConfigTree _config;

	public PrepareRequestMessage(ConfigTree config) {
		super();
		_config = config;
	}
	
	public Message preapreRequest(Message message){
		HashMap requestMap = new HashMap();
		/*
		requestMap.put("shortFinalRequest.flightNo", "EK546");
		requestMap.put("shortFinalRequest.flightDates", "19/01/2010");
		requestMap.put("shortFinalRequest.ShortFinals", "True");
		*/
		
		RequestDTO dto = new RequestDTO("EK546", "19/01/2010", "True");
		requestMap.put("shortFinalRequest", dto);
		message.getBody().add(requestMap);
		return message;
	}
	

}

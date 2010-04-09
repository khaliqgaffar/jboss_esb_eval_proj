package com.emirates;

import java.util.Map;

import org.jboss.soa.esb.actions.AbstractActionLifecycle;
import org.jboss.soa.esb.helpers.ConfigTree;
import org.jboss.soa.esb.message.Message;

import com.emirates.model.RequestDTO;
import com.emirates.model.ResponseDTO;

public class ResponseForIncomingRequest extends AbstractActionLifecycle {
	
	protected ConfigTree _config;


	
	
	
	public ResponseForIncomingRequest(ConfigTree config) {
		super();
		_config = config;
	}





	public Message sendReply(Message message)throws Exception
	{
		message.getBody().add("<sch:shortFinalResponse xmlns:sch = \"http://www.tibco.com/schemas/SAMPLEXML/Schema.xsd2\"><sch:status>SUCCESS</sch:status></sch:shortFinalResponse>");
		return message;
	}
	
	public Message validateFromSmooks(Message message)throws Exception
	{
		//message.getBody().add("<sch:shortFinalResponse xmlns:sch = \"http://www.tibco.com/schemas/SAMPLEXML/Schema.xsd2\"><sch:status>SUCCESS</sch:status></sch:shortFinalResponse>");
	    System.out.println(message.getBody());
	    Map javaResultMap = (Map)message.getBody().get();
	    ResponseDTO response = null;
		RequestDTO request = (RequestDTO) javaResultMap.get("RequestDTO");
		if(request!=null){
			System.out.println("Flight Number ::" + request.getFlightNo());
			response = new ResponseDTO();
			response.setStatus("SUCCESS-AYMAN");
		}else{
			System.out.println("Request DTO is null");
		}
		//javaResultMap.put("responseDTO", response);
		//message.getBody().add("responseDTO" ,response);
		message.getBody().add("originalMessage",message.getBody().get());
		message.getBody().add(response);
		return message;
	}
	

}

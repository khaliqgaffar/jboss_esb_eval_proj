package com.emirates;

import java.util.Map;

import org.jboss.internal.soa.esb.message.format.xml.body.content.TextBodyImpl;
import org.jboss.soa.esb.actions.AbstractActionLifecycle;
import org.jboss.soa.esb.helpers.ConfigTree;
import org.jboss.soa.esb.message.Body;
import org.jboss.soa.esb.message.Message;


public class DisplayAndConvertDBMessage extends AbstractActionLifecycle {
	protected ConfigTree _config;

	public DisplayAndConvertDBMessage(ConfigTree config) {
		super();
		_config = config;
	}

	public Message displayMessage(Message message) throws Exception {
		logHeader();
		Map<String, Object> rowData = (Map) message.getBody().get();
		StringBuffer results = new StringBuffer();
		results.append("<TibcoAuditLogs>");
		for (Map.Entry<String, Object> curr : rowData.entrySet()) {
			results.append("<" + curr.getKey()+">"+  curr.getValue()+"</" + curr.getKey()+">");
		}
		results.append("</TibcoAuditLogs>");
		System.out.println(results.toString());
		logFooter();

		// Set message properties and message body so that SystemPrintln will
		// display message
		//message.getProperties().setProperty("jbesbfilename",
				//"helloworldSQlAction.log");
		//Body body = null;
		//message.getBody().replace(body
		
		message.getBody().add(results.toString());
		message.getBody().add("audit_Message", results.toString());
		return message;
	}

	// This makes it easier to read on the console
	private void logHeader() {
		System.out
				.println("\n&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
	}

	private void logFooter() {
		System.out
				.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n");
	}
}

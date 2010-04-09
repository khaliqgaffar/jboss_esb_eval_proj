package com.emirates;

import org.jboss.soa.esb.actions.AbstractActionLifecycle;
import org.jboss.soa.esb.helpers.ConfigTree;
import org.jboss.soa.esb.message.Body;
import org.jboss.soa.esb.message.Message;

public class DisplayMessage extends AbstractActionLifecycle {
	protected ConfigTree _config;

	public DisplayMessage(ConfigTree config) {
		super();
		_config = config;
	}

	public Message printMessage(Message message) throws Exception {
		logHeader();
		System.out.println("Body: " + message.getBody().get().toString());
		Body msgBody = message.getBody();
		if(msgBody.get("audit_Message")==null){
			System.out.println("Body Content: " + msgBody.get("audit_Message").toString());
		}
		logFooter();
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

package com.emirates;

import org.jboss.soa.esb.client.ServiceInvoker;
import org.jboss.soa.esb.message.Message;
import org.jboss.soa.esb.message.format.MessageFactory;
import org.jboss.soa.esb.message.format.MessageType;

public class testWebserviceConsuner {
   public void sendMessage(String message) throws Exception
   {
      // Create the delivery adapter for the target service (cache it)
      System.setProperty("javax.xml.registry.ConnectionFactoryClass",
            "org.apache.ws.scout.registry.ConnectionFactoryImpl");

      // Create the delivery adapter for the target service (cache it)
      ServiceInvoker deliveryAdapter = new ServiceInvoker("com.emirates",
            "serviceConsumer");
      

      // Create and populate the request message...
      Message requestMessage = MessageFactory.getInstance().getMessage(
            MessageType.JBOSS_XML);

      requestMessage.getBody().add(message);

      // Deliver the request message synchronously - timeout after 20
      // seconds...
      deliveryAdapter.deliverAsync(requestMessage);
   }
   
   public static void main(String args[]) throws Exception
   {
	   testWebserviceConsuner sm = new testWebserviceConsuner();
      sm.sendMessage(args[0]);
   }

}

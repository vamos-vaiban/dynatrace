package com.xoriant.mqTest;

import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;


import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class SecondConsumer {

	// System exit status value (assume unset value to be 1)
	private static int status = 1;

	// Create variables for the connection to MQ
	private static final String HOST = "localhost"; // Host name or IP address
	private static final int PORT = 1414; // Listener port for your queue manager
	private static final String CHANNEL = "CodeToApp"; // Channel name
	private static final String QMGR = "QM1"; // Queue manager name
	private static final String APP_USER = "mquser"; // User name that application uses to connect to MQ
	private static final String APP_PASSWORD = "mq@12345678"; // Password that the application uses to connect to MQ
	private static final String QUEUE_NAME = "SampleQueue2"; // Queue that the application uses to put and get messages to and from


	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) throws InterruptedException{

		// Variables
		JMSContext context = null;
		Destination destination = null;
		Destination source = null;
		JMSProducer producer = null;
		JMSConsumer consumer = null;

		try {
			// Create a connection factory
			JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
			JmsConnectionFactory cf = ff.createConnectionFactory();

			// Set the properties
			cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, HOST);
			cf.setIntProperty(WMQConstants.WMQ_PORT, PORT);
			cf.setStringProperty(WMQConstants.WMQ_CHANNEL, CHANNEL);
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
			cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, QMGR);
			cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "JmsPutGet (JMS)");
			cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
			cf.setStringProperty(WMQConstants.USERID, APP_USER);
			cf.setStringProperty(WMQConstants.PASSWORD, APP_PASSWORD);
			//cf.setStringProperty(WMQConstants.WMQ_SSL_CIPHER_SUITE, "*TLS12");

			// Create JMS objects
			context = cf.createContext();
			source = context.createQueue("queue:///" + QUEUE_NAME);
			try {
			boolean append = true;
	        FileHandler handler = new FileHandler("info.log", append);
	 
	        Logger logger = Logger.getLogger("com.javacodegeeks.snippets.core");
	        logger.addHandler(handler);
			
	        
			for(int i=1;i<1000;i++) {
				
				
		         
		       
				consumer = context.createConsumer(source); // autoclosable
				String receivedMessage = consumer.receiveBody(String.class, 15000); // in ms or 15 seconds
				logger.severe(receivedMessage);
				System.out.println("\nReceived message from :\n"+ QUEUE_NAME+" : " + receivedMessage);
				
				Thread.sleep((int)(Math.random()*(5000-4000)+4000));
				
			}
			}catch(Exception e) {
				e.getMessage();
			}
			
			
			

//			consumer = context.createConsumer(destination); // autoclosable
//			String receivedMessage = consumer.receiveBody(String.class, 15000); // in ms or 15 seconds
//
//			System.out.println("\nReceived message:\n" + receivedMessage);

                        

			recordSuccess();
		} catch (JMSException jmsex) {
			recordFailure(jmsex);
		}

		System.exit(status);

	} // end main()

	/**
	 * Record this run as successful.
	 */
	private static void recordSuccess() {
		System.out.println("SUCCESS");
		status = 0;
		return;
	}

	/**
	 * Record this run as failure.
	 *
	 * @param ex
	 */
	private static void recordFailure(Exception ex) {
		if (ex != null) {
			if (ex instanceof JMSException) {
				processJMSException((JMSException) ex);
			} else {
				System.out.println(ex);
			}
		}
		System.out.println("FAILURE");
		status = -1;
		return;
	}

	/**
	 * Process a JMSException and any associated inner exceptions.
	 *
	 * @param jmsex
	 */
	private static void processJMSException(JMSException jmsex) {
		System.out.println(jmsex);
		Throwable innerException = jmsex.getLinkedException();
		if (innerException != null) {
			System.out.println("Inner exception(s):");
		}
		while (innerException != null) {
			System.out.println(innerException);
			innerException = innerException.getCause();
		}
		return;
	}

}


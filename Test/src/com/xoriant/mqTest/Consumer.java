package com.xoriant.mqTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import com.xoriant.mqTest.JDBC_integration.*;



public class Consumer {

	// System exit status value (assume unset value to be 1)
	private static int status = 1;

	// Create variables for the connection to MQ
	private static final String HOST = "localhost"; // Host name or IP address
	private static final int PORT = 1414; // Listener port for your queue manager
	private static final String CHANNEL = "CodeToApp"; // Channel name
	private static final String QMGR = "QM1"; // Queue manager name
	private static final String APP_USER = "mquser"; // User name that application uses to connect to MQ
	private static final String APP_PASSWORD = "mq@12345678"; // Password that the application uses to connect to MQ
	private static final String SOURCE_QUEUE_NAME = "SampleQueue";
	private static final String DESTINATION_QUEUE_NAME = "SampleQueue2";
	
	
	
 
	
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
			source = context.createQueue("queue:///" + SOURCE_QUEUE_NAME);
			
			destination= context.createQueue("queue:///" + DESTINATION_QUEUE_NAME);
			
			
			int index = 0;
			while(true) {
			consumer = context.createConsumer(source); // autoclosable
			String receivedMessage = consumer.receiveBody(String.class, 15000); // in ms or 15 seconds
			
			producer = context.createProducer();
			producer.send(destination, receivedMessage);
			System.out.println("Sent message: " + receivedMessage+" to queue: "+ DESTINATION_QUEUE_NAME);
			
			JDBC_Integration.addDataToTable(++index,receivedMessage);
			System.out.println("\nReceived message:\n" + receivedMessage);
			
			Thread.sleep((int)(Math.random()*(3000-2000)+2000));

			recordSuccess();}
			
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
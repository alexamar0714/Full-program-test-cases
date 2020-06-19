package net.neophyte.messaging.jms;

import javax.jms.DeliveryMode;
import javax.jms.Session;

/**
 * 
 * @author shuvro
 *
 */
public abstract class Configuration {

	public static final int IGNORE = -1;
	private static final String brokerUrl = "myFactoryLookup";//"amqp://localhost:32796";
	private static final String userId = "admin";
	private static final String password = "admin";
	private static final String queueName = "TEST.QUEUE";
	private static final String topicName = "TEST.TOPIC";
	private final static int MSG_SIZE_IN_BYTES = 1024;// 1KB
	private final static int messageCount = 100;
	private final static long runTime = IGNORE;// 1 minute
	private final static long receiveTimeout = 10000;
	private final static int ackMode = Session.AUTO_ACKNOWLEDGE;
	private final static int deliveryPersistent = DeliveryMode.PERSISTENT;

	public static String getBrokerurl() {
		return brokerUrl;
	}

	public static String getUserid() {
		return userId;
	}

	public static String getPassword() {
		return password;
	}

	public static String getQueueName() {
		return queueName;
	}

	public static String getTopicName() {
		return topicName;
	}

	public static int getMessageSize() {
		return MSG_SIZE_IN_BYTES;
	}

	public static int getMessageCount() {
		return messageCount;
	}

	public static long getRuntime() {
		return runTime;
	}

	public static long getReceivetimeout() {
		return receiveTimeout;
	}

	public static int getAckmode() {
		return ackMode;
	}

	public static int getDeliverypersistent() {
		return deliveryPersistent;
	}
}

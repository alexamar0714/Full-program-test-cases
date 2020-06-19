package net.neophyte.messaging.jms.topic;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.NamingException;

import net.neophyte.messaging.jms.AbstractJMSClient;
import net.neophyte.messaging.jms.Configuration;
import net.neophyte.messaging.jms.SimpleConnectionProvider;
import net.neophyte.messaging.jms.utils.Util;

/**
 * A simple JMS durable topic subscriber that receives message in async mode
 * 
 * @author shuvro
 *
 */
public class SimpleDurableAsyncSubscriber extends AbstractJMSClient {

	private static String CLIENT_ID = "myClientId";
	private static String SUBSCRIPTION_NAME = "myTopicSubscription";

	public static void main(String[] arg) {
		SimpleDurableAsyncSubscriber pc = new SimpleDurableAsyncSubscriber();
		System.out.println("-Calling subscribeAndReceive-");
		pc.subscribeAndReceive(Configuration.getMessageCount(),
				Configuration.getRuntime());
		System.exit(0);
	}

	public void subscribeAndReceive(long numOfMessages, long runTime) {
		Connection connection = null;
		Session session = null;
		MessageConsumer msgReceiver = null;
		startTime = System.currentTimeMillis();
		try {
			connection = SimpleConnectionProvider.createConnectionInstance(
					Configuration.getBrokerurl(), Configuration.getUserid(),
					Configuration.getPassword());
			/*
			 * Note that we have to set the client id before the connection has
			 * been used at all
			 */
			connection.setClientID(CLIENT_ID);
			session = SimpleConnectionProvider
					.getSession(Session.AUTO_ACKNOWLEDGE);
			Topic topic = session.createTopic(Configuration.getTopicName());
			/*
			 * Creates an unshared durable subscription on the specified topic
			 * (if one does not already exist) and creates a consumer on that
			 * durable subscription. So, once this program runs and a
			 * subscription is created, trying to run another instance of this
			 * program would fail and cause an exception because of the unshared
			 * nature of this durable connection. To overcome this problem one
			 * would have to use the createSharedConsumer method if the JMS
			 * provider supports it.
			 */
			msgReceiver = session.createDurableSubscriber(topic,
					SUBSCRIPTION_NAME);

			msgReceiver.setMessageListener(new SimpleMessageListener());
			connection.start();
			while (runTimeRemains(runTime)
					|| moreMessagesToReceive(numOfMessages)) {
				try {
					Thread.sleep(200); /* sleep 200 mili seconds */
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			long totalRunTime = System.currentTimeMillis() - startTime;
			logger.info("Total run time: "
					+ Util.getHh_Mm_Ss_ssss_Time(totalRunTime)
					+ ", Total messages received: " + msgs.get());
			logger.info("Total errors: " + errors.get());
		} catch (JMSException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (NamingException e1) {
			logger.error(e1.getMessage());
			e1.printStackTrace();
		} finally {
			SimpleConnectionProvider.closeConnection();
		}
	}
}
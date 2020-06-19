package net.neophyte.messaging.jms.topic;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.NamingException;

import net.neophyte.messaging.jms.AbstractJMSClient;
import net.neophyte.messaging.jms.Configuration;
import net.neophyte.messaging.jms.SimpleConnectionProvider;
import net.neophyte.messaging.jms.utils.Util;

/**
 * A simple JMS topic subscriber that receives message in sync mode
 * 
 * @author shuvro
 *
 */
public class SimpleSyncSubscriber extends AbstractJMSClient {

	public static void main(String[] arg) {
		SimpleSyncSubscriber pc = new SimpleSyncSubscriber();
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
			session = SimpleConnectionProvider
					.getSession(Session.AUTO_ACKNOWLEDGE);
			Topic topic = session.createTopic(Configuration.getTopicName());
			msgReceiver = session.createConsumer(topic);
			connection.start();

			Message receivedMessage = null;
			while (runTimeRemains(runTime)
					|| moreMessagesToReceive(numOfMessages)) {
				try {
					receivedMessage = msgReceiver.receive(Configuration.getReceivetimeout());
					if (Util.isNotNull(receivedMessage)) {
						msgs.incrementAndGet();
						if (receivedMessage instanceof TextMessage) {
							TextMessage textMessage = (TextMessage) receivedMessage;
							System.out.println("Received: "
									+ textMessage.getText());
						}
					}
				} catch (Exception e) {
					errors.incrementAndGet();
					logger.info(e.getMessage());
				}
			}
			long totalRunTime = System.currentTimeMillis() - startTime;
			logger.info("Total run time: "
					+ Util.getHh_Mm_Ss_ssss_Time(totalRunTime)
					+ ", Total messages received: " + msgs.get());
			logger.info("Total errors: " + errors.get());
		} catch (JMSException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NamingException e1) {
			logger.error(e1.getMessage());
			e1.printStackTrace();
		} finally {
			SimpleConnectionProvider.closeConnection();
		}
	}
}
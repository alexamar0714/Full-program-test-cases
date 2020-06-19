package net.neophyte.messaging.jms.queue;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import net.neophyte.messaging.jms.AbstractJMSClient;
import net.neophyte.messaging.jms.Configuration;
import net.neophyte.messaging.jms.SimpleConnectionProvider;
import net.neophyte.messaging.jms.utils.Util;

/**
 * A simple JMS message consumer that receives messages in sync mode
 * 
 * @author shuvro
 *
 */
public class SimpleSyncConsumer extends AbstractJMSClient {

	public static void main(String[] arg) {
		SimpleSyncConsumer pc = new SimpleSyncConsumer();
		System.out.println("-Calling receiveMessages-");
		pc.recieveMessages(Configuration.getMessageCount(),
				Configuration.getRuntime());
		System.exit(0);
	}

	private void recieveMessages(long numOfMessages, long runTime) {
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
			Queue queue = session.createQueue(Configuration
					.getQueueName());
			msgReceiver = session.createConsumer(queue);
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
							String text = textMessage.getText();
							System.out.println("Received: " + text);
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
					+ ", Total messages consumed: " + msgs.get());
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
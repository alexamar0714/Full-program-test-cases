package net.neophyte.messaging.jms.queue;

import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import net.neophyte.messaging.jms.AbstractJMSClient;
import net.neophyte.messaging.jms.Configuration;
import net.neophyte.messaging.jms.SimpleConnectionProvider;
import net.neophyte.messaging.jms.utils.Util;

/**
 * A simple JMS message browser
 * 
 * @author shuvro
 *
 */
public class SimpleBrowser extends AbstractJMSClient {

	public static void main(String[] arg) {
		SimpleBrowser pc = new SimpleBrowser();
		System.out.println("-Calling browseMessages()-");
		pc.browseMessages(Configuration.getMessageCount(),
				Configuration.getRuntime());
		System.exit(0);
	}

	private void browseMessages(long numOfMessages, long runTime) {
		Connection connection = null;
		Session session = null;
		QueueBrowser browser = null;
		startTime = System.currentTimeMillis();
		try {
			connection = SimpleConnectionProvider.createConnectionInstance(
					Configuration.getBrokerurl(), Configuration.getUserid(),
					Configuration.getPassword());
			session = SimpleConnectionProvider
					.getSession(Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue(Configuration
					.getQueueName());
			connection.start();
			browser = session.createBrowser(queue);

			@SuppressWarnings("unchecked")
			Enumeration<Message> messagesStream = (Enumeration<Message>) browser
					.getEnumeration();

			Message current;
			while (messagesStream.hasMoreElements()
					&& (runTimeRemains(runTime) || moreMessagesToBrowse(numOfMessages))) {
				try {
					current = messagesStream.nextElement();
					msgs.incrementAndGet();
					if (current instanceof TextMessage) {
						TextMessage textMessage = (TextMessage) current;
						String text = textMessage.getText();
						System.out.println("Browsed: " + text);
					}
				} catch (Exception e) {
					errors.incrementAndGet();
					logger.info(e.getMessage());
				}
			}
			long totalRunTime = System.currentTimeMillis() - startTime;
			logger.info("Total run time: "
					+ Util.getHh_Mm_Ss_ssss_Time(totalRunTime)
					+ ", Total messages browsed: " + msgs.get());
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
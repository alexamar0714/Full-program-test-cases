package net.neophyte.messaging.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author shuvro
 *
 */
public class SimpleMessageListener implements MessageListener {
	private static Logger logger = LoggerFactory
			.getLogger(SimpleMessageListener.class);

	@Override
	public void onMessage(Message msg) {
		if (msg instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) msg;
			String text = "";
			try {
				text = textMessage.getText();
			} catch (JMSException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			System.out.println("Received: " + text);
		} else {
			System.out.println("Received: " + msg);
		}
	}
}

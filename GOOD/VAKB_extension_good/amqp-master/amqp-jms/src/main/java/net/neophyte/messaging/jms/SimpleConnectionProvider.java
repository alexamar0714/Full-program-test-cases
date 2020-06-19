package net.neophyte.messaging.jms;

import java.util.concurrent.atomic.AtomicReference;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A dumb Singleton utility class to create JMS connection over AMQP protocol.
 * It does not contain any logic to manage connections. It provides
 * functionality for creation and closing of the connection. It also does not
 * try to hide the connection object
 *
 * @author shuvro
 */
public class SimpleConnectionProvider {

	private static Logger logger = LoggerFactory.getLogger(SimpleConnectionProvider.class);
	private static AtomicReference<Connection> CONNECTION_ATOMIC_REFERENCE = new AtomicReference<>();

	private SimpleConnectionProvider() {
	}

	public static Connection createConnectionInstance(String brokerUrl, String userName, String password)
			throws JMSException, NamingException {
		if (CONNECTION_ATOMIC_REFERENCE.get() == null) {
			synchronized (CONNECTION_ATOMIC_REFERENCE) {
				if (CONNECTION_ATOMIC_REFERENCE.get() == null) {
					Connection connection = createConnection(brokerUrl, userName, password);
					CONNECTION_ATOMIC_REFERENCE.set(connection);
				}
			}
		}

		return CONNECTION_ATOMIC_REFERENCE.get();
	}

	/**
	 * @param brokerUrl
	 *            The JMS broker url
	 * @param userId
	 *            The user id
	 * @param password
	 *            The password
	 * @return {@link Connection}
	 * @throws JMSException
	 * @throws NamingException
	 */
	private static Connection createConnection(String brokerUrl, String userId, String password)
			throws JMSException, NamingException {

		Context context = new InitialContext();

		ConnectionFactory factory = (ConnectionFactory) context.lookup(brokerUrl);
		// Get a new instance of ConnectionFactory
		// create a connection - providing the user id and password
		Connection connection = factory.createConnection(userId, password);
		return connection;
	}

	/**
	 *
	 * @param ackMode
	 *            The acknowledgement mode
	 * @return A {@link Session} on the provided Connection
	 * @throws JMSException
	 *             An instance of {@link JMSException}
	 */
	public static Session getSession(int ackMode) throws JMSException {
		Session session = null;
		if (CONNECTION_ATOMIC_REFERENCE.get() != null) {
			boolean transacted = (ackMode == Session.SESSION_TRANSACTED ? true : false);
			session = CONNECTION_ATOMIC_REFERENCE.get().createSession(transacted, ackMode);
		} else {
			logger.debug("A connection instance needs to be created first before trying to create session!");
		}
		return session;
	}

	public static void closeConnection() {
		if (CONNECTION_ATOMIC_REFERENCE.get() != null) {
			synchronized (CONNECTION_ATOMIC_REFERENCE) {
				if (CONNECTION_ATOMIC_REFERENCE.get() != null) {
					try {
						CONNECTION_ATOMIC_REFERENCE.get().close();
					} catch (JMSException jmse) {
						jmse.printStackTrace();
						logger.debug(jmse.getMessage());
					}
					CONNECTION_ATOMIC_REFERENCE.set(null);
				}
			}
		}
	}
}
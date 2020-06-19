package ca.uhn.fhir.jpa.subscription.email;

import ca.uhn.fhir.jpa.testutil.RandomServerPortProvider;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import org.hl7.fhir.dstu3.model.IdType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;

import static org.junit.Assert.*;

public class JavaMailEmailSenderTest {

	private static final Logger ourLog = LoggerFactory.getLogger(JavaMailEmailSenderTest.class);
	private static GreenMail ourTestSmtp;
	private static int ourPort;

	@Test
	public void testSend() throws Exception {
		JavaMailEmailSender sender = new JavaMailEmailSender();
		sender.setSmtpServerHostname("localhost");
		sender.setSmtpServerPort(ourPort);
		sender.setSmtpServerUsername(null);
		sender.setSmtpServerPassword(null);
		sender.start();

		String body = "foo";

		EmailDetails details = new EmailDetails();
		details.setSubscription(new IdType("Subscription/123"));
		details.setFrom("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="2741484867425f464a574b420944484a">[email protected]</a> ");
		details.setTo(Arrays.asList(" <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="a1d5ce90e1c4d9c0ccd1cdc48fc2cecc">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="25514a1765405d44485549400b464a48">[email protected]</a>   "));
		details.setSubjectTemplate("test subject");
		details.setBodyTemplate(body);
		sender.send(details);

		MimeMessage[] messages = ourTestSmtp.getReceivedMessages();
		assertEquals(2, messages.length);
		ourLog.info("Received: " + GreenMailUtil.getWholeMessage(messages[0]));
		assertEquals("test subject", messages[0].getSubject());
		assertEquals(1, messages[0].getFrom().length);
		assertEquals("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="187e7777587d60797568747d367b7775">[email protected]</a>", ((InternetAddress)messages[0].getFrom()[0]).getAddress());
		assertEquals(2, messages[0].getAllRecipients().length);
		assertEquals("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4f3b207e0f2a372e223f232a612c2022">[email protected]</a>", ((InternetAddress)messages[0].getAllRecipients()[0]).getAddress());
		assertEquals("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="295d461b694c51484459454c074a4644">[email protected]</a>", ((InternetAddress)messages[0].getAllRecipients()[1]).getAddress());
		assertEquals(1, messages[0].getHeader("Content-Type").length);
		assertEquals("text/plain; charset=us-ascii", messages[0].getHeader("Content-Type")[0]);
		String foundBody = GreenMailUtil.getBody(messages[0]);
		assertEquals("foo", foundBody);
	}

	@AfterClass
	public static void afterClass() {
		ourTestSmtp.stop();
	}

	@BeforeClass
	public static void beforeClass() {
		ourPort = RandomServerPortProvider.findFreePort();
		ServerSetup smtp = new ServerSetup(ourPort, null, ServerSetup.PROTOCOL_SMTP);
		smtp.setServerStartupTimeout(2000);
		ourTestSmtp = new GreenMail(smtp);
		ourTestSmtp.start();
	}

}

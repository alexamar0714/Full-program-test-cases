package Email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SmtpAuthenticator extends Authenticator{
	public SmtpAuthenticator() {

	    super();
	}

	@Override
	public PasswordAuthentication getPasswordAuthentication() {
	 String username = "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="beddc48d8e8e8dcad7d3dbddccd7cdd7cdfed9d3dfd7d290ddd1d3">[email protected]</a>";
	 String password = "3003liuyang";
	    if ((username != null) && (username.length() > 0) && (password != null) 
	      && (password.length   () > 0)) {

	        return new PasswordAuthentication(username, password);
	    }

	    return null;
	}
}

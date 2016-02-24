package cn.lijiabei.summary.mail.javaMail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MailAuthenticator extends Authenticator {
	String userName = null;
	String password = null;

	public MailAuthenticator() {
		super();
	}

	public MailAuthenticator(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(userName, password);
	}

}

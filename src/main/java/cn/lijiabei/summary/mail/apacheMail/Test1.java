package cn.lijiabei.summary.mail.apacheMail;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

/**
 * @ClassName: Test1
 * @Description:普通文本的邮件
 */
public class Test1 {

	public static void main(String[] args) {
		try {
			// 简单文本邮件
			Email email = new SimpleEmail();
			email.setHostName("smtp.yeah.net");
			email.setSmtpPort(25);
			email.setAuthentication("ljbhs22@yeah.net", "ljb19901116");// 邮件服务器验证：用户名/密码
			email.setSSLOnConnect(true);
			email.setCharset("UTF-8");// 必须放在前面，否则乱码
			email.addTo("lijiabei@bizpartner.cn");
			email.setFrom("ljbhs22@yeah.net", "李");
			email.setSubject("subject中文");
			email.setMsg("msg中文");
			email.send();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

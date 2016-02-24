package cn.lijiabei.summary.mail.apacheMail;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;

/**
 * @ClassName: Test2
 * @Description: 待附件的邮件
 */
public class Test2 {

	public static void main(String[] args) {
		try {

			// 带附件的邮件
			MultiPartEmail email = new MultiPartEmail();
			email.setHostName("smtp.yeah.net");
			email.setSmtpPort(25);
			email.setAuthentication("ljbhs22@yeah.net", "ljb19901116");// 邮件服务器验证：用户名/密码
			email.setSSLOnConnect(true);
			email.setCharset("UTF-8");// 必须放在前面，否则乱码
			email.addTo("lijiabei@bizpartner.cn");
			email.setFrom("ljbhs22@yeah.net", "李");
			email.setSubject("subject中文");
			email.setMsg("msg中文");

			EmailAttachment attachment = new EmailAttachment();
			attachment.setPath("/Users/lijiabei/mywork/personal/homecoming/document/17473FFA-0B8C-4DBC-9FDD-62EA8BF63A35.png");// 本地文件
			// attachment.setURL(new URL("http://xxx/a.gif"));//远程文件
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			attachment.setDescription("附件test");
			attachment.setName("test");

			email.attach(attachment);
			email.send();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

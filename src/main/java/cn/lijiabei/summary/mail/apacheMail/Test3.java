package cn.lijiabei.summary.mail.apacheMail;

import java.io.File;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;

/**
 * @ClassName: Test3
 * @Description:html格式邮件（带附件）
 */
public class Test3 {

	public static void main(String[] args) {
		try {
			// HTML格式邮件

			HtmlEmail email = new HtmlEmail();
			email.setHostName("smtp.yeah.net");
			email.setSmtpPort(25);
			email.setAuthentication("ljbhs22@yeah.net", "ljb19901116");// 邮件服务器验证：用户名/密码
			email.setSSLOnConnect(true);
			email.setCharset("UTF-8");// 必须放在前面，否则乱码
			email.addTo("lijiabei@bizpartner.cn");
			email.setFrom("ljbhs22@yeah.net", "李");
			email.setSubject("subject中文");

			// 添加附件
			EmailAttachment attachment = new EmailAttachment();
			attachment.setPath("/Users/lijiabei/mywork/personal/homecoming/document/17473FFA-0B8C-4DBC-9FDD-62EA8BF63A35.png");// 本地文件
			// attachment.setURL(new URL("http://xxx/a.gif"));//远程文件
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			attachment.setDescription("test");
			attachment.setName("test.png");
			email.attach(attachment);

			// 内嵌图片
			String cid = email.embed(new File("/Users/lijiabei/mywork/personal/homecoming/document/17473FFA-0B8C-4DBC-9FDD-62EA8BF63A35.png"));
			email.setHtmlMsg("<b>msg中文</b><b>aaa<img src='cid:" + cid + "'>bbb</b>");

			email.send();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

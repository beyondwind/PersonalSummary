package cn.lijiabei.summary.mail.javaMail;

public class TestMail {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 这个类主要是设置邮件
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost("smtp.yeah.net");
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName("ljbhs22@yeah.net");
		mailInfo.setPassword("ljb1990/1116");// 您的邮箱密码
		mailInfo.setFromAddress("ljbhs22@yeah.net");
		mailInfo.setToAddress("lijiabei@bizpartner.cn");
		mailInfo.setSubject("来自网易的邮件");
		mailInfo.setContent("二年级的渣渣也敢吃辣条？！内容部分,这个是测试，请删除它把！");
		SimpleMailSender sms = new SimpleMailSender();
		sms.sendTextMail(mailInfo);
	}

}

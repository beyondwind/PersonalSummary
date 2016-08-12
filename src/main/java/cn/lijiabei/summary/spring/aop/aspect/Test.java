package cn.lijiabei.summary.spring.aop.aspect;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {

	private static ApplicationContext applicationContext;

	public static void main(String[] args) {
		applicationContext = new ClassPathXmlApplicationContext("classpath:spring-applicationcontext/aspect.xml");
		BizService bizService = (BizService) applicationContext.getBean("bizService");
		bizService.init();
	}

}

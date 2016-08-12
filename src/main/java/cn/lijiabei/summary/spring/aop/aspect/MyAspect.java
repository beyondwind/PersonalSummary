package cn.lijiabei.summary.spring.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;

public class MyAspect {

	public void before() {
		System.out.println("Aspect do before");
	}

	public void after() {
		System.out.println("Aspect do after");
	}

	public void afterReturning() {
		System.out.println("Aspect do afterReturning");
	}

	public void afterThrowing() {
		System.out.println("Aspect do afterThrowing");
	}

	// 可以抛出异常，也可以catch，看业务需要，抛出后会触发afterThrowing方法
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		Object o = null;
		System.out.println("Aspect do around 1");
		o = joinPoint.proceed();
		System.out.println("Aspect do around 2");
		return o;
	}
}

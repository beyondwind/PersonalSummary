package cn.lijiabei.summary.exception.runtimeException;

/**
 * @description syso->><br/>
 * ============输出内容=========<br/>
 * catch test1<br/>
 * finish test1<br/>
 * Exception in thread "main" cn.lijiabei.summary.exception.runtimeException.TestException: test2<br/>
 * <t/>at cn.lijiabei.summary.exception.runtimeException.Test.test(Test.java:18)<br/>
 * <t/>at cn.lijiabei.summary.exception.runtimeException.Test.main(Test.java:13)<br/>
 * ========================<br/>
 * @comment RuntimeException 能够被try...catch捕获，但是如果不加try...catch依然可以执行，出错直接交给了jvm处理，程序直接中断<br/>
 * 在平时的代码编写中避免使用润提莫exception，容易抛出未能捕获且会造成程序崩溃的异常
 */
public class Test {

	public static void main(String[] args) {
		try {
			test(1);
		} catch (Exception e) {
			System.out.println("catch test1");
		}
		System.out.println("finish test1");

		test(2);
		System.out.println("finish test2");
	}

	public static void test(int i) {
		throw new TestException("test" + i);
	}

}

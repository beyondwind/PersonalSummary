package cn.lijiabei.summary.exception.oom;

/**
 * @Description: 方法区（Method Area）与Java堆一样，是各个线程共享的内存区域，<br/>
 * 它用于存储已被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据。
 */
public class MethodAreaOutOfMemory {

	public static void main(String[] args) {
		// 大量动态生成的类，比如cglib的类的代理，大量JSP或动态产生JSP文件的应用

	}

}

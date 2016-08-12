package cn.lijiabei.summary.exception.oom;

/**
 * @Description: 栈内存溢出
 * @comment 一般与方法递归次数过多或者方法中有产生大量数据的循环有关<br/>
 * 是否有递归调用<br/>
 * 是否有大量循环或死循环<br/>
 * 全局变量是否过多<br/>
 */
public class StackOutOfMemory {

	public static void main(String[] args) {
		new StackOutOfMemory().recursion();
	}

	public void recursion() {
		long time = System.currentTimeMillis();
		recursion();
	}
}

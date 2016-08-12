package cn.lijiabei.summary.exception.oom;

/**
 * @Description:虚拟机必须为每个被装载的类型维护一个常量池。<br/> 常量池就是该类型所用常量的一个有序集合，<br/>
 * 包括直接常量（String ,integer和floating point常量）和对其他类型、字段和方法的符号引用。<br/>
 * 池中的数据项就像数组一样是通过索引访问的。因为常量池存储了相应类型所用到的所有类型、字段和方法的符号引用
 */
public class ConstantPoolOutOfMemory {

	public static void main(String[] args) {
		// 项目启动方法区内存很小或者项目中的静态变量极其多时才会发生

	}

}

package cn.lijiabei.summary.exception.oom;

public class YoungOutOfMemory {

	public static void main(String[] args) {
		// 设置XX：MaxTenuringThreshold为一个很大的值
		// 使对象无法及时的移动到年老代中，导致年轻代内存溢出
		// TODO 讲道理说，应该不会呀，如果没达到XX：MaxTenuringThreshold，放不下就会丢向老年代了?
	}

}

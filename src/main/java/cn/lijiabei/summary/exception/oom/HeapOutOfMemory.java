package cn.lijiabei.summary.exception.oom;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 对内存溢出
 * @comment 堆溢出 情况多见于对象过多，存在多余引用，使对象未及时释放<br/>
 * 对象实例以及数组存放在堆内存，无限向堆内存丢入未释放的对象，入下段程序，最后假死了。。。陷入无限等待（FULL GC）<br/>
 * <br/>
 * 2016-07-28T23:00:53.970-0800: 153.159: [Full GC [PSYoungGen: 348513K->348513K(417280K)] [ParOldGen: 1397750K->1397750K(1397760K)] 1746264K->1746264K(1815040K) [PSPermGen:
 * 2599K->2599K(21504K)], 4.5210850 secs] [Times: user=14.52 sys=0.19, real=4.52 secs]<br/>
 * 2016-07-28T23:00:58.491-0800: 157.680: [Full GC [PSYoungGen: 348513K->348513K(417280K)] [ParOldGen: 1397752K->1397752K(1397760K)] 1746266K->1746266K(1815040K) [PSPermGen:
 * 2600K->2600K(21504K)], 4.7507640 secs] [Times: user=14.99 sys=0.22, real=4.75 secs] <br/>
 * 2016-07-28T23:01:03.243-0800: 162.432: [Full GC [PSYoungGen: 348513K->348513K(417280K)] [ParOldGen: 1397754K->1397754K(1397760K)] 1746267K->1746267K(1815040K) [PSPermGen:
 * 2601K->2601K(21504K)], 4.5099030 secs] [Times: user=14.95 sys=0.20, real=4.51 secs] <br/>
 * 2016-07-28T23:01:07.753-0800: 166.942: [Full GC [PSYoungGen: 348513K->348513K(417280K)] [ParOldGen: 1397756K->1397756K(1397760K)] 1746269K->1746269K(1815040K) [PSPermGen:
 * 2602K->2602K(21504K)], 4.2797390 secs] [Times: user=14.88 sys=0.15, real=4.28 secs] <br/>
 * 2016-07-28T23:01:12.033-0800: 171.222: [Full GC [PSYoungGen: 348513K->348513K(417280K)] [ParOldGen: 1397758K->1397758K(1397760K)] 1746271K->1746271K(1815040K) [PSPermGen:
 * 2604K->2604K(21504K)], 4.4681220 secs] [Times: user=14.74 sys=0.19, real=4.47 secs] <br/>
 * 2016-07-28T23:01:16.501-0800: 175.690: [Full GC [PSYoungGen: 348513K->348513K(417280K)] [ParOldGen: 1397759K->1397759K(1397760K)] 1746273K->1746273K(1815040K) [PSPermGen:
 * 2604K->2604K(21504K)], 4.3426320 secs] [Times: user=14.80 sys=0.17, real=4.34 secs] <br/>
 */
public class HeapOutOfMemory {

	public static void main(String[] args) {
		List<String> strArray = new ArrayList<String>(10000_0000);
		for (int i = 0; i < 10000_0000; i++) {
			strArray.add(String.valueOf(i));
			if (i % 10000 == 0) {
				System.out.println(i);
			}
		}
	}

}

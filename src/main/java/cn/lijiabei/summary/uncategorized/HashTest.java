package cn.lijiabei.summary.uncategorized;

public class HashTest {

	public static void main(String[] args) {
		Integer i1 = new Integer(1);
		Integer i2 = new Integer(2);
		Integer i200 = new Integer(200);

		Long l1 = new Long(1L);
		Long l2 = new Long(1);
		Long l200 = new Long(200);
		
		Double d1=new Double(1.0);
		System.out.println("Integer i1=new Integer(1);  -->" + i1.hashCode());
		System.out.println("Integer i2=new Integer(2);  -->" + i2.hashCode());
		System.out.println("Integer i200 = new Integer(200);  -->" + i200.hashCode());
		System.out.println("Long l1=new Long(1L);  -->" + l1.hashCode());
		System.out.println("Long l2=new Long(1);  -->" + l2.hashCode());
		System.out.println("Long l200 = new Long(200);  -->" + l200.hashCode());
		System.out.println("Double d1=new Double(1.0);  -->" + d1.hashCode());
	}

}

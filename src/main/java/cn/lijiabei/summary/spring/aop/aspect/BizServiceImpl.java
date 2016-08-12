package cn.lijiabei.summary.spring.aop.aspect;

public class BizServiceImpl implements BizService {

	@Override
	public void init() {
		System.out.println("BizService do init");
	}

}

package cn.lijiabei.summary.exception.runtimeException;

public class TestException extends RuntimeException {

	private static final long serialVersionUID = -8159777705669129156L;

	public TestException(){
	}

	public TestException(String s){
		super(s);
	}
}
package cn.lijiabei.summary.zookeeper.taskcenter.core;

public class LockException extends Exception {

	private static final long serialVersionUID = -446097755854954015L;

	public LockException(){

	}

	public LockException(String code, String message, Throwable cause){
		super(message, cause);
	}

	public LockException(String code, String message){
		super(message);
	}

	public LockException(Throwable cause){
		super(cause);
	}
}

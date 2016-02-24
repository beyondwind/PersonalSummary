package cn.lijiabei.summary.zookeeper.official.tutorial;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class SyncPrimitive implements Watcher {

	static ZooKeeper zk = null;
	static Integer mutex = null;

	String root;

	SyncPrimitive(String address){
		if (null == zk) {
			try {
				System.out.println("Starting zk:");
				zk = new ZooKeeper(address, 3000, this);
				mutex = new Integer(-1);
				System.out.println("Finished starting zk:" + zk);
			} catch (Exception e) {
				System.out.println(e.toString());
				zk = null;
			}
		}
	}

	@Override
	public void process(WatchedEvent event) {
		synchronized (mutex) {
			mutex.notify();
		}
	}

	
}

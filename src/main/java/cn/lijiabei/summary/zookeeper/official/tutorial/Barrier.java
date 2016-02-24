package cn.lijiabei.summary.zookeeper.official.tutorial;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

public class Barrier extends SyncPrimitive {

	String name = null;
	int size = 0;

	Barrier(String address, String root, int size){
		super(address);
		this.root = root;
		this.size = size;

		if (null != zk) {
			try {
				Stat s = zk.exists(root, false);
				if (null == s) {
					zk.create(root, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				}
			} catch (KeeperException e) {
				System.out.println("Keeper exception when instantiating queue: " + e.toString());
			} catch (InterruptedException e) {
				System.out.println("Interrupted exception");
			}
		}

		Long time = new Date().getTime();
		name = new String(time.toString());
	}

	boolean enter() throws KeeperException, InterruptedException {
		String path = zk.create(root + "/" + name, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		System.out.println("create path:" + path);
		while (true) {
			synchronized (mutex) {
				List<String> list = zk.getChildren(root, true);

				if (list.size() < size) {
					mutex.wait();
				} else {
					System.out.println(name + "<enter>");
					return true;
				}
			}
		}
	}

	boolean leave() throws KeeperException, InterruptedException {
		zk.delete(root + "/" + name, 0);
		while (true) {
			synchronized (mutex) {
				List<String> list = zk.getChildren(root, true);

				if (list.size() < size) {
					mutex.wait();
				} else {
					System.out.println(name + "<leave>");
					return true;
				}
			}
		}
	}

	public static void main(String[] args) {
		Barrier b = new Barrier("127.0.0.1:2181", "/barrier", 1);
		try {
			boolean flag = b.enter();
			if (!flag) System.out.println("Error when entering the barrier");
		} catch (KeeperException e) {

		} catch (InterruptedException e) {

		}

		// Generate random integer
		Random rand = new Random();
		int r = rand.nextInt(100);
		// Loop for rand iterations
		for (int i = 0; i < r; i++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {

			}
		}
		try {
			b.leave();
		} catch (KeeperException e) {

		} catch (InterruptedException e) {

		}
		System.out.println("Left barrier");
	}
}

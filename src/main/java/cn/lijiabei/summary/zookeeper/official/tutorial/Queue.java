package cn.lijiabei.summary.zookeeper.official.tutorial;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

public class Queue extends SyncPrimitive {

	Queue(String address, String root){
		super(address);
		this.root = root;

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

	}

	boolean produce(int i) throws KeeperException, InterruptedException {
		ByteBuffer b = ByteBuffer.allocate(4);
		byte[] value;

		b.putInt(i);
		value = b.array();

		String path = zk.create(root + "/element", value, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
		System.out.println("create path:" + path);

		return true;
	}

	int consume() throws KeeperException, InterruptedException {
		int retvalue = -1;
		Stat stat = null;

		while (true) {
			synchronized (mutex) {
				List<String> list = zk.getChildren(root, true);

				if (list.size() == 0) {
					System.out.println("going to wait");
					mutex.wait();
				} else {
					// 找出最先进队列的
					String[] elements = list.toArray(new String[list.size()]);
					Arrays.sort(elements);

					System.out.println("Temporary value: " + root + "/" + elements[0]);

					byte[] b = zk.getData(root + "/" + elements[0], false, stat);
					zk.delete(root + "/" + elements[0], 0);

					ByteBuffer byteBuffer = ByteBuffer.wrap(b);
					retvalue = byteBuffer.getInt();

					return retvalue;
				}
			}
		}
	}

	public static void main(String[] args) {
		Queue q = new Queue("", "/app1");

		int i;
		Integer max = new Integer(10);

		System.out.println("Producer");
		for (i = 0; i < max; i++) {
			try {
				q.produce(10 + i);
			} catch (KeeperException e) {

			} catch (InterruptedException e) {

			}
		}
		System.out.println("Producer end");

		System.out.println("Consumer begin");
		for (i = 0; i < max; i++) {
			try {
				int r = q.consume();
				System.out.println("Item: " + r);
			} catch (KeeperException e) {
				i--;
			} catch (InterruptedException e) {

			}
		}

		System.out.println("Consumer end");
	}
}

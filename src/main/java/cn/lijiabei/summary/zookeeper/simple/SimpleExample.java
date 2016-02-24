package cn.lijiabei.summary.zookeeper.simple;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class SimpleExample {

	public static void main(String[] args) {
		try {
			// 创建一个与服务器的连接
			ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 2000, new Watcher() {

				// 监控所有被触发的事件
				public void process(WatchedEvent event) {
					System.out.println("已经触发了" + event.getType() + "事件！");
				}
			});

			// 创建一个目录节点,data参数最大1 MB (1,048,576 bytes),Arrays larger than this will cause a KeeperExecption
			zk.create("/testRootPath", "testDataContent".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

			// 创建一个子目录节点
			zk.create("/testRootPath/testChildPath1", "testChildContent".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

			// 取出/testRootPath目录上的数据，结果为testDataContent
			System.out.println(new String(zk.getData("/testRootPath", false, null)));

			// 取出目录下的子目录节点列表,getChildren的第二个参数，如果设置为true，会监听NodeChildrenChanged事件
			System.out.println(zk.getChildren("/testRootPath", true));

			// 修改子目录节点数据
			zk.setData("/testRootPath/testChildPath1", "testChildContent1".getBytes(), -1);

			System.out.println("目录节点状态:[" + zk.exists("/testRootPath", true) + "]");

			// 创建第二个子目录节点
			zk.create("/testRootPath/testChildPath2", "testChildContent2".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			System.out.println(new String(zk.getData("/testRootPath/testChildPath2", true, null)));

			// 删除子节点
			zk.delete("/testRootPath/testChildPath2", -1);
			zk.delete("/testRootPath/testChildPath1", -1);

			// 删除root节点
			zk.delete("/testRootPath", -1);

			zk.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

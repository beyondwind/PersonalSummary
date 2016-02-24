package cn.lijiabei.summary.zookeeper.taskcenter.core;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.lijiabei.summary.zookeeper.taskcenter.TaskKeeper;
import cn.lijiabei.summary.zookeeper.taskcenter.TaskProxy;

public class ZkTaskKeeper implements TaskKeeper {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	// zookeeper集群配置
	private String zkServers;// = "192.168.1.22:2181,192.168.1.23:2181";// zookeeper集群默认hosts
	private Integer zkTimeOut;// = 3000;// ms, 默认连接超时时间

	private TaskProxy taskProxy;// 业务逻辑处理接口，代理业务具体实现

	private ZkClient zkClient = null;// zookeeper 客户端

	private Thread popThread = null;// 任务分发线程

	private String serverIp = null;// 当前服务器作为客户端的自身IP

	private String SERVER_DISTRIBUTED_PATH = null;// 当前服务器已分配任务父节点

	private Lock lock = null;// 本服务器多线程锁，防止同一台服务器多次获得锁

	private String currentLock;// 当前获得的分布式锁

	private String waitLock;// 等待的分布式锁

	private boolean run = true;// 任务分发线程运行状态

	public ZkTaskKeeper(){
		lock = new ReentrantLock();
	}

	public void init() {
		try {
			if (StringUtils.isBlank(zkServers)) {
				log.warn("can not find config of zookeeper, ignore.");
				return;
			}
			if (zkClient == null) {
				zkClient = new ZkClient(zkServers, zkTimeOut);
			}

			// 获取自身的外网ip作为节点名称
			serverIp = IpUtil.getRealIp();
			SERVER_DISTRIBUTED_PATH = ZkConstants.DISTRIBUTED + ZkConstants.PATH_SPLIT + serverIp;// root/distributed/serverIp

			initNode();// 初始化各个znode节点（包含服务器自己用的和公用的）
			lastDistributed();// 把之前未完成的任务完成
			initEventsHandle();// 注册时间监听器
			initPopThread();// 开始任务派发线程
			log.info("zkClient init success, zkServers:{}, server_ip:{}, server task distribute path:{}", zkServers, serverIp, SERVER_DISTRIBUTED_PATH);
		} catch (UnknownHostException e) {
			log.error("zkClient init UnknownHostException", e);
		} catch (Exception e) {
			log.error("zkClient init Exception", e);
		}
	}

	private void initNode() throws Exception {
		// 如果多个服务器同时创建节点，异常tryCatch，节点还是创建成功的，可以正常走下去
		try {
			// 创建任务中心持久化节点
			if (!zkClient.exists(ZkConstants.COMMON)) {
				zkClient.createPersistent(ZkConstants.COMMON, true);
				log.info("node {} create success", ZkConstants.COMMON);
			}
		} catch (Exception e) {
			log.info("node {} exist", ZkConstants.COMMON);
		}

		try {
			// 创建服务器列表持久化父节点
			if (!zkClient.exists(ZkConstants.SERVERS)) {
				zkClient.createPersistent(ZkConstants.SERVERS, true);
				log.info("node {} create success", ZkConstants.SERVERS);
			}
		} catch (Exception e) {
			log.info("node {} exist", ZkConstants.SERVERS);
		}

		try {
			// 创建任务分配中心持久化节点
			if (!zkClient.exists(ZkConstants.DISTRIBUTED)) {
				zkClient.createPersistent(ZkConstants.DISTRIBUTED, true);
				log.info("node {} create success", ZkConstants.DISTRIBUTED);
			}
		} catch (Exception e) {
			log.info("node {} exist", ZkConstants.DISTRIBUTED);
		}

		try {
			// 创建分布式锁持久化父节点
			if (!zkClient.exists(ZkConstants.LOCK_PATH)) {
				zkClient.createPersistent(ZkConstants.LOCK_PATH, true);
				log.info("node {} create success", ZkConstants.LOCK_PATH);
			}
		} catch (Exception e) {
			log.info("node {} exist", ZkConstants.LOCK_PATH);
		}

		try {
			// 创建各服务器各自的任务分配节点和服务注册节点
			if (!zkClient.exists(SERVER_DISTRIBUTED_PATH)) {
				zkClient.createPersistent(SERVER_DISTRIBUTED_PATH);
				log.info("node " + SERVER_DISTRIBUTED_PATH + " create success");
			}
		} catch (Exception e) {
			log.info("node {} exist", SERVER_DISTRIBUTED_PATH);
		}

		try {
			// 防止延时造成的旧连接节点还未消失，此处将旧节点删除重新创建
			if (zkClient.exists(ZkConstants.SERVERS + ZkConstants.PATH_SPLIT + serverIp)) {
				zkClient.delete(ZkConstants.SERVERS + ZkConstants.PATH_SPLIT + serverIp);
			}
			zkClient.createEphemeral(ZkConstants.SERVERS + ZkConstants.PATH_SPLIT + serverIp);
			log.info("node {} create success", ZkConstants.SERVERS + ZkConstants.PATH_SPLIT + serverIp);
		} catch (Exception e) {
			log.info("node {} exist", ZkConstants.SERVERS + ZkConstants.PATH_SPLIT + serverIp);
		}

		log.info("all node init success");
	}

	private void lastDistributed() {
		try {
			// 加锁，防止本地多线程同时操作
			lock.lock();
			List<String> currentChilds = null;
			if (zkClient.exists(SERVER_DISTRIBUTED_PATH)) {
				currentChilds = zkClient.getChildren(SERVER_DISTRIBUTED_PATH);
			}
			if (CollectionUtils.isNotEmpty(currentChilds)) {
				log.info("{}(fullPath:{}) handle tasks : {}", serverIp, SERVER_DISTRIBUTED_PATH, currentChilds);
				for (String child : currentChilds) {
					try {
						// 由taskProxy代理任务的实际业务逻辑
						taskProxy.dealWithTask(child);
						zkClient.delete(SERVER_DISTRIBUTED_PATH + ZkConstants.PATH_SPLIT + child);
						log.info("task(node:{}) in server:{} completed!", child, serverIp);
					} catch (Exception e) {
						log.warn("dealWithTask Exception", e);
						log.warn("dealWithTask Exception; node:{}", child);
					}
				}
			}
		} catch (Exception e) {
			log.warn("reBackDistributed Exception", e);
		} finally {
			lock.unlock();
		}
	}

	private void initEventsHandle() {
		// 注册状态变化事件监听
		zkClient.subscribeStateChanges(new IZkStateListener() {

			@Override
			public void handleStateChanged(KeeperState state) throws Exception {
				if (state == KeeperState.Disconnected || state == KeeperState.Expired) {
					log.warn("zkClient : {} disconnected, waiting for reconnecting", serverIp);
					zkClient.unsubscribeAll();// 解除所有事件监听,重连会重新初始化事件监听
					run = false;// 连接断开后，停止任务分发线程
				}
			}

			@Override
			public void handleNewSession() throws Exception {
				log.info("client:{} reconnected, it's about to init", serverIp);
				init();
			}

			@Override
			public void handleSessionEstablishmentError(Throwable error) throws Exception {
				log.info("client:{} EstablishmentError", serverIp);
			}
		});

		// 注册Server节点事件监听，以处理服务器挂掉之后得任务重分配
		zkClient.subscribeChildChanges(ZkConstants.SERVERS, new IZkChildListener() {

			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				try {
					lock();// 分布式上锁
					List<String> serverList = zkClient.getChildren(ZkConstants.DISTRIBUTED);
					// 作差集，找出挂掉的服务器
					if (CollectionUtils.isNotEmpty(serverList) && serverList.removeAll(currentChilds) && !serverList.isEmpty()) {
						// 某几台服务器挂了
						log.warn("server:{} disconnected, maybe it's terminaled!", serverList);
						for (String server : serverList) {
							String serverPath = ZkConstants.DISTRIBUTED + ZkConstants.PATH_SPLIT + server;
							List<String> children = zkClient.getChildren(serverPath);
							if (CollectionUtils.isNotEmpty(children)) {
								for (String node : children) {
									try {
										String[] split = node.split(ZkConstants.NODE_SPLIT);
										// 将任务放回common
										boolean result = push(Integer.valueOf(split[0]), Long.valueOf(split[1]));
										if (result) {
											// 成功放回common之后，删除已分配节点下的任务
											result = zkClient.delete(serverPath + ZkConstants.PATH_SPLIT + node);
											if (result) {
												log.info("put task(node:{}) from server:{} back to common:{} success!", node, ZkConstants.DISTRIBUTED + ZkConstants.PATH_SPLIT
														+ server, ZkConstants.COMMON);
											} else {
												log.warn("delete task(node:{}) of server:{} distributed failed!", node, serverPath);
											}
										} else {
											log.warn("put task(node:{}) from server:{} to common:{} failed!", node, serverPath, ZkConstants.COMMON);
										}
									} catch (Exception e) {
										log.error("put task(node:" + node + ") from server:" + serverPath + " to common:" + ZkConstants.COMMON + " failed!", e);
									}
								}
							}
						}
					}
				} catch (LockException e) {
					log.error("handleChildChange SERVERS : " + ZkConstants.SERVERS + " lock Exception", e);
				} finally {
					unlock();// 分布式锁，解锁
				}
			}
		});

		// XXX 确认最后分配的任务能否取到
		// 注册自身Server子节点事件监听，以处理分配过来得任务
		zkClient.subscribeChildChanges(SERVER_DISTRIBUTED_PATH, new IZkChildListener() {

			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				try {
					// 加锁，防止本地多线程同时操作
					lock.lock();
					if (CollectionUtils.isEmpty(currentChilds)) {
						return;
					}

					log.info("{}(fullPath:{}) handle tasks : {}", serverIp, parentPath, currentChilds);
					for (String child : currentChilds) {
						try {
							// 由taskProxy代理任务的实际业务逻辑
							taskProxy.dealWithTask(child);
							zkClient.delete(parentPath + ZkConstants.PATH_SPLIT + child);
							log.info("task(node:{}) in server:{} completed!", child, serverIp);
						} catch (Exception e) {
							log.info("task(node:" + child + ") in server:" + serverIp + " completed!", e);
						}
					}
				} catch (Exception e) {
					log.error("handleChildChange SERVER_PATH : " + SERVER_DISTRIBUTED_PATH + " Exception", e);
				} finally {
					lock.unlock();
				}
			}
		});

		log.info("all eventHandles subscribed success");
	}

	/**
	 * @Title: initPopThread
	 * @Description: 初始化任务分配线程；断线重连的情况下，先停止旧线程，再新起线程
	 * @return void 返回类型
	 */
	private void initPopThread() {
		popThread = new Thread(new Runnable() {

			@Override
			public void run() {
				long delay = ZkConstants.DEFAILT_DELAY_MILLIS;
				run = true;// 重启线程
				while (run) {
					try {
						boolean result = pop();
						// 如果pop为false，让线程等待时间加倍；否则，将线程等待时间恢复默认等待时间
						if (!result) {
							if (delay * 2 <= ZkConstants.MAX_DELAY_MILLIS) {
								delay *= 2;
							}
						} else {
							delay = ZkConstants.DEFAILT_DELAY_MILLIS;
						}
						log.info("Thread:{} waiting for poping, sleep {} ms", Thread.currentThread().getId(), delay);
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						log.error("pop thread interrupted", e);
					}
				}
				log.info("pop thread:{} stop running, isRun:{}", Thread.currentThread().getId(), run);
			}
		});
		popThread.start();
		log.info("popThread init success");
	}

	@Override
	public List<Long> push(int taskType, List<Long> taskIds) {
		List<Long> list = new ArrayList<Long>();
		log.debug("taskIdList size : {}", taskIds.size());
		for (Long taskId : taskIds) {
			boolean result = false;
			if (!existInDistrbuted(taskType + ZkConstants.NODE_SPLIT + taskId)) {// 校验任务是否已经在已分配任务的队列中
				result = push(taskType, taskId);
				if (!result) {
					list.add(taskId);
				}
			} else {
				log.error("push failed, task {}(type:{}) already exists in distributed", taskId, taskType);
				list.add(taskId);
			}
		}

		log.debug("failedList size : {}", list.size());
		return list;
	}

	@Override
	public boolean push(int taskType, long taskId) {
		try {
			String path = ZkConstants.COMMON + ZkConstants.PATH_SPLIT + taskType + ZkConstants.NODE_SPLIT + taskId;
			// 防止初始化任务时，多个服务器插入同样的节点
			if (!zkClient.exists(path)) {
				zkClient.createPersistent(path);
				log.info("push success, task {}(type:{}) has been push into taskcenter", taskId, taskType);
				return true;
			} else {
				log.info("push failed, task {}(type:{}) already exists in taskcenter", taskId, taskType);
			}
		} catch (Exception e) {
			log.error("push Exception", e);
			log.warn("push Exception; taskType:{}, taskId:{}", taskType, taskId);
		}

		return false;
	}

	/**
	 * @Title: existInDistrbuted
	 * @Description: 初始化时需校验一个任务是否已经存在于待分配和已分配队列中
	 * @param node 节点名称
	 * @return 设定文件 boolean 返回类型
	 */
	private boolean existInDistrbuted(String node) {
		try {
			List<String> distributePathList = zkClient.getChildren(ZkConstants.DISTRIBUTED);
			for (String path : distributePathList) {
				if (zkClient.exists(ZkConstants.DISTRIBUTED + ZkConstants.PATH_SPLIT + path + ZkConstants.PATH_SPLIT + node)) {
					return true;
				}
			}
		} catch (Exception e) {
			log.error("existInDistrbuted error, zkClient not init", e);
		}

		return false;
	}

	/**
	 * @Title: pop
	 * @Description: 任务分发
	 * @return 设定文件 boolean 返回类型，true说明有待分发任务；false说明没有待分发任务
	 */
	private boolean pop() {
		if (!zkClient.exists(ZkConstants.COMMON)) {
			return false;
		}

		// 获取common下的所有节点任务，如果为空，不加锁直接返回；不为空才加锁并进行任务分发
		List<String> childList = zkClient.getChildren(ZkConstants.COMMON);
		if (CollectionUtils.isEmpty(childList)) {
			log.info("there is no task in common:{}", ZkConstants.COMMON);
			return false;
		}

		List<String> servers = getServers();
		if (CollectionUtils.isEmpty(servers)) {
			log.info("there is no server connected!");
			return false;
		}

		try {
			// 加锁，防止多台server同时操作
			lock();
			childList = zkClient.getChildren(ZkConstants.COMMON);// 加锁之后重新获取子节点列表，保证没有脏数据
			for (String node : childList) {
				if (!zkClient.exists(ZkConstants.COMMON + ZkConstants.PATH_SPLIT + node)) {
					// 不存在说明已被其他线程分发，此处忽略，并继续分发下一个任务(这种场景应该不存在)
					log.warn("task {} has been distributed to other server, ignore", node);
					continue;
				}
				String[] data = node.split(ZkConstants.NODE_SPLIT);
				servers = getServers();
				// 取模，取绝对值防止出现负数导致数组越界
				int id = Math.abs(Integer.valueOf(data[1])) % servers.size();
				String serverIp = servers.get(id);
				String serverDistributePath = ZkConstants.DISTRIBUTED + ZkConstants.PATH_SPLIT + serverIp;
				// 将任务分配到各个服务器
				if (!zkClient.exists(serverDistributePath + ZkConstants.PATH_SPLIT + node)) {
					zkClient.createPersistent(serverDistributePath + ZkConstants.PATH_SPLIT + node);
					log.info("task {} has been distributed to the {}th server:{}", node, id, serverIp);
				} else {
					log.warn("task {} exists on server {}, don't need to add", node, serverIp);
				}
				// 删除待分配状态的节点
				zkClient.delete(ZkConstants.COMMON + ZkConstants.PATH_SPLIT + node);
			}
		} catch (LockException e) {
			log.error("pop lock Exception", e);
		} catch (Exception e) {
			log.error("pop Exception", e);
		} finally {
			// 解除锁
			unlock();
		}

		return true;
	}

	public void lock() throws LockException {
		if (lock == null) {
			lock = new ReentrantLock();
		}
		lock.lock();
		if (tryLock()) {
			return;
		}
		waitForLock(waitLock);// 等待锁
	}

	private void unlock() {
		if (currentLock == null) {
			// currentLock为空，不需要释放分布式锁
			lock.unlock();
			return;
		}

		try {
			zkClient.delete(ZkConstants.LOCK_PATH + ZkConstants.PATH_SPLIT + currentLock);
			log.info("Thread {} lock : {} released", Thread.currentThread().getId(), currentLock);
		} catch (Exception e) {
			log.error("unlock Exception", e);
		} finally {
			currentLock = null;
			lock.unlock();
		}
	}

	/**
	 * @Title: tryLock
	 * @Description: 尝试加锁
	 * @return 设定文件 boolean 返回类型
	 */
	private boolean tryLock() throws LockException {
		try {
			String currentLockNode = zkClient.createEphemeralSequential(ZkConstants.LOCK_PATH + ZkConstants.PATH_SPLIT, new byte[0]);
			currentLock = currentLockNode.substring(currentLockNode.lastIndexOf(ZkConstants.PATH_SPLIT) + 1);
			List<String> locks = zkClient.getChildren(ZkConstants.LOCK_PATH);
			Collections.sort(locks);
			if (currentLock.equals(locks.get(0))) {
				// 如果是最小的节点,则表示取得锁
				log.info("tryLock: Thread {} get lock : {}", Thread.currentThread().getId(), currentLock);
				return true;
			}
			// 如果不是最小的节点，找到比自己小1的节点
			waitLock = locks.get(Collections.binarySearch(locks, currentLock) - 1);
		} catch (Exception e) {
			log.warn("tryLock Exception", e);
			throw new LockException(e);
		}

		return false;
	}

	private boolean waitForLock(final String waitNode) throws LockException {
		try {
			boolean exists = zkClient.exists(ZkConstants.LOCK_PATH + ZkConstants.PATH_SPLIT + waitNode);
			// 判断比自己小一个数的节点是否存在,如果不存在则无需等待锁,存在则注册监听
			if (exists) {
				final CountDownLatch latch = new CountDownLatch(1);
				zkClient.subscribeChildChanges(ZkConstants.LOCK_PATH + ZkConstants.PATH_SPLIT + waitNode, new IZkChildListener() {

					@Override
					public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
						latch.countDown();
					}
				});

				log.info("waitForLock: Thread {} waiting for lock : {}", Thread.currentThread().getId(), waitNode);
				while (true) {
					latch.await(ZkConstants.DEFAULT_WAIT_TIME, TimeUnit.MILLISECONDS);
					exists = zkClient.exists(ZkConstants.LOCK_PATH + ZkConstants.PATH_SPLIT + waitNode);
					if (!exists) {
						log.info("waitForLock: waiting lock : {} released, Thread {} get lock : {}", waitNode, Thread.currentThread().getId(), currentLock);
						waitLock = null;// 清空等待锁
						break;
					}
				}
			}
			return true;
		} catch (Exception e) {
			log.warn("waitForLock Exception", e);
			throw new LockException(e);
		}
	}

	private List<String> getServers() {
		List<String> children = zkClient.getChildren(ZkConstants.SERVERS);
		List<String> servers = new ArrayList<String>();
		for (String child : children) {
			if (!child.startsWith("192.168") || child.equals("127.0.0.1")) {
				servers.add(child);
			}
		}
		return servers;
	}

	public void setTaskProxy(TaskProxy taskProxy) {
		this.taskProxy = taskProxy;
	}

	public void setZkServers(String zkServers) {
		this.zkServers = zkServers;
	}

	public void setZkTimeOut(Integer zkTimeOut) {
		this.zkTimeOut = zkTimeOut;
	}
}

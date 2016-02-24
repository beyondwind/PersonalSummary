package cn.lijiabei.summary.zookeeper.taskcenter.core;

public class ZkConstants {

	public static final String NODE_SPLIT = "_";// 任务节点名称中的分隔符
	public static final String PATH_SPLIT = "/";// 节点与子节点间的分隔符
	public static final long DEFAULT_WAIT_TIME = 3000;// 锁，等待时长

	// 任务分发线程等待时间
	public static long DEFAILT_DELAY_MILLIS = 500;// ms, default delay that Thread sleep
	public static long MAX_DELAY_MILLIS = 12800;// ms, max delay that Thread sleep

	// 节点配置
	public static String ROOT = "/taskcenter/root";// 根目录节点
	public static String COMMON = ROOT + ZkConstants.PATH_SPLIT + "common";// 待分配任务父节点
	public static String DISTRIBUTED = ROOT + ZkConstants.PATH_SPLIT + "distributed";// 已分配任务父节点,下面再挂载服务器标识作为子节点
	public static String SERVERS = ROOT + ZkConstants.PATH_SPLIT + "servers";// 所有可用服务器列表父节点
	public static String LOCK_PATH = ROOT + ZkConstants.PATH_SPLIT + "locks";// 分布式锁父节点
}

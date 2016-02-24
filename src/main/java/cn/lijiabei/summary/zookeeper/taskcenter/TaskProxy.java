package cn.lijiabei.summary.zookeeper.taskcenter;

public interface TaskProxy {

	/**
	 * @Title: dealWithTask
	 * @Description:根据node值来决定ZkCommonTask的调用
	 * @param nodeName taskType + taskId,以ZkConstants.NODE_SPLIT做分隔
	 */
	void dealWithTask(String nodeName);

}

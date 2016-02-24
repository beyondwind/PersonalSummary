package cn.lijiabei.summary.zookeeper.taskcenter;

import java.util.List;

public interface TaskKeeper {

	/**
	 * @Title: push
	 * @Description: 添加任务到任务中心
	 * @param taskType 任务类型，int，值由使用方自定义
	 * @param taskId 任务id
	 * @return 设定文件 boolean 返回类型，添加成功或失败
	 */
	public boolean push(int taskType, long taskId);

	/**
	 * @Title: push
	 * @Description: 批量添加任务到任务中心，服务器启动时初始化使用，返回添加失败或已存在于任务中心的任务id
	 * @param taskType 任务类型，int，值由使用方自定义
	 * @param taskIds 任务id列表
	 * @return 设定文件 List<Long> 返回类型，返回添加失败的任务
	 */
	public List<Long> push(int taskType, List<Long> taskIds);
}

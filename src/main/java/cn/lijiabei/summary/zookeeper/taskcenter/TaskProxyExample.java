package cn.lijiabei.summary.zookeeper.taskcenter;

public class TaskProxyExample implements TaskProxy {

	public static final int TASK_TYPE_ORDER = 1;
	public static final int TASK_TYPE_ITEM = 2;

	private CommonTask orderService;
	private CommonTask itemService;

	@Override
	public void dealWithTask(String nodeName) {
		String[] data = nodeName.split("_");
		Integer type = Integer.valueOf(data[0]);
		switch (type) {
			case TASK_TYPE_ORDER:
				orderService.dealCommonTask(Long.valueOf(data[1]));
			case TASK_TYPE_ITEM:
				itemService.dealCommonTask(Long.valueOf(data[1]));
			default:
				break;
		}
	}

	public void setOrderService(CommonTask orderService) {
		this.orderService = orderService;
	}

	public void setItemService(CommonTask itemService) {
		this.itemService = itemService;
	}

}

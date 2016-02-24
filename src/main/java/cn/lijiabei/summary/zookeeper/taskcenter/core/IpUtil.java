package cn.lijiabei.summary.zookeeper.taskcenter.core;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IpUtil {

	/**
	 * @Title: getRealIp
	 * @Description:获取ip，如果有外网ip，先返回外网ip，否则返回本地ip
	 * @return String 返回类型
	 */
	public static String getRealIp() throws SocketException {
		String localIp = null;// 本地IP，如果没有配置外网IP则返回它
		String netIp = null;// 外网IP
		boolean hasNetIp = false;
		Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();

		while (netInterfaces.hasMoreElements() && !hasNetIp) {
			NetworkInterface ni = netInterfaces.nextElement();
			Enumeration<InetAddress> address = ni.getInetAddresses();
			InetAddress ip = null;
			while (address.hasMoreElements()) {
				ip = address.nextElement();
				if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
					netIp = ip.getHostAddress();
					hasNetIp = true;
					break;
				} else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
					localIp = ip.getHostAddress();
				}
			}
		}

		if (netIp != null && !"".equals(netIp)) {
			return netIp;
		} else {
			return localIp;
		}
	}
}

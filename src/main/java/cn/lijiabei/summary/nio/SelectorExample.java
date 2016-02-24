package cn.lijiabei.summary.nio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SelectorExample {

	public static void main(String[] args) {
		try {
			Selector selector = Selector.open();

			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.connect(new InetSocketAddress("http://www.lijiabei.cn", 80));
			socketChannel.configureBlocking(false);

			socketChannel.register(selector, SelectionKey.OP_READ);// 注册Selector

			while (true) {
				int readyChannels = selector.select();// 查看是否有就绪的channel
				if (readyChannels == 0) continue;

				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
				while (keyIterator.hasNext()) {// 遍历就绪的channel
					SelectionKey selectionKey = (SelectionKey) keyIterator.next();
					// 根据监听的事件做处理
					if (selectionKey.isAcceptable()) {

					} else if (selectionKey.isConnectable()) {

					} else if (selectionKey.isReadable()) {

					} else if (selectionKey.isWritable()) {

					}
					keyIterator.remove();
				}
			}

			// socketChannel.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

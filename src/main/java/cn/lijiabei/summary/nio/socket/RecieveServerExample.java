package cn.lijiabei.summary.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class RecieveServerExample {

	static int totalCount = 0;

	public static void main(String[] args) {
		try {
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.bind(new InetSocketAddress(8808));
			serverSocketChannel.configureBlocking(false);// 非阻塞
			while (true) {
				SocketChannel socketChannel = serverSocketChannel.accept();

				if (null != socketChannel) {
					System.out.print("message:");
					ByteBuffer buffer = ByteBuffer.allocate(128);

					int byteRead = socketChannel.read(buffer);
					while (byteRead != -1) {
						StringBuffer content = new StringBuffer();
						buffer.flip();// 切换成读模式
						while (buffer.hasRemaining()) {
							buffer.getChar();
							char b = (char) buffer.get();
							content.append(b);
						}
						System.out.println(content.toString());
						buffer.clear();
						byteRead = socketChannel.read(buffer);
					}
				}
				totalCount++;
				if (totalCount > 1000) {
					System.out.println("finish");
					break;
				}
				Thread.sleep(500L);
			}
			serverSocketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}

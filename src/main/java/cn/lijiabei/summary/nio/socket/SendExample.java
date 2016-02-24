package cn.lijiabei.summary.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SendExample {

	public static void main(String[] args) {
		try {
			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.connect(new InetSocketAddress("127.0.0.1", 8808));

			String newData = "I'am a test..." + System.currentTimeMillis();

			ByteBuffer buffer = ByteBuffer.allocate(48);
			buffer.put(newData.getBytes());

			buffer.flip();

			while (buffer.hasRemaining()) {
				socketChannel.write(buffer);
			}

			socketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

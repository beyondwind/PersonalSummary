package cn.lijiabei.summary.nio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class SimpleExample {

	public static void main(String[] args) {
		try {
			RandomAccessFile randFile = new RandomAccessFile("/Users/lijiabei/log/spring.log", "rw");
			FileChannel fileChannel = randFile.getChannel();

			ByteBuffer buf = ByteBuffer.allocate(64);
			int byteRead = fileChannel.read(buf);
			while (byteRead != -1) {
				buf.flip();// 切换成读模式
				while (buf.hasRemaining()) {
					System.out.println(buf.getChar());
				}
				buf.clear();
				byteRead = fileChannel.read(buf);
			}

			randFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

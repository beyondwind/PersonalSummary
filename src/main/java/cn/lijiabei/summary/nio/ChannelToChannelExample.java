package cn.lijiabei.summary.nio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class ChannelToChannelExample {

	public static void main(String[] args) {
		try {
			RandomAccessFile fileFrom = new RandomAccessFile("/Users/lijiabei/log/spring.log", "rw");
			FileChannel fromChannel = fileFrom.getChannel();

			RandomAccessFile fileTo = new RandomAccessFile("/Users/lijiabei/log/root.log", "rw");
			FileChannel toChannel = fileTo.getChannel();

			long count = fromChannel.size();

			fromChannel.transferTo(0, count, toChannel);

			fileFrom.close();
			fileTo.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

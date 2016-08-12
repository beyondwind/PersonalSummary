package cn.lijiabei.summary.exception.oom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 堆外内存溢出
 */
public class DirectMemoryOutOfMemory {

	public static void main(String[] args) {

		// NIO 分配堆外的缓冲区
		List<ByteBuffer> buffers = new ArrayList<>();
		while (true) {
			ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024 * 1024);
			buffers.add(buffer);
		}
	}

}

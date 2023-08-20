package com.kstudy.myopengl.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class BufferHelper {
    /**
     * 获取浮点形缓冲数据
     * @param vertexes 数据集
     * @return fb 浮点形缓冲
     */
    public static FloatBuffer getFloatBuffer(float[] vertexes) {
        FloatBuffer fb;
        // 分配一块本地内存（不受 GC 管理）
        // 顶点坐标个数 * 坐标数据类型
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertexes.length * 4/*sizeof float*/);
        // 设置使用设备硬件的本地字节序（保证数据排序一致）
        byteBuffer.order(ByteOrder.nativeOrder());
        // 从ByteBuffer中创建一个浮点缓冲区
        fb = byteBuffer.asFloatBuffer();
        // 写入坐标数组
        fb.put(vertexes);
        // 设置默认的读取位置，从第一个坐标开始
        fb.position(0);
        return fb;
    }
}

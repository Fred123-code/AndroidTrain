package com.kstudy.rtmplib;

import android.app.Activity;
import android.view.SurfaceHolder;

import com.kstudy.rtmplib.video.VideoChannel;
import com.kstudy.rtmplib.audio.AudioChannel;

public class RTMPMainPusher {
    static {
        System.loadLibrary("rtmplib");/*rtmp-jni*/
    }

    private VideoChannel videoChannel;
    private AudioChannel audioChannel;

    public RTMPMainPusher(Activity activity, int cameraId, int width, int height, int fps, int bitrate) {
        // 初始化C++层
        native_init();

        videoChannel = new VideoChannel(this, activity, cameraId, width, height, fps, bitrate);
        audioChannel = new AudioChannel(this);
    }

    public void setPreviewDisplay(SurfaceHolder surfaceHolder) {
        videoChannel.setPreviewDisplay(surfaceHolder);
    }

    public void switchCamera() {
        videoChannel.switchCamera();
    }

    public int getInputSamples() {
        return native_getInputSamples();
    }

    /**
     * 开始直播
     * @param path rtmp地址
     */
    public void startLive(String path) {
        native_start(path);
        videoChannel.setLive(true);
        audioChannel.setLive(true);
    }

    /**
     * 停止直播
     */
    public void stopLive() {
        videoChannel.setLive(false);
        audioChannel.setLive(false);
        native_stop();
    }


    /**
     * 释放工作
     */
    public void release() {
        videoChannel.release();
        audioChannel.release();
        native_release();
    }

    public native void native_init(); // 初始化
    public native void native_start(String path); // 开始直播path:rtmp推流地址
    public native void native_stop(); // 停止直播
    public native void native_release();

    //视频
    public native void native_initVideoEncoder(int width, int height, int fps, int bitrate);
    public native void native_pushVideo(byte[] data); // 相机画面的数据 byte[] 推给 C++层

    //音频
    public native void native_initAudioEncoder(int sampleRate, int numChannels); // 初始化faac音频编码器
    public native int native_getInputSamples(); // 获取facc编码器 样本数
    public native void native_pushAudio(byte[] bytes); // 把audioRecord采集的原始数据，给C++层编码-->入队---> 发给流媒体服务器
}

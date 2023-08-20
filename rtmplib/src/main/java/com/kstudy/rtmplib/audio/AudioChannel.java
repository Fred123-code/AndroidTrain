package com.kstudy.rtmplib.audio;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.kstudy.rtmplib.RTMPMainPusher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioChannel {
    private RTMPMainPusher mPusher;
    private ExecutorService mExecutorService;
    private volatile boolean isLive;
    private int mChannels = 2; // 通道数为2，说明是2个通道(人类的耳朵，两个耳朵， 左声道/右声道)
    int inputSamples; // 4096
    private AudioRecord mAudioRecord; // AudioRecord采集Android麦克风音频数据 --> C++层 --> 编码 --> 封包 ---> 加入队列

    @SuppressLint("MissingPermission")
    public AudioChannel(RTMPMainPusher pusher) {
        this.mPusher = pusher;
        mExecutorService = Executors.newSingleThreadExecutor();
        int channelConfig;
        if (mChannels == 2) {
            channelConfig = AudioFormat.CHANNEL_IN_STEREO; // 双声道
        } else {
            channelConfig = AudioFormat.CHANNEL_IN_MONO; // 单声道
        }

        // 初始化faac音频编码器
        mPusher.native_initAudioEncoder(44100, mChannels);
        // (getInputSamples单通道样本数1024 * 通道数2)=2048 * 2(一个样本16bit，2字节) = 4096
        inputSamples = mPusher.getInputSamples() * 2;

        // AudioRecord.getMinBufferSize 得到的minBufferSize 能大不能小，最好是 * 2
        int minBufferSize = AudioRecord.getMinBufferSize(44100, channelConfig, AudioFormat.ENCODING_PCM_16BIT) * 2;

        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, // 安卓手机的麦克风
                44100,  // 采样率
                channelConfig, // 声道数 双声道
                AudioFormat.ENCODING_PCM_16BIT, // 位深 16位 2字节
                Math.max(inputSamples, minBufferSize)); // 缓冲区大小（以字节为单位）：max在两者中取最大的，内置缓冲buffsize大一些 没关系的，能大 但是不能小

    }

    public void setLive(boolean live) {
        isLive = live;
        if (isLive) {
            mExecutorService.submit(new AudioTask()); // 子线程启动 Runnable（AudioTask）
        } else {
            mAudioRecord.stop(); // 停止录音
        }
    }

    public void release() {
        if (mAudioRecord != null) {
            mAudioRecord.release();
            mAudioRecord = null;
        }
    }

    private class AudioTask implements Runnable {
        @Override
        public void run() {
            mAudioRecord.startRecording();
            // 单通道样本数：1024
            // 位深： 16bit位 2字节
            // 声道数：双声道
            // 以上规格：之前说过多遍了，经验值是4096
            // 1024单通道样本数 * 2 * 2 = 4096
            byte[] bytes = new byte[inputSamples]; // 接收录制声音数据的 byte[]
            while (isLive) {
                // 每次读多少数据要根据编码器来定
                int len = mAudioRecord.read(bytes, 0, bytes.length);
                if (len > 0) {
                    // 成功采集到音频数据了
                    // 对音频数据进行编码并发送（将编码后的数据push到安全队列中）
                    mPusher.native_pushAudio(bytes);
                }
            }
            mAudioRecord.stop(); // 停止录音
        }
    }
}

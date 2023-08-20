package com.kstudy.rtmplib.video;

import android.app.Activity;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.kstudy.common.CameraHelper;
import com.kstudy.rtmplib.RTMPMainPusher;

public class VideoChannel {
    private CameraHelper mCameraHelper;
    private int mBitrate;    //码率
    private int mFps;
    private boolean isLive; // 是否直播：非常重要的标记，开始直播就是true，停止直播就是false，通过此标记控制是否发送数据给C++层
    private RTMPMainPusher mPusher;

    public VideoChannel(RTMPMainPusher pusher, Activity activity, int cameraId, int width, int height, int fps, int bitrate) {
        this.mPusher = pusher; // 回调给总部：中转站
        this.mFps = fps; // fps 每秒钟多少帧
        this.mBitrate = bitrate; // 码率
        initCamera(activity, cameraId, width, height);
    }

    private void initCamera(Activity activity, int cameraId, int width, int height) {
        mCameraHelper = new CameraHelper(activity, cameraId, width, height); // Camera相机预览帮助类
        mCameraHelper.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                // 设置Camera相机预览帮助类，onPreviewFrame(nv21)数据的回调监听
                // data == nv21数据(y420的字节)
                if (isLive) {
                    mPusher.native_pushVideo(data);
                }
            }
        });
        mCameraHelper.setOnChangedSizeListener(new CameraHelper.OnChangedSizeListener() {
            @Override
            public void onChanged(int width, int height) {
                // 视频编码器的初始化有关：width，height，fps，bitrate
                mPusher.native_initVideoEncoder(width, height, mFps, mBitrate); // 初始化x264编码器
            }
        });
    }

    public void setPreviewDisplay(SurfaceHolder surfaceHolder) {
        mCameraHelper.setPreviewDisplay(surfaceHolder);
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public void switchCamera() {
        mCameraHelper.switchCamera();
    }

    // 调用帮助类-->停止预览
    public void release() {
        mCameraHelper.stopPreview();
    }
}

package com.kstudy.common;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class CameraHelper {
    private static final String TAG = "CameraHelper";
    private Activity mActivity; // Activity实例
    private int mWidth; // 宽度
    private int mHeight; // 高度
    private int mCameraId; // 前置 后置 摄像
    private Camera mCamera;
    private byte[] buffer; // 相机画面数据
    private int mRotation; // 标记切换摄像头
    private SurfaceHolder mSurfaceHolder; // SurfaceView的帮助类
    private Camera.PreviewCallback mPreviewCallback; // 相机预览回调接口
    private OnChangedSizeListener mOnChangedSizeListener; // 回调接口 把宽和高 给外界的

    public void setPreviewDisplay(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {

            }
            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                // 释放摄像头
                stopPreview();
                // 开启摄像头
                startPreview();
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                stopPreview();
            }
        });
    }

    public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
        this.mPreviewCallback = previewCallback;
    }

    public void setOnChangedSizeListener(OnChangedSizeListener onChangedSizeListener) {
        this.mOnChangedSizeListener = onChangedSizeListener;
    }

    public void switchCamera() {
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        stopPreview();
        startPreview();
    }

    public interface OnChangedSizeListener {
        void onChanged(int width, int height);
    }

    public CameraHelper(Activity activity, int cameraId, int width, int height) {
        this.mActivity = activity;
        this.mCameraId = cameraId;
        this.mWidth = width;
        this.mHeight = height;
    }

    private void startPreview() {
        mCamera = Camera.open(mCameraId);
        //配置camera的属性
        Camera.Parameters parameters = mCamera.getParameters();
        //设置预览数据格式为nv21
        parameters.setPreviewFormat(ImageFormat.NV21);
        //这是摄像头宽、高
        setPreviewSize(parameters);
        // 设置摄像头 图像传感器的角度、方向
        setPreviewOrientation(parameters);
        mCamera.setParameters(parameters);
        //YUV420
        buffer = new byte[mWidth * mHeight * 3 / 2];
        //数据缓存区
        mCamera.addCallbackBuffer(buffer);
        mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                if (mPreviewCallback != null) {
                    mPreviewCallback.onPreviewFrame(data, camera);
                }
                camera.addCallbackBuffer(buffer);
            }
        });
        //设置预览画面
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mOnChangedSizeListener != null) {
            mOnChangedSizeListener.onChanged(mWidth, mHeight);
        }
        //开启预览
        mCamera.startPreview();
    }

    private void setPreviewSize(Camera.Parameters parameters) {
        // 获取摄像头支持的宽、高
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size size = supportedPreviewSizes.get(0);
        Log.d(TAG, "Camera支持: " + size.width + "x" + size.height);
        // 选择一个与设置的差距最小的支持分辨率
        int m = Math.abs(size.height * size.width - mWidth * mHeight);
        supportedPreviewSizes.remove(0);
        Iterator<Camera.Size> iterator = supportedPreviewSizes.iterator();
        // 遍历
        while (iterator.hasNext()) {
            Camera.Size next = iterator.next();
            Log.d(TAG, "支持 " + next.width + "x" + next.height);
            int n = Math.abs(next.height * next.width - mWidth * mHeight);
            if (n < m) {
                m = n;
                size = next;
            }
        }
        mWidth = size.width;
        mHeight = size.height;
        parameters.setPreviewSize(mWidth, mHeight);
        Log.d(TAG, "预览分辨率 width:" + size.width + " height:" + size.height);
    }

    private void setPreviewOrientation(Camera.Parameters parameters) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        mRotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (mRotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90: // 横屏 左边是头部(home键在右边)
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:// 横屏 头部在右边
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }

        mCamera.setDisplayOrientation(result);
    }

    public void stopPreview() {
        if (mCamera != null) {
            //预览数据回调接口
            mCamera.setPreviewCallback(null);
            //停止预览
            mCamera.stopPreview();
            //释放摄像头
            mCamera.release();
            mCamera = null;
        }
    }

}

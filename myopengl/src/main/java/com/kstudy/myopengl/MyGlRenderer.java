package com.kstudy.myopengl;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glGenTextures;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLSurfaceView;

import com.kstudy.common.GLCameraHelper;
import com.kstudy.myopengl.face.FaceTrack;
import com.kstudy.myopengl.fliter.BigEyeFilter;
import com.kstudy.myopengl.fliter.CameraFilter;
import com.kstudy.myopengl.fliter.ScreenFilter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

// 自定义渲染器
public class MyGlRenderer implements GLSurfaceView.Renderer {
    private final GLSurfaceView mGLSurfaceView;
    private GLCameraHelper mGLCameraHelper;
    private SurfaceTexture mSurfaceTexture;

    private int[] mTextureID;

    private CameraFilter mCameraFilter;
    private ScreenFilter mScreenFilter;

    float[] mtx = new float[16]; // 矩阵数据，变换矩阵

    private int mWidth;
    private int mHeight;

    private MyMediaRecorder mMediaRecorder;

    //特效
    private BigEyeFilter mBigEyeFilter;
    private FaceTrack mFaceTrack;

    public MyGlRenderer(GLSurfaceView glSurfaceView) {
        this.mGLSurfaceView = glSurfaceView;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mGLCameraHelper = new GLCameraHelper((Activity) mGLSurfaceView.getContext(),
                Camera.CameraInfo.CAMERA_FACING_FRONT,
                640, 480);

        // 获取纹理ID
        mTextureID = new int[1];
        glGenTextures(mTextureID.length, mTextureID, 0);
        // 实例化纹理对象
        mSurfaceTexture = new SurfaceTexture(mTextureID[0]);
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                mGLSurfaceView.requestRender();
            }
        });

        mCameraFilter = new CameraFilter(mGLSurfaceView.getContext()); // FBO 先
        mScreenFilter = new ScreenFilter(mGLSurfaceView.getContext()); // 渲染屏幕 再

        // 初始化录制工具类
        EGLContext eglContext = EGL14.eglGetCurrentContext();
//        mMediaRecorder = new MyMediaRecorder(640, 480,
//                STORAGE_URL + System.currentTimeMillis() + ".mp4", eglContext,
//                myGLSurfaceView.getContext());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        mGLCameraHelper.startPreview(mSurfaceTexture); // 开始预览
        mCameraFilter.onReady(width, height); // FBO 先
        mScreenFilter.onReady(width,  height); // 渲染屏幕 再
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 每次清空之前
        glClearColor(255, 0 ,0, 0); // 屏幕清理成颜色 红色，清理成红色的黑板一样
        // GL_COLOR_BUFFER_BIT 颜色缓冲区
        // GL_DEPTH_BUFFER_BIT 深度缓冲区
        // GL_STENCIL_BUFFER_BIT 模型缓冲区
        glClear(GL_COLOR_BUFFER_BIT);

        // 绘制摄像头数据
        mSurfaceTexture.updateTexImage();  // 将纹理图像更新为图像流中最新的帧数据【刷新一下】

        // 画布，矩阵数据
        mSurfaceTexture.getTransformMatrix(mtx);

        mCameraFilter.setMatrix(mtx);
        int textureId = mCameraFilter.onDrawFrame(mTextureID[0]); // 摄像头，矩阵

        if (mBigEyeFilter != null) {
            //TODO:大眼特性添加
//            mBigEyeFilter.setFace(mFaceTrack.getFace());
//            textureId = mBigEyeFilter.onDrawFrame(textureId);
        }

        mScreenFilter.onDrawFrame(textureId); // textureId==最终成果的纹理ID

//        mMediaRecorder.encodeFrame(textureId, mSurfaceTexture.getTimestamp());
    }

    public void enableBigEye(boolean isChecked) {
        // BigEyeFilter bigEyeFilter = new BigEyeFilter(); // 这样可以吗  不行，必须在EGL线程里面绘制

        mGLSurfaceView.queueEvent(new Runnable() { // 把大眼渲染代码，加入到， GLSurfaceView 的 内置EGL 的 GLTHread里面
            public void run() {
                if (isChecked) {
                    mBigEyeFilter = new BigEyeFilter(mGLSurfaceView.getContext());
                    mBigEyeFilter.onReady(mWidth, mHeight);
                } else {
                    mBigEyeFilter.release();
                    mBigEyeFilter = null;
                }
            }
        });
    }
}

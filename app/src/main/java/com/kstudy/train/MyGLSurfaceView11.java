package com.kstudy.train;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.kstudy.myopengl.MyGlRenderer;

public class MyGLSurfaceView11 extends GLSurfaceView {
    private MyGlRenderer mRenderer;

    public MyGLSurfaceView11(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        Log.e("**********************", "MyGLSurfaceView: ");
    }

    private void init() {
        // 设置EGL版本: OpenGLES 2.0
        setEGLContextClientVersion(2);

        // 设置渲染器
        // 注意：
        // EGL 开启一个 GLThread.start  run { Renderer.onSurfaceCreated ...onSurfaceChanged  onDrawFrame }
        // 如果这三个函数，不让GLThread调用，会崩溃，所以他内部的设计，必须通过GLThread调用来调用三个函数
        mRenderer = new MyGlRenderer(this);
        setRenderer(mRenderer);

        // 设置渲染器模式
        // RENDERMODE_WHEN_DIRTY 按需渲染，有帧数据的时候，才会去渲染（ 效率高，麻烦，后面需要手动调用一次才行）
        // RENDERMODE_CONTINUOUSLY 每隔16毫秒，读取更新一次，（如果没有显示上一帧）
        setRenderMode(RENDERMODE_WHEN_DIRTY); // 手动模式 - 效率高，麻烦，后面需要手动调用一次才行

    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }
}

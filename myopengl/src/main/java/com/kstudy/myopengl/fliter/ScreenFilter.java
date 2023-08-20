package com.kstudy.myopengl.fliter;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

import android.content.Context;

import com.kstudy.myopengl.R;

public class ScreenFilter extends BaseFilter {
    public ScreenFilter(Context context) {
        super(context, R.raw.base_vertex, R.raw.base_fragment); // base_vertex(没有矩阵) base_fragment(没有OES 是sampler2D)
    }

    @Override
    public int onDrawFrame(int textureId) {
        // 设置视窗大小
        glViewport(0, 0, mWidth, mHeight);
        glUseProgram(mProgramId); // 必须要使用着色器程序一次

        // 顶点坐标赋值
        mVertexBuffer.position(0); // 顶点坐标赋值
        glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 0, mVertexBuffer); // 传值
        glEnableVertexAttribArray(vPosition); // 激活
        // 纹理坐标赋值
        mTextureBuffer.position(0); // 纹理坐标赋值
        glVertexAttribPointer(vCoord, 2, GL_FLOAT, false, 0, mTextureBuffer); // 传值
        glEnableVertexAttribArray(vCoord); // 激活

        // 只需要把OpenGL的纹理ID，渲染到屏幕上就可以了，不需要矩阵数据传递给顶点着色器了
        // 变换矩阵 把mtx矩阵数据 传递到 vMatrix
        // glUniformMatrix4fv(vMatrix, 1, false, mtx, 0);
        glActiveTexture(GL_TEXTURE0); // 激活图层

        glBindTexture(GL_TEXTURE_2D ,textureId);

        // CameraFilter已经做过了，直接显示，用OepnGL 2D GL_TEXTURE_2D 显示就行了
        // glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId); // 由于这种方式并不是通用的，所以先去除

        glUniform1i(vTexture, 0); // 传递采样器
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4); // 通知 opengl 绘制

        return textureId;
    }

    @Override
    public void onReady(int width, int height) {
        super.onReady(width, height);
    }
}

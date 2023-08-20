package com.kstudy.myopengl.fliter;

import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;

import android.content.Context;

import com.kstudy.myopengl.utils.BufferHelper;
import com.kstudy.myopengl.utils.ShaderHelper;
import com.kstudy.myopengl.utils.TextResourceReader;

import java.nio.FloatBuffer;

public abstract class BaseFilter {
    private int mVertexSourceId; // 子类传递过来的顶点着色器代码ID
    private int mFragmentSourceId; // 子类传递过来的片元着色器代码ID

    protected FloatBuffer mVertexBuffer; // 顶点坐标数据缓冲区
    protected FloatBuffer mTextureBuffer; // 纹理坐标数据缓冲区

    protected int mProgramId; // 着色器程序

    protected int vPosition;  // 顶点着色器：顶点位置
    protected int vCoord; // 顶点着色器：纹理坐标
    protected int vMatrix; // 顶点着色器：变换矩阵

    protected int vTexture; // 片元着色器：采样器

    protected int mWidth; // 宽度
    protected int mHeight; // 高度

    public BaseFilter(Context context, int vertexSourceId, int fragmentSourceId) {
        this.mVertexSourceId = vertexSourceId;
        this.mFragmentSourceId = fragmentSourceId;

        // 顶点相关 坐标系
        float[] VERTEX = {
                -1.0f, -1.0f,
                1.0f, -1.0f,
                -1.0f, 1.0f,
                1.0f, 1.0f,
        };
        mVertexBuffer = BufferHelper.getFloatBuffer(VERTEX); // 保存到 顶点坐标数据缓冲区

        // 纹理相关 坐标系
        float[] TEXTURE = {
                0.0f, 0.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
        };
        mTextureBuffer = BufferHelper.getFloatBuffer(TEXTURE); // 保存到 纹理坐标数据缓冲区

        init(context);
        changeTextureData();
    }
    /**
     * 修改纹理坐标 textureData（有需求可以重写该方法）
     */
    protected void changeTextureData() {

    }

    private void init(Context context) {
        String vertexSource = TextResourceReader.readTextFileFromResource(context, mVertexSourceId); // 顶点着色器代码字符串
        String fragmentSource = TextResourceReader.readTextFileFromResource(context, mFragmentSourceId); // 片元着色器代码字符串

        int vertexShaderId = ShaderHelper.compileVertexShader(vertexSource); // 编译顶点着色器代码字符串
        int fragmentShaderId = ShaderHelper.compileFragmentShader(fragmentSource); // 编译片元着色器代码字符串

        mProgramId = ShaderHelper.linkProgram(vertexShaderId, fragmentShaderId); // 链接 顶点着色器ID，片元着色器ID 最终输出着色器程序

        // 删除 顶点 片元 着色器ID
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);

        // 顶点着色器里面的如下：
        vPosition = glGetAttribLocation(mProgramId, "vPosition"); // 顶点着色器：的索引值
        vCoord = glGetAttribLocation(mProgramId, "vCoord"); // 顶点着色器：纹理坐标，采样器采样图片的坐标 的索引值
        vMatrix = glGetUniformLocation(mProgramId, "vMatrix"); // 顶点着色器：变换矩阵 的索引值
        // 片元着色器里面的如下：
        vTexture = glGetUniformLocation(mProgramId, "vTexture"); // 片元着色器：采样器
    }

    public abstract int onDrawFrame(int textureId);

    public void onReady(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public void release() {
        glDeleteProgram(mProgramId);
    }
}

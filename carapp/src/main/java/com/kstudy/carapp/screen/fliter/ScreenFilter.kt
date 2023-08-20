package com.kstudy.carapp.screen.fliter

import android.content.Context
import android.opengl.GLES20
import com.kstudy.carapp.screen.utils.OpenGLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class ScreenFilter(context: Context) {
    private var vPosition: Int? = null  // 顶点着色器：顶点位置
    private var vCoord: Int? = null     // 顶点着色器：纹理坐标
    private var vTexture: Int? = null   // 片元着色器：采样器
    private var vMatrix: Int? = null    // 顶点着色器：变换矩阵

    private var vertexBuffer: FloatBuffer  // 顶点坐标缓存区
    private var textureBuffer: FloatBuffer // 纹理坐标

    private var program: Int? = null

    private var mWidth: Int? = null   // 片元着色器：采样器
    private var mHeight: Int? = null    // 顶点着色器：变换矩阵

    private var mtx: FloatArray? = null

    fun setSize(width: Int, height: Int) {
        mWidth = width
        mHeight = height
    }

    fun setTransformMatrix(mtx: FloatArray) {
        this.mtx = mtx
    }

    fun onDraw(surfaceTexName: Int) {
        //设置绘制区域
        GLES20.glViewport(0, 0, mWidth!!, mHeight!!)
        GLES20.glUseProgram(program!!)

        vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(vPosition!!, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        //CPU传数据到GPU，默认情况下着色器无法读取到这个数据。 需要启用一下才可以读取
        GLES20.glEnableVertexAttribArray(vPosition!!)


        textureBuffer.position(0)
        GLES20.glVertexAttribPointer(vCoord!!, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)
        //CPU传数据到GPU，默认情况下着色器无法读取到这个数据。 需要启用一下才可以读取
        GLES20.glEnableVertexAttribArray(vCoord!!)

        //相当于激活一个用来显示图片的画框
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, surfaceTexName)
        // 0: 图层ID  GL_TEXTURE0
        GLES20.glUniform1i(vTexture!!,0)


        GLES20.glUniformMatrix4fv(vMatrix!!, 1, false, mtx, 0)

        //通知画画，
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0)
    }

    init {
        vertexBuffer = ByteBuffer.allocateDirect(4 * 4 * 2).order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        val VERTEX: FloatArray = floatArrayOf(
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f
        )
        vertexBuffer.clear()
        vertexBuffer.put(VERTEX)

        textureBuffer =  ByteBuffer.allocateDirect(4 * 4 * 2).order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        val TEXTURE: FloatArray = floatArrayOf(
                0.0f, 0.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f
        )
        textureBuffer.clear()
        textureBuffer.put(TEXTURE)

        var vertexSharder = OpenGLUtils.readAssetTextFile(context, "vshader.vsh")
        var fragSharder = OpenGLUtils.readAssetTextFile(context, "fshader.fsh")

        //着色器程序准备好
        program = OpenGLUtils.loadProgram(vertexSharder, fragSharder)

        //获取程序中的变量 索引
        program!!.let {
            vPosition = GLES20.glGetAttribLocation(it, "vPosition")
            vCoord = GLES20.glGetAttribLocation(it, "vCoord")
            vTexture = GLES20.glGetUniformLocation(it, "vTexture")
            vMatrix = GLES20.glGetUniformLocation(it, "vMatrix")
        }
    }
}

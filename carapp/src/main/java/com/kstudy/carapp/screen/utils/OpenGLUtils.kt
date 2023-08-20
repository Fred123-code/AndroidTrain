package com.kstudy.carapp.screen.utils

import android.content.Context
import android.opengl.GLES20
import java.io.BufferedReader
import java.io.InputStreamReader

class OpenGLUtils {

    companion object {
        @JvmStatic
        fun readAssetTextFile(context: Context, filename: String) : String{
            var bufferedReader: BufferedReader? = null
            val sb = StringBuilder()
            val inputStream = context.assets.open(filename)
            bufferedReader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                sb.append(line)
                sb.append("\n")
            }
            bufferedReader.close()
            return sb.toString()
        }
        @JvmStatic
        fun loadProgram(vertexSharder: String, fragSharder: String): Int? {
            /**
             * 顶点着色器
             */
            val vShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
            //加载着色器代码
            GLES20.glShaderSource(vShader, vertexSharder)
            //编译（配置）
            GLES20.glCompileShader(vShader)

            //查看配置 是否成功
            val status = IntArray(1)
            GLES20.glGetShaderiv(vShader, GLES20.GL_COMPILE_STATUS, status, 0)
            if (status[0] != GLES20.GL_TRUE) {
                throw IllegalStateException("load vertex shader:" + GLES20.glGetShaderInfoLog(vShader))
            }

            /**
             *  片元着色器
             */
            val fShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
            //加载着色器代码
            GLES20.glShaderSource(fShader, fragSharder)
            //编译（配置）
            GLES20.glCompileShader(fShader)

            //查看配置 是否成功
            GLES20.glGetShaderiv(fShader, GLES20.GL_COMPILE_STATUS, status, 0)
            if (status[0] != GLES20.GL_TRUE) {
                throw IllegalStateException("load fragment shader:" + GLES20.glGetShaderInfoLog(fShader))
            }

            /**
             * 创建着色器程序
             */
            val program = GLES20.glCreateProgram()
            //绑定顶点和片元
            GLES20.glAttachShader(program, vShader)
            GLES20.glAttachShader(program, fShader)
            //链接着色器程序
            GLES20.glLinkProgram(program)

            //获得状态
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0)
            if (status[0] != GLES20.GL_TRUE) {
                throw IllegalStateException("link program:" + GLES20.glGetProgramInfoLog(program))
            }
            GLES20.glDeleteShader(vShader)
            GLES20.glDeleteShader(fShader)

            return program
        }
    }

}
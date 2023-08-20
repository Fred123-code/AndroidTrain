package com.kstudy.train

import android.app.Application
import android.util.Log
import com.alibaba.android.arouter.BuildConfig
import com.alibaba.android.arouter.launcher.ARouter
import org.opencv.android.OpenCVLoader

class AppApplication : Application() {
    private val TAG: String = "AppApplication"

    override fun onCreate() {
        super.onCreate()
//        initLeakCanary()
        initArouter()
        initopencv()
    }

    private fun initArouter() {
        ARouter.openLog();     // 打印日志
        ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        ARouter.init(this);
    }

    private fun initopencv() {
        //初始化 opencv
        val success : Boolean = OpenCVLoader.initDebug();
        if (success) {
            Log.i(TAG, "initLoadOpenCV: openCV load success")
        } else {
            Log.e(TAG, "initLoadOpenCV: openCV load failed")
        }
    }


}
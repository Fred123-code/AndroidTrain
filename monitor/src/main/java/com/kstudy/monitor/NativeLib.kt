package com.kstudy.monitor

class NativeLib {

    /**
     * A native method that is implemented by the 'monitor' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'monitor' library on application startup.
        init {
            System.loadLibrary("monitor")
        }
    }
}
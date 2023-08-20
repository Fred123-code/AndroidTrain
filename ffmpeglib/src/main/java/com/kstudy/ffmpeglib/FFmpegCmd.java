package com.kstudy.ffmpeglib;

import android.util.Log;

import androidx.annotation.IntDef;

import com.blankj.utilcode.util.ThreadUtils;
import com.kstudy.common.utils.ThreadPoolUtil;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FFmpegCmd {
    private static final String TAG = "FFmpegCmd";

    static {
        System.loadLibrary("ffmpegjni");
    }

    private static final int STATE_INIT = 0;

    private static final int STATE_RUNNING = 1;

    private static final int STATE_FINISH = 2;

    private static final int STATE_ERROR = 3;

    private static FFmpegCmdListener mProgressListener;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_INIT, STATE_RUNNING, STATE_FINISH, STATE_ERROR})
    public @interface FFmpegState {}

    public static void execute(final String[] commands, final FFmpegCmdListener onHandleListener) {
        mProgressListener = onHandleListener;
        ThreadUtils.executeByIo(new ThreadUtils.Task<Integer>() {
            @Override
            public Integer doInBackground() throws Throwable {
                onHandleListener.onStart();
                return handle(commands);
            }

            @Override
            public void onSuccess(Integer result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(Throwable t) {

            }
        });
    }

    public static void executeProbe(final String[] commands, final FFmpegCmdListener onHandleListener) {
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (onHandleListener != null) {
                    onHandleListener.onStart();
                }
                //call JNI interface to execute FFprobe cmd
                String result = handleProbe(commands);
                if (onHandleListener != null) {
                    onHandleListener.onMessage(result);
                }
            }
        });
    }

    public static void onProgressCallback(int position, int duration, @FFmpegState int state) {
        Log.i(TAG, "onProgressCallback: --position=" + position + "--duration=" + duration + "--state=" + state);
        if (position > duration && duration > 0) {
            return;
        }
        if (mProgressListener != null) {
            if (position > 0 && duration > 0) {
                int progress = 100 * position / duration ;
                if (progress < 100) {
                    mProgressListener.onProgress(progress, duration);
                }
            } else {
                mProgressListener.onProgress(position, duration);
            }
        }
    }

    public static void onMsgCallback(String msg) {
        Log.i(TAG, "onMsgCallback: ******************" + msg);
        if (msg!=null&&!msg.isEmpty()){
            Log.i(TAG, "onMsgCallback: from native msg=" + msg);
            if (msg.startsWith("silence") && mProgressListener != null) {
                mProgressListener.onMessage(msg);
            }
        }
    }

    public interface  FFmpegCmdListener {
        default void onStart(){}
        void onSuccess(int result);
        default void onMessage(String msg){}
        default void onProgress(int progress, int duration){}
    }

    public native static int handle(String[] commands);
    public native static String handleProbe(String[] commands);
}

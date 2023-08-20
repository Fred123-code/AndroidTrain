package com.kstudy.monitor.traffic;

import android.app.Activity;
import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class TrafficCheckHelper {
    private static volatile TrafficCheckHelper mTrafficCheckHelper;
    private static volatile TrafficFactory mFrafficFactory;
    private static volatile TrafficController mTrafficController;
    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;

    private TrafficCheckHelper() {
        mTrafficController = new TrafficController();
        mFrafficFactory = new TrafficFactory(mTrafficController);
        activityLifecycleCallbacks = mFrafficFactory.createDefaultActivityLifecycleCallbacks();
    }

    public static TrafficCheckHelper getInstance() {
        if (mTrafficCheckHelper == null) {
            synchronized (TrafficCheckHelper.class) {
                if (mTrafficCheckHelper == null) {
                    mTrafficCheckHelper = new TrafficCheckHelper();
                }
            }
        }

        return mTrafficCheckHelper;
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    public void start(Activity activity) {
        activity.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);

        if (mTrafficController.getTrafficTaskSet().isEmpty()) mTrafficController.getTrafficTaskSet().add(mFrafficFactory.createDefaultTrafficTask());
        mTrafficController.startTrafficTask(activity);
    }

    public void stop(Activity activity) {
        activityLifecycleCallbacks.onActivityStopped(activity);
        mTrafficController.stopTrafficTask(activity);
    }

    public TrafficInfo getTrafficInfoActivity(Activity activity) {
        return mTrafficController.getTrafficInfoActivity(activity);
    }

    public void addTrafficTask(TrafficTaskImpl trafficTask) {
        mTrafficController.addTrafficTask(trafficTask);
    }

    public void removeTrafficTask(TrafficTaskImpl trafficTask) {
        mTrafficController.removeTrafficTask(trafficTask);
    }

    public void updateTrafficRX(Activity activity) {
        mFrafficFactory.updateTrafficRX(activity);
    }
}

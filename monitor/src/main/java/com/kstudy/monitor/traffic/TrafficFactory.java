package com.kstudy.monitor.traffic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.net.TrafficStats;
import android.os.Bundle;

import com.kstudy.common.utils.ThreadPoolUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/*
* 基础的Traffic处理
* */
public class TrafficFactory {
    private TrafficController trafficController;
    private static long mCurrentStats;

    public TrafficFactory(TrafficController trafficController) {
        this.trafficController = trafficController;
    }

    public Application.ActivityLifecycleCallbacks createDefaultActivityLifecycleCallbacks(){
        return new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (!trafficController.getTrafficInfoActivityHashMap().containsKey(activity)) {
                    TrafficInfo trafficInfo = new TrafficInfo();
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                    Date date = new Date(System.currentTimeMillis());
                    trafficInfo.time = simpleDateFormat.format(date);
                    trafficInfo.activity = activity;
                    trafficInfo.trafficCost = 0;
                    trafficInfo.activityName = activity.getClass().getSimpleName();
                    trafficController.getTrafficInfoActivityHashMap().put(activity, trafficInfo);
                }
                mCurrentStats = TrafficStats.getUidRxBytes(android.os.Process.myUid());
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                updateTrafficRX(activity);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                TrafficInfo item = trafficController.getTrafficInfoActivityHashMap().get(activity);
                if (item != null) {
                    for (TrafficTaskImpl task : trafficController.getTrafficTaskSet()) {
                        task.stop(activity);
                        trafficController.getTrafficInfoActivityHashMap().remove(activity);
                    }
                    item.clear();
                }
            }
        };
    }

    public TrafficTaskImpl createDefaultTrafficTask(){
        return new TrafficTaskImpl() {
            @Override
            public void start(Activity activity) {

            }

            @Override
            public void stop(Activity activity) {
                trafficController.getTrafficInfoActivityHashMap().remove(activity);
            }
        };
    }

    public void updateTrafficRX(Activity activity) {
        TrafficInfo item = trafficController.getTrafficInfoActivityHashMap().get(activity);
        if (item != null) {
            item.trafficCost = (TrafficStats.getUidRxBytes(android.os.Process.myUid()) - mCurrentStats) / 1024 / 1024; //MB
        }
    }
}

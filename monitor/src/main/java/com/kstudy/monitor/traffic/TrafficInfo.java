package com.kstudy.monitor.traffic;

import android.app.Activity;

public class TrafficInfo {
    public String time; //格式
    public Activity activity;
    public String activityName;
    public long trafficCost;

    void clear() {
        time = null;
        activity = null;
        activityName = null;
        trafficCost = 0;
    }
}

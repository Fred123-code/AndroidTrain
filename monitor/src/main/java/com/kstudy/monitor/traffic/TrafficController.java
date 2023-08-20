package com.kstudy.monitor.traffic;

import android.app.Activity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TrafficController {
    private HashMap<Activity,TrafficInfo> trafficInfoActivityHashMap = new HashMap<>();
    private Set<TrafficTaskImpl> trafficTaskSet = new HashSet<>();

    public TrafficInfo getTrafficInfoActivity(Activity activity) {
        return trafficInfoActivityHashMap.get(activity);
    }

    public void startTrafficTask(Activity activity) {

        for (TrafficTaskImpl task : trafficTaskSet) {
            task.start(activity);
        }
    }

    public void stopTrafficTask(Activity activity) {
        for (TrafficTaskImpl task : trafficTaskSet) {
            task.stop(activity);
        }
    }

    public void addTrafficTask(TrafficTaskImpl trafficTask) {
        trafficTaskSet.add(trafficTask);
    }

    public void removeTrafficTask(TrafficTaskImpl trafficTask) {
        trafficTaskSet.remove(trafficTask);
    }

    public HashMap<Activity, TrafficInfo> getTrafficInfoActivityHashMap() {
        return trafficInfoActivityHashMap;
    }

    public Set<TrafficTaskImpl> getTrafficTaskSet() {
        return trafficTaskSet;
    }
}

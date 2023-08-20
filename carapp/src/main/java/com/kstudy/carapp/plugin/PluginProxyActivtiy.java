package com.kstudy.carapp.plugin;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

public class PluginProxyActivtiy extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("PluginProxyActivtiy", "onCreate: 启动插件的Activity");
    }
}

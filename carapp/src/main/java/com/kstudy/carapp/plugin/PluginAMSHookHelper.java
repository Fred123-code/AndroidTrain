package com.kstudy.carapp.plugin;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class PluginAMSHookHelper {
    private static final String TAG = "PluginAMSHookHelper";
    private static final String TARGET_INTENT = "target_intent";
    private static final int EXECUTE_TRANSACTION = 159;

    public static void hookAMS() {
        try {
            Field singletonField = null;
            System.out.println("**********" + Build.VERSION.SDK_INT);
            // 小于8.0
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Class<?> clazz = Class.forName("android.app.ActivityManagerNative");
                singletonField = clazz.getDeclaredField("gDefault");
                //小于10.0
            } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                Class<?> activityTaskManagerClazz = Class.forName("android.app.ActivityManager");
                singletonField = activityTaskManagerClazz.getDeclaredField("IActivityManagerSingleton");
            } else {
                Class<?> activityTaskManagerClazz = Class.forName("android.app.ActivityTaskManager");
                singletonField = activityTaskManagerClazz.getDeclaredField("IActivityTaskManagerSingleton");
            }

            singletonField.setAccessible(true);
            Object singleton = singletonField.get(null);

            // 获取 系统的 IActivityManager 对象
            Class<?> singletonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);

            //通过activityTaskManagerSingleTon对象的mInstance属性就可以获取ActivityTaskManager.getService()的值,正是要代理的对象
            Method getMethod = singletonClass.getMethod("get");
            getMethod.setAccessible(true);
            getMethod.invoke(singleton);

            final Object mInstance = mInstanceField.get(singleton);
            // IActivityManager = ActivityManager.getService()
            Class<?> iActivityManagerClass = null;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                //IActivityManager#startActivity   @UnsupportedAppUsage(  maxTargetSdk = 29) 受限制的灰名单，直接反射 Class.forName 无法代理到startActivity方法
                iActivityManagerClass = Class.forName("android.app.IActivityManager");
            } else {
                iActivityManagerClass = Class.forName("android.app.IActivityTaskManager");
            }

            // 创建动态代理对象
            Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{iActivityManagerClass}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            /**
                             * IActivityManager#startActivity(whoThread, who.getBasePackageName(), intent,
                             *                         intent.resolveTypeIfNeeded(who.getContentResolver()),
                             *                         token, target != null ? target.mEmbeddedID : null,
                             *                         requestCode, 0, null, options)
                             */

                            if ("startActivity".equals(method.getName())) {
                                int index = -1;
                                for (int i = 0; i < args.length; i++) {
                                    if (args[i] instanceof Intent) {
                                        index = i;
                                        break;
                                    }
                                }

                                //启动插件
                                Intent intent = (Intent) args[index];

                                Intent proxyIntent = new Intent();
                                proxyIntent.setClassName("com.kstudy.appplugin.plugin",
                                        "com.kstudy.appplugin.plugin.PluginProxyActivtiy");
                                System.out.println("22222222222222222222222222222222222222222222222222222222222");
                                proxyIntent.putExtra(TARGET_INTENT, intent);
                                args[index] = proxyIntent;
                            }

                            return method.invoke(mInstance, args);
                        }
                    });

            // ActivityManager.getService() 替换成 proxyInstance
            mInstanceField.set(singleton, proxyInstance);

        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("********************************PluginAMSHookHelper失败了");
        }
    }

    public static void hookH() {
        try {
            Class<?> clazz = Class.forName("android.app.ActivityThread");
            // 获取 ActivityThread 对象
            Field activityThreadField = clazz.getDeclaredField("sCurrentActivityThread");
            activityThreadField.setAccessible(true);
            Object activityThread = activityThreadField.get(null);

            // 获取 mH 对象
            Field mHField = clazz.getDeclaredField("mH");
            mHField.setAccessible(true);
            final Handler mH = (Handler) mHField.get(activityThread);
            Field mCallbackField = Handler.class.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);

            Handler.Callback callback = new Handler.Callback() {
                @Override
                public boolean handleMessage(@NonNull Message msg) {
                    // 找到 Intent的方便替换的地方  --- 在这个类里面 ActivityClientRecord --- Intent intent 非静态
                    // msg.obj == ActivityClientRecord
                    switch (msg.what) {
                        case 100:
                            try {
                                Field intentField = msg.obj.getClass().getDeclaredField("intent");
                                intentField.setAccessible(true);
                                // 启动代理Intent
                                Intent proxyIntent = (Intent) intentField.get(msg.obj);
                                // 启动插件的 Intent
                                Intent intent = proxyIntent.getParcelableExtra(TARGET_INTENT);
                                if (intent != null) {
                                    intentField.set(msg.obj, intent);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println("333333333333333333333333333333333333333333333333333333333333333333333333");
                            break;
                        case EXECUTE_TRANSACTION://API 30
                            // 获取 mActivityCallbacks 对象
                            try {
                                Field mActivityCallbacksField = msg.obj.getClass().getDeclaredField("mActivityCallbacks");
                                mActivityCallbacksField.setAccessible(true);
                                List mActivityCallbacks = (List) mActivityCallbacksField.get(msg.obj);

                                for (int i = 0; i < mActivityCallbacks.size(); i++) {
                                    if (mActivityCallbacks.get(i).getClass().getName().equals("android.app.servertransaction.LaunchActivityItem")) {
                                        Object launchActivityItem = mActivityCallbacks.get(i);
                                        // 获取启动代理的 Intent
                                        Field mIntentField = launchActivityItem.getClass().getDeclaredField("mIntent");
                                        mIntentField.setAccessible(true);
                                        Intent proxyIntent = (Intent) mIntentField.get(launchActivityItem);

                                        // 目标 intent 替换 proxyIntent
                                        Intent intent = proxyIntent.getParcelableExtra(TARGET_INTENT);
                                        if (intent != null) {
                                            mIntentField.set(launchActivityItem, intent);
                                        }
                                    }
                                }
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            System.out.println("4444444444444444444444444444444444444444444444444444444444444444444");
                            break;
                    }
                    return false;
                }
            };

            // 替换系统的 callBack
            mCallbackField.set(mH, callback);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            System.out.println("********************************PluginAMSHookHelper失败了");
        }
    }
}

package com.kstudy.carapp.plugin;

import android.content.Context;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Objects;

import dalvik.system.DexClassLoader;

public class PluginLoadUtils {
    public static final String storage_url_plugins_apk = "/storage/emulated/0/Download/plugins_test.apk";

    public static void loadClass(Context context,String pathAPK) {
        try {
            Class<?> clazz = Class.forName("dalvik.system.BaseDexClassLoader");
            Field pathListField = clazz.getDeclaredField("pathList");
            pathListField.setAccessible(true);

            Class<?> dexPathListClass = Class.forName("dalvik.system.DexPathList");
            Field dexElementsField = dexPathListClass.getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);

            // (BaseDexClassLoader (DexPathList pathlist(Element[])))
            // 宿主的类加载器
            ClassLoader pathClassLoader = context.getClassLoader();
            // DexPathList类的对象
            Object hostPathList = pathListField.get(pathClassLoader);
            // 宿主的 dexElements
            Object[] hostDexElements = (Object[]) dexElementsField.get(hostPathList);

            // 插件的 类加载器
            ClassLoader dexClassLoader = new DexClassLoader(pathAPK, context.getCacheDir().getAbsolutePath(),
                    null, pathClassLoader);
            // DexPathList类的对象
            Object pluginPathList = pathListField.get(dexClassLoader);
            // 插件的 dexElements
            Object[] pluginDexElements = (Object[]) dexElementsField.get(pluginPathList);

            // 创建一个新数组
            Object[] newDexElements = (Object[]) Array.newInstance(Objects.requireNonNull(hostDexElements.getClass().getComponentType()),
                    hostDexElements.length + pluginDexElements.length);
            System.arraycopy(hostDexElements, 0, newDexElements, 0,hostDexElements.length);
            System.arraycopy(pluginDexElements, 0, newDexElements, hostDexElements.length, pluginDexElements.length);

            // hostDexElements = newDexElements
            dexElementsField.set(hostPathList, newDexElements);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            System.out.println("********************************PluginLoadUtils失败了");
        }
    }
}

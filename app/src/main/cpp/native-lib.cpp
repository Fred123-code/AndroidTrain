#include "android/log.h"
#include <jni.h>
#include <string>

#define TAG "JNISTUDY"
// __VA_ARGS__ 代表 ...的可变参数
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__);
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__);
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__);


#include "H264Decoder.h"
extern "C" {
#include <libavformat/avformat.h>
}

jstring getversionFromFFMPEG(JNIEnv *env) {
    return env->NewStringUTF(av_version_info());
}

jstring getconfigueFromFFMPEG(JNIEnv *env, jobject thisObj) {
    return env->NewStringUTF(avformat_configuration());
}

static const JNINativeMethod jniNativeMethod[] = {
        {"versionFromJNI",   "()Ljava/lang/String;", (jstring *) getversionFromFFMPEG},
        {"configureFromJNI", "()Ljava/lang/String;", (jstring *) getconfigueFromFFMPEG}
};
JavaVM *jVm = nullptr;
static const char *configureFragmentClassName = "com/kstudy/train/fragment/ConfigureFragment";

extern "C"
JNIEXPORT jint JNI_OnLoad(JavaVM *javaVm, void *) {
    ::jVm = javaVm;
    // 做动态注册 全部做完
    JNIEnv *jniEnv = nullptr;
    int result = javaVm->GetEnv(reinterpret_cast<void **>(&jniEnv), JNI_VERSION_1_4);
    // result 等于0 就是成功 【C库 FFmpeg 成功就是0】
    if (result != JNI_OK) {
        return -1; // 会奔溃，故意奔溃
    }
    LOGI("System.loadLibrary ---》 JNI Load init");
    jclass configureFragmentClass = jniEnv->FindClass(configureFragmentClassName);
    // jint RegisterNatives(Class, 我们的数组==jniNativeMethod， 注册的数量 = 2
    jniEnv->RegisterNatives(configureFragmentClass,
                            jniNativeMethod,
                            sizeof (jniNativeMethod) / sizeof(JNINativeMethod));
    LOGI("动态注册成功");
    return JNI_VERSION_1_4;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_kstudy_train_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_kstudy_train_fragment_H264DecoderFragment_infoH264FromJNI(JNIEnv *env, jobject thiz) {
    H264Decoder h264Decoder;
    h264Decoder.start();
    return env->NewStringUTF("");
}
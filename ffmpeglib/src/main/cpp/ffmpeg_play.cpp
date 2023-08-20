#include <jni.h>
#include "AudioChannel.h"
#include "VideoChannel.h"
#include "JNICallbakcHelper.h"
#include "SimplePlayer.h"
JavaVM *vm = 0;
jint JNI_OnLoad(JavaVM * vm, void * args) {
    ::vm = vm;
    return JNI_VERSION_1_6;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_kstudy_ffmpeglib_player_SimplePlayer_prepareNative(JNIEnv *env, jobject job,
                                                            jstring data_source) {
    const char * data_source_ = env->GetStringUTFChars(data_source, nullptr);
    auto *helper = new JNICallbakcHelper(vm, env, job); // C++子线程回调 ， C++主线程回调
    new SimplePlayer(data_source_, helper);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_kstudy_ffmpeglib_player_SimplePlayer_startNative(JNIEnv *env, jobject thiz) {
    // TODO: implement startNative()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_kstudy_ffmpeglib_player_SimplePlayer_stopNative(JNIEnv *env, jobject thiz) {
    // TODO: implement stopNative()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_kstudy_ffmpeglib_player_SimplePlayer_releaseNative(JNIEnv *env, jobject thiz) {
    // TODO: implement releaseNative()
}
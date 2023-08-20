#include <jni.h>

extern "C" {
#include "fftools/ffmpeg.h"
#include "fftools/ffprobe.h"
#include <libavutil/log.h>
#include "tools_log.h"
}


#define INPUT_SIZE (4 * 1024)
#define FFMPEG_TAG "FFmpegCmd"
#define ALOGI(TAG, FORMAT, ...) __android_log_vprint(ANDROID_LOG_INFO, TAG, FORMAT, ##__VA_ARGS__)
#define ALOGE(TAG, FORMAT, ...) __android_log_vprint(ANDROID_LOG_ERROR, TAG, FORMAT, ##__VA_ARGS__)

JNIEnv *ff_env;
jclass ff_class;
jmethodID method_progress_callback;
jmethodID method_msg_callback;

void init(JNIEnv *env) {
    ff_env = env;
    ff_class = env->FindClass("com/kstudy/ffmpeglib/FFmpegCmd");
    method_progress_callback = env->GetStaticMethodID(ff_class, "onProgressCallback", "(III)V");
    method_msg_callback = env->GetStaticMethodID(ff_class, "onMsgCallback", "(Ljava/lang/String;)V");
}

void msg_callback(const char *format, va_list args) {
    if (ff_env && method_msg_callback) {
        char * ff_msg = (char *) malloc(sizeof (char) * INPUT_SIZE);
        vsprintf(ff_msg, format, args);
        jstring jstr = ff_env->NewStringUTF(ff_msg);
        ff_env->CallStaticVoidMethod(ff_class, method_msg_callback, jstr);
        free(ff_msg);
    }
}

int err_count;
void log_callback(void *ptr, int level, const char *format, va_list args) {
    switch (level) {
        case AV_LOG_INFO:
            ALOGI(FFMPEG_TAG, format, args);
            if (format && strncmp("silence", format, strlen("silence")) == 0) {
                msg_callback(format, args);
            }
            break;
        case AV_LOG_ERROR:
            ALOGE(FFMPEG_TAG, format, args);
            if (err_count < 10) {
                err_count=0;
                msg_callback(format, args);
            }
            break;
        default:
            break;
    }

    va_list l;
    //TODO:I don't known
    char * line = static_cast<char *>(malloc(1024 * sizeof(char)));
    static int print_prefix = 1;
    va_copy(l, args);
    av_log_format_line(ptr, level, format, l, line, 1024, &print_prefix);
    va_end(l);
    line[1024] = '\0';
//    LOGE("ffmpeg", "%s", line);

    free(line);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_kstudy_ffmpeglib_FFmpegCmd_handle(JNIEnv *env, jclass clazz, jobjectArray commands) {
    init(env);
    // set the level of log
    av_log_set_level(AV_LOG_INFO);
    // set the callback of log, and redirect to print android log
    av_log_set_callback(log_callback);

    int argc = env->GetArrayLength(commands);
    char **argv = (char **) malloc(argc * sizeof(char *));
    int i;
    int result;
    for (i = 0; i < argc; i++) {
        jstring jstr = (jstring) env->GetObjectArrayElement(commands, i);
        char *temp = (char *) env->GetStringUTFChars( jstr, 0);
        argv[i] = static_cast<char *>(malloc(INPUT_SIZE));
        strcpy(argv[i], temp);
        env->ReleaseStringUTFChars(jstr, temp);
    }

    //execute ffmpeg cmd
    result = run(argc, argv);
    //release memory
    for (i = 0; i < argc; i++) {
        free(argv[i]);
    }
    free(argv);
    return result;
}

void progress_callback(int position, int duration, int state) {
    if (ff_env && ff_class && method_progress_callback) {
        ff_env->CallStaticVoidMethod(ff_class, method_progress_callback, position, duration, state);
    }
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_kstudy_ffmpeglib_FFmpegCmd_handleProbe(JNIEnv *env, jclass clazz, jobjectArray commands) {
//    // set the level of log
//    av_log_set_level(AV_LOG_INFO);
//    // set the callback of log, and redirect to print android log
//    av_log_set_callback(log_callback);
    int argc = env->GetArrayLength(commands);
    char **argv = (char **) malloc(argc * sizeof(char *));
    int i;
    for (i = 0; i < argc; i++) {
        jstring jstr = (jstring) env->GetObjectArrayElement( commands, i);
        char *temp = (char *) env->GetStringUTFChars(jstr, 0);
        argv[i] = static_cast<char *>(malloc(1024));
        strcpy(argv[i], temp);
        env->ReleaseStringUTFChars(jstr, temp);
    }
    //execute ffprobe command
    char *result = reinterpret_cast<char *>(ffprobe_run(argc, argv));
    //release memory
    for (i = 0; i < argc; i++) {
        free(argv[i]);
    }
    free(argv);
//    jstring jstr = ff_env->NewStringUTF("666666");
//    ff_env->CallStaticVoidMethod(ff_class, method_msg_callback, jstr);
    return env->NewStringUTF(result);
}
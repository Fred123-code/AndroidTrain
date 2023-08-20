//
// Created by 张大爷 on 2023/7/6.
//

#ifndef ANDROIDTRAIN_JNICALLBAKCHELPER_H
#define ANDROIDTRAIN_JNICALLBAKCHELPER_H

#include <jni.h>
#include "tools_log.h"

class JNICallbakcHelper {
private:
    JavaVM *vm = 0;
    JNIEnv *env = 0;
    jobject job;
    jmethodID jmd_prepared;
    jmethodID jmd_onError;
public:
    JNICallbakcHelper(JavaVM *vm, JNIEnv *env, jobject job);
    virtual ~JNICallbakcHelper();

    void onPrepared(int thread_mode);
    void onError(int thread_mode, int error_code);
};


#endif //ANDROIDTRAIN_JNICALLBAKCHELPER_H

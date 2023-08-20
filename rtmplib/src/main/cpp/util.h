//
// Created by 张大爷 on 2023/6/2.
//

#ifndef ANDROIDTRAIN_UTIL_H
#define ANDROIDTRAIN_UTIL_H

#include <android/log.h>

//定义释放的宏函数
#define DELETE(object) if(object){delete object; object = 0;}

//定义日志打印宏函数
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "Kstudy",__VA_ARGS__)

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "Kstudy",__VA_ARGS__)

#endif //ANDROIDTRAIN_UTIL_H

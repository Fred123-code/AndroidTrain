//
// Created by 张大爷 on 2023/7/5.
//

#ifndef ANDROIDTRAIN_SIMPLEPLAYER_H
#define ANDROIDTRAIN_SIMPLEPLAYER_H

#include <pthread.h>
#include "AudioChannel.h"
#include "VideoChannel.h"
#include "JNICallbakcHelper.h"

extern "C" {
#include <libavformat/avformat.h>
}

class SimplePlayer {
private:
    char *data_source = 0; // 指针 请赋初始值
    pthread_t pid_prepare;
    pthread_t pid_start;
    AVFormatContext *formatContext = 0; // 媒体上下文 封装格式
    AudioChannel *audio_channel = 0;
    VideoChannel *video_channel = 0;
    JNICallbakcHelper *helper = 0;
    bool isPlaying; // 是否播放
public:
    SimplePlayer(const char *data_source, JNICallbakcHelper *helper);
    ~SimplePlayer();

    void prepare();
    void prepare_();

    void start();

    void start_();
};


#endif //ANDROIDTRAIN_SIMPLEPLAYER_H

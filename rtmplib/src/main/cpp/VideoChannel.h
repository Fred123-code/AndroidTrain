//
// Created on 2023/6/2.
//

#ifndef ANDROIDTRAIN_VIDEOCHANNEL_H
#define ANDROIDTRAIN_VIDEOCHANNEL_H

#include <pthread.h>
#include <rtmp.h>
#include "util.h"
#include <x264.h>

class VideoChannel {
public:
    typedef void (*VideoPacketCallback)(RTMPPacket *packet);
private:
    pthread_mutex_t mutex;
    int mWidth; // 宽
    int mHeight; // 高
    int mFps; // 帧率
    int mBitrate; // 码率
    int y_len; // Y分量的长度
    int uv_len; // uv分量的长度

    x264_t *videoEncoder = nullptr; // x264编码器
    x264_picture_t *pic_in = nullptr; // 先理解是每一张图片 pic
    VideoPacketCallback videoPacketCallback;

    void sendSpsPps(uint8_t *sps, uint8_t *pps, int sps_len, int pps_len);
    void sendFrame(int type, int payload, uint8_t *payload1);
public:
    VideoChannel();
    ~VideoChannel();

    void initVideoEncoder(int width, int height, int fps, int bitrate);
    void encodeData(signed char *data);
    void setVideoCallback(VideoPacketCallback videoPacketCallback);
};


#endif //ANDROIDTRAIN_VIDEOCHANNEL_H

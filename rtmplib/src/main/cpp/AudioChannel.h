//
// Created by 张大爷 on 2023/6/3.
//

#ifndef ANDROIDTRAIN_AUDIOCHANNEL_H
#define ANDROIDTRAIN_AUDIOCHANNEL_H

#include <jni.h>
#include <faac.h>
#include <sys/types.h>
#include "util.h"
#include <rtmp.h>
#include <cstring>

class AudioChannel {
public:
    typedef void (*AudioPacketCallback)(RTMPPacket *packet);

private:
    u_long inputSamples;            // faac输出的样本数
    u_long maxOutputBytes;          // faac 编码器 最大能输出的字节数
    int mChannels;                  // 通道数
    faacEncHandle audioEncoder = 0; // 音频编码器
    u_char *buffer = 0; // 后面要用到的 缓冲区
    AudioPacketCallback audioPacketCallback;
public:
    AudioChannel();
    ~AudioChannel();

    void initAudioEncoder(int sample_rate, int num_channels);
    jint getInputSamples();
    void encodeData(int8_t *data);
    void setAudioCallback(AudioPacketCallback audioPacketCallback);
    RTMPPacket * getAudioSeqHeader();
};


#endif //ANDROIDTRAIN_AUDIOCHANNEL_H

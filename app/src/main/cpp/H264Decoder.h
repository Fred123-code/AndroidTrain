#ifndef NDKTRAIN_H264DECODER_H
#define NDKTRAIN_H264DECODER_H

#define TAG "KSTUDY"
// __VA_ARGS__ 代表 ...的可变参数
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__);
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__);
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__);
#include <string>
extern "C"{
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>

#include "ffmpeg_log.h"
}
/*                  封装格式数据【FLV、MP4、MKV...】
 *
 *     【AAC、MP3】音频压缩数据               视频压缩数据【H264、MPEG..】
 *
 *       【PCM...】音频采样数据               视频像素数据【YUV】
 *
 *                           音视频同步
 * */

class H264Decoder {
private:
    int width;
    int height;
    size_t yFrameSize;
    size_t uvFrameSize;
protected:
    AVFormatContext * avFormatContext;  //FFMPEG解封装（flv，mp4，rmvb，avi）功能的结构体
    AVCodecContext * avCodecContext;
    AVCodec * avCodec;
    AVPacket * avPacket;                //存储压缩编码数据相关信息的结构体
    AVFrame * avFrame;                  //用于存储原始数据（即非压缩数据，例如对视频来说是YUV，RGB，对音频来说是PCM），此外还包含了一些相关的信息
public:
    H264Decoder();

    int start();
    int stop();
};

static char * message= new char[64];
#endif //NDKTRAIN_H264DECODER_H

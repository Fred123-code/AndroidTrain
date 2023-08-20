#include "H264Decoder.h"
int H264Decoder::start() {
    const char * mp3_url= "/storage/emulated/0/Download/1111.mp3";
    const char * mp4_url= "/storage/emulated/0/Download/2222.mp4";
    const char * test_url= "/storage/emulated/0/Download/test.h264";
    avFormatContext = avformat_alloc_context();
    int result = avformat_open_input(&avFormatContext, mp4_url, nullptr, nullptr);
    if (result != 0) {
        av_strerror(result, message, 64);
        av_log(nullptr, AV_LOG_ERROR, "%s error: %d,%s", "avformat_open_input", result, message);
        return result;
    }

    result = avformat_find_stream_info(avFormatContext, nullptr);
    if (result!=0){
        av_strerror(result, message, 64);
        av_log(nullptr, AV_LOG_ERROR, "%s error: %d,%s", "avformat_find_stream_info", result, message);
        return result;
    } else {
        int start_time = avFormatContext->streams[0]->start_time;
        std::string info = "************************";
        info.append("start_time=");
        info.append(std::to_string(start_time));
        LOGI("%s",info.c_str());
    }

    avCodec = avcodec_find_decoder(AV_CODEC_ID_H264);
    avCodecContext = avcodec_alloc_context3(avCodec);
    result = avcodec_open2(avCodecContext, avCodec, nullptr);
    if (result!=0){
        av_strerror(result, message, 64);
        av_log(nullptr, AV_LOG_ERROR, "%s error: %d,%s", "avcodec_open2", result, message);
        return result;
    }

    avPacket = av_packet_alloc();
    av_init_packet(avPacket);
    avFrame = av_frame_alloc();

    width = avFormatContext->streams[0]->codecpar->width;
    height = avFormatContext->streams[0]->codecpar->height;
    yFrameSize = (size_t) (width * height);
    uvFrameSize = uvFrameSize>>2;   //YUV420
    av_log(nullptr,AV_LOG_DEBUG,"w=%d,h=%d,yframe=%d,uvframe=%d",width,height,yFrameSize,uvFrameSize);
    av_log(nullptr,AV_LOG_DEBUG,"start success");
    return 0;
}

int H264Decoder::stop() {
    return 0;
}

H264Decoder::H264Decoder() {
    av_register_all();
    av_log_set_callback(callback_report);
}

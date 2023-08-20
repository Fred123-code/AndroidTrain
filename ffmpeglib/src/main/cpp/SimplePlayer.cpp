//
// Created by 张大爷 on 2023/7/5.
//

#include "SimplePlayer.h"

void *task_prepare(void *args) {
    auto *player =  static_cast<SimplePlayer *>(args);
    player->prepare_();
    return 0;
}

void *task_start(void *args) {
    auto *player = static_cast<SimplePlayer *>(args);
    player->start_();
    return 0;
}

SimplePlayer::SimplePlayer(const char *data_source, JNICallbakcHelper *helper) {
    this->data_source = new char[strlen(data_source) + 1];
    strcpy(this->data_source, data_source);

    this->helper = helper;
}

SimplePlayer::~SimplePlayer() {
    if (data_source) {
        delete data_source;
    }

    if (helper) {
        delete helper;
    }
}

void SimplePlayer::prepare() {
    pthread_create(&pid_prepare, 0, task_prepare, this);
}

void SimplePlayer::prepare_() {
    // 1.打开媒体地址（文件路径， 直播地址rtmp）
    formatContext = avformat_alloc_context();

    AVDictionary *dictionary = 0;
    av_dict_set(&dictionary, "timeout", "5000000", 0); // 单位微妙

    int r = avformat_open_input(&formatContext, data_source, 0, &dictionary);

    av_dict_free(&dictionary);

    if (r) {
        if (helper) {
            helper->onError(THREAD_CHILD, FFMPEG_CAN_NOT_OPEN_URL);

            // char * errorInfo = av_err2str(r); // 根据你的返回值 得到错误详情
        }
        return;
    }
    //  2.查找媒体中的音视频流的信息
    r = avformat_find_stream_info(formatContext, 0);
    if (r < 0) {
        if (helper) {
            helper->onError(THREAD_CHILD, FFMPEG_CAN_NOT_FIND_STREAMS);
        }
        return;
    }

    // 3.根据流信息，流的个数，用循环来找
    for (int i = 0; i < formatContext->nb_streams; ++i) {
        AVStream *stream = formatContext->streams[i];
        AVCodecParameters *parameters = stream->codecpar;

        /**
         * 获取编解码器
         */
        AVCodec *codec = avcodec_find_decoder(parameters->codec_id);
        if (!codec) {
            // TODO 第二节课新增
            if (helper) {
                helper->onError(THREAD_CHILD, FFMPEG_FIND_DECODER_FAIL);
            }
        }

        /**
         * 编解码器 上下文 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
         */
        AVCodecContext *codecContext = avcodec_alloc_context3(codec);
        if (!codecContext) {
            if (helper) {
                helper->onError(THREAD_CHILD, FFMPEG_ALLOC_CODEC_CONTEXT_FAIL);
            }
            return;
        }

        /**
         * parameters copy codecContext
         */
        r = avcodec_parameters_to_context(codecContext, parameters);
        if (r < 0) {
            if (helper) {
                helper->onError(THREAD_CHILD, FFMPEG_CODEC_CONTEXT_PARAMETERS_FAIL);
            }
            return;
        }

        /**
         * 打开解码器
         */
        r = avcodec_open2(codecContext, codec, 0);
        if (r) {
            if (helper) {
                helper->onError(THREAD_CHILD, FFMPEG_OPEN_DECODER_FAIL);
            }
            return;
        }

        /**
         * 从编解码器参数中，获取流的类型 codec_type  ===  音频 视频
         */
        if (parameters->codec_type == AVMediaType::AVMEDIA_TYPE_AUDIO) { // 音频
            audio_channel = new AudioChannel(i, codecContext);
        } else if (parameters->codec_type == AVMediaType::AVMEDIA_TYPE_VIDEO) { // 视频
            video_channel = new VideoChannel(i, codecContext);
        }
    }

    /**
    * 4.其他异常判断
    */
    if (!audio_channel && !video_channel) {
        // TODO 第二节课新增
        if (helper) {
            helper->onError(THREAD_CHILD, FFMPEG_NOMEDIA);
        }
        return;
    }

    /**
     * 5.准备成功
     */
//    if (helper) {
//        helper->onPrepared(THREAD_CHILD);
//    }
}

void SimplePlayer::start() {
    isPlaying = 1;

    if (video_channel) {
        video_channel->start();
    }

    if (audio_channel) {
       audio_channel->start();
    }

    pthread_create(&pid_start, 0, task_start, this);
}

void SimplePlayer::start_() {
    while (isPlaying) {
        AVPacket * packet = av_packet_alloc();
        int ret = av_read_frame(formatContext, packet);
        if (!ret) {
            if (video_channel && video_channel->stream_index == packet->stream_index) {
                // 代表是视频
                video_channel->packets.insertToQueue(packet);
            } else if (audio_channel && audio_channel->stream_index == packet->stream_index) {
                // 代表是音频
                // audio_channel->packets.insertToQueue(packet);
            }
        } else if (ret == AVERROR_EOF) { //   end of file == 读到文件末尾了 == AVERROR_EOF

        } else { // av_read_frame 出现了错误，结束当前循环
            break;
        }
    }

    isPlaying = 0;
    video_channel->stop();
    audio_channel->stop();
}

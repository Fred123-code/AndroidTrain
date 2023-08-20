#include "VideoChannel.h"

VideoChannel::VideoChannel(int stream_index, AVCodecContext *codecContext)
        : BaseChannel(stream_index, codecContext) {

}

VideoChannel::~VideoChannel() {

}

void VideoChannel::stop() {

}

void *task_video_decode(void *args) {
    auto *video_channel = static_cast<VideoChannel *>(args);
    video_channel->video_decode();
    return 0;
}

void *task_video_play(void *args) {
    auto *video_channel = static_cast<VideoChannel *>(args);
    video_channel->video_play();
    return 0;
}

void VideoChannel::start() {
    isPlaying = 1;

    // 队列开始工作了
    packets.setWork(1);
    frames.setWork(1);

    // 第一个线程： 视频：取出队列的压缩包 进行编码 编码后的原始包 再push队列中去
    pthread_create(&pid_video_decode, 0, task_video_decode, this);

    // 第二线线程：视频：从队列取出原始包，播放
    pthread_create(&pid_video_play, 0, task_video_play, this);
}

void VideoChannel::video_decode() {
    AVPacket *pkt = 0;
    while (isPlaying) {
        int ret = packets.getQueueAndDel(pkt); // 阻塞式函数
        if (!isPlaying) {
            break; // 如果关闭了播放，跳出循环，releaseAVPacket(&pkt);
        }

        if (!ret) { // ret == 0
            continue; // 哪怕是没有成功，也要继续（假设：你生产太慢(压缩包加入队列)，我消费就等一下你）
        }

        // 最新的FFmpeg，和旧版本差别很大， 新版本：1.发送pkt（压缩包）给缓冲区，  2.从缓冲区拿出来（原始包）
        ret = avcodec_send_packet(codecContext, pkt);

        // FFmpeg源码缓存一份pkt，大胆释放即可
        releaseAVPacket(&pkt);

        if (ret) {
            break; // avcodec_send_packet 出现了错误
        }

        // 下面是从 缓冲区 获取 原始包
        AVFrame *frame = av_frame_alloc();
        ret = avcodec_receive_frame(codecContext, frame);
        if (ret == AVERROR(EAGAIN)) {
            // B帧  B帧参考前面成功  B帧参考后面失败   可能是P帧没有出来，再拿一次就行了
            continue;
        } else if (ret != 0) {
            break; // 错误了
        }
        // 重要拿到了 原始包
        frames.insertToQueue(frame);
    } // end while
    releaseAVPacket(&pkt);
}

void VideoChannel::video_play() { // 第二线线程：视频：从队列取出原始包，播放 【真正干活了】

    // SWS_FAST_BILINEAR == 很快 可能会模糊
    // SWS_BILINEAR 适中算法

    AVFrame *frame = 0;
    uint8_t *dst_data[4]; // RGBA
    int dst_linesize[4]; // RGBA
    // 原始包（YUV数据）  ---->[libswscale]   Android屏幕（RGBA数据）

    //给 dst_data 申请内存   width * height * 4 xxxx
    av_image_alloc(dst_data, dst_linesize,
                   codecContext->width, codecContext->height, AV_PIX_FMT_RGBA, 1);

    SwsContext *sws_ctx = sws_getContext(
            // 下面是输入环节
            codecContext->width,
            codecContext->height,
            codecContext->pix_fmt, // 自动获取 xxx.mp4 的像素格式  AV_PIX_FMT_YUV420P // 写死的

            // 下面是输出环节
            codecContext->width,
            codecContext->height,
            AV_PIX_FMT_RGBA,
            SWS_BILINEAR, NULL, NULL, NULL);


    while (isPlaying) {
        int ret = frames.getQueueAndDel(frame);
        if (!isPlaying) {
            break; // 如果关闭了播放，跳出循环，releaseAVPacket(&pkt);
        }
        if (!ret) { // ret == 0
            continue; // 哪怕是没有成功，也要继续（假设：你生产太慢(原始包加入队列)，我消费就等一下你）
        }

        // 格式转换 yuv ---> rgba
        sws_scale(sws_ctx,
                // 下面是输入环节 YUV的数据
                  frame->data, frame->linesize,
                  0, codecContext->height,

                // 下面是输出环节  成果：RGBA数据
                  dst_data,
                  dst_linesize
        );

        // ANatvieWindows 渲染工作  1   2下节课
        // SurfaceView ----- ANatvieWindows
    }
}

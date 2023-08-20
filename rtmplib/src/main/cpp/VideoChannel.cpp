//
// Created by 张大爷 on 2023/6/2.
//


#include <cstring>
#include "VideoChannel.h"

VideoChannel::VideoChannel() {
    pthread_mutex_init(&mutex, 0);
}

VideoChannel::~VideoChannel() {
    pthread_mutex_destroy(&mutex);
}

void VideoChannel::initVideoEncoder(int width, int height, int fps, int bitrate) {
    pthread_mutex_lock(&mutex);

    mWidth = width;
    mHeight = height;
    mFps = fps;
    mBitrate = bitrate;
    //y_len固定公式width * height
    y_len = width * height;
    uv_len = y_len / 4;

    if (videoEncoder) {
        x264_encoder_close(videoEncoder);
        videoEncoder = nullptr;
    }

    if (pic_in) {
        x264_picture_clean(pic_in);
        DELETE(pic_in);
    }

    // 初始化x264编码器
    x264_param_t param; // x264的参数集
    // 设置编码器属性
    x264_param_default_preset(&param, "ultrafast"/*最快*/, "zerolatency"/*零延迟*/);
    // 编码规格
    param.i_level_idc = 32; // 3.2 中等偏上的规格  自动用 码率，模糊程度，分辨率
    // 输入数据格式是 YUV420P
    param.i_csp = X264_CSP_I420;
    param.i_width = width;
    param.i_height = height;
    // 不能有B帧，如果有B帧会影响编码、解码效率（快）
    param.i_bframe = 0; //0:false 1:true
    // 码率控制方式。CQP(恒定质量)，CRF(恒定码率)，ABR(平均码率)
    param.rc.i_rc_method = X264_RC_CRF;
    // 设置码率,别太大
    param.rc.i_bitrate = bitrate / 1000;
    // 设置了i_vbv_max_bitrate就必须设置buffer大小，码率控制区大小，单位Kb/s
    param.rc.i_vbv_buffer_size = bitrate / 1000;
    param.rc.i_vbv_max_bitrate = bitrate / 1000 * 1.2;      // 瞬时最大码率 网络波动导致的
    // 码率控制 ，而是通过 fps 来控制码率
    param.b_vfr_input = 0;
    // 分子 分母
    // 帧率分子
    param.i_fps_num = fps;
    // 帧率分母
    param.i_fps_den = 1;
    param.i_timebase_den = param.i_fps_num;
    param.i_timebase_num = param.i_fps_den;
    // 帧距离(关键帧)  2s一个关键帧，不过x264内部有兜底
    param.i_keyint_max = fps * 2;
    // sps序列参数   pps图像参数集，所以需要设置header(sps pps)
    // 是否复制sps和pps放在每个关键帧的前面 该参数设置是让每个关键帧(I帧)都附带sps/pps
    param.b_repeat_headers = 1;
    // 并行编码线程数
    param.i_threads = 1;
    // profile=baseline级别,并上面的参数进行提交
    x264_param_apply_profile(&param, "baseline");
    // 输入图像初始化
    pic_in = new x264_picture_t; // 本身空间的初始化
    x264_picture_alloc(pic_in, param.i_csp/*x264内部初始化*/, param.i_width, param.i_height);

    // 打开编码器
    videoEncoder = x264_encoder_open(&param);
    if (videoEncoder) {
        LOGE("x264编码器打开成功");
    }

    pthread_mutex_unlock(&mutex);
}

/*
 * 计算整个 SPS 和 PPS 数据的大小
        数据示例 :
                                 17 00 00 00 00
        0x00000192	:   01 64 00 32 FF E1 00 19
        0x0000019a	:   67 64 00 32 AC D9 80 78
        0x000001a2	:   02 27 E5 84 00 00 03 00
        0x000001aa	:   04 00 00 1F 40 3C 60 C6
        0x000001b2	:   68 01 00 05 68 E9 7B 2C
        0x000001ba	:   8B 00 00 00 39

        17 帧类型, 1 字节
        00 数据类型, 1 字节
        00 00 00 合成时间, 3 字节
        01 版本信息, 1 字节
        64 00 32 编码规则, 3 字节
        FF NALU 长度, 1 字节
        E1 SPS 个数, 1 字节
        00 19 SPS 长度, 2 字节

        截止到当前位置有 13 字节数据

        spsLen 字节数据, 这里是 25 字节

                        67 64 00 32 AC D9 80 78
        0x000001a2	:   02 27 E5 84 00 00 03 00
        0x000001aa	:   04 00 00 1F 40 3C 60 C6
        0x000001b2	:   68

        01 PPS 个数, 1 字节
        00 05 PPS 长度, 2 字节

        ppsLen 字节的 PPS 数据
                                    68 E9 7B 2C
        0x000001ba	:   8B

        后面的 00 00 00 39 是视频标签的总长度
        这里再 RTMP 标签中可以不用封装
 * */
void VideoChannel::encodeData(signed char *data) {
    pthread_mutex_lock(&mutex);

    // 把nv21的y分量 Copy i420的y分量
    memcpy(pic_in->img.plane[0], data, y_len);
    // 把nv21的vuvuvuvu 转化成 i420的 uuuuvvvv
    for (int i = 0; i < uv_len; ++i) {
        *(pic_in->img.plane[1] + i) = *(data + y_len + 2 * i +1);   // u 数据
        *(pic_in->img.plane[2] + i) = *(data + y_len + 2 * i);      // u 数据
    }

    x264_nal_t *nal = nullptr;  // 通过H.264编码得到NAL数组
    int pi_nal;                 // pi_nal是nal中输出的NAL单元的数量
    x264_picture_t pic_out;     // 输出编码后图片

    int ret = x264_encoder_encode(videoEncoder, &nal, &pi_nal, pic_in, &pic_out);
    if (ret < 0) {
        LOGE("x264编码失败");
        //一旦编码失败了，一定要解锁，否则有概率性造成死锁了
        pthread_mutex_unlock(&mutex);
        return;
    }

    int sps_len, pps_len;       // sps 和 pps 的长度
    uint8_t sps[100];           // 用于接收 sps 的数组定义
    uint8_t pps[100];           // 用于接收 pps 的数组定义
    pic_in->i_pts += 1;         // 累加 pts显示的时间,dts解码的时间

    for (int i = 0; i < pi_nal; ++i) {
        // AVC 序列头 : 如果是 SPS PPS 数据帧 , 可以判定分隔符就是 00 00 00 01 四字节
        // H.264 视频帧 : 对于视频数据帧 , 不确定当前的 H.264 数据的分隔符是 00 00 00 01 还是 00 00 01 , 需要开发者进行判定
        // 判定方法 : 根据 第 2 位 的值判定
        if (nal[i].i_type == NAL_SPS) {
            sps_len = nal[i].i_payload - 4;
            memcpy(sps, nal[i].p_payload + 4, sps_len);
        } else if (nal[i].i_type == NAL_PPS) {
            pps_len = nal[i].i_payload - 4;
            memcpy(pps, nal[i].p_payload + 4, pps_len);

            // sps + pps
            sendSpsPps(sps, pps, sps_len, pps_len);
        } else {
            // 由于已经关闭了B帧，仅发送I帧和P帧即可
            sendFrame(nal[i].i_type, nal[i].i_payload/*编码后的数据长度*/, nal[i].p_payload/* 编码后的数据*/);
        }
    }

    pthread_mutex_unlock(&mutex);
}

void VideoChannel::sendSpsPps(uint8_t *sps, uint8_t *pps, int sps_len, int pps_len) {
    // 封装头:1帧类型,3数据类型,1合成时间,1版本信息,3编码规则,1NALU长度,总共有10个字节
    // 封装SPS数据:1SPS个数,2SPS长度,SPS数据,分别有1+2+SPSLen
    // 封装PPS数据:1PPS个数,2PPS长度,PPS数据,分别有1+2+PPSLen
    int rtmpPackagesize = 10 + 3 + sps_len + 3 + pps_len;

    RTMPPacket * packet = new RTMPPacket;

    RTMPPacket_Alloc(packet, rtmpPackagesize); // 堆区实例化 RTMPPacket

    int i = 0;
    // 帧类型数据 : 分为两部分;
    // 前 4 位表示帧类型, 1 表示关键帧, 2 表示普通帧
    // 后 4 位表示编码类型, 7 表示 AVC 视频编码
    packet->m_body[i++] = 0x17;
    // 数据类型, 00 表示 AVC 序列头
    packet->m_body[i++] = 0x00;
    // 合成时间, 一般设置 00 00 00
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    // 版本信息
    packet->m_body[i++] = 0x01;
    // 编码规格
    packet->m_body[i++] = sps[1];
    packet->m_body[i++] = sps[2];
    packet->m_body[i++] = sps[3];
    // NALU 长度
    packet->m_body[i++] = 0xFF;

    // SPS 个数
    packet->m_body[i++] = 0xE1;
    // SPS 长度, 占 2 字节
    packet->m_body[i++] = (sps_len >> 8) & 0xFF;    // 设置长度的高位
    packet->m_body[i++] = sps_len & 0xFF;           // 设置长度的低位
    memcpy(&packet->m_body[i], sps, sps_len);       // sps拷贝
    i += sps_len;                                   // 拷贝完sps数据, i移位

    // PPS 个数
    packet->m_body[i++] = 0x01;
    // PPS 数据的长度, 占 2 字节
    packet->m_body[i++] = (pps_len >> 8) & 0xFF;    // 设置长度的高位
    packet->m_body[i++] = (pps_len) & 0xFF;         // 设置长度的低位
    memcpy(&packet->m_body[i], pps, pps_len);       // 拷贝 SPS 数据
    i += pps_len;                                   // 拷贝完pps数据, i移位

    // 封包处理
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;  // 包类型 视频包
    packet->m_nBodySize = rtmpPackagesize;          // 设置好 sps+pps的总大小
    packet->m_nChannel = 10;                        // 通道ID，随便写一个，注意：不要写的和rtmp.c(里面的m_nChannel有冲突 4301行)
    packet->m_nTimeStamp = 0;                       // sps pps 包 没有时间戳
    packet->m_hasAbsTimestamp = 0;                  // 时间戳绝对或相对,对于 SPS PPS 赋值 0 即可
    packet->m_headerType = RTMP_PACKET_SIZE_MEDIUM; // 包的类型：数据量比较少，不像帧数据(那就很大了)，所以设置中等大小的包

    // packet 存入队列
    videoPacketCallback(packet);
}

void VideoChannel::sendFrame(int type, int payload, uint8_t *pPayload) {
    if (pPayload[2] == 0x00){
        // 00 00 00 01
        // 要将 x264 编码出的数据个数减去 4, 只统计实际的数据帧个数
        pPayload += 4;
        // 从 x264 编码后的数据向外拿数据时, 越过开始的 00 00 00 01 数据
        payload -= 4;
    }else if(pPayload[2] == 0x01){
        // 00 00 01
        // 要将 x264 编码出的数据个数减去 3, 只统计实际的数据帧个数
        pPayload +=3;
        // 从 x264 编码后的数据向外拿数据时, 越过开始的 00 00 01 数据
        payload -= 3;
    }
    // RTMP 协议中 H.264 数据帧格式
    // 1帧类型[关键帧 17, 非关键帧 27] + 1包类型[1表示数据帧(关键帧/非关键帧), 0表示AVC序列头数据] + 3合成时间 + 4数据长度 = 9字节
    int rtmpPackagesize = 9 + payload;

    RTMPPacket *packet = new RTMPPacket;

    RTMPPacket_Alloc(packet, rtmpPackagesize);

    // 区分关键帧 和 非关键帧
    packet->m_body[0] = 0x27; // 普通帧 非关键帧
    if(type == NAL_SLICE_IDR){
        packet->m_body[0] = 0x17; // 关键帧
        LOGE("I帧");
    }
    // 数据类型
    packet->m_body[1] = 0x01;
    // 合成时间, 一般设置 00 00 00
    packet->m_body[2] = 0x00;
    packet->m_body[3] = 0x00;
    packet->m_body[4] = 0x00;
    // 帧数据的长度
    packet->m_body[5] = (payload >> 24) & 0xFF;
    packet->m_body[6] = (payload >> 16) & 0xFF;
    packet->m_body[7] = (payload >> 8) & 0xFF;
    packet->m_body[8] = payload & 0xFF;

    memcpy(&packet->m_body[9], pPayload, payload);  // 拷贝H264的裸数据

    // 封包处理
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;      // 包类型 视频包
    packet->m_nBodySize = rtmpPackagesize;              // 设置好 关键帧 或 普通帧 的总大小
    packet->m_nChannel = 10;                            // 通道ID，随便写一个，注意：不要写的和rtmp.c(里面的m_nChannel有冲突 4301行)
    packet->m_nTimeStamp = -1;                          // sps pps 包 没有时间戳
    packet->m_hasAbsTimestamp = 0;                      // 时间戳绝对或相对 也没有时间搓
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE ;     // 包的类型：若是关键帧的话，数据量比较大，所以设置大包

    // 把最终的 帧类型 RTMPPacket 存入队列
    videoPacketCallback(packet);

}

void VideoChannel::setVideoCallback(VideoChannel::VideoPacketCallback videoPacketCallback) {
    this->videoPacketCallback = videoPacketCallback;
}

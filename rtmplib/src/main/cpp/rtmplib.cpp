#include <jni.h>
#include <string>
#include <rtmp.h>
#include "AudioChannel.h"
#include "VideoChannel.h"
#include "safe_queue.h"
bool isStart;
pthread_t pid_start;
uint32_t start_time;
bool readyPushing;
SafeQueue<RTMPPacket *> packets; // 不区分音频和视频，（音频 & 视频）一直存储，  start直接拿出去发送给流媒体服务器（添加到队列中的是压缩包）
VideoChannel *videoChannel = nullptr;
AudioChannel *audioChannel = nullptr;

void releasePackets(RTMPPacket *& packet) {
    if (packet) {
        RTMPPacket_Free(packet);
        delete packet;
        packet = nullptr;
    }
}

void videoPacketCallback(RTMPPacket *packet) {
    if (packet) {
        if (packet->m_nTimeStamp == -1) {
            packet->m_nTimeStamp = RTMP_GetTime() - start_time; // 如果是sps+pps 没有时间搓，如果是I帧就需要有时间搓
        }
        packets.push(packet); // 把压缩包 存入队列里面
    }
}

void audioPacketCallback(RTMPPacket *packet) {
    if (packet) {
        if (packet->m_nTimeStamp == -1) {
            packet->m_nTimeStamp = RTMP_GetTime() - start_time; // 如果是sps+pps 没有时间搓，如果是I帧就需要有时间搓
        }
        packets.push(packet); // 把压缩包 存入队列里面
    }
}

void * run_task_start(void * args) {
    char *url = static_cast<char *>(args);
    LOGE("%s", url);

    RTMP *rtmp = nullptr;
    int ret;
    do {// 为了方便流程控制
        // 开辟 rmtp
        rtmp = RTMP_Alloc();
        if (!rtmp) {
            LOGE("rtmp 初始化失败");
            break;
        }
        // 2 rtmp 初始化
        RTMP_Init(rtmp);
        rtmp->Link.timeout = 5; //以秒为单位的连接超时
        // 3，rtmp 设置流媒体地址
        ret = RTMP_SetupURL(rtmp, url);
        if (!ret) { // FFmpeg 0 就是成功      RTMP 0 就是失败
            LOGE("rtmp 初始化失败");
            break;
        }
        // 4. 开启输出模式
        RTMP_EnableWrite(rtmp);
        // 5  建立连接
        ret = RTMP_Connect(rtmp, 0);
        if (!ret) { // FFmpeg 0 就是成功      RTMP 0 就是失败
            LOGE("rtmp 连接建立失败:%d, url:%s", ret, url);
            break;
        }
        // 6 连接流
        ret = RTMP_ConnectStream(rtmp, 0);
        if (!ret) { // FFmpeg 0 就是成功      RTMP 0 就是失败
            LOGE("rtmp 连接流失败");
            break;
        }

        start_time = RTMP_GetTime();

        readyPushing = true;

        // 队列开始工作
        packets.setWork(1);
        // TODO 测试是不用发送序列头信息，是没有任何问题的，但是还是规矩
        // 一定要在开启work之后
        audioPacketCallback(audioChannel->getAudioSeqHeader());
        // 压缩后的包，通过x264高级视频编码，编码后的包
        RTMPPacket *packet = nullptr;

        while (readyPushing) {
            packets.pop(packet);
            if (!isStart) {
                break;
            }
            // 如果队列是空的，会一直阻塞在这里
            if (!packet) {
                continue;
            }

            // 给rtmp的流 ID
            packet->m_nInfoField2 = rtmp->m_stream_id;
            // 成功取出数据包，发送
            ret = RTMP_SendPacket(rtmp, packet, 1);

            // packet已经发给服务器了，就可以大胆释放packet
            releasePackets(packet);

            if (!ret) { // FFmpeg 0 就是成功      RTMP 0 就是失败
                LOGE("rtmp 发送包 失败 自动断开服务器");
                break;
            }
        }

        releasePackets(packet);
    } while (false);

    isStart = false;
    readyPushing = false;
    packets.setWork(0);
    packets.clear();

    if (rtmp) {
        RTMP_Close(rtmp); // 先关闭
        RTMP_Free(rtmp); // 再释放，如果直接释放，可能是释放失败
    }

    delete url;

    return nullptr;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_kstudy_rtmplib_RTMPMainPusher_native_1pushVideo(JNIEnv *env, jobject thiz, jbyteArray data_) {
    // data == nv21数据  编码  加入队列
    if (!videoChannel || !readyPushing) { return; }

    // data == 相机 nv21 交错模式数据
    jbyte * data = env->GetByteArrayElements(data_, nullptr);
    if (videoChannel) {
        // 1.把原始数据编码成 压缩后的数据
        // 2.把压缩后的RTMPPacket 加入队列
        videoChannel->encodeData(data);
    }
    env->ReleaseByteArrayElements(data_, data, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_kstudy_rtmplib_RTMPMainPusher_native_1initVideoEncoder(JNIEnv *env, jobject thiz,
                                                                jint width, jint height, jint fps,
                                                                jint bitrate) {
    if (videoChannel) {
        videoChannel->initVideoEncoder(width, height, fps, bitrate);
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_kstudy_rtmplib_RTMPMainPusher_native_1init(JNIEnv *env, jobject thiz) {
    videoChannel = new VideoChannel();
    audioChannel = new AudioChannel();
    videoChannel->setVideoCallback(videoPacketCallback);
    audioChannel->setAudioCallback(audioPacketCallback);
    packets.setReleaseCallback(releasePackets);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_kstudy_rtmplib_RTMPMainPusher_native_1start(JNIEnv *env, jobject thiz, jstring path_) {
    // 子线程 1.连接流媒体服务器     2.发包
    if (isStart) {
        return;
    }

    isStart = true;

    const char * path = env->GetStringUTFChars(path_, 0);
    //深拷贝
    char * url = new char[strlen(path) + 1]; // delete url;
    strcpy(url, path);

    pthread_create(&pid_start, 0, run_task_start, url);

    env->ReleaseStringUTFChars(path_, path);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_kstudy_rtmplib_RTMPMainPusher_native_1stop(JNIEnv *env, jobject thiz) {
    isStart = false;
    readyPushing = false;
    packets.setWork(0); // 队列不准工作
    pthread_join(pid_start, nullptr); // 稳稳等待start线程执行完成后，我在做后面的处理工作
}
extern "C"
JNIEXPORT void JNICALL
Java_com_kstudy_rtmplib_RTMPMainPusher_native_1release(JNIEnv *env, jobject thiz) {
    DELETE(videoChannel);
    DELETE(audioChannel);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_kstudy_rtmplib_RTMPMainPusher_native_1initAudioEncoder(JNIEnv *env, jobject thiz,
                                                                jint sample_rate,
                                                                jint num_channels) {
    if (audioChannel) {
        audioChannel->initAudioEncoder(sample_rate, num_channels);
    }
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_kstudy_rtmplib_RTMPMainPusher_native_1getInputSamples(JNIEnv *env, jobject thiz) {
    if (audioChannel) {
        return audioChannel->getInputSamples();
    }
    return 0;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_kstudy_rtmplib_RTMPMainPusher_native_1pushAudio(JNIEnv *env, jobject thiz,
                                                         jbyteArray bytes) {
    if (!audioChannel || !readyPushing) {
        return;
    }

    jbyte *data = env->GetByteArrayElements(bytes, nullptr);
    audioChannel->encodeData(data); // 核心函数：对音频数据 【进行faac的编码工作】
    env->ReleaseByteArrayElements(bytes, data, 0);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_kstudy_rtmplib_NativeLib_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
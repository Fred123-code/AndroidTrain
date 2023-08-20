#include "AudioChannel.h"


AudioChannel::AudioChannel(int stream_index, AVCodecContext *codecContext)
:BaseChannel(stream_index, codecContext)
{

}

AudioChannel::~AudioChannel() {

}

void AudioChannel::stop() {

}

void AudioChannel::start() {

}


// 第一个线程： 音频：取出队列的压缩包 进行编码 编码后的原始包 再push队列中去

// 第二线线程：音频：从队列取出原始包，播放



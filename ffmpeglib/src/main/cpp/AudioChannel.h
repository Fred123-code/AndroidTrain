#ifndef DERRYPLAYER_AUDIOCHANNEL_H
#define DERRYPLAYER_AUDIOCHANNEL_H


#include "BaseChannel.h"

class AudioChannel : public BaseChannel {

public:
    AudioChannel(int stream_index, AVCodecContext *codecContext);

    virtual ~AudioChannel();

    void stop();

    void start();
};


#endif //DERRYPLAYER_AUDIOCHANNEL_H

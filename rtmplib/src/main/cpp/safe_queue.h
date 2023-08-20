//
// Created by 张大爷 on 2023/6/2.
//

#ifndef ANDROIDTRAIN_SAFE_QUEUE_H
#define ANDROIDTRAIN_SAFE_QUEUE_H
#include <queue>
#include <pthread.h>

using namespace std;

template<typename T>
class SafeQueue {
    typedef void (*ReleaseCallback)(T &);

private:
    queue<T> q;
    pthread_mutex_t mutex;
    pthread_cond_t cond;
    int work; // 标记队列是否工作
    ReleaseCallback releaseCallback;
public:
    SafeQueue() {
        pthread_mutex_init(&mutex, 0); // 动态初始化互斥锁
        pthread_cond_init(&cond, 0);
    }

    ~SafeQueue() {
        pthread_mutex_destroy(&mutex);
        pthread_cond_destroy(&cond);
    }

    void setReleaseCallback(ReleaseCallback releaseCallback) {
        this->releaseCallback = releaseCallback;
    }

    /**
     * 设置队列的工作状态
     * @param work
     */
    void setWork(int work) {
        pthread_mutex_lock(&mutex);
        this->work = work;
        pthread_cond_signal(&cond);
        pthread_mutex_unlock(&mutex);
    }

    /**
     * 判断队列是否为空
     * @return
     */
    int empty() {
        return q.empty();
    }

    /**
     * 获取队列大小
     * @return
     */
    int size() {
        return q.size();
    }

    /**
     * 清空队列 队列中的元素如何释放？ 让外界释放
     */
    void clear() {
        pthread_mutex_lock(&mutex);
        unsigned int size = q.size();
        for (int i = 0; i < size; ++i) {
            T value = q.front();
            if (releaseCallback) {
                releaseCallback(value);
            }
            q.pop();
        }
        pthread_mutex_unlock(&mutex);
    }

    void push(T value) {
        pthread_mutex_lock(&mutex);
        if (work) {
            // 工作状态需要push
            q.push(value);
            pthread_cond_signal(&cond);
        } else {
            // 非工作状态
            if (releaseCallback) {
                releaseCallback(value); // T无法释放， 让外界释放
            }
        }
        pthread_mutex_unlock(&mutex);
    }

    int pop(T &value) {
        int ret = 0;
        pthread_mutex_lock(&mutex);
        while (work && q.empty()) {
            //工作状态，说明确实需要pop，但是队列为空，需要等待
            pthread_cond_wait(&cond, &mutex);
        }
        if (!q.empty()) {
            value = q.front();
            //弹出
            q.pop();
            ret = 1;
        }
        pthread_mutex_unlock(&mutex);
        return ret;
    }
};
#endif //ANDROIDTRAIN_SAFE_QUEUE_H

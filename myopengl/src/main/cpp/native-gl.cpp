#include <jni.h>


extern "C"
JNIEXPORT jlong JNICALL
Java_com_kstudy_myopengl_face_FaceTrack_native_1create(JNIEnv *env, jobject thiz, jstring model_,
                                                       jstring seeta_) {

}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_kstudy_myopengl_face_FaceTrack_native_1detector(JNIEnv *env, jobject thiz, jlong self,
                                                         jbyteArray data, jint camera_id,
                                                         jint width, jint height) {

}
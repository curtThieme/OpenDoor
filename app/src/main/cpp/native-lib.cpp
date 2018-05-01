#include <jni.h>
#include <string>

extern "C"
jstring
Java_com_example_curt_opendoor_OpenDoor_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "";
    return env->NewStringUTF(hello.c_str());
}

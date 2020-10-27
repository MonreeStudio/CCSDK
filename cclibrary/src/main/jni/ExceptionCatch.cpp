
#include <jni.h>
#include <iostream>
#include <android/log.h>
#define LOG_TAG "JNILog"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#include <signal.h>
#include <setjmp.h>
#include <pthread.h>

// 定义代码跳转锚点
sigjmp_buf JUMP_ANCHOR;
volatile sig_atomic_t error_cnt = 0;
JavaVM *jvm;

void exception_handler(int errorCode){
    JNIEnv *mEnv;
    jint rs = jvm->AttachCurrentThread(&mEnv, nullptr);
    assert (rs == JNI_OK);

    error_cnt += 1;
//    LOGE("JNI_ERROR, error code %d, cnt %d", errorCode, error_cnt);

    // DO SOME CLEAN STAFF HERE...
    if (mEnv && mEnv->ExceptionCheck()) {  // 检查JNI调用是否有引发异常
        LOGE("检测到异常", errorCode, error_cnt);
        mEnv->ExceptionDescribe();
//        LOGE("上面为异常描述", errorCode, error_cnt);
        jthrowable jthrowable = mEnv->ExceptionOccurred();
        if(jthrowable == nullptr)
            LOGE("异常为空", errorCode, error_cnt);
        mEnv->ExceptionClear();        // 清除引发的异常，在Java层不会打印异常的堆栈信息
        mEnv->Throw(jthrowable);
        LOGE("向外抛出异常", errorCode, error_cnt);
//        jvm->DetachCurrentThread();
        // jump to main function to do exception process
//        mEnv->ThrowNew(mEnv->FindClass("java/lang/Exception"), "哦豁");
        siglongjmp(JUMP_ANCHOR, 0);
//    } else {
//            if(mEnv == nullptr) {
//                LOGE("mEnv为空哦", errorCode, error_cnt);
//            }
//            else {
//                LOGE("莫得办法了", errorCode, error_cnt);
//                jthrowable jthrowable = mEnv->ExceptionOccurred();
//                if(jthrowable == nullptr) {
//                    LOGE("异常为空", errorCode, error_cnt);
////                    mEnv->ThrowNew(mEnv->FindClass("java/lang/Exception"), "哦豁");
////                    siglongjmp(JUMP_ANCHOR, 0);
//                    return;
//                } else {
//                    LOGE("异常不为空", errorCode, error_cnt);
//                }
////                jvm->DetachCurrentThread();
//                siglongjmp(JUMP_ANCHOR, 0);
//            }
    }

    jvm->DetachCurrentThread();
    // jump to main function to do exception process
    siglongjmp(JUMP_ANCHOR, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_ssjj_cclibrary_JNI_init(JNIEnv *env, jclass clazz) {
    jint rs = env->GetJavaVM(&jvm);
    assert(rs == JNI_OK);

    // 代码跳转锚点
    if (sigsetjmp(JUMP_ANCHOR, 1) != 0) {
        //        return -1;
        return;
    }

    // 注册要捕捉的系统信号量
    struct sigaction sigact{};
    struct sigaction old_action{};
    sigaction(SIGABRT, nullptr, &old_action);
    if (old_action.sa_handler != SIG_IGN) {
        sigset_t block_mask;
        sigemptyset(&block_mask);
        sigaddset(&block_mask, SIGABRT); // handler处理捕捉到的信号量时，需要阻塞的信号
        sigaddset(&block_mask, SIGSEGV); // handler处理捕捉到的信号量时，需要阻塞的信号

        sigemptyset(&sigact.sa_mask);
        sigact.sa_flags = 0;
        sigact.sa_mask = block_mask;
        sigact.sa_handler = exception_handler;
        sigaction(SIGABRT, &sigact, nullptr); // 注册要捕捉的信号
        sigaction(SIGSEGV, &sigact, nullptr); // 注册要捕捉的信号
        LOGE("信号量检测");
    }
}
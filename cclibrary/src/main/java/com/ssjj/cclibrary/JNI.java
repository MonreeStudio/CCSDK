package com.ssjj.cclibrary;

public class JNI {
    public static native void init();

    static {
        System.loadLibrary("jni_exceptionCatch");
    }
}

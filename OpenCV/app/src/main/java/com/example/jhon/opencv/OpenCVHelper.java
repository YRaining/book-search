package com.example.jhon.opencv;

/**
 * Created by Jhon on 2016/1/2.
 */
public class OpenCVHelper {
    static {
        System.loadLibrary("OpenCV");
    }
    public static native String gray(int[] buf, int w, int h);
}

package com.research.glrecoder;

import android.util.Log;
import android.view.Surface;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * Created by weiersyuan on 2016/11/17.
 */

public class WindowSurface {

    private static final String TAG = "WindowSurface";
    private EGLDisplay mEglDisplay;

    /**
     * two Surface use the same Context
     * also the same EGLConfig
     */
    private EGLContext mEglContext;
    private EGLConfig mEglConfig;
    /**
     * related to native surface
     */
    private EGLSurface mWindowSurface;

    /**
     * related to the surface on which wo encode
     */
    public EGLSurface mEncoderSurface;

    private EGL10 mEgl;

    public WindowSurface(EGLConfig eglConfig, Surface encodeSurface) {
        mEglConfig = eglConfig;
        mEgl= (EGL10) EGLContext.getEGL();
        mEglContext = mEgl.eglGetCurrentContext();
        mEglDisplay = mEgl.eglGetCurrentDisplay();
        mWindowSurface = mEgl.eglGetCurrentSurface(EGL10.EGL_DRAW);
        mEncoderSurface = createWindowSurface(encodeSurface);
        Log.i(TAG, "eglCreateWindowSurface:" + mEgl.eglGetError());
    }

    public EGLSurface createWindowSurface(Surface surface) {
        int [] attrs = {EGL10.EGL_NONE};
        return mEgl.eglCreateWindowSurface(mEglDisplay, mEglConfig, surface, attrs);
    }

    public void makeWindowSurfaceCurrent() {
        mEgl.eglMakeCurrent(mEglDisplay, mWindowSurface, mWindowSurface, mEglContext);
    }

    public  void makeEncoderSurfaceCurrent() {
        mEgl.eglMakeCurrent(mEglDisplay,mEncoderSurface, mEncoderSurface, mEglContext);
    }

    public void swapEncoderSurfaceBuffer() {
        if (mEgl.eglSwapBuffers(mEglDisplay, mEncoderSurface)) {
            Log.i(TAG, "eglSwapBuffers:" + mEgl.eglGetError());
        }
    }

    public void releaseEncoderSurface() {
        mEgl.eglDestroySurface(mEglDisplay, mEncoderSurface);
    }

}

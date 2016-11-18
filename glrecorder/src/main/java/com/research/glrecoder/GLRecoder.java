package com.research.glrecoder;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Created by weiersyuan on 2016/11/17.
 */

public class GLRecoder {
    private static Object obj = new Object();
    private static final int EGL_RECORDABLE_ANDROID = 0x3142;
    private static float[] mIdentityMatrix;
    private static final String TAG = "GLRecoder";
    private static ScreenRectangle mScreenRectangle;

    private static int mWidth;
    private static int mHeight;

    private static WindowSurface mWindowSurface;

    private static EGLConfig mEglConfig;

    private static VideoThread mVideoThread;

    private static  boolean isRecord;


    // A simple EGL config chooser for get recordable config.
    private static GLSurfaceView.EGLConfigChooser mDefaultConfigChooser = new GLSurfaceView.EGLConfigChooser() {
        @Override
        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            int renderableType = 4;

            // The actual surface is generally RGBA or RGBX, so situationally omitting alpha
            // doesn't really help.  It can also lead to a huge performance hit on glReadPixels()
            // when reading into a GL_RGBA buffer.
            int[] attribList = {
                    EGL11.EGL_RED_SIZE, 8,
                    EGL11.EGL_GREEN_SIZE, 8,
                    EGL11.EGL_BLUE_SIZE, 8,
                    EGL11.EGL_ALPHA_SIZE, 8,
                    //EGL11.EGL_DEPTH_SIZE, 16,
                    //EGL11.EGL_STENCIL_SIZE, 8,
                    EGL11.EGL_RENDERABLE_TYPE, renderableType,
                    EGL_RECORDABLE_ANDROID, 1,      // set recordable [@-3]
                    EGL11.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfigs = new int[1];
            if (!egl.eglChooseConfig(display, attribList, configs, configs.length,
                    numConfigs)) {
                return null;
            }
            return configs[0];
        }
    };
    private static int mTick;

    public static GLSurfaceView.EGLConfigChooser getEGLConfigChooser() {
        return mDefaultConfigChooser;
    }

    public static void init(int width, int height, EGLConfig eglConfig) {
        mWidth = width;
        mHeight = height;
        mEglConfig = eglConfig;
        mIdentityMatrix = new float[16];
        Matrix.setIdentityM(mIdentityMatrix, 0);
        mScreenRectangle = new ScreenRectangle(width, height);
    }


    public static void startEncoder (File file) throws IOException {
        int BIT_RATE = 4000000;
        mVideoThread = new VideoThread("video thread");
        mVideoThread.start();
        mVideoThread.init(mWidth, mHeight,BIT_RATE, file);
        mWindowSurface = new WindowSurface(mEglConfig, mVideoThread.getInputSurface());
        isRecord = true;
    }

    public static void stopEncoder() {
        mVideoThread.stopRecording();
        mVideoThread = null;
        mWindowSurface.releaseEncoderSurface();
    }

    public static void beginDraw() {
        if (mTick % 2 == 0) return;
        // bind the framebuffer to off-screen
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mScreenRectangle.getmFrameBufferID());
        GLUtil.checkGLError("beginDraw glBindFramebuffer");
        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.e(TAG, "glCheckFramebufferStatus error");
        }
    }

    public static void endDraw() {
        Log.i(TAG, "enter endDraw");
        // bind the framebuffer to the screen, also the default display
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLUtil.checkGLError("endDraw glBindFramebuffer");
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        // draw to the default surface
        mScreenRectangle.draw(mIdentityMatrix);

        // draw to encoder
        if (isRecord) {
            // record half
            if (mTick % 2 == 0) {
                ++mTick;
                return;
            }
            ++mTick;

            mVideoThread.frameAvailableSoon();
            mWindowSurface.makeEncoderSurfaceCurrent();
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            mScreenRectangle.draw(mIdentityMatrix);
            mWindowSurface.swapEncoderSurfaceBuffer();
            mWindowSurface.makeWindowSurfaceCurrent();
        }
    }

}

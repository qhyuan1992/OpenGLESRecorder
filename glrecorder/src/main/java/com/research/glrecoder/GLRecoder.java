package com.research.glrecoder;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created by weiersyuan on 2016/11/17.
 */

public class GLRecoder {

    private static float[] mIdentityMatrix;
    private static final String TAG = "GLRecoder";
    private static ScreenRectangle mScreenRectangle;

    private static int mWidth;
    private static int mHeight;


    public static void init(int width, int height, EGLConfig eglConfig) {
        mIdentityMatrix = new float[16];
        Matrix.setIdentityM(mIdentityMatrix, 0);
        mScreenRectangle = new ScreenRectangle(width, height);

    }
    public static void beginDraw() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mScreenRectangle.getmFrameBufferID());
        GLUtil.checkGLError("beginDraw glBindFramebuffer");
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mScreenRectangle.getmOffScreenTextureID(), 0);
        GLUtil.checkGLError("beginDraw glFramebufferTexture2D");
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, mScreenRectangle.getmRenderBufferID());
        GLUtil.checkGLError("beginDraw glFramebufferRenderbuffer");

        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.e(TAG, "glCheckFramebufferStatus error");
        }
        GLUtil.checkGLError("beginDraw glBindFramebuffer");
    }

    public static void endDraw() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLUtil.checkGLError("endDraw glBindFramebuffer");
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        mScreenRectangle.draw(mIdentityMatrix);

    }

}

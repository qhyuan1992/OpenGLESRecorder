package com.research.glrecoder;

import android.opengl.GLES20;
import java.nio.FloatBuffer;

/**
 * Created by weiersyuan on 2016/11/17.
 */

public class ScreenRectangle {

    private static final float SCREEN_RECTANGLE_COORDS[] = {
            -1,-1,
            1,-1,
            -1, 1,
            1, 1
    };

    private static final float SCREEN_RECTANGLE_TEXTURE_COORDS[] = {
            0, 1,
            1, 1,
            0, 0,
            1, 0
    };

    private  static final FloatBuffer SCREEN_RECTANGLE_VERTEX_BUFFER = GLUtil.createFloatBuffer(SCREEN_RECTANGLE_COORDS);

    private  static final FloatBuffer SCREEN_RECTANGLE_TEXTURE_BUFFER = GLUtil.createFloatBuffer(SCREEN_RECTANGLE_TEXTURE_COORDS);

    private int program;

    private int mFrameBufferID;
    int mOffScreenTexture;

    int mRenderBuffer;
    private int mUMVPMatrixHandle;
    private int mAPosition;
    private int mATextureCoord;


    public ScreenRectangle(int width, int height) {
        initGL();
        createTexture(width, height);
        createRenderBuffer(width, height);
        createFrameBuffer();
    }

    public int getmFrameBufferID() {
        return mFrameBufferID;
    }

    private void initGL() {
        program = GLUtil.createProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);
        GLUtil.checkGLError("createProgram");
        mUMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        mAPosition = GLES20.glGetAttribLocation(program, "aPosition");
        mATextureCoord = GLES20.glGetAttribLocation(program, "aTextureCoord");
    }
    
    public void draw(float [] mvpMatrix) {
        GLES20.glUseProgram(program);
        GLUtil.checkGLError("useProgram");
        GLES20.glUniformMatrix4fv(mUMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLUtil.checkGLError("glUniformMatrix4fv");
        GLES20.glVertexAttribPointer(mAPosition, 2, GLES20.GL_FLOAT, false, 0, SCREEN_RECTANGLE_VERTEX_BUFFER);
        GLES20.glVertexAttribPointer(mATextureCoord, 2, GLES20.GL_FLOAT, false, 0, SCREEN_RECTANGLE_TEXTURE_BUFFER);
        GLES20.glEnableVertexAttribArray(mAPosition);
        GLES20.glEnableVertexAttribArray(mATextureCoord);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mOffScreenTexture);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLUtil.checkGLError("glDrawArrays");

        GLES20.glDisableVertexAttribArray(mAPosition);
        GLES20.glDisableVertexAttribArray(mATextureCoord);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glUseProgram(0);
    }

    private void createFrameBuffer() {
        int [] framebuffers = new int[1];
        GLES20.glGenFramebuffers(1, framebuffers, 0);
        int framebufferId = framebuffers[0];
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebufferId);
        GLUtil.checkGLError("glBindFramebuffer");
        mFrameBufferID = framebufferId;
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mOffScreenTexture, 0);
        GLUtil.checkGLError("beginDraw glFramebufferTexture2D");
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, mRenderBuffer);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    private void createRenderBuffer(int width, int height) {
        int [] renderbuffers = new int[1];
        GLES20.glGenRenderbuffers(1, renderbuffers, 0);
        GLUtil.checkGLError("genRenderBuffer");
        int renderId = renderbuffers[0];
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderId);
        GLUtil.checkGLError("bindRenderBuffer");
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);
        mRenderBuffer = renderId;
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
    }

    private void createTexture(int width, int height) {
        int [] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLUtil.checkGLError("genTexture");
        int textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLUtil.checkGLError("bindTexture");
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, width, height, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_MIRRORED_REPEAT);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_MIRRORED_REPEAT);
        GLUtil.checkGLError("setTextureParams");
        mOffScreenTexture = textureId;
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    private static final String VERTEX_SHADER_CODE =
                    "uniform mat4 uMVPMatrix;" +
                    "attribute vec2 aPosition;" +
                    "attribute vec2 aTextureCoord;" +
                    "varying vec2 vTextureCoord;" +
                    "void main() {" +
                    "gl_Position = uMVPMatrix * vec4(aPosition, 0, 1);" +
                    "vTextureCoord = aTextureCoord;" +
                    "}";

    private static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
            "varying vec2 vTextureCoord;" +
            "uniform sampler2D uTexture;" +
            "void main() {" +
            "gl_FragColor = texture2D(uTexture, vTextureCoord);" +
            "}";

}

package com.research.glrecoder;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by weiersyuan on 2016/11/17.
 */

public class GLUtil {

    private static final String TAG = "GLUtil";
    private static int SIZE_OF_FLOAT = 4;


    public static int createProgram(String vertexShaderCode, String fragmentShaderCode) {
        int program = GLES20.glCreateProgram();
        if (0 == program) {
            Log.e(TAG, "create program error");
            return 0;
        }
        int vertexShaderID = compileVertexShader(vertexShaderCode);
        int fragmentShaderID = compileFragmentShader(fragmentShaderCode);
        GLES20.glAttachShader(program, vertexShaderID);
        GLES20.glAttachShader(program, fragmentShaderID);
        GLES20.glLinkProgram(program);
        int [] linkStatus = new int [1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (0 == linkStatus[0]) {
            GLES20.glDeleteProgram(program);
            Log.e(TAG, "link program error");
            return 0;
        }
        return program;
    }

    private static int compileVertexShader(String vertexShaderCode) {
        return compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
    }

    private static int compileFragmentShader(String fragmentShaderCode) {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
    }

    private static int compileShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        if (shader == 0) {
            Log.e(TAG, "create shader error");
        }
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        int [] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader,GLES20.GL_COMPILE_STATUS,compileStatus, 0);
        if (0 == compileStatus[0]) {
            GLES20.glDeleteShader(shader);
            Log.e(TAG, "compile shader error");
            return 0;
        }
        return shader;
    }


    public static FloatBuffer createFloatBuffer(float[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * SIZE_OF_FLOAT);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(array);
        fb.position(0);
        return fb;
    }

    public static void checkGLError (String operation) {
        int errerCode = GLES20.glGetError();
        if (errerCode != GLES20.GL_NO_ERROR) {
            String msg = operation + ":error" + errerCode;
            Log.e(TAG, msg);
            throw new RuntimeException(msg);
        }
    }

}

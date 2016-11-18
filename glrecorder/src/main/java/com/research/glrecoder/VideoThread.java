package com.research.glrecoder;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.io.IOException;

/**
 * Created by weiersyuan on 2016/11/17.
 */

public class VideoThread extends HandlerThread{

    private static final int MSG_STOP_RECORDING = 1;
    private static final int MSG_FRAME_AVAILABLE = 2;
    private static final String TAG = "VideoThread";

    private VideoEncoderCore mVideoEncoder;
    private Handler mHandler;

    public VideoThread(String name) {
        super(name);
    }
    public void init(int width, int height, int bitRate, File file) throws IOException {
        mVideoEncoder = new VideoEncoderCore(width, height, bitRate, file);
        mHandler = new Handler(this.getLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                int what = inputMessage.what;
                switch (what) {
                    case MSG_STOP_RECORDING:
                        handleStopRecording();
                        Looper.myLooper().quit();
                        break;
                    case MSG_FRAME_AVAILABLE:
                        Log.i(TAG, "enter Frame Coming");
                        handleFrameAvailable();
                        break;
                    default:
                        throw new RuntimeException("Unhandled msg what=" + what);

                }
            }
        };
    }

    public Surface getInputSurface() {
        return mVideoEncoder.getInputSurface();
    }

    /**
     * Handles notification of an available frame.
     */
    private void handleFrameAvailable() {
        mVideoEncoder.drainEncoder(false);
    }

    /**
     * Handles a request to stop encoding.
     */
    private void handleStopRecording() {
        mVideoEncoder.drainEncoder(true);
        mVideoEncoder.release();
    }
    /**
     * Tells the video recorder to stop recording.  (Call from non-encoder thread.)
     * <p>
     * Returns immediately; the encoder/muxer may not yet be finished creating the movie.
     * <p>
     * TODO: have the encoder thread invoke a callback on the UI thread just before it shuts down
     * so we can provide reasonable status UI (and let the caller know that movie encoding
     * has completed).
     */
    public void stopRecording() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_STOP_RECORDING));
        // We don't know when these will actually finish (or even start).  We don't want to
        // delay the UI thread though, so we return immediately.
    }

    /**
     * Tells the video recorder that a new frame is arriving soon.  (Call from non-encoder thread.)
     * <p>
     * This function sends a message and returns immediately.  This is fine -- the purpose is
     * to wake the encoder thread up to do work so the producer side doesn't block.
     */
    public void frameAvailableSoon() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_FRAME_AVAILABLE));
    }
}

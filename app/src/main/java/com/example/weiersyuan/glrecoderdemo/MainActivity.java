package com.example.weiersyuan.glrecoderdemo;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.research.glrecoder.GLRecoder;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ShapeView mGLView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new ShapeView(this);
        setContentView(mGLView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    @Override
    protected void onDestroy() {
        GLRecoder.stopEncoder();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }
}

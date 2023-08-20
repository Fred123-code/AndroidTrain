package com.kstudy.train.fragment;

import android.hardware.Camera;

import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kstudy.rtmplib.RTMPMainPusher;
import com.kstudy.train.R;
import com.kstudy.train.base.BaseFragment;
import com.kstudy.train.databinding.Fragmet6Binding;


public class RTMPFragment extends BaseFragment<Fragmet6Binding> implements View.OnClickListener {
    private RTMPMainPusher mPusher;
    private final String url_host = "rtmp://sendtc3a.douyu.com/live/";
    private final String url_var = "5462474rwzJRttCZ?wsSecret=e5b93682a0c018dab8f782ecb834bcb9&wsTime=647e8df4&wsSeek=off&wm=0&tw=0&roirecognition=0&record=flv&origin=tct&txHost=sendtc3.douyu.com";
    private final String url1 = url_host + url_var;
    private final String url2 = "rtmp://192.168.88.1:1935/live/test1";
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btn1.setOnClickListener(this::onClick);
        binding.btn2.setOnClickListener(this::onClick);
        binding.btn3.setOnClickListener(this::onClick);
        mPusher = new RTMPMainPusher(getActivity(), Camera.CameraInfo.CAMERA_FACING_BACK, 640, 480, 25, 800000);
        SurfaceHolder surfaceHolder = binding.surfaceView1.getHolder();
        mPusher.setPreviewDisplay(surfaceHolder);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn1:
                mPusher.switchCamera();
                break;
            case R.id.btn2:
                mPusher.startLive(url2);
                break;
            case R.id.btn3:
                mPusher.stopLive();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPusher.release();
    }
}

package com.kstudy.train.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kstudy.ffmpeglib.FFmpegCmd;
import com.kstudy.ffmpeglib.utils.FFmpegCmdUtils;
import com.kstudy.train.base.BaseFragment;
import com.kstudy.train.databinding.Fragmet4Binding;

public class FFmpegCMDFragment extends BaseFragment<Fragmet4Binding> {
    private static final int MESSAGE_FFMPEG = 0;
    private static final int MESSAGE_FFPROBE = 1;

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_FFMPEG:
                    binding.content.setText(msg.getData().getString("msg"));
                    break;
                case MESSAGE_FFPROBE:
                    System.out.println("***********" + msg.getData().getString("msg"));
                    binding.content.setText(msg.getData().getString("msg"));
                    break;
            }
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnFfmpeg.setOnClickListener(v -> {
            String pathDir = Environment.getExternalStorageDirectory().getPath() + "/Download/";
            System.out.println(pathDir);
            String[] commands = {
                    "ffmpeg",
                    "-i",
                    pathDir + "2222.mp4",
                    "-ss",
                    "00:00:02",
                    "-t",
                    "00:00:40",
                    "-c",
                    "copy",
                    pathDir + "out.mp4"
            };

            FFmpegCmd.execute(commands, new FFmpegCmd.FFmpegCmdListener() {
                @Override
                public void onMessage(String msg) {
                    System.out.println("***********");
                    Message message = Message.obtain();
                    message.what = MESSAGE_FFMPEG;
                    Bundle bundle = message.getData();
                    bundle.putString("msg",msg);
                    handler.sendMessage(message);
                }

                @Override
                public void onSuccess(int result) {

                }
            });
        });

        binding.btnFfprobe.setOnClickListener(v -> {
            String pathDir = Environment.getExternalStorageDirectory().getPath() + "/Download/";
            String[] cmd = FFmpegCmdUtils.probeFormat(pathDir + "2222.mp4");
            FFmpegCmd.executeProbe(cmd, new FFmpegCmd.FFmpegCmdListener() {
                @Override
                public void onSuccess(int result) {

                }

                @Override
                public void onMessage(String msg) {
                    System.out.println("***********" + msg);
                    Message message = Message.obtain();
                    message.what = MESSAGE_FFPROBE;
                    Bundle bundle = message.getData();
                    bundle.putString("msg",msg);
                    handler.sendMessage(message);
                }
            });
        });
    }


}

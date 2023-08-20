package com.kstudy.train.fragment;

import static com.kstudy.common.Contants.storage_url_mp4;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kstudy.common.utils.ThreadPoolUtil;
import com.kstudy.ffmpeglib.player.SimplePlayer;
import com.kstudy.train.base.BaseFragment;
import com.kstudy.train.databinding.Fragmet7Binding;

import java.io.File;

public class FFmpegPlayerFragment extends BaseFragment<Fragmet7Binding> {
    private SimplePlayer player;
    private TextView tv_state;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        player = new SimplePlayer();
        player.setDataSource(storage_url_mp4);
        player.setOnPreparedListener(new SimplePlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
               binding.tvState.post(new Runnable() {
                   @Override
                   public void run() {
                       tv_state.setTextColor(Color.GREEN); // 绿色
                       tv_state.setText("恭喜init初始化成功");
                   }
               });
                player.start();
            }
        });

        player.setOnErrorListener(new SimplePlayer.OnErrorListener() {
            @Override
            public void onError(String errorCode) {
                binding.tvState.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_state.setTextColor(Color.RED); // 红色
                        tv_state.setText("哎呀,错误啦，错误:" + errorCode);
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        player.prepare();
    }

    @Override
    public void onPause() {
        super.onPause();
        player.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.release();
    }
}

package com.kstudy.train.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kstudy.train.base.BaseFragment;
import com.kstudy.train.databinding.Fragmet2Binding;

public class H264DecoderFragment extends BaseFragment<Fragmet2Binding> {
    public native String infoH264FromJNI();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.textDecoder264.setText(String.format("66=%s",infoH264FromJNI()));
    }
}

package com.kstudy.train.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kstudy.train.base.BaseFragment;
import com.kstudy.train.databinding.Fragmet1Binding;

public class ConfigureFragment extends BaseFragment<Fragmet1Binding> {
    public native String versionFromJNI();
    public native String configureFromJNI();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.textVersion.setText(String.format("Version=%s", versionFromJNI()));
        binding.textConfig.setText(String.format("Configure=%s", configureFromJNI()));
    }
}

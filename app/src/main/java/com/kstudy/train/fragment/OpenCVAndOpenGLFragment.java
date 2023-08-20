package com.kstudy.train.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kstudy.train.R;
import com.kstudy.train.base.BaseFragment;
import com.kstudy.train.databinding.Fragmet5Binding;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class OpenCVAndOpenGLFragment extends BaseFragment<Fragmet5Binding> implements View.OnClickListener {
    private Bitmap mBitmap_baidu;
    private Mat src_baidu = new Mat();
    private Mat dst_baidu = new Mat();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btn1.setOnClickListener(this::onClick);
        binding.btn2.setOnClickListener(this::onClick);
        binding.btnGray.setOnClickListener(this::onClick);
        binding.btnXorMat.setOnClickListener(this::onClick);
        binding.btnThreshold.setOnClickListener(this::onClick);
        binding.ll1SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                threshold(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_gray:
                binding.ll1SeekBar.setVisibility(View.GONE);
                gray();
                break;
            case R.id.btn_xorMat:
                binding.ll1SeekBar.setVisibility(View.GONE);
                xorMat();
                break;
            case R.id.btn_threshold:
                binding.ll1SeekBar.setVisibility(View.VISIBLE);
                threshold(125);
                break;
            default:
                binding.ll1SeekBar.setVisibility(View.GONE);
                updateUI(id);
                break;
        }
    }

    private void updateUI(int btnId) {
        binding.ll1Title.setVisibility(btnId == R.id.btn1 ? View.VISIBLE : View.GONE);
        binding.ll2Title.setVisibility(btnId == R.id.btn2 ? View.VISIBLE : View.GONE);
        binding.ll1Content.setVisibility(btnId == R.id.btn1 ? View.VISIBLE : View.GONE);
        binding.ll2Content.setVisibility(btnId == R.id.btn2 ? View.VISIBLE : View.GONE);
    }

    private void gray() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.img);
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2GRAY);  //二值化
        Utils.matToBitmap(dst, bitmap);                         //把mat转换回bitmap
        binding.ll1ImgNew.setImageBitmap(bitmap);
        src.release();
        dst.release();
    }

    private void xorMat() {
        Mat mat1 = null;
        Mat mat2 = null;
        try {
            mat1 = Utils.loadResource(getContext(), R.mipmap.img);
            mat2 = Utils.loadResource(getContext(), R.mipmap.img);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Mat dst = new Mat();
        Core.bitwise_xor(mat1, mat2, dst);
        //转换回Bitmap
        Bitmap newBitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, newBitmap);
        binding.ll1ImgNew.setImageBitmap(newBitmap);
        mat1.release();
        mat2.release();
        dst.release();
    }

    public void threshold(double threshold) {
        if (mBitmap_baidu==null)  {
            mBitmap_baidu = BitmapFactory.decodeResource(getResources(), R.mipmap.img);
            Utils.bitmapToMat(mBitmap_baidu, src_baidu);
            Imgproc.cvtColor(src_baidu, dst_baidu, Imgproc.COLOR_RGB2GRAY);         //先把源文转为灰度图
        }
        //阈值
        Imgproc.threshold(src_baidu, dst_baidu, threshold, 255, Imgproc.THRESH_BINARY);
        Utils.matToBitmap(dst_baidu, mBitmap_baidu);
        binding.ll1ImgNew.setImageBitmap(mBitmap_baidu);
    }
}

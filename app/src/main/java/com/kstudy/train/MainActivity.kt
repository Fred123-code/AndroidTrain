package com.kstudy.train

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.kstudy.train.databinding.ActivityMainBinding
import com.kstudy.train.fragment.*

@Route(path = "/app/MainActivity")
class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var mCurrentFragment: Fragment? = null
    private lateinit var mConfigureFragment: ConfigureFragment
    private lateinit var mH264DecoderFragment: H264DecoderFragment
    private lateinit var mMediaCodecFragment: MediaCodecFragment
    private lateinit var mFFmpegCMDFragment: FFmpegCMDFragment
    private lateinit var mOpenCVAndOpenGLFragment: OpenCVAndOpenGLFragment
    private lateinit var mRTMPFragment: RTMPFragment
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener(this::onClick)
        binding.btn1.setOnClickListener(this::onClick)
        binding.btn2.setOnClickListener(this::onClick)
        binding.btn3.setOnClickListener(this::onClick)
        binding.btn4.setOnClickListener(this::onClick)
        binding.btn5.setOnClickListener(this::onClick)
        binding.btn6.setOnClickListener(this::onClick)
        binding.btn7.setOnClickListener(this::onClick)
    }

    /**
     * A native method that is implemented by the 'train' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'train' library on application startup.
        init {
            System.loadLibrary("train")
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_back -> {
                if (mCurrentFragment != null) {
                    replaceFragment(null)
                    mCurrentFragment = null
                }
            }

            R.id.btn1 -> {
                mConfigureFragment = ConfigureFragment()
                mCurrentFragment = mConfigureFragment
                replaceFragment(mCurrentFragment)
            }

            R.id.btn2 -> {
                mH264DecoderFragment = H264DecoderFragment()
                mCurrentFragment = mH264DecoderFragment
                replaceFragment(mCurrentFragment)
            }

            R.id.btn3 -> {
                mMediaCodecFragment = MediaCodecFragment()
                mCurrentFragment = mMediaCodecFragment
                replaceFragment(mCurrentFragment)
            }

            R.id.btn4 -> {
                mFFmpegCMDFragment = FFmpegCMDFragment()
                mCurrentFragment = mFFmpegCMDFragment
                replaceFragment(mCurrentFragment)
            }

            R.id.btn5 -> {
                mOpenCVAndOpenGLFragment = OpenCVAndOpenGLFragment()
                mCurrentFragment = mOpenCVAndOpenGLFragment
                replaceFragment(mCurrentFragment)
            }

            R.id.btn6 -> {
                mRTMPFragment = RTMPFragment()
                mCurrentFragment = mRTMPFragment
                replaceFragment(mCurrentFragment)
            }

            R.id.btn7 -> {
                ARouter.getInstance()
                    .build("/appplugin/MainActivity")
                    .navigation();
            }
        }
    }

    fun replaceFragment(fragment: Fragment?) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (fragment == null) {
            transaction.remove(mCurrentFragment!!)
        } else {
            transaction.replace(R.id.fragemt_temp, fragment)
        }
        transaction.commit()
    }


}
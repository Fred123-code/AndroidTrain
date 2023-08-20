package com.kstudy.carapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import com.kstudy.carapp.monitor.MonitorNetActivity
import com.kstudy.carapp.screen.ScreenMainActivity
import com.kstudy.www.ASMWorker
import com.kstudy.www.BuildConfig
import java.io.File
import java.io.FileOutputStream

class CarMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)


        AsyncLayoutInflater(this).inflate(R.layout.activity_main,null) { view, _, _ ->
            setContentView(view)
            findViewById<Button>(R.id.btn_test1).setOnClickListener(View.OnClickListener { Thread.sleep(8000) })
            findViewById<Button>(R.id.btn_test2).setOnClickListener(View.OnClickListener { Toast.makeText(this,"22222",
                Toast.LENGTH_SHORT).show() })
            findViewById<Button>(R.id.btn_same_screen).setOnClickListener(View.OnClickListener { startActivity(
                Intent(this, ScreenMainActivity::class.java)
            ) })
            findViewById<Button>(R.id.btn_monitor_net).setOnClickListener(View.OnClickListener { startActivity(
                Intent(this, MonitorNetActivity::class.java)
            ) })
        }

        if (BuildConfig.DEBUG) {
            val file = File("/storage/emulated/0/a.txt")
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(1)
            fileOutputStream.close()

            Toast.makeText(this,"已经打印除严格模式错误", Toast.LENGTH_LONG).show()

            ASMWorker().method1()
        }
    }
}
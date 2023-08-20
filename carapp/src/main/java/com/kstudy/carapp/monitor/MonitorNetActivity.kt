package com.kstudy.carapp.monitor

import android.net.TrafficStats
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.kstudy.carapp.R
import com.kstudy.monitor.traffic.TrafficCheckHelper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class MonitorNetActivity : AppCompatActivity() {



    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.monitor_activity_main)

        val instance = TrafficCheckHelper.getInstance()
        instance.start(this)

        var mCurrentStats: Long = 0

        Thread{
            while (true) {
                Thread.sleep(1000)
                instance.getTrafficInfoActivity(this).apply {
                    println("""$time $activityName $trafficCost""")
                }
                instance.updateTrafficRX(this)
            }
        }

        findViewById<Button>(R.id.btn_test_traffic).setOnClickListener {
            val url11 = "https://dldir1.qq.com/weixin/android/weixin8018android2060_arm64.apk"
            val url22 = "https://www.baidu.com"
            Thread {
                downloadAPkByHttpUrlConnection(url11)
            }.start() }
    }

    fun downloadAPkByHttpUrlConnection(urlString: String) {
        val url = URL(urlString)
        val connection= url.openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val file = File(filesDir, "test.apk")
                println("#########################"+filesDir)
                val fileOutputStream = FileOutputStream(file)
                val data = ByteArray(1024)
                var length: Int
                while (inputStream.read(data).also { length = it } != -1) {
                    fileOutputStream.write(data, 0, length)
                }
                inputStream.close()
                fileOutputStream.close()
                connection.disconnect()

                runOnUiThread {
                    Toast.makeText(this, "Download finished", Toast.LENGTH_LONG).show()
                    println("Download finished")
                }

                val delete = file.delete()
                if (delete) {
                    runOnUiThread {
                        Toast.makeText(this, "download finish, delete the file", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this, "Download failed: Response code ${connection.responseCode}", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(this, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } finally {
            connection.disconnect()
        }

    }
}
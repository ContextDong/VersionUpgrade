package com.cherry.upgrade.download

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-4
 */

class DownloadService : Service() {

    override fun onCreate() {
        super.onCreate()
        isStart = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, DownloadEngine.getNotification(this).build())
        }
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        //开始执行更新任务
        DownloadEngine.downloadAPK(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        isStart = false
        DownloadEngine.cancel()
    }


    companion object {

        var isStart = false
            private set

        fun start(context: Context) {
            val intent = Intent(context, DownloadService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                /**
                 * 在系统创建服务后，应用有5秒的时间来调用该服务的 startForeground() 方法以显示新服务的用户可见通知。
                 * 如果应用在此时间限制内未调用 startForeground()，则系统将停止服务并声明此应用为 ANR。
                 * 解决方案：使用startForegroundService启动服务后，在service的onCreate方法中调用startForeground()
                 */
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            if (context is DownloadService) {
                context.stopSelf()
            } else {
                context.stopService(Intent(context, DownloadService::class.java))
            }
        }

    }

}



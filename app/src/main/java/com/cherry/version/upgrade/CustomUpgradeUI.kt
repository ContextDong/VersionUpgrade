package com.cherry.version.upgrade

import android.app.Notification
import android.content.Context
import android.os.Handler
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.cherry.upgrade.download.DownloadListener
import com.cherry.upgrade.ui.AbstractUpgradeUI
import com.cherry.upgrade.ui.IBeforeCheckCallback
import com.cherry.upgrade.ui.INotification

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-11
 */

class CustomUpgradeUI(override val context: Context) : IBeforeCheckCallback, AbstractUpgradeUI(),
        DownloadListener, INotification {

    private val dialog by lazy {
        UpgradeDialog()
    }


    override fun beforeCheck(context: Context) {
        Toast.makeText(context, "正在检查更新", Toast.LENGTH_SHORT).show()
    }

    override fun noHasVersion() {
        Toast.makeText(context, "没有新版本了", Toast.LENGTH_SHORT).show()
    }

    override fun createUpgradeUI(forceUpgrade: Boolean) {
        //nothing
    }

    override fun showUpgradeUI() {
        dialog.show((context as AppCompatActivity).supportFragmentManager, "download_dialog")
        dialog.setUserOptionCallback(callback)
    }

    override fun hideUpgradeUI() {
        Handler().postDelayed({
            dialog.dismiss()
        }, 2000)
    }

    override fun onDownloadStart(apkFileSavePath: String) {
        //nothing
    }

    override fun onDownloading(progress: Int) {
        dialog.progress(progress)
    }

    override fun onDownloadSuccess(file: String) {
        Log.e("==========MainFragment", "下载成功了:$file")
        Toast.makeText(context, "下载成功了", Toast.LENGTH_SHORT).show()
        dialog.success()
    }

    override fun onDownloadFail(message: String) {
        Log.e("==========MainFragment", "下载失败$message")
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        dialog.fail()
    }

    override fun onStartSetupNotification(builder: NotificationCompat.Builder): Notification {
        return builder.setContentTitle("新版本来了")
                .setContentText("此版本修复了好多Bug")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setWhen(System.currentTimeMillis())
                .build()
    }

    override fun onProgressNotification(progress: Int, builder: NotificationCompat.Builder): Notification {
        return builder.setContentTitle("正在下载中,请稍候...")
                .setContentText("$progress%")
                .setProgress(100, progress, false)
                .build()
    }

    override fun onCompleteNotification(builder: NotificationCompat.Builder): NotificationCompat.Builder {
        return builder.setContentTitle("下载完成")
                .setContentText("点击安装")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setWhen(System.currentTimeMillis())
    }

}
package com.cherry.upgrade.download

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.PermissionChecker
import com.cherry.upgrade.UpgradeClient
import com.cherry.upgrade.download.model.*
import com.cherry.upgrade.http.Http
import com.cherry.upgrade.ui.INotification
import com.cherry.upgrade.util.AppUtil
import com.cherry.upgrade.util.FileUtil
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.properties.Delegates

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-4
 */

object DownloadEngine {

    private const val CHANNEL_ID = "app_update_id"
    private const val CHANNEL_NAME = "app_update_channel"
    private const val NOTIFY_ID = 0

    private var disposable: Disposable? = null
    private var serviceAlive by Delegates.notNull<Boolean>()
    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null

    fun downloadAPK(versionService: DownloadService) {

        serviceAlive = true
        if (checkPermission(versionService, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            download(versionService)
        } else {
            //没有写入存储卡权限
            BuilderHelper.downloadBuilder?.downloadListener?.onDownloadFail("没有写入存储卡权限")
            UpgradeClient.stop(versionService.applicationContext)
        }
    }

    private fun download(service: DownloadService) {

        val builder = BuilderHelper.downloadBuilder ?: return
        val apkSavePath = builder.apkFileSavePath
        val downloadUrl = builder.downloadUrl
        val listener = builder.downloadListener

        FileUtil.checkAndDeleteAPK(service, apkSavePath)

        if (!serviceAlive) {
            return
        }
        val iNotification = builder.iNotification
        disposable = Observable
                .create<DownloadInfo> {
                    download(downloadUrl, apkSavePath, it)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ info ->
                    //next
                    when (info) {
                        is DowloadStart -> {
                            if (iNotification != null) {
                                setupNotification(service, iNotification)
                            }
                            listener?.onDownloadStart(apkSavePath)
                        }

                        is Downloading -> {
                            listener?.onDownloading(info.progress)
                            val notification = iNotification?.onProgressNotification(info.progress, notificationBuilder!!)
                            notification?.flags = Notification.FLAG_AUTO_CANCEL or Notification.FLAG_ONLY_ALERT_ONCE
                            notificationManager?.notify(NOTIFY_ID, notification)
                        }
                        is DownloadComplete -> {
                            listener?.onDownloadSuccess(apkSavePath)
                            install(service, apkSavePath, iNotification)
                        }
                        is DownloadFailure -> {
                            handleFailure(service, listener, info.message, iNotification)
                        }
                    }
                }, { e ->
                    run {
                        //error
                        handleFailure(service, listener, e.message ?: "", iNotification)
                    }
                })
    }

    private fun handleFailure(context: Context, listener: DownloadListener?, error: String,
                              iNotification: INotification?) {
        listener?.onDownloadFail(error)
        if (iNotification == null) {
            return
        }
        notificationManager?.cancel(NOTIFY_ID)
        UpgradeClient.stop(context)
    }


    private fun setupNotification(context: Context, notification: INotification) {
        notificationManager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationBuilder = getNotification(context)
        notificationManager?.notify(NOTIFY_ID, notification.onStartSetupNotification(notificationBuilder!!))
    }

    fun getNotification(context: Context): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            channel.enableVibration(false)
            channel.enableLights(false)
            notificationManager?.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(context, CHANNEL_ID)
    }

    private fun download(url: String, path: String, emitter: ObservableEmitter<DownloadInfo>) {
        emitter.onNext(DowloadStart())
        val request = Request.Builder().url(url).build()
        val response = Http.client.newCall(request).execute()
        if (response.isSuccessful) {
            var inputStream: InputStream? = null
            val buf = ByteArray(2048)

            var fos: FileOutputStream? = null
            try {
                // 储存下载文件的目录
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                } else {
                    val pathFile = file.parentFile
                    if (!pathFile.exists()) {
                        pathFile.mkdirs()
                    }
                    file.createNewFile()
                }

                inputStream = response.body()!!.byteStream()
                val totalLength = response.body()!!.contentLength()

                fos = FileOutputStream(file)
                var sum: Long = 0
                var len: Int
                var preProgress = 0
                val downloading = Downloading(0)
                while (true) {
                    len = inputStream.read(buf)
                    if (len == -1) {
                        break
                    }
                    fos.write(buf, 0, len)
                    sum += len.toLong()
                    val progress = (sum.toDouble() / totalLength * 100).toInt()
                    if (preProgress != progress) {
                        downloading.progress = progress
                        emitter.onNext(downloading)
                        preProgress = progress
                    }
                }

                fos.flush()
                emitter.onNext(DownloadComplete())

            } catch (e: Exception) {
                e.printStackTrace()
                emitter.onNext(DownloadFailure(e.message ?: ""))
            } finally {
                inputStream?.close()
                fos?.close()
            }
        } else {
            emitter.onNext(DownloadFailure(response.message()))
        }
    }

    private fun install(context: Context, apkFilePath: String, iNotification: INotification?) {

        val apkFile = File(apkFilePath)
        if (AppUtil.isAppForeground(context)) {
            notificationManager?.cancel(NOTIFY_ID)
            AppUtil.install(context, apkFile)

        } else {
            if (iNotification == null) {
                return
            }

            val installAppIntent = AppUtil.getInstallAppIntent(context, apkFile)
            val contentIntent = PendingIntent.getActivity(context, 0, installAppIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            notificationBuilder?.setContentIntent(contentIntent)?.setDefaults(Notification.DEFAULT_ALL)
            val notification = iNotification.onCompleteNotification(notificationBuilder!!).build()
            notification.flags = Notification.FLAG_AUTO_CANCEL
            notificationManager?.notify(NOTIFY_ID, notification)
        }
        UpgradeClient.stop(context)
    }

    private fun checkPermission(context: Context, permission: String): Boolean {
        return PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED
    }

    fun cancel() {
        serviceAlive = false
        if (disposable?.isDisposed != true) {
            disposable?.isDisposed
        }
    }

}
package com.cherry.upgrade.ui

import android.app.Notification
import android.support.v4.app.NotificationCompat

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-13
 */

interface INotification {

    /**
     * 开始下载,初始化通知栏
     */
    fun onStartSetupNotification(builder: NotificationCompat.Builder): Notification

    /**
     * 正在下载,更新通知栏
     */
    fun onProgressNotification(progress: Int, builder: NotificationCompat.Builder): Notification

    /**
     * 下载结束,更新通知栏
     */
    fun onCompleteNotification(builder: NotificationCompat.Builder): NotificationCompat.Builder

}
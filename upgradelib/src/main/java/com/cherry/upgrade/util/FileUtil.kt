package com.cherry.upgrade.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import java.io.File

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-4
 */

object FileUtil {


    fun checkApkFileIsExit(context: Context, apkFilePath: String): Boolean {
        val file = File(apkFilePath)
        return if (file.exists()) {
            try {
                //本地缓存的安装包
                val pm = context.packageManager
                val info = pm.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES)

                val localVersionCode = info.versionCode
                val currentVersionCode = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
                context.packageName.equals(info.packageName, ignoreCase = false) && localVersionCode != currentVersionCode
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        } else {
            false
        }
    }

    /**
     * 验证安装包是否存在，并且在安装成功情况下删除安装包
     */
    fun checkAndDeleteAPK(context: Context, downloadPath: String) {
        //判断versioncode与当前版本不一样的apk是否存在，存在删除安装包
        try {
            if (!checkApkFileIsExit(context, downloadPath)) {
                File(downloadPath).delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun enableSD() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED || !Environment.isExternalStorageRemovable()

}
package com.cherry.upgrade.checker

import android.content.Context
import android.os.Environment
import com.cherry.upgrade.download.BuilderHelper
import com.cherry.upgrade.download.DownloadService
import com.cherry.upgrade.ui.IShowUpgradeUI
import com.cherry.upgrade.ui.IUserOptionCallback
import com.cherry.upgrade.util.*
import java.io.File

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-11
 */

object CheckManager {

    private var checker: IChecker? = null

    fun check(checker: IChecker, builder: CheckerBuilder, callback: ICheckerCallback) {
        this.checker = checker
        if (builder.checker is DefaultCheckerEngine) {
            if (!builder.httpUrl.legalHttp()) {
                callback.onFailure("非法的请求地址")
                return
            }
        }
        checker.check(builder) { it ->
            handle(builder.ui, it)
        }
    }

    private fun handle(ui: IShowUpgradeUI, response: CheckResponse) {
        val uiCallback = ui.uiCallback
        if (response.hasNewVersion) {
            if (response.forceUpgrade) {
                uiCallback.forceUpgrade(ui.context, response)
            } else {
                //推荐更新,显示更新提示
                ui.showUpgradeUI(object : IUserOptionCallback {
                    override fun ignoreNewVersion(context: Context) {
                        SpUtil.saveIgnoreVersion(context)
                        ui.hideUpgradeUI()
                    }

                    override fun cancelNewVersion() {
                        ui.hideUpgradeUI()
                    }

                    override fun upgrade(context: Context) {
                        downloadThenInstall(context, response)
                    }
                })
            }
        } else {
            //没有新版本
            uiCallback.noHasVersion()
        }

        //其他处理
        uiCallback.checkResult(response.originResponse)
    }

    fun downloadThenInstall(context: Context, response: CheckResponse) {
        BuilderHelper.downloadBuilder?.run {
            downloadUrl = response.downloadUrl ?: ""
            if (!downloadUrl.legalHttp()) {
                downloadListener?.onDownloadFail("非法的http下载地址")
                return
            }

            var apkParentDir = if (apkFileSaveDir.isEmpty()) {
                if (FileUtil.enableSD()) {
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
                } else {
                    context.cacheDir.absolutePath
                }
            } else {
                apkFileSaveDir
            }

            if (!apkParentDir.endsWith(File.separator)) {
                apkParentDir += File.separator
            }

            val apkFileName = if (apkName.isEmpty()) {
                downloadUrl.substring(downloadUrl.lastIndexOf(File.separatorChar) + 1)
            } else {
                apkName
            }
            apkFileSavePath = apkParentDir + apkFileName

            if (!apkFileSavePath.legalApkPath()) {
                downloadListener?.onDownloadFail("非法的apk文件路径")
                return
            }

            if (FileUtil.checkApkFileIsExit(context, apkFileSavePath)) {
                AppUtil.install(context, File(apkFileSavePath))
                return
            }

            execute(context)
        }
    }

    fun cancel(context: Context) {
        checker?.cancel()
        DownloadService.stop(context)
    }


}
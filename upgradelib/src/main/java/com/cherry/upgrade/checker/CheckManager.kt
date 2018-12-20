package com.cherry.upgrade.checker

import android.content.Context
import android.os.Environment
import com.cherry.upgrade.download.BuilderHelper
import com.cherry.upgrade.download.DownloadService
import com.cherry.upgrade.ui.AbstractUpgradeUI
import com.cherry.upgrade.ui.IUserOptionCallback
import com.cherry.upgrade.util.*
import java.io.File

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-11
 */

internal object CheckManager {

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
            handle(builder, it)
        }
    }

    private fun handle(builder: CheckerBuilder, response: CheckResponse) {
        val ui = builder.ui
        if (response.hasNewVersion) {
            ui.createUpgradeUI(response.forceUpgrade)
            //其他处理
            ui.checkResult(response.originResponse)

            //强制更新默认不显示更新的UI,直接下载更新
            if (response.forceUpgrade) {
                if (builder.forceUpgradeUI) {
                    showUpgradeUI(ui, response)

                } else {
                    //不显示强制更新ui直接下载更新
                    CheckManager.downloadThenInstall(ui.context, response)
                }
            } else {
                showUpgradeUI(ui, response)
            }
        } else {
            //没有新版本
            ui.noHasVersion()
        }
    }

    private fun showUpgradeUI(ui: AbstractUpgradeUI, response: CheckResponse) {
        ui.setOnUserOptionCallback(object : IUserOptionCallback {
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
        ui.showUpgradeUI()
    }

    private fun downloadThenInstall(context: Context, response: CheckResponse) {
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
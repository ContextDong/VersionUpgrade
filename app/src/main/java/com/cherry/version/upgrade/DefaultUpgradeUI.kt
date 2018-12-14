package com.cherry.version.upgrade

import android.app.ProgressDialog
import android.content.Context
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import com.cherry.upgrade.download.DownloadListener
import com.cherry.upgrade.ui.IBeforeCheckCallback
import com.cherry.upgrade.ui.IShowUpgradeUI
import com.cherry.upgrade.ui.IUpgradeUICallback
import com.cherry.upgrade.ui.IUserOptionCallback

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-11
 */

class DefaultUpgradeUI(override val context: Context) : IBeforeCheckCallback, IShowUpgradeUI, DownloadListener {

    override val uiCallback: IUpgradeUICallback = object : IUpgradeUICallback {
        override fun noHasVersion() {
            Toast.makeText(context, "没有新版本了", Toast.LENGTH_SHORT).show()
        }
    }

    private val progressDialog by lazy {
        val dialog = ProgressDialog(context)
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        dialog.max = 100
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog
    }

    private var upgradeDialog: AlertDialog? = null

    override fun beforeCheck(context: Context) {
        Toast.makeText(context, "正在检查更新", Toast.LENGTH_SHORT).show()
    }

    override fun showUpgradeUI(callback: IUserOptionCallback) {
        upgradeDialog = createDialog(callback)
        upgradeDialog?.show()
    }

    override fun hideUpgradeUI() {
        upgradeDialog?.dismiss()
    }

    private fun createDialog(callback: IUserOptionCallback): AlertDialog {
        return AlertDialog.Builder(context)
                .setTitle("更新")
                .setSingleChoiceItems(arrayOf("忽略更新", "取消", "确认"), -1) { _, which ->
                    when (which) {
                        0 -> callback.ignoreNewVersion(context)
                        1 -> callback.cancelNewVersion()
                        2 -> {
                            upgradeDialog?.dismiss()
                            callback.upgrade(context)
                        }

                    }
                }
                .create()
    }

    override fun onDownloadStart(apkFileSavePath: String) {
        progressDialog.show()
    }

    override fun onDownloading(progress: Int) {
        progressDialog.progress = progress
    }

    override fun onDownloadSuccess(file: String) {
        Toast.makeText(context, "下载成功了", Toast.LENGTH_SHORT).show()
        progressDialog.dismiss()
    }

    override fun onDownloadFail(message: String) {
        Log.e("==========MainActivity", "下载失败$message")
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        progressDialog.dismiss()
    }

}
package com.cherry.upgrade.download

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-4
 */

interface DownloadListener {

    fun onDownloadStart(apkFileSavePath: String)
    fun onDownloading(progress: Int)
    fun onDownloadSuccess(file: String)
    fun onDownloadFail(message: String)
}
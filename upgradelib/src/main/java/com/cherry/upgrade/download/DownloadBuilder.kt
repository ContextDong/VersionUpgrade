package com.cherry.upgrade.download

import android.content.Context
import com.cherry.upgrade.ui.INotification

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-4
 */

class DownloadBuilder private constructor() {

    var downloadUrl: String = ""
        internal set(value) {
            //以第一次设置的地址为准,即设置Builder为准
            if (downloadUrl.isEmpty()) {
                field = value
            }
        }

    var downloadListener: DownloadListener? = null
        private set

    var apkFileSaveDir: String = ""
        private set

    var apkName: String = ""
        internal set(value) {
            //以第一次设置的地址为准,即设置Builder为准
            if (apkName.isEmpty()) {
                field = value
            }
        }

    internal var apkFileSavePath: String = ""

    var iNotification: INotification? = null
        private set

    internal fun execute(context: Context) {
        if (!DownloadService.isStart && BuilderHelper.downloadBuilder != null) {
            DownloadService.start(context)
        }
    }

    class Builder {

        private val builder by lazy {
            DownloadBuilder()
        }

        fun setDownloadUrl(url: String): Builder {
            builder.downloadUrl = url
            return this
        }

        fun setOnDownloadListener(listener: DownloadListener): Builder {
            builder.downloadListener = listener
            return this
        }

        fun setApkFileSaveDir(dirPath: String): Builder {
            builder.apkFileSaveDir = dirPath
            return this
        }

        fun setApkName(apkName: String): Builder {
            builder.apkName = apkName
            return this
        }

        fun setNotification(iNotification: INotification): Builder {
            builder.iNotification = iNotification
            return this
        }

        fun build(): DownloadBuilder {
            BuilderHelper.downloadBuilder = this.builder
            return builder
        }

    }

}
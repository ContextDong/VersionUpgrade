package com.cherry.upgrade.checker

import android.content.Context
import com.cherry.upgrade.download.DownloadBuilder
import com.cherry.upgrade.ui.IBeforeCheckCallback
import com.cherry.upgrade.ui.IShowUpgradeUI
import com.cherry.upgrade.util.AppUtil
import com.cherry.upgrade.util.SpUtil
import kotlin.properties.Delegates

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-3
 */

class CheckerBuilder private constructor() {

    var requestMethod: HttpRequestMethod = HttpRequestMethod.GET
        private set

    var httpUrl: String = ""
        private set

    var httpParams: Map<String, String>? = null
        private set

    var httpHeader: Map<String, String>? = null
        private set

    var onlyWifi: Boolean = false
        private set

    var checker: IChecker = DefaultCheckerEngine
        private set

    var ui: IShowUpgradeUI by Delegates.notNull()
        private set

    var listener: ICheckerCallback by Delegates.notNull()
        private set

    fun beforeCheck(context: Context, before: IBeforeCheckCallback): CheckerBuilder {
        if (isUpgrade(context)) {
            before.beforeCheck(context)
        }
        return this
    }

    fun request(context: Context, callback: ICheckerCallback): CheckerBuilder {
        this.listener = callback
        if (isUpgrade(context)) {
            CheckManager.check(checker, this, callback)
        }
        return this
    }

    private fun isUpgrade(context: Context): Boolean {
        return !SpUtil.isNeedIgnore(context) && if (onlyWifi) AppUtil.isWifi(context) else true
    }

    fun showUI(ui: IShowUpgradeUI): DownloadBuilder.Builder {
        this.ui = ui
        return DownloadBuilder.Builder()
    }


    class Builder {

        private val builder by lazy {
            CheckerBuilder()
        }

        fun setHttpEngine(iChecker: IChecker): Builder {
            builder.checker = iChecker
            return this
        }

        fun setUrl(url: String): Builder {
            builder.httpUrl = url
            return this
        }

        fun setRequestMethod(method: HttpRequestMethod): Builder {
            builder.requestMethod = method
            return this
        }

        fun setRequestParams(params: Map<String, String>): Builder {
            builder.httpParams = params
            return this
        }

        fun setRequestHeader(header: Map<String, String>): Builder {
            builder.httpHeader = header
            return this
        }

        fun onlyWifi(onlyWifi: Boolean): Builder {
            builder.onlyWifi = onlyWifi
            return this
        }

        fun build(): CheckerBuilder = builder

    }

}


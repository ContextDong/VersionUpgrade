package com.cherry.upgrade.ui

import android.content.Context
import com.cherry.upgrade.checker.CheckManager
import com.cherry.upgrade.checker.CheckResponse

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-11
 */

interface IUpgradeUICallback {

    /**
     * 强制更新,默认不提示直接下载更新
     */
    fun forceUpgrade(context: Context, response: CheckResponse) {
        CheckManager.downloadThenInstall(context, response)
    }

    fun noHasVersion()

    /**
     * 其他处理
     */
    fun checkResult(originResponse: String) {}

}
package com.cherry.upgrade

import android.content.Context
import com.cherry.upgrade.checker.CheckManager
import com.cherry.upgrade.checker.CheckerBuilder
import com.cherry.upgrade.download.BuilderHelper
import com.cherry.upgrade.http.Http

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-4
 */

object UpgradeClient {

    /**
     * 更新请求接口
     */
    fun checkUpgrade() = CheckerBuilder.Builder()

    fun stop(context: Context) {
        Http.client.dispatcher().cancelAll()
        CheckManager.cancel(context)
        //清除参数
        BuilderHelper.downloadBuilder = null
    }

}
package com.cherry.upgrade.ui

import android.content.Context

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-11
 */

internal interface IShowUpgradeUI {

    val context: Context

    fun noHasVersion(){}

    /**
     * 其他处理
     */
    fun checkResult(originResponse: String) {}

    fun createUpgradeUI(forceUpgrade: Boolean)

    fun showUpgradeUI()

    fun hideUpgradeUI()

}
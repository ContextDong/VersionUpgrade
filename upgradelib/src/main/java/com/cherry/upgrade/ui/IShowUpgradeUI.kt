package com.cherry.upgrade.ui

import android.content.Context

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-11
 */

interface IShowUpgradeUI {

    val context: Context

    val uiCallback: IUpgradeUICallback

    fun showUpgradeUI(callback: IUserOptionCallback)

    fun hideUpgradeUI()

}
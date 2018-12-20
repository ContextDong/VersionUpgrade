package com.cherry.upgrade.ui

import kotlin.properties.Delegates

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-19
 */

abstract class AbstractUpgradeUI : IShowUpgradeUI {


    var callback: IUserOptionCallback by Delegates.notNull()

    fun setOnUserOptionCallback(callback: IUserOptionCallback) {
        this.callback = callback
    }

}
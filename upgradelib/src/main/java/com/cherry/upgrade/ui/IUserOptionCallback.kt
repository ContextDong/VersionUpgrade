package com.cherry.upgrade.ui

import android.content.Context

/**
 * 用户UI界面操作回调
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-11
 */

interface IUserOptionCallback {

    /**
     * 忽略新版本,之后不会更新
     */
    fun ignoreNewVersion(context: Context)


    /**
     * 取消本次更新,下次会触发更新
     */
    fun cancelNewVersion()

    /**
     * 更新
     */
    fun upgrade(context: Context)


}
package com.cherry.upgrade.util

import android.content.Context
import android.content.SharedPreferences

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-11
 */

object SpUtil {

    private const val IGNORE_VERSION = "ignore_version"
    private const val PREFS_FILE = "update_app_config.xml"

    private fun getSP(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
    }

    fun saveIgnoreVersion(context: Context) {
        getSP(context).edit().putBoolean(IGNORE_VERSION, true).apply()
    }

    fun isNeedIgnore(context: Context): Boolean {
        return getSP(context).getBoolean(IGNORE_VERSION, false)
    }
}
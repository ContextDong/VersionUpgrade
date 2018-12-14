package com.cherry.upgrade.checker

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-7
 */

data class CheckResponse private constructor(val forceUpgrade: Boolean,
                                             val hasNewVersion: Boolean,
                                             val originResponse: String,
                                             var downloadUrl: String? = null,
                                             var apkName: String? = null) {

    companion object {

        @JvmOverloads
        fun create(forceUpgrade: Boolean, hasNewVersion: Boolean, originResponse: String, downloadUrl: String? = null, apkName: String? = null)
                = CheckResponse(forceUpgrade, hasNewVersion, originResponse, downloadUrl, apkName)
    }

}
package com.cherry.upgrade.util

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-7
 */

fun String.legalHttp() = this.isNotEmpty()
        && (this.startsWith("http://", true) || this.startsWith("https://"))

fun String.legalApkPath() = this.isNotEmpty() && this.endsWith(".apk")
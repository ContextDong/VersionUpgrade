package com.cherry.upgrade.http

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-4
 */

class TrustAllHostnameVerifier : HostnameVerifier {
    override fun verify(hostname: String?, session: SSLSession?) = true
}
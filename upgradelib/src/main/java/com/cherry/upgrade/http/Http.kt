package com.cherry.upgrade.http

import com.cherry.upgrade.checker.CheckerBuilder
import okhttp3.*
import org.json.JSONObject
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-4
 */

internal object Http {

    val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.SECONDS)
                .readTimeout(5000, TimeUnit.SECONDS)
                .writeTimeout(5000, TimeUnit.SECONDS)
                .sslSocketFactory(createSSLSocketFactory()!!)
                .hostnameVerifier(TrustAllHostnameVerifier())
                .build()
    }

    private fun createSSLSocketFactory(): SSLSocketFactory? {
        return try {
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, arrayOf<TrustManager>(TrustAllCerts()), SecureRandom())
            sc.socketFactory
        } catch (e: Exception) {
            null
        }
    }

    private fun <T : Request.Builder> addHeaders(builder: T, httpHeader: Map<String, String>?) {
        if (httpHeader != null) {
            for (header in httpHeader) {
                builder.addHeader(header.key, header.value)
            }
        }
    }

    //////////////GET///////////////////////

    fun get(versionBuilder: CheckerBuilder): Request {
        return Request.Builder().apply {
            addHeaders(this, versionBuilder.httpHeader)
            url(convertUrl(versionBuilder.httpUrl, versionBuilder.httpParams))
        }.build()
    }

    private fun convertUrl(httpUrl: String, httpParams: Map<String, String>?): String {
        return if (httpParams != null) {
            StringBuilder().apply {
                append(httpUrl).append("?")
                for (params in httpParams) {
                    append(params.key).append("=").append(params.value).append("&")
                }
                this.length - 1
            }.toString()
        } else {
            httpUrl
        }
    }


    //////////////GET///////////////////////


    //////////////POST///////////////////////
    fun post(versionBuilder: CheckerBuilder): Request {
        return Request.Builder().apply {
            addHeaders(this, versionBuilder.httpHeader)
            post(getRequestPostParams(versionBuilder.httpParams))
            url(versionBuilder.httpUrl)
        }.build()
    }

    private fun getRequestPostParams(httpParams: Map<String, String>?): FormBody {
        return FormBody.Builder().apply {
            if (httpParams != null) {
                for (params in httpParams) {
                    add(params.key, params.value)
                }
            }
        }.build()
    }

    //////////////POST///////////////////////


    //////////////POSTJSON///////////////////////

    fun postJson(versionBuilder: CheckerBuilder): Request {
        val jsonType = MediaType.parse("application/json; charset=utf-8")
        return Request.Builder().apply {
            addHeaders(this, versionBuilder.httpHeader)
            post(RequestBody.create(jsonType, getRequestJsonParams(versionBuilder.httpParams)))
            url(versionBuilder.httpUrl)
        }.build()
    }

    private fun getRequestJsonParams(httpParams: Map<String, String>?): String {
        return JSONObject().apply {
            if (httpParams != null) {
                for (params in httpParams) {
                    put(params.key, params.value)
                }
            }
        }.toString()
    }

    //////////////POSTJSON///////////////////////
}
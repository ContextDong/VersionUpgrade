package com.cherry.version.upgrade

import android.util.Log
import com.cherry.upgrade.checker.CheckResponse
import com.cherry.upgrade.checker.CheckerBuilder
import com.cherry.upgrade.checker.IChecker
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import com.zhy.http.okhttp.request.RequestCall
import okhttp3.Call
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-13
 */

class CustomCheckEngine : IChecker {

    private var build: RequestCall by Delegates.notNull()

    init {
        val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build()

        OkHttpUtils.initClient(okHttpClient)

    }


    override fun check(checkerBuilder: CheckerBuilder, callback: (checkResponse: CheckResponse) -> Unit) {
        build = OkHttpUtils.get()
                .url("http://www.wanandroid.com/tools/mockapi/5686/upgrade_fragment")
                .build()
        build.execute(object : StringCallback() {
            override fun onResponse(response: String, id: Int) {
                //注意要将结果回调回去!!!
                callback(checkerBuilder.listener.onSuccess(response))
            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                //自己处理
                Log.e("=====okhttpUtil", e.message ?: "")
            }
        })
    }

    override fun cancel() {
        build.cancel()
    }
}
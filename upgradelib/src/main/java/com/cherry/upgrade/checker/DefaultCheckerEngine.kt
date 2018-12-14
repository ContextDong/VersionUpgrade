package com.cherry.upgrade.checker

import com.cherry.upgrade.http.Http
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observables.GroupedObservable
import io.reactivex.schedulers.Schedulers
import okhttp3.Response

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-4
 */

internal object DefaultCheckerEngine : IChecker {

    private var disposable: Disposable? = null

    override fun check(checkerBuilder: CheckerBuilder, callback: (checkResponse: CheckResponse) -> Unit) {
        val method = checkerBuilder.requestMethod
        val request = when (method) {
            HttpRequestMethod.GET -> {
                Http.get(checkerBuilder)
            }
            HttpRequestMethod.POST -> {
                Http.post(checkerBuilder)
            }
            HttpRequestMethod.POSTJSON -> {
                Http.postJson(checkerBuilder)
            }
        }

        disposable = Observable.just(request)
                .map {
                    Http.client.newCall(it).execute()
                }
                .groupBy {
                    it.isSuccessful
                }
                .subscribeOn(Schedulers.io())
                .subscribe({ group ->
                    if (group.key == true) {
                        onSuccess(group, checkerBuilder, callback)
                    } else {
                        onFailure(group, checkerBuilder)
                    }
                }, { e ->
                    checkerBuilder.listener.onFailure(e.message ?: "")
                })
    }

    private fun onSuccess(group: GroupedObservable<Boolean, Response>, versionBuilder: CheckerBuilder, callback: (checkResponse: CheckResponse) -> Unit) {
        group.map { response ->
            response.body()?.string() ?: ""
        }.observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it ->
                    callback(versionBuilder.listener.onSuccess(it))
                }, { e ->
                    versionBuilder.listener.onFailure(e.message ?: "")
                })
    }

    private fun onFailure(group: GroupedObservable<Boolean, Response>, versionBuilder: CheckerBuilder) {
        group.map {
            it.message()
        }.observeOn(AndroidSchedulers.mainThread())
                .subscribe({ message ->
                    versionBuilder.listener.onFailure(message)
                }, { e ->
                    versionBuilder.listener.onFailure(e.message ?: "")
                })
    }


    override fun cancel() {
        if (disposable?.isDisposed != true) {
            disposable?.isDisposed
        }
    }

}
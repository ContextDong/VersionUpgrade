package com.cherry.upgrade.checker

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-4
 */

interface ICheckerCallback {

    fun onSuccess(result: String): CheckResponse

    fun onFailure(message: String){}
}
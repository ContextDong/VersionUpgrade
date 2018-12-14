package com.cherry.upgrade.checker

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-11
 */

interface IChecker {

    fun check(checkerBuilder: CheckerBuilder, callback: (checkResponse: CheckResponse) -> Unit)

    fun cancel()

}
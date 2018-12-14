package com.cherry.version.upgrade

import com.cherry.upgrade.util.legalApkPath
import com.cherry.upgrade.util.legalHttp
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun legalHttp() {
        assertTrue("http://www.wanandroid.com/tools/mockapi/5686/upgrade".legalHttp())
    }

    @Test
    fun legalApkPath() {
        assertTrue("/storage/emulated/0/aaaTestUpgrade/test.apk".legalApkPath())
    }

}

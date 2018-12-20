package com.cherry.version.upgrade

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.cherry.upgrade.UpgradeClient
import com.cherry.upgrade.checker.CheckResponse
import com.cherry.upgrade.checker.HttpRequestMethod
import com.cherry.upgrade.checker.ICheckerCallback
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        defaultUpgrade.setOnClickListener {

            requestPermission { updateVersion1() }
        }
        notificationUpgrade.setOnClickListener {
            requestPermission { updateVersion2() }
        }


    }


    private fun requestPermission(fn: () -> Unit) {
        RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { grant ->
                    if (grant) {
                        fn()
                    }
                }
    }

    private fun updateVersion2() {
        val upgradeUI = NotificationUpgradeUI(this)
        UpgradeClient.checkUpgrade()
                .setUrl("http://www.wanandroid.com/tools/mockapi/5686/upgrade")
                .setRequestMethod(HttpRequestMethod.GET)
                .setRequestParams(hashMapOf())
                .onlyWifi(true)
                .build()
                .beforeCheck(this, upgradeUI)
                .request(this, object : ICheckerCallback {
                    override fun onSuccess(result: String): CheckResponse {

                        Log.e("==========MainActivity", result)

                        //解析数据
                        val jsonParse = JSONObject(result)
                        val downloadUrl = jsonParse.getString("apkUrl")

                        val minVersion = jsonParse.getString("minVersionCode")
                        val newVersion = jsonParse.getString("newVersionCode")

                        val forceUpgrade = Version.parse(BuildConfig.VERSION_NAME)
                                ?.isLowerThan(Version.parse(minVersion)) ?: false

                        val isNewVersion = Version.parse(BuildConfig.VERSION_NAME)
                                ?.isLowerThan(Version.parse(newVersion)) ?: false
                        return CheckResponse.create(forceUpgrade, isNewVersion, result, downloadUrl)
                    }

                    override fun onFailure(message: String) {
                        Log.e("==========MainActivity", "请求失败$message")
                        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                })
                .showUI(upgradeUI)
                .setNotification(upgradeUI)
                .setOnDownloadListener(upgradeUI)
                .build()
    }

    private fun updateVersion1() {

        val upgradeUI = DefaultUpgradeUI(this)
        UpgradeClient.checkUpgrade()
                .setUrl("http://www.wanandroid.com/tools/mockapi/5686/upgrade")
                .setRequestMethod(HttpRequestMethod.GET)
                .setRequestParams(hashMapOf())
                .forceUpgradeUI(false)//不显示强制更新UI
                .build()
                .beforeCheck(this, upgradeUI)
                .request(this, object : ICheckerCallback {
                    override fun onSuccess(result: String): CheckResponse {

                        Log.e("==========MainActivity", result)

                        //解析数据
                        val jsonParse = JSONObject(result)
                        val downloadUrl = jsonParse.getString("apkUrl")

                        val minVersion = jsonParse.getString("minVersionCode")
                        val newVersion = jsonParse.getString("newVersionCode")

                        val forceUpgrade = Version.parse(BuildConfig.VERSION_NAME)
                                ?.isLowerThan(Version.parse(minVersion)) ?: false

                        val isNewVersion = Version.parse(BuildConfig.VERSION_NAME)
                                ?.isLowerThan(Version.parse(newVersion)) ?: false
                        return CheckResponse.create(forceUpgrade, isNewVersion, result, downloadUrl)
                    }

                    override fun onFailure(message: String) {
                        Log.e("==========MainActivity", "请求失败$message")
                        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                })
                .showUI(upgradeUI)
                .setApkFileSaveDir(Environment.getExternalStorageDirectory().absolutePath + "/aaaTestUpgrade/")
                .setApkName("test.apk")
                .setOnDownloadListener(upgradeUI)
                .build()
    }


}

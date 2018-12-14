package com.cherry.version.upgrade;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cherry.upgrade.UpgradeClient;
import com.cherry.upgrade.checker.CheckResponse;
import com.cherry.upgrade.checker.ICheckerCallback;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.functions.Consumer;

/**
 * @author 董棉生(dongmiansheng @ parkingwang.com)
 * @since 18-12-7
 */

public class MainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.customUpgrade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doWork();
            }
        });
    }

    @SuppressLint("CheckResult")
    private void doWork() {
        new RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            work();
                        }
                    }
                });
    }

    private void work() {
        CustomUpgradeUI upgradeUI = new CustomUpgradeUI(getActivity());
        UpgradeClient.INSTANCE
                .checkUpgrade()
                .onlyWifi(true)
                //设置自定义检查请求接口后,build url,method和header参数失效
                .setHttpEngine(new CustomCheckEngine())
                .build()
                .request(getActivity(), new ICheckerCallback() {
                    @Override
                    public void onFailure(@NotNull String message) {
                        Log.e("==========MainFragment", "请求失败:" + message);
                    }

                    @NotNull
                    @Override
                    public CheckResponse onSuccess(@NotNull String result) {
                        Log.e("==========MainFragment", result);

                        //解析数据
                        JSONObject jsonParse;
                        boolean forceUpgrade = false;
                        boolean isNewVersion = false;
                        String downloadUrl = "";
                        try {
                            jsonParse = new JSONObject(result);
                            downloadUrl = jsonParse.getString("apkUrl");

                            String minVersion = jsonParse.getString("minVersionCode");
                            String newVersion = jsonParse.getString("newVersionCode");
                            Version localVersion = Version.Companion.parse(BuildConfig.VERSION_NAME);
                            Version serviceMinVersion = Version.Companion.parse(minVersion);
                            Version serviceVersion = Version.Companion.parse(newVersion);
                            forceUpgrade = localVersion.isLowerThan(serviceMinVersion);
                            isNewVersion = localVersion.isLowerThan(serviceVersion);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return CheckResponse.Companion.create(
                                forceUpgrade, isNewVersion, result, downloadUrl);
                    }
                })
                .showUI(upgradeUI)
                .setNotification(upgradeUI)
                .setOnDownloadListener(upgradeUI)
                .build();
    }
}

package com.cherry.version.upgrade

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import com.cherry.upgrade.ui.IUserOptionCallback
import com.pitt.library.fresh.FreshDownloadView

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-12-13
 */

class UpgradeDialog : DialogFragment() {

    private lateinit var button: FreshDownloadView

    private lateinit var callback: IUserOptionCallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.fragment_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button = view.findViewById(R.id.pitt)
        view.findViewById<Button>(R.id.ignore).setOnClickListener {
            callback.ignoreNewVersion(context!!)
        }
        view.findViewById<Button>(R.id.cancel).setOnClickListener {
            callback.cancelNewVersion()
        }
        view.findViewById<Button>(R.id.upgrade).setOnClickListener {
            callback.upgrade(context!!)
        }
    }


    fun progress(progress: Int) {
        button.upDateProgress(progress)
    }

    fun success() {
        button.showDownloadOk()
    }

    fun fail() {
        button.showDownloadError()
    }

    fun setUserOptionCallback(callback: IUserOptionCallback) {
        this.callback = callback
    }

}
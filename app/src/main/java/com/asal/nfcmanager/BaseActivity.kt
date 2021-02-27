package com.asal.nfcmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.kaopiz.kprogresshud.KProgressHUD
import com.wang.avi.AVLoadingIndicatorView


open class BaseActivity : AppCompatActivity() {

    lateinit var loadingIndicatorView: AVLoadingIndicatorView
    lateinit var hud: KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingIndicatorView = AVLoadingIndicatorView(this,null)
        loadingIndicatorView.setIndicator("BallPulseIndicator")
        hud = KProgressHUD.create(this)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setDimAmount(0.5f)
                .setCustomView(loadingIndicatorView)
    }
}
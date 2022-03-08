package com.qipa.newboxproject.app.weight.dialog

import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.daimajia.numberprogressbar.NumberProgressBar
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.upload.bean.CheckVersionResult
import com.zenglb.downloadinstaller.DownloadInstaller
import com.zenglb.downloadinstaller.DownloadProgressCallBack
import java.lang.Exception

class UpdateDialog(var contexts: Activity,var checkVersionResult : CheckVersionResult?) : AlertDialog(contexts) {
    var mContext: Activity
    var mUnConfirm: TextView? = null
    var mContent: TextView? = null
    var mBtnConfirm: TextView? = null
    var mVersion: TextView? = null
    var mTitle: TextView? = null
    var progressBar: NumberProgressBar? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.getWindow()?.setBackgroundDrawable(BitmapDrawable())
        setContentView(R.layout.update_app_alert)
        this.setCancelable(false)
        mVersion = findViewById<TextView>(R.id.version)
        mUnConfirm = findViewById<TextView>(R.id.btn_close)
        mContent = findViewById<TextView>(R.id.content)
        mTitle = findViewById<TextView>(R.id.title)
        progressBar = findViewById(R.id.tips_progress)
        mBtnConfirm = findViewById<TextView>(R.id.btn_confirm)
//        //加载自己的布局
        val lp: WindowManager.LayoutParams = contexts.getWindow().getAttributes()
//        //设置宽高，高度默认是自适应的，宽度根据屏幕宽度比例设置
//        lp.width = ScreenUtils.getScreenWidth() / 10 * 8
//        //这里设置居中
        lp.gravity = Gravity.CENTER
        contexts.getWindow().setAttributes(lp)

        // mContent.setText(checkVersionResult.getDescription());
        mVersion?.setText(checkVersionResult?.versionName)
        if (checkVersionResult?.updateType === 1) {
            mTitle?.text = "强制更新"
            mUnConfirm?.text = "退出应用"
        }
        mBtnConfirm?.setOnClickListener { v: View? ->
            //变成灰色
            mBtnConfirm?.setBackgroundResource(R.drawable.shape_btn_grey_bg)
            if (checkVersionResult?.updateType === 1) {
                progressBar?.setVisibility(View.VISIBLE)
                DownloadInstaller(mContext,
                    checkVersionResult?.packageUrl,
                    true,
                    object : DownloadProgressCallBack {
                        override fun downloadProgress(progress: Int) {
                            contexts?.runOnUiThread(Runnable { progressBar?.setProgress(progress) })
                            if (progress == 100) {
                            }
                        }

                        override fun downloadException(e: Exception) {
                            val a = 1
                        }

                        override fun onInstallStart() {
                            contexts.finish()
                        }
                    }).start()
            } else {
                DownloadInstaller(
                    mContext,
                    checkVersionResult?.packageUrl
                ).start()
                Toast.makeText(mContext, "后台下载升级中", Toast.LENGTH_LONG).show()
            }
        }
        mUnConfirm?.setOnClickListener { v: View? ->
            if (checkVersionResult?.updateType === 1) {
                contexts.finish()
            } else {
                dismiss()
            }
        }
    }

    init {
        mContext = contexts
    }
}
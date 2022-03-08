package com.qipa.newboxproject.ui.activity

import android.content.ClipData
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.activity_error.*
import kotlinx.android.synthetic.main.include_toolbar.*
import com.qipa.jetpackmvvm.base.viewmodel.BaseViewModel
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseActivity
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.app.ext.showMessage
import com.qipa.newboxproject.app.util.SettingUtil
import com.qipa.newboxproject.app.util.StatusBarUtil
import com.qipa.newboxproject.databinding.ActivityErrorBinding
import com.qipa.jetpackmvvm.ext.util.clipboardManager
import com.qipa.jetpackmvvm.ext.view.clickNoRepeat


/**
 * 描述　:
 */
class ErrorActivity : BaseActivity<BaseViewModel, ActivityErrorBinding>() {

    override fun layoutId() = R.layout.activity_error

    override fun initView(savedInstanceState: Bundle?)  {
        toolbar.init("发生错误")
        supportActionBar?.setBackgroundDrawable(ColorDrawable(SettingUtil.getColor(this)))
        StatusBarUtil.setColor(this, SettingUtil.getColor(this), 0)
        val config = CustomActivityOnCrash.getConfigFromIntent(intent)
        errorRestart.clickNoRepeat{
            config?.run {
                CustomActivityOnCrash.restartApplication(this@ErrorActivity, this)
            }
        }
        errorSendError.clickNoRepeat {
            CustomActivityOnCrash.getStackTraceFromIntent(intent)?.let {
                showMessage(it,"发现有Bug不去打开发者脸？","必须打",{
                    val mClipData = ClipData.newPlainText("errorLog",it)
                    // 将ClipData内容放到系统剪贴板里。
                    clipboardManager?.setPrimaryClip(mClipData)
                    ToastUtils.showShort("已复制错误日志")
                    try {
                        val url = "mqqwpa://im/chat?chat_type=wpa&uin=824868922"
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } catch (e: Exception) {
                        ToastUtils.showShort("请先安装QQ")
                    }
                },"我不敢")
            }


        }
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }
}
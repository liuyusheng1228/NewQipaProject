package com.qipa.qipaimbase.chat.chatset

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.Nullable
import com.qipa.qipaimbase.R
import com.qipa.qipaimbase.R2
import com.qipa.qipaimbase.chat.chatset.ichatset.IChatSetView
import com.qipa.qipaimbase.utils.ToastUtils
import com.qipa.qipaimbase.utils.http.jsons.JsonGetIgnoreInfo
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import com.qipa.qipaimbase.utils.image.ImageLoaderUtils
import com.qipa.qipaimbase.utils.mvpbase.IModel
import com.qipa.qipaimbase.utils.mvpbase.IPresenter
import com.qipa.qipaimbase.utils.mvpbase.IView
import com.qipa.qipaimbase.view.TitleBar

class ChatSetActivity : IChatSetView() {
    lateinit var titleBar: TitleBar

    lateinit var ivIcon: ImageView

    lateinit var tvNickName: TextView

    var sBan: Switch? = null
    var sTop: Switch? = null
    private var userId: String? = null
    private var icon: String? = null
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatset)
        initView()
    }

    private fun initView() {
        titleBar = findViewById(R.id.titleBar)
        ivIcon= findViewById(R.id.ivIcon)
        tvNickName = findViewById(R.id.tvNickName)
        sBan = findViewById(R.id.sBan)
        sBan?.setOnClickListener {
            onBanChange()
        }
        sTop?.setOnClickListener {
            onTopChange()
        }
        titleBar?.setTitle(resources.getString(R.string.chatset_title))
        titleBar?.setLeftImageEvent(R.drawable.arrow_left) { v -> finish() }
        userId = intent.getStringExtra(EXTRA_USERID)
        icon = intent.getStringExtra(EXTRA_ICON)
        ImageLoaderUtils.getInstance().loadImage(this, icon, R.drawable.head_placeholder, ivIcon)
        tvNickName!!.text = userId
        presenter!!.getIgnoreStatus(userId)
    }

    fun onTopChange() {
        presenter!!.changeTopStatus()
    }

    fun onBanChange() {
        presenter!!.changeIgnoreStatus(userId, sBan!!.isChecked)
    }

    override fun onTopChangeStatusResult(success: Boolean) {}

    override fun onIgnoreChangeStatusResult(success: Boolean) {
        ToastUtils.showText(this, if (success) "成功" else "失败")
        if (!success) {
            sBan!!.isChecked = !sBan!!.isChecked
        }
    }

    override fun onGetIgnoreStatus(result: JsonResult<JsonRequestResult>) {
        if (!result.success()) {
            ToastUtils.showText(this, "获取勿扰设置失败")
            return
        }
        val ignoreInfo = result.get() as JsonGetIgnoreInfo
        sBan!!.isChecked = ignoreInfo.data.switchX === 0
    }

    override fun getIPresenter(): IPresenter<in IView, in IModel>? {
        return ChatSetPresenter(this) as IPresenter<in IView, in IModel>
    }


    companion object {
        private const val EXTRA_USERID = "EXTRA_USERID"
        private const val EXTRA_ICON = "EXTRA_ICON"
        fun startActivity(activity: Activity, userId: String?, icon: String?) {
            val intent = Intent(activity, ChatSetActivity::class.java)
            intent.putExtra(EXTRA_USERID, userId)
            intent.putExtra(EXTRA_ICON, icon)
            activity.startActivity(intent)
        }
    }
}

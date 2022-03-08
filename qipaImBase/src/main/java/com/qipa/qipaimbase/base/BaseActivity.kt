package com.qipa.qipaimbase.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.qipa.qipaimbase.utils.event.IMStatus
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

open class BaseActivity : AppCompatActivity() {
    protected override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
    }

    protected override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBaseImStatusChange(status: IMStatus) {
        when (status.status) {
//            PhotonIMClient.IM_STATE_KICK, PhotonIMClient.IM_STATE_AUTH_FAILED -> {
//                val businessListener: ImBaseBridge.BusinessListener =
//                    ImBaseBridge.getInstance().getBusinessListener()
//                if (businessListener != null) {
//                    businessListener.onKickUser(this)
//                }
//            }
        }
    }
}
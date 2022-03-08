package com.qipa.qipaimbase.chat

import android.content.Intent
import com.qipa.qipaimbase.ImBaseBridge
import com.qipa.qipaimbase.session.PhotonIMMessage
import com.qipa.qipaimbase.utils.CollectionUtils
import com.qipa.qipaimbase.utils.mvpbase.IModel
import com.qipa.qipaimbase.utils.mvpbase.IPresenter
import com.qipa.qipaimbase.utils.mvpbase.IView

class ChatGroupActivity : ChatBaseActivity() {
    override fun onInfoClick() {
        val businessListener: ImBaseBridge.BusinessListener? = ImBaseBridge.instance?.businessListener
        if (businessListener != null) {
            businessListener.onGroupInfoClick(this, chatWith)
        }
    }

    override fun onAtCharacterInput() {
        val businessListener: ImBaseBridge.BusinessListener? = ImBaseBridge.instance?.businessListener
        if (businessListener != null) {
            businessListener.onAtListener(this, chatWith)
        }
    }

    override val isGroup: Boolean
        protected get() = true

    override fun getIPresenter():IPresenter<in IView, in IModel>? {
        return ChatGroupPresenter(this) as IPresenter<in IView, in IModel>
    }

    override fun getChatIcon(msg: PhotonIMMessage?): String? {
        return null
    }

    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_AT) {
            val resultNames = data?.getStringArrayListExtra(EXTRA_RESULT_NAME)
            if (CollectionUtils.isEmpty(resultNames)) {
                return
            }
            if (data?.getBooleanExtra(EXTRA_ALL, false)!!) {
                etInput?.addAtContent(null, AT_ALL_CONTENT)
                return
            }
            val resultIds = data.getStringArrayListExtra(EXTRA_RESULT_ID)
            for (i in resultNames!!.indices) {
                etInput?.addAtContent(resultIds!![i], resultNames[i].toString() + " ")
            }
        }
    }

    companion object {
        const val RESULT_AT = 250
        const val EXTRA_RESULT_NAME = "EXTRA_RESULT_NAME"
        const val EXTRA_RESULT_ID = "EXTRA_RESULT_ID"
        const val EXTRA_ALL = "EXTRA_ALL"
    }
}

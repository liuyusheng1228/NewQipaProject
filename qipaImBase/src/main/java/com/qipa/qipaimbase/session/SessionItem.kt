package com.qipa.qipaimbase.session

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.qipa.qipaimbase.ImBaseBridge
import com.qipa.qipaimbase.R
import com.qipa.qipaimbase.utils.Constants
import com.qipa.qipaimbase.utils.image.ImageLoaderUtils
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.ItemTypeAbstract
import com.qipa.qipaimbase.utils.recycleadapter.RvViewHolder
import java.lang.String

class SessionItem(private val updateOtherInfoListener: UpdateOtherInfoListener?) :
    ItemTypeAbstract() {
    private var iconView: ImageView? = null
    private var sessionData: SessionData? = null
    override fun openClick(): Boolean {
        return true
    }

    override val type = Constants.ITEM_TYPE_MSG

    override fun openLongClick(): Boolean {
        return true
    }

    override val layout: Int
        get() = R.layout.item_msg

    override fun fillContent(rvViewHolder: RvViewHolder?, position: Int, data: ItemData?) {
        sessionData = data as SessionData?
        var isSendFromMe = false
        if (sessionData?.lastMsgFr != null) {
            val businessListener: ImBaseBridge.BusinessListener? =
                ImBaseBridge.instance?.businessListener
            if (businessListener != null) {
                isSendFromMe = sessionData?.lastMsgFr.equals(businessListener.userId)
            }
        }
        sessionData?.itemPosition = (position)
        if (!TextUtils.isEmpty(sessionData?.nickName)) {
            (rvViewHolder?.getView(R.id.tvNickName) as TextView).setText(sessionData?.nickName)
        } else {
            (rvViewHolder?.getView(R.id.tvNickName) as TextView).setText(sessionData?.chatWith)
        }
        if (sessionData!!.isIgnoreAlert) {
            rvViewHolder.getView(R.id.ivBan).visibility = View.VISIBLE
        } else {
            rvViewHolder.getView(R.id.ivBan).visibility = View.GONE
        }
        if (TextUtils.isEmpty(sessionData?.timeContent)) {
            (rvViewHolder.getView(R.id.tvTime) as TextView).text = ""
        } else {
            (rvViewHolder.getView(R.id.tvTime) as TextView).setText(sessionData?.timeContent)
        }
        //       Z if (sessionData.isSticky()) {
//            rvViewHolder.getView(R.id.ivTop).setVisibility(View.VISIBLE);
//        } else {
        rvViewHolder.getView(R.id.ivTop).visibility = View.GONE
        //        }
        if (sessionData!!.isShowAtTip) {
            (rvViewHolder.getView(R.id.tvMsgContent) as TextView).setText(sessionData?.atMsg)
        } else {
            if (TextUtils.isEmpty(sessionData?.lastMsgFrName)) {
                (rvViewHolder.getView(R.id.tvMsgContent) as TextView).setText(sessionData?.lastMsgContent)
            } else {
                (rvViewHolder.getView(R.id.tvMsgContent) as TextView).text =
                    String.format(
                        "%s:%s",
                        sessionData?.lastMsgFrName,
                        sessionData?.lastMsgContent
                    )
            }
        }
        if (sessionData?.unreadCount!! > 0) {
            val tvTemp = rvViewHolder.getView(R.id.tvUnRead) as TextView
            tvTemp.visibility = View.VISIBLE
            //            if (sessionData.getUnreadCount() > UNREAD_MAX_SHOW) {
//                tvTemp.setText(UNREAD_MAX_SHOW + "+");
//            } else {
            tvTemp.setText(sessionData?.unreadCount.toString() + "")
            //            }
        } else {
            rvViewHolder.getView(R.id.tvUnRead).visibility = View.GONE
        }
        iconView = rvViewHolder.getView(R.id.ivIcon) as ImageView
        ImageLoaderUtils.getInstance().loadImage(
            iconView!!.context,
            sessionData?.icon,
            R.drawable.head_placeholder,
            iconView
        )
        if (updateOtherInfoListener != null) {
            if (sessionData?.nickName == null || shouldUpdateFromName(isSendFromMe)) {
                updateOtherInfoListener.onUpdateOtherInfo(sessionData)
            }
        }
    }

    private fun shouldUpdateFromName(isSendFromMe: Boolean): Boolean {
        return sessionData?.chatType === PhotonIMMessage.GROUP && sessionData!!.isUpdateFromInfo && !isSendFromMe
    }

    override val onClickViews: IntArray
        get() = intArrayOf(R.id.item_msg_llRoot)
    override val onLongClickViews: IntArray
        get() = intArrayOf(R.id.item_msg_llRoot)

    interface UpdateOtherInfoListener {
        fun onUpdateOtherInfo(sessionData: SessionData?)
    }

    companion object {
        private const val UNREAD_MAX_SHOW = 99
    }
}
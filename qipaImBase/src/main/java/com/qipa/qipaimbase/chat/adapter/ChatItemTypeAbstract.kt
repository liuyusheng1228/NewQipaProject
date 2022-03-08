package com.qipa.qipaimbase.chat.adapter

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import com.qipa.qipaimbase.R
import com.qipa.qipaimbase.chat.ChatData
import com.qipa.qipaimbase.session.PhotonIMMessage
import com.qipa.qipaimbase.utils.image.ImageLoaderUtils
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.ItemTypeAbstract
import com.qipa.qipaimbase.utils.recycleadapter.RvViewHolder

abstract class ChatItemTypeAbstract : ItemTypeAbstract() {
    protected var chatData: ChatData? = null
    private val illegalContent: String? = null
    private val sendStatsRead: String? = null
    private val sendStatsSent: String? = null


    @CallSuper
    override fun fillContent(rvViewHolder: RvViewHolder?, position: Int, data: ItemData?) {
        chatData = data as ChatData?
        chatData?.listPostion = (position)
        val view = rvViewHolder?.getView(R.id.tvTime) as TextView
        if (chatData?.timeContent != null) {
            view.visibility = View.VISIBLE
            view.setText(chatData?.timeContent)
        } else {
            view.visibility = View.GONE
        }
    }

    protected fun fillMsgContent(rvViewHolder: RvViewHolder) {
        val view: View = rvViewHolder.getView(R.id.tvSysInfo)
        view.visibility = View.GONE
        rvViewHolder.getView(R.id.llMsgRoot).setVisibility(View.VISIBLE)
        when (chatData?.msgType) {
            PhotonIMMessage.TEXT -> {
                val content = rvViewHolder.getView(R.id.tvContent) as TextView
                content.visibility = View.VISIBLE
                content.setText(chatData?.contentShow)
                rvViewHolder.getView(R.id.llVoice).setVisibility(View.GONE)
                rvViewHolder.getView(R.id.ivPic).setVisibility(View.GONE)
                ImageLoaderUtils.getInstance().loadImage(
                    view.context,
                    chatData?.icon,
                    R.drawable.head_placeholder,
                    rvViewHolder.getView(R.id.ivIcon) as ImageView
                )
            }
            PhotonIMMessage.AUDIO -> {
                rvViewHolder.getView(R.id.llVoice).setVisibility(View.VISIBLE)
                (rvViewHolder.getView(R.id.tvVoiceDuration) as TextView).setText(
                    chatData?.mediaTime.toString() + ""
                )
                rvViewHolder.getView(R.id.tvContent).setVisibility(View.GONE)
                rvViewHolder.getView(R.id.ivPic).setVisibility(View.GONE)
                ImageLoaderUtils.getInstance().loadImage(
                    view.context,
                    chatData?.icon,
                    R.drawable.head_placeholder,
                    rvViewHolder.getView(R.id.ivIcon) as ImageView
                )
            }
            PhotonIMMessage.IMAGE -> {
                val pic = rvViewHolder.getView(R.id.ivPic) as ImageView
                pic.visibility = View.VISIBLE
                // TODO: 2019-08-07 chatData.getLocalFile() 判断对方的
                if (!TextUtils.isEmpty(chatData?.localFile)) {
                    ImageLoaderUtils.getInstance().loadImage(
                        pic.context,
                        chatData?.localFile,
                        R.drawable.chat_placeholder,
                        pic
                    )
                } else {
                    ImageLoaderUtils.getInstance().loadImage(
                        pic.context,
                        chatData?.fileUrl,
                        R.drawable.chat_placeholder,
                        pic
                    )
                }
                rvViewHolder.getView(R.id.tvContent).setVisibility(View.GONE)
                rvViewHolder.getView(R.id.llVoice).setVisibility(View.GONE)
                ImageLoaderUtils.getInstance().loadImage(
                    view.context,
                    chatData?.icon,
                    R.drawable.head_placeholder,
                    rvViewHolder.getView(R.id.ivIcon) as ImageView
                )
            }
        }
    }
}

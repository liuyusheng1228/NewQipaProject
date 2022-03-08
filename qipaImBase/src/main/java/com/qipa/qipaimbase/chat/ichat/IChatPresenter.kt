package com.qipa.qipaimbase.chat.ichat

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.qipa.qipaimbase.chat.ChatData
import com.qipa.qipaimbase.utils.mvpbase.IModel
import com.qipa.qipaimbase.utils.mvpbase.IPresenter
import com.qipa.qipaimbase.utils.mvpbase.IView
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.RvBaseAdapter
import java.io.File

abstract class IChatPresenter<V : IChatView?, M : IChatModel?>(iView: V) :
    IPresenter<V, M>(iView) {
    abstract fun loadLocalHistory(
        chatType: Int, chatWith: String?, anchorMsgId: String?,
        beforeAuthor: Boolean, asc: Boolean,
        size: Int, myId: String?
    )

    abstract fun loadAllHistory(chatType: Int, chatWith: String?, size: Int, beginTimeStamp: Long)
    abstract fun loadAllHistory(
        chatType: Int,
        chatWith: String?,
        size: Int,
        beginTimeStamp: Long,
        endTimeStamp: Long
    )

    //    public abstract void loadRemoteHistory();
    //    public abstract void sendText(int chatType, String content, String chatWith, String fromId, String toId, String icon);
    //    public abstract void reSendText(int chatType, String msgId, String content, String chatWith, String fromId, String toId, String icon);
    //    public abstract void sendPic(int chatType, String absolutePath, String chatWith, String fromId, String toId, String icon);
    //    public abstract void reSendPic(int chatType, String msgId, String absolutePath, String chatWith, String fromId, String toId, String icon);
    //    public abstract void sendVoice(int chatType, String absolutePath, long time, String chatWith, String fromId, String toId, String icon);
    //    public abstract void reSendVoice(int chatType, String msgId, String absolutePath, long time, String chatWith, String fromId, String toId, String icon);
    abstract fun revertMsg(data: ChatData?)
    abstract fun sendReadMsg(messageData: ChatData?)
    abstract fun startRecord(context: Context?): File?
    abstract fun stopRecord()
    abstract fun play(context: Context?, fileName: String?)
    abstract fun cancelPlay()
    abstract fun cancelRecord()
    abstract fun getVoiceFile(chatData: ChatData?)
    abstract fun updateExtra(messageData: ChatData?)
    abstract fun removeChat(chatType: Int, chatWith: String?, id: String?)
    abstract fun getInfo(chatData: ChatData?)
    override fun getEmptyView(): V {
        return object : IChatView() {
            override fun onloadHistoryResult(
                chatData: List<ChatData>?,
                chatDataMap: Map<String?, ChatData>?
            ) {
            }

            override fun onRevertResult(data1: ChatData?, error: Int, msg: String?) {}
            override fun onGetChatVoiceFileResult(data: ChatData?, path: String?) {}
            override fun onRecordFinish(duration: Long) {}
            override fun updateUnreadStatus(data: ChatData?) {}
            override fun onGetIcon(chatData: ChatData?, iconUrl: String?, name: String?) {}
            override fun onRecordFailed() {}
            override fun getIPresenter(): IPresenter<in IView, in IModel>? {
                return null
            }

            override fun getRecycleView(): RecyclerView? {
                return null
            }

            override fun getAdapter(): RvBaseAdapter<ItemData>? {
                return null
            }


        } as V
    }

    abstract fun destoryVoiceHelper()
    abstract fun sendMsg(chatDataBuilder: ChatData.Builder?): ChatData?
    abstract fun reSendMsg(chatData: ChatData?)
}
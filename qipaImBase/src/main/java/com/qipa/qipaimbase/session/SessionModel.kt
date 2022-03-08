package com.qipa.qipaimbase.session

import android.text.TextUtils
import com.qipa.qipaimbase.ImBaseBridge
import com.qipa.qipaimbase.chat.ChatData
import com.qipa.qipaimbase.chat.ChatModel
import com.qipa.qipaimbase.session.isession.ISessionModel
import com.qipa.qipaimbase.utils.CollectionUtils
import com.qipa.qipaimbase.utils.Utils
import com.qipa.qipaimbase.utils.dbhelper.DBHelperUtils
import com.qipa.qipaimbase.utils.dbhelper.Profile
import com.qipa.qipaimbase.utils.http.jsons.*
import com.qipa.qipaimbase.utils.task.AsycTaskUtil
import com.qipa.qipaimbase.utils.task.TaskExecutor
import java.util.ArrayList
import java.util.concurrent.Callable


class SessionModel : ISessionModel() {
    override fun loadLocalHostoryMsg(onLoadHistoryListener: OnLoadHistoryListener?) {
        TaskExecutor.instance.createAsycTask({ localHistoryMsg }
        ,object : AsycTaskUtil.OnTaskListener{
                override fun onTaskFinished(result: Any?) {
                    onLoadHistoryListener?.onLoadHistory(result as List<SessionData?>)
                }

            })
    }

    override fun getOtherInfo(
        sessionData: SessionData?,
        onGetOtherInfoListener: OnGetOtherInfoListener?
    ) {
        TaskExecutor.instance.createAsycTask({ getOtherInfoInner(sessionData) }
        ,object : AsycTaskUtil.OnTaskListener{
                override fun onTaskFinished(result: Any?) {
                    if (onGetOtherInfoListener != null) {
                        onGetOtherInfoListener.onGetOtherInfo(result as JsonResult<in JsonRequestResult>)
                    }
                }

            })
    }

    private fun getOtherInfoInner(sessionData: SessionData?): Any? {
        return if (sessionData?.chatType ===PhotonIMMessage.GROUP) {
            if (sessionData?.nickName == null) {
                return getGroupInfo(sessionData.chatWith, sessionData)
            } else if (sessionData.isUpdateFromInfo) {
                return getUserinfo(sessionData.lastMsgFr, sessionData, false)
            }
            null
        } else {
            sessionData?.let { getUserinfo(sessionData?.chatWith, it, true) }
        }
    }

    private fun getGroupInfo(otherId: String?, sessionData: SessionData): Any? {
        val businessListener: ImBaseBridge.BusinessListener = ImBaseBridge.instance?.businessListener
            ?: return null
        val othersInfo: JsonResult<in JsonRequestResult> = businessListener.getGroupProfile(otherId)
        if (othersInfo.success()) {
            val jsonGroupProfile: JsonGroupProfile = othersInfo.get() as JsonGroupProfile
            val profile: JsonGroupProfile.DataBean.ProfileBean =
                jsonGroupProfile.getData().getProfile()
            val extra = sessionData.getExtra(profile.getName(), profile.getAvatar())
            //待修改
//            PhotonIMDatabase.getInstance()
//                .updateSessionExtra(sessionData.getChatType(), sessionData.getChatWith(), extra)
        }
        return othersInfo
    }

    private fun getUserinfo(
        otherId: String?,
        sessionData: SessionData,
        saveSessionExtra: Boolean
    ): Any? {
        val businessListener: ImBaseBridge.BusinessListener = ImBaseBridge.instance?.businessListener
            ?: return null
        val othersInfo: JsonResult<in JsonRequestResult> = businessListener.getOthersInfo(arrayOf(otherId))
        if (othersInfo.success()) {
            if ((othersInfo.get() as JsonOtherInfoMulti).getData().getLists().size > 0) {
                val lists: List<JsonOtherInfoMulti.DataBean.ListsBean> =
                    (othersInfo.get() as JsonOtherInfoMulti).getData().getLists()
                DBHelperUtils.instance.saveProfile(
                    lists[0].getUserId(),
                    lists[0].getAvatar(), lists[0].getNickname()
                )
            }
        }
        if (saveSessionExtra && othersInfo.success()) {
            // TODO: 2019-08-09 对服务器返回的数据进行校验
            val jsonOtherInfo: JsonOtherInfoMulti = othersInfo.get() as JsonOtherInfoMulti
            if (jsonOtherInfo.getData().getLists().size <= 0) {
                return othersInfo
            }
            val listsBean: JsonOtherInfoMulti.DataBean.ListsBean =
                jsonOtherInfo.getData().getLists().get(0)
            val extra = sessionData.getExtra(listsBean.getNickname(), listsBean.getAvatar())
            //待修改
//            PhotonIMDatabase.getInstance()
//                .updateSessionExtra(sessionData.getChatType(), sessionData.getChatWith(), extra)
        }
        return othersInfo
    }

   override fun saveSession(sessionData: SessionData?) {
        TaskExecutor.instance.createAsycTask(Callable {
            sessionData?.let {
                saveSessionInner(
                    it
                )
            }
        } as Callable<*>)
    }

    override fun deleteSession(
        data: SessionData?,
        onDeleteSessionListener: OnDeleteSessionListener?
    ) {
        TaskExecutor.instance.createAsycTask({
            if (data != null) {
                deleteSessionInner(data)
            }
        },object : AsycTaskUtil.OnTaskListener{
            override fun onTaskFinished(result: Any?) {
                if (onDeleteSessionListener != null) {
                    onDeleteSessionListener.onDeleteSession()
                }
            }

        })
    }

   override fun clearSession(data: SessionData?, onClearSessionListener: OnClearSessionListener?) {
        TaskExecutor.instance.createAsycTask({ data?.let { clearSessionInner(it) } },object : AsycTaskUtil.OnTaskListener
        {
            override fun onTaskFinished(result: Any?) {
                if (onClearSessionListener != null) {
                    onClearSessionListener.onClearSession()
                }
            }

        })
    }

    override fun getNewSession(
        chatType: Int,
        chatWith: String?,
        onGetSessionListener: OnGetSessionListener?
    ) {
//        TaskExecutor.getInstance().createAsycTask(()-> getSessionInner(chatType,chatWith), result -> {
//            if (onGetSessionListener != null) {
//                onGetSessionListener.onGetSession(result);
//            }
//        });
    }

    override fun getAllUnReadCount(onGetAllUnReadCount: OnGetAllUnReadCount?) {
        TaskExecutor.instance
            .createAsycTask({
                //待修改
//                PhotonIMDatabase.getInstance().getTotalUnreadCount()
            },object : AsycTaskUtil.OnTaskListener{
                override fun onTaskFinished(result: Any?) {
                    if (onGetAllUnReadCount != null) {
                        onGetAllUnReadCount.onGetAllUnReadCount(result as Int)
                    }
                }

            })
    }

    override fun updateSessionUnreadCount(chatType: Int, chatWith: String?, unReadCount: Int) {
//        TaskExecutor.instance.createAsycTask {
//            if (chatType == Constants.CHAT_GROUP) {
//                //待修改
//                PhotonIMDatabase.getInstance()
//                    .updateSessionAtType(chatType, chatWith, PhotonIMMessage.SESSION_NO_AT)
//            }
//            PhotonIMDatabase.getInstance().updateSessionUnreadCount(chatType, chatWith, unReadCount)
//            null
//        }
    }

    override fun loadHistoryFromRemote(onLoadHistoryFromRemoteListener: OnLoadHistoryFromRemoteListener?) {
        TaskExecutor.instance.createAsycTask({ requestJson },object : AsycTaskUtil.OnTaskListener{
            override fun onTaskFinished(result: Any?) {
                if (onLoadHistoryFromRemoteListener != null) {
                    onLoadHistoryFromRemoteListener.onLoadHistoryFromRemote(result as List<SessionData?>)
                }
            }

        })
    }

    override fun resendSendingStatusMsgs() {
        TaskExecutor.instance.createAsycTaskChat(label@ Callable {
            val messageList: ArrayList<PhotonIMMessage> = arrayListOf()
            //待修改
//                PhotonIMDatabase.getInstance().findMessageListByStatus(Constants.CHAT_SENDING)
            if (CollectionUtils.isEmpty(messageList)) {
                return@Callable null
            }
            val iconTemp: String? = ImBaseBridge.instance?.myIcon
            val businessListener: ImBaseBridge.BusinessListener? =
                ImBaseBridge.instance?.businessListener
            var myId = ""
            if (businessListener != null) {
                myId = businessListener.userId.toString()
            }
            for (photonIMMessage in messageList) {
                val chatData: ChatData = ChatData.Builder()
                    .msgType(photonIMMessage.messageType)
                    .chatWith(photonIMMessage.chatWith)
                    .localFile(photonIMMessage.localFile)
                    .fileUrl(photonIMMessage.fileUrl)
                    .from(photonIMMessage.from)
                    .chatType(photonIMMessage.chatType)
                    .to(photonIMMessage.to)
                    .time(photonIMMessage.time)
                    .notic(photonIMMessage.notic)
                    .content(photonIMMessage.content)
                    .icon(iconTemp)
                    .msgId(photonIMMessage.id)
                    .msgStatus(photonIMMessage.status)
                    .itemType(ChatModel.getItemType(photonIMMessage, myId))
                    .voiceDuration(photonIMMessage.mediaTime)
                    .build()
//                PhotonIMClient.getInstance()
//                    .sendMessage(photonIMMessage, object : PhotonIMSendCallback() {
//                        fun onSent(code: Int, msg: String?, retTime: Long) {
//                            EventBus.getDefault().post(msg?.let {
//                                ChatDataWrapper(
//                                    chatData,
//                                    code,
//                                    it
//                                )
//                            })
//                        }
//                    })

//                PhotonIMClient.getInstance().sendMessage(photonIMMessage, (code, msg) -> {
//                    EventBus.getDefault().post(new ChatDataWrapper(chatData, code, msg));
//                });
            }
            null
        } as Callable<*>)
    }

    override fun updateSessionAtType(sessionData: SessionData?) {
        //待修改
//        TaskExecutor.instance.createAsycTask {
//            PhotonIMDatabase.getInstance().updateSessionAtType(
//                sessionData?.getChatType(),
//                sessionData?.getChatWith(),
//                Constants.CHAT_SESSION_NO_AT
//            )
            null
//        }
    }


    private fun getSessionInner(chatType: Int, chatWith: String): Any? {
        val session: PhotonIMSession = PhotonIMSession()
//            PhotonIMDatabase.getInstance().findSession(chatType, chatWith)
        return SessionData(session)
    }


    private fun clearSessionInner(data: SessionData): Any? {
        //待修改
//        PhotonIMDatabase.getInstance().clearMessage(data.getChatType(), data.getChatWith())
        return null
    }

    private fun deleteSessionInner(data: SessionData): Any? {
        //待修改
//        PhotonIMDatabase.getInstance().deleteSession(data.getChatType(), data.getChatWith(), true)
        return null
    }

    private fun saveSessionInner(sessionData: SessionData): Any? {
        val photonIMSession = sessionData.convertToPhotonIMSession()
        //待修改
//        PhotonIMDatabase.getInstance().saveSession(photonIMSession)
        return null
    }

    //                        .sticky(list.getIsTop() == 0) //不考虑置顶
    private val requestJson: Any?
        private get() {
            val businessListener: ImBaseBridge.BusinessListener =
                ImBaseBridge.instance?.businessListener
                    ?: return null
            val recentUser: JsonContactRecent? = businessListener.recentUser
            if (recentUser?.success()!!) {
                val lists: List<JsonContactRecent.DataBean.ListsBean> =
                    recentUser?.getData()?.getLists() as List<JsonContactRecent.DataBean.ListsBean>
                val sessionData: MutableList<SessionData> = ArrayList<SessionData>(lists.size)
                var msgDataTemp: SessionData
                val photonIMSessions: MutableList<PhotonIMSession?> = ArrayList<PhotonIMSession?>()
                for (list in lists) {
                    if (list == null) {
                        continue
                    }
//                    msgDataTemp = SessionData.Builder()
//                        .lastMsgContent(//待修改
////                            PhotonIMDatabase.getInstance().getSessionLastMsgId(
////                                list.getType(),
////                                if (list.getType() === Constants.CHAT_SINGLE) list.getUserId() else list.getId()
////                            )
////                        ) //                        .sticky(list.getIsTop() == 0) //不考虑置顶
//                        .chatWith(if (list.getType() === Constants.CHAT_SINGLE) list.getUserId() else list.getId())
//                        .chatType(list.getType())
//                        .build()!!
//                    msgDataTemp.setExtra(list.getNickname(), list.getAvatar())
//                    photonIMSessions.add(msgDataTemp.convertToPhotonIMSession())
//                    sessionData.add(msgDataTemp)
                }
                //待修改
//                PhotonIMDatabase.getInstance().saveSessionBatch(photonIMSessions)
                return sessionData
            }
            return null
        }

    companion object {
        //置顶// FIXME 默认自定义消息根据lastMsgContent进行填充，待修改
        // TODO: 2019-08-09 没有加载更多
        val localHistoryMsg: Any?
            get() {
                // TODO: 2019-08-09 没有加载更多
                val sessionList: ArrayList<PhotonIMSession> = arrayListOf()//待修改
//                    PhotonIMDatabase.getInstance().findSessionList(0, 50, false)
                if (CollectionUtils.isEmpty(sessionList)) {
                    return null
                }
                val result = ArrayList<SessionData>(sessionList.size)
                var sessionData: SessionData
                val sticker = ArrayList<SessionData>()
                var tempContent: String
                var showAtMsg: Boolean
                var lastMsgFrName: String
                var updateFromInfo: Boolean
                for (photonIMSession in sessionList) {
                    lastMsgFrName = ""
                    updateFromInfo = false
                    tempContent = when (photonIMSession.lastMsgType) {
                        PhotonIMMessage.AUDIO -> if (!TextUtils.isEmpty(photonIMSession.lastMsgContent)) {
                            photonIMSession.lastMsgContent
                        } else {
                            "[语音]"
                        }
                        PhotonIMMessage.IMAGE -> if (!TextUtils.isEmpty(photonIMSession.lastMsgContent)) {
                            photonIMSession.lastMsgContent
                        } else {
                            "[图片]"
                        }
                        PhotonIMMessage.VIDEO -> if (!TextUtils.isEmpty(photonIMSession.lastMsgContent)) {
                            photonIMSession.lastMsgContent
                        } else {
                            "[视频]"
                        }
                        PhotonIMMessage.TEXT -> photonIMSession.lastMsgContent
                        PhotonIMMessage.RAW ->                     // FIXME 默认自定义消息根据lastMsgContent进行填充，待修改
                            "[自定义消息]" + photonIMSession.lastMsgContent
                        else -> "[未知消息]"
                    }
                    var isAtMeMsg = false
                    var loginUserId = ""
                    val businessListener: ImBaseBridge.BusinessListener? =
                        ImBaseBridge.instance?.businessListener
                    if (businessListener != null) {
                        loginUserId = businessListener.userId.toString()
                    }
                    if (!photonIMSession.lastMsgFr.equals(loginUserId)
                        && photonIMSession.chatType === PhotonIMMessage.GROUP
                    ) {
                        isAtMeMsg = isAtMeMsg(photonIMSession)
                        val profile: Profile? =
                            DBHelperUtils.instance.findProfile(photonIMSession.lastMsgFr)
                        updateFromInfo = profile == null
                        lastMsgFrName =
                            if (profile == null) photonIMSession.lastMsgFr else profile.name.toString()
                    }
                    sessionData = SessionData.Builder()
                        .chatType(photonIMSession.chatType)
                        .chatWith(photonIMSession.chatWith)
                        .draft(photonIMSession.draft)
                        .extra(photonIMSession.extra)
                        .ignoreAlert(photonIMSession.ignoreAlert)
                        .orderId(photonIMSession.orderId)
                        .unreadCount(photonIMSession.unreadCount)
                        .lastMsgContent(tempContent)
                        .lastMsgFr(photonIMSession.lastMsgFr)
                        .lastMsgFrName(lastMsgFrName)
                        .updateFromInfo(updateFromInfo)
                        .lastMsgStatus(photonIMSession.lastMsgStatus)
                        .lastMsgId(photonIMSession.lastMsgId)
                        .lastMsgTime(photonIMSession.lastMsgTime)
                        .lastMsgTo(photonIMSession.lastMsgTo)
                        .lastMsgType(photonIMSession.lastMsgType)
                        .showAtTip((isAtMeMsg && showAtTip(photonIMSession)).also {
                            showAtMsg = it
                        })
                        .generateAtMsg(
                            if (showAtMsg) Utils.generateAtMsg(
                                lastMsgFrName,
                                tempContent
                            ) else null
                        )
                        .build()!!
                    if (sessionData.isSticky) {
                        sticker.add(sessionData)
                    } else {
                        result.add(sessionData)
                    }
                }
                if (sticker.size != 0) { //置顶
                    result.addAll(sticker)
                }
                return result
            }

        private fun isAtMeMsg(photonIMSession: PhotonIMSession): Boolean {
            return photonIMSession.atType === PhotonIMMessage.SESSION_AT_ME || photonIMSession.atType ===PhotonIMMessage.SESSION_AT_ALL
            //        return true;
        }

        private fun showAtTip(photonIMSession: PhotonIMSession): Boolean {
            return photonIMSession.unreadCount !== 0
        }
    }
}
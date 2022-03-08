package com.qipa.qipaimbase.chat

import com.qipa.qipaimbase.ImBaseBridge
import com.qipa.qipaimbase.chat.ichat.IChatModel
import com.qipa.qipaimbase.session.PhotonIMMessage
import com.qipa.qipaimbase.utils.Constants
import com.qipa.qipaimbase.utils.TimeUtils
import com.qipa.qipaimbase.utils.dbhelper.DBHelperUtils
import com.qipa.qipaimbase.utils.dbhelper.Profile
import com.qipa.qipaimbase.utils.event.ChatDataWrapper
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import com.qipa.qipaimbase.utils.looperexecute.CustomRunnable
import com.qipa.qipaimbase.utils.looperexecute.MainLooperExecuteUtil
import com.qipa.qipaimbase.utils.task.AsycTaskUtil
import com.qipa.qipaimbase.utils.task.TaskExecutor
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.Callable


class ChatModel : IChatModel() {
    override fun loadLocalHistory(
        chatType: Int,
        chatWith: String?,
        anchorMsgId: String?,
        beforeAuthor: Boolean,
        asc: Boolean,
        size: Int,
        myId: String?,
        listener: OnLoadHistoryListener?
    ) {
        TaskExecutor.instance.createAsycTask({
            getLocalHistory(
                chatType,
                chatWith!!, anchorMsgId!!, beforeAuthor, asc, size, myId!!
            )
        },object : AsycTaskUtil.OnTaskListener{
            override fun onTaskFinished(result: Any?) {
                val map = result as Map<*, *>?
                if (map == null) {
                    listener?.onLoadHistory(null, null)
                    return
                }
                listener?.onLoadHistory(
                    map["list"] as List<ChatData?>?,
                    map["map"] as Map<String?, ChatData?>?
                )
            }

        }
        )
    }

    private fun getLocalHistory(
        chatType: Int,
        chatWith: String,
        anchorMsgId: String,
        beforeAuthor: Boolean,
        asc: Boolean,
        size: Int,
        myId: String
    ): Any? {
        //待修改
//        ArrayList<PhotonIMMessage> photonIMMessages = PhotonIMDatabase.getInstance().findMessageListByIdRange(chatType, chatWith, anchorMsgId, beforeAuthor, asc, size);
//        val result: PhotonIMDatabase.LoadHistoryResult =
//            PhotonIMDatabase.getInstance().loadHistoryMessage(chatType, chatWith, anchorMsgId, size)
        return null
    }

    private fun convertMap(photonIMMessages: ArrayList<PhotonIMMessage>?): Any? {
        if (photonIMMessages == null) {
            return null
        }
        val r: MutableMap<String, Any> = HashMap(2)
        val result: MutableList<ChatData> = ArrayList(photonIMMessages.size)
        val resultMap: MutableMap<String, ChatData> = HashMap(photonIMMessages.size)
        var chatData: ChatData
        var preData: ChatData? = null
        var profileTemp: Profile
        var iconTemp: String?
        var nameTemp: String? = null
        var msgStatus: Int
        val businessListener: ImBaseBridge.BusinessListener? = ImBaseBridge.instance?.businessListener
        var myId = ""
        if (businessListener != null) {
            myId = businessListener.userId.toString()
        }
        for (photonIMMessage in photonIMMessages) {
            if (photonIMMessage.from.equals(myId)) {
                iconTemp = ImBaseBridge.instance?.myIcon
            } else {
                profileTemp = DBHelperUtils.instance.findProfile(photonIMMessage.from)!!
                iconTemp = if (profileTemp != null) profileTemp.icon else null
                nameTemp = if (profileTemp != null) profileTemp.name else null
            }
            msgStatus = photonIMMessage.status
            //            if (photonIMMessage.status == PhotonIMMessage.SENDING) {//从数据库读取的消息状态如果是发送中，转换为发送失败
//                msgStatus = PhotonIMMessage.SEND_FAILED;
//            }
            chatData = ChatData.Builder()
                .msgType(photonIMMessage.messageType)
                .chatWith(photonIMMessage.chatWith)
                .localFile(photonIMMessage.localFile)
                .fileUrl(photonIMMessage.fileUrl)
                .from(photonIMMessage.from)
                .fromName(nameTemp)
                .chatType(photonIMMessage.chatType)
                .to(photonIMMessage.to)
                .time(photonIMMessage.time)
                .timeContent(
                    TimeUtils.getTimeContent(
                        photonIMMessage.time,
                        if (preData == null) 0 else preData.time
                    )
                )
                .notic(photonIMMessage.notic)
                .content(photonIMMessage.content)
                .remainHistory(photonIMMessage.remainHistory) //                    .extra(photonIMMessage.extra)
                //                    .icon(photonIMMessage)
                .icon(iconTemp)
                .msgId(photonIMMessage.id)
                .msgStatus(msgStatus)
                .itemType(getItemType(photonIMMessage, myId))
                .voiceDuration(photonIMMessage.mediaTime)
                .build()
            result.add(chatData)
            resultMap[photonIMMessage.id] = chatData
            preData = chatData
        }
        r["list"] = result
        r["map"] = resultMap
        return r
    }

    override fun loadAllHistory(
        chatType: Int,
        chatWith: String?,
        size: Int,
        beginTimeStamp: Long,
        listener: OnLoadHistoryListener?
    ) {
        TaskExecutor.instance
            .createAsycTask({ chatWith?.let { getAllHistory(chatType, it, size, beginTimeStamp) } }
            ,object : AsycTaskUtil.OnTaskListener{
                    override fun onTaskFinished(result: Any?) {
                        if (listener == null) {
                            return
                        }
                        val map = result as Map<*, *>
                        if (map == null) {
                            listener.onLoadHistory(null, null)
                        } else {
                            listener.onLoadHistory(
                                map["list"] as List<ChatData?>?,
                                map["map"] as Map<String?, ChatData?>?
                            )
                        }
                    }

                })
    }

    override fun loadAllHistory(
        chatType: Int,
        chatWith: String?,
        size: Int,
        beginTimeStamp: Long,
        endTimeStamp: Long,
        listener: OnLoadHistoryListener?
    ) {
        TaskExecutor.instance.createAsycTask({
            getAllHistory(
                chatType,
                chatWith!!, size, beginTimeStamp, endTimeStamp
            )
        }
        ,object :  AsycTaskUtil.OnTaskListener{
                override fun onTaskFinished(result: Any?) {
                    if (listener == null) {
                        return
                    }
                    val map = result as Map<*, *>
                    if (map == null) {
                        listener.onLoadHistory(null, null)
                    } else {
                        listener.onLoadHistory(
                            map["list"] as List<ChatData?>?,
                            map["map"] as Map<String?, ChatData?>?
                        )
                    }
                }

            })
    }

    private fun getAllHistory(
        chatType: Int,
        chatWith: String,
        size: Int,
        beginTimeStamp: Long
    ): Any? {
        val resultList = ArrayList<PhotonIMMessage>()
        //待修改
//        val result: PhotonIMDatabase.SyncHistoryResult =
//            PhotonIMDatabase.getInstance()
//                .syncHistoryMessagesFromServer(chatType, chatWith, size, 0)
//                ?: return null
//        if (result.ec === 0) {
//            resultList.addAll(result.syncMsgList)
//        }
        return convertMap(resultList)
    }



    private fun getAllHistory(
        chatType: Int,
        chatWith: String,
        size: Int,
        beginTimeStamp: Long,
        endTimeStamp: Long
    ): Any? {
        val resultList = ArrayList<PhotonIMMessage>()
        //待修改
//        val result: PhotonIMDatabase.SyncHistoryResult = PhotonIMDatabase.getInstance()
//            .syncHistoryMessagesFromServer(
//                chatType,
//                chatWith,
//                "",
//                size,
//                beginTimeStamp,
//                endTimeStamp
//            )
//            ?: return null
//        if (result.ec === 0) {
//            PhotonIMDatabase.getInstance().saveMessageBatch(chatType, chatWith, result.syncMsgList)
//            resultList.addAll(result.syncMsgList)
//        }
        return convertMap(resultList)
    }

    override fun sendTextMsg(chatData: ChatData?) {
        chatData?.let { sendTextMsgInner(it) }
    }

    private fun sendTextMsgInner(chatData: ChatData) {
        EventBus.getDefault().post(ChatDataWrapper(chatData, PhotonIMMessage.SENDING,
            null
        ))
        TaskExecutor.instance.createAsycTask { sendMsg(chatData, null) }
    }

    override fun uploadPic(chatData: ChatData?, onPicUploadListener: OnPicUploadListener?) {
        if (chatData != null) {
            uploadPicInner(chatData, onPicUploadListener)
        }
    }

    private fun uploadPicInner(chatData: ChatData, onPicUploadListener: OnPicUploadListener?) {
        EventBus.getDefault().post(ChatDataWrapper(chatData, PhotonIMMessage.SENDING,
            null
        ))
        TaskExecutor.instance.createAsycTask({
            val message: PhotonIMMessage = chatData.convertToIMMessage()
            //待修改
//            PhotonIMDatabase.getInstance().saveMessage(message)
            val businessListener: ImBaseBridge.BusinessListener? =
                ImBaseBridge.instance?.businessListener
                    ?: return@createAsycTask null
            businessListener?.sendPic(chatData.localFile)
        },object :AsycTaskUtil.OnTaskListener{
            override fun onTaskFinished(result: Any?) {
                if (onPicUploadListener != null) {
                    onPicUploadListener.onPicUpload(chatData, result as JsonResult<JsonRequestResult>)
                }
            }

        })
    }

   override fun uploadVoiceFile(
       chatData: ChatData?,
       onVoiceUploadListener: OnVoiceUploadListener?
   ) {
       if (chatData != null) {
           uploadVoiceFileInner(chatData, onVoiceUploadListener)
       }
    }

    private fun uploadVoiceFileInner(
        chatData: ChatData,
        onVoiceUploadListener: OnVoiceUploadListener?
    ) {
        EventBus.getDefault().post(ChatDataWrapper(chatData, PhotonIMMessage.SENDING,
            null
        ))
        TaskExecutor.instance.createAsycTask({
            val message: PhotonIMMessage = chatData.convertToIMMessage()
            //待修改
//            PhotonIMDatabase.getInstance().saveMessage(message)
            val businessListener: ImBaseBridge.BusinessListener =
                ImBaseBridge.instance?.businessListener
                    ?: return@createAsycTask null
            businessListener.sendVoiceFile(chatData.localFile)
        },object : AsycTaskUtil.OnTaskListener{
            override fun onTaskFinished(result: Any?) {
                if (onVoiceUploadListener != null) {
                    onVoiceUploadListener.onVoiceFileUpload(chatData, result as JsonResult<JsonRequestResult>)
                }
            }

        })
    }

    override fun sendPicMsg(chatData: ChatData?, onMsgSendListener: OnMsgSendListener?) {
        TaskExecutor.instance.createAsycTask { chatData?.let { sendMsg(it, onMsgSendListener) } }
    }

    override fun sendMsgMulti(chatDatas: List<ChatData?>?, onMsgSendListener: OnMsgSendListener?) {
        TaskExecutor.instance.createAsycTask() {
            if (chatDatas?.size!! <= 0) return@createAsycTask null
            if (chatDatas != null) {
                for (chatDatum in chatDatas) {
                    chatDatum?.let { sendMsg(it, null) }
                }
            }
            null
        }
    }

    override fun sendVoiceFileMsg(chatData: ChatData?, onMsgSendListener: OnMsgSendListener?) {
        TaskExecutor.instance.createAsycTaskChat { chatData?.let { sendMsg(it, onMsgSendListener) } }
    }

    override fun revertMsg(data: ChatData?, onRevertListener: OnRevertListener?) {
        TaskExecutor.instance.createAsycTask { data?.let { sendRevertMsg(it, onRevertListener) } }
    }

    override fun sendReadMsg(messageData: ChatData?, onSendReadListener: OnSendReadListener?) {
        TaskExecutor.instance.createAsycTask {
            if (messageData != null) {
                sendReadMsgInner(
                    messageData,
                    onSendReadListener
                )
            }
        }
    }
    override fun getVoiceFile(data: ChatData?, savePath: String?, onGetFileListener: OnGetFileListener?) {
        TaskExecutor.instance.createAsycTask {
            data?.fileUrl?.let {
                getVoiceFileInner(data, it,
                    savePath!!, object : OnGetFileListener {
                        override fun onGetFile(path: String?) {
        //                        PhotonIMDatabase.getInstance().updateMessageLocalFile(
        //                            data.getChatType(),
        //                            data.getChatWith(),
        //                            data.getMsgId(),
        //                            savePath
        //                        )
                            val customRunnable = CustomRunnable()
                            customRunnable.setRunnable {
                                onGetFileListener?.onGetFile(path)
                            }
                            MainLooperExecuteUtil.instance?.post(customRunnable)
                        }
                    })
            }
        }
    }

    override fun updateExtra(messageData: ChatData?) {
        TaskExecutor.instance.createAsycTask() {
            //待修改
//            PhotonIMDatabase.getInstance()
//                .updateMessageExtra(
//                    messageData.getChatType(), messageData.getChatWith(),
//                    messageData.getMsgId(), null
//                )
//            null
        }
    }

    override fun removeChat(chatType: Int, chatWith: String?, id: String?) {
        TaskExecutor.instance.createAsycTask {
            //待修改
//            PhotonIMDatabase.getInstance().deleteMessage(chatType, chatWith, id)
            null
        }
    }

    override fun updateStatus(chatType: Int, chatWith: String?, id: String?, status: Int) {
        TaskExecutor.instance.createAsycTask(Callable<Any?> {
//            PhotonIMDatabase.getInstance().updataMessageStatus(chatType, chatWith, id, status)
            null
        })
    }

    private fun getVoiceFileInner(
        data: ChatData,
        fileUrl: String,
        savePath: String,
        onGetFileListener: OnGetFileListener
    ): Any? {
//        val jsonResult: JsonResult =
//            HttpUtils.getInstance().getFile(fileUrl, savePath, onGetFileListener) as JsonResult
        return null
    }


    private fun sendReadMsgInner(
        messageData: ChatData,
        onSendReadListener: OnSendReadListener?
    ): Any? {
        val msgList: MutableList<String> = ArrayList()
        messageData.msgId?.let { msgList.add(it) }
        //待修改
//        PhotonIMClient.getInstance().sendReadMessage(
//            messageData.getTo(),
//            messageData.getFrom(),
//            msgList,
//            object : PhotonIMSendCallback() {
//                fun onSent(error: Int, msg: String?, retTime: Long) {
//                    LogUtils.log(TAG, String.format("send error:%d", error))
//                    if (error == MSG_ERROR_CODE_SUCCESS) {
//                        PhotonIMDatabase.getInstance().updataMessageStatus(
//                            messageData.getChatType(),
//                            messageData.getChatWith(),
//                            messageData.getMsgId(),
//                            PhotonIMMessage.RECV_READ
//                        )
//                    }
//                    if (onSendReadListener != null) {
//                        val customRunnable = CustomRunnable()
//                        customRunnable.setRunnable {
//                            onSendReadListener.onSendRead(
//                                messageData,
//                                error,
//                                msg
//                            )
//                        }
//                        MainLooperExecuteUtil.getInstance().post(customRunnable)
//                    }
//                }
//            })
        //
        return null
    }

    private fun sendRevertMsg(data: ChatData, onRevertListener: OnRevertListener?): Any? {
        //待修改
//        PhotonIMClient.getInstance().recallMessage(
//            data.getChatType(),
//            data.getFrom(),
//            data.getTo(),
//            data.getMsgId(),
//            data.getTime(),
//            object : PhotonIMSendCallback() {
//                fun onSent(error: Int, msg: String?, retTime: Long) {
//                    if (onRevertListener != null) {
//                        val customRunnable = CustomRunnable()
//                        customRunnable.setRunnable { onRevertListener.onRevert(data, error, msg) }
//                        MainLooperExecuteUtil.getInstance().post(customRunnable)
//                    }
//                }
//            })
        return null
    }

    private fun sendMsg(chatData: ChatData, onMsgSendListener: OnMsgSendListener?): Any? {
        val message: PhotonIMMessage = chatData.convertToIMMessage()
        //待修改
//        PhotonIMDatabase.getInstance().saveMessage(message)
//        PhotonIMClient.getInstance().sendMessage(message, object : PhotonIMSendCallback() {
//            fun onSent(code: Int, msg: String?, retTime: Long) {
//                EventBus.getDefault().post(ChatDataWrapper(chatData, code, msg))
//            }
//        })
        return null
    }

    companion object {
        private const val TAG = "ChatModel"
        const val MSG_ERROR_CODE_SUCCESS = 0 //发送成功
        const val MSG_ERROR_CODE_SERVER_ERROR = 1000 //服务器内部错误
        const val MSG_ERROR_CODE_SERVER_NO_GROUP_MEMBER = 503 //非群成员不能发送
        const val MSG_ERROR_CODE_GROUP_CLOSED = 1008 //客户端群消息开关关闭
        const val MSG_ERROR_CODE_TEXT_ILLEGAL = 1001 //消息内容包含敏感词
        const val MSG_ERROR_CODE_PIC_ILLEGAL = 1002 //图片非法
        const val MSG_ERROR_CODE_FREQUENCY = 1003 //消息发送频率过高
        const val MSG_ERROR_CODE_CANT_REVOKE = 1004 //消息不可撤回
        const val MSG_ERROR_CODE_UPLOAD_PIC_FAILED = 2000 //图片上传失败  自己定义，非服务器返回
        const val MSG_ERROR_CODE_TIME_OUT = -1 // 发送超时，java层定义，非服务器返回
        fun getItemType(photonIMMessage: PhotonIMMessage, myId: String?): Int {
            if (photonIMMessage.status === PhotonIMMessage.RECALL) {
                return Constants.ITEM_TYPE_CHAT_SYSINFO
            }
            return if (photonIMMessage.from.equals(myId)) Constants.ITEM_TYPE_CHAT_NORMAL_RIGHT else Constants.ITEM_TYPE_CHAT_NORMAL_LEFT
        }
    }
}
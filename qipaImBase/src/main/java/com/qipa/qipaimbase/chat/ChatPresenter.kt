package com.qipa.qipaimbase.chat

import android.content.Context
import android.os.Environment
import top.oply.opuslib.OpusRecorder
import com.qipa.qipaimbase.ImBaseBridge
import com.qipa.qipaimbase.chat.ichat.IChatModel
import com.qipa.qipaimbase.chat.ichat.IChatPresenter
import com.qipa.qipaimbase.chat.ichat.IChatView
import com.qipa.qipaimbase.session.PhotonIMMessage
import com.qipa.qipaimbase.utils.Constants
import com.qipa.qipaimbase.utils.FileUtils
import com.qipa.qipaimbase.utils.LogUtils
import com.qipa.qipaimbase.utils.VoiceHelper
import com.qipa.qipaimbase.utils.event.ChatDataWrapper
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import com.qipa.qipaimbase.utils.http.jsons.JsonUploadImage
import com.qipa.qipaimbase.utils.http.jsons.JsonUploadVoice
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.*


open class ChatPresenter(iView: IChatView?) : IChatPresenter<IChatView?, IChatModel?>(iView) {
    private var voiceHelper: VoiceHelper? = null

    @Volatile
    private var voiceTimeStart: Long = 0
    private var sendReadStatusSet: MutableSet<String>? = null
    private var getUserInfoSet: MutableSet<String>? = null
    override fun loadLocalHistory(
        chatType: Int, chatWith: String?, anchorMsgId: String?,
        beforeAuthor: Boolean, asc: Boolean,
        size: Int, myId: String?
    ) {
        getiModel()?.loadLocalHistory(chatType,
            chatWith,
            anchorMsgId,
            beforeAuthor,
            asc,
            size,
            myId, object : IChatModel.OnLoadHistoryListener{
                override fun onLoadHistory(
                    chatData: List<ChatData?>?,
                    chatDataMap: Map<String?, ChatData?>?
                ) {
                    getIView()?.onloadHistoryResult(
                        chatData as List<ChatData>?, chatDataMap as Map<String?, ChatData>?
                    )
                }

            })
    }

    override fun loadAllHistory(chatType: Int, chatWith: String?, size: Int, beginTimeStamp: Long) {
        getiModel()?.loadAllHistory(chatType, chatWith, size, beginTimeStamp,
            object : IChatModel.OnLoadHistoryListener{
                override fun onLoadHistory(
                    chatData: List<ChatData?>?,
                    chatDataMap: Map<String?, ChatData?>?
                ) {
                    getIView()?.onloadHistoryResult(chatData as List<ChatData>,
                        chatDataMap as Map<String?, ChatData>
                    )
                }

            })
    }

    override fun loadAllHistory(
        chatType: Int,
        chatWith: String?,
        size: Int,
        beginTimeStamp: Long,
        endTimeStamp: Long
    ) {
        getiModel()!!.loadAllHistory(chatType, chatWith, size, beginTimeStamp, endTimeStamp,
            object : IChatModel.OnLoadHistoryListener{
                override fun onLoadHistory(
                    chatData: List<ChatData?>?,
                    chatDataMap: Map<String?, ChatData?>?
                ) {
                    getIView()?.onloadHistoryResult(chatData as List<ChatData>,
                        chatDataMap as Map<String?, ChatData>
                    )
                }

            })
    }
    //    @Override
    //    public void loadRemoteHistory() {
    //        getiModel().loadRemoteHistorey(new IChatModel.OnLoadHistoryListener() {
    //            @Override
    //            public void onLoadHistory(List<ChatData> chatData, Map<String, ChatData> chatDataMap) {
    //                getIView().loadRemoteHistorey(chatData, chatDataMap);
    //            }
    //        });
    //    }
    //    @Override
    //    public void sendText(int chatType, String content, String chatWith, String fromId, String toId, String icon) {
    //        sendTextInner(chatType, null, content, chatWith, fromId, toId, icon);
    //    }
    override fun sendMsg(chatDataBuilder: ChatData.Builder?): ChatData? {
        val chatData = chatDataBuilder?.msgId(msgID)?.time(System.currentTimeMillis())
            ?.itemType(Constants.ITEM_TYPE_CHAT_NORMAL_RIGHT)
            ?.msgStatus(PhotonIMMessage.SENDING)?.build()
        when (chatDataBuilder?.msgType) {
            PhotonIMMessage.TEXT -> getiModel()?.sendTextMsg(chatData)
            PhotonIMMessage.IMAGE -> chatData?.let { sendPicMsgInner(it) }
            PhotonIMMessage.AUDIO -> chatData?.let { sendVoice(it) }
        }
        return chatData
    }

    override fun reSendMsg(chatData: ChatData?) {
        chatData?.notic = ("")
        chatData?.msgStatus = (PhotonIMMessage.SENDING)
        chatData?.time = (System.currentTimeMillis())
        when (chatData?.msgType) {
            PhotonIMMessage.TEXT -> getiModel()?.sendTextMsg(chatData)
            PhotonIMMessage.IMAGE -> sendPicMsgInner(chatData)
            PhotonIMMessage.AUDIO -> sendVoice(chatData)
        }
    }

    private val msgID: String
        private get() = UUID.randomUUID().toString()

    //    @Override
    //    public void reSendText(int chatType, String msgId, String content, String chatWith, String fromId, String toId, String icon) {
    //        sendTextInner(chatType, msgId, content, chatWith, fromId, toId, icon);
    //    }
    //    private void sendTextInner(int chatType, String msgId, String content, String chatWith, String fromId, String toId, String icon) {
    //        getiModel().sendTextMsg(chatType, msgId, content, chatWith, fromId, toId, icon, null);
    //    }
    //    @Override
    //    public void sendPic(int chatType, String absolutePath, String chatWith, String fromId, String toId, String icon) {
    //        sendPicInner(chatType, null, absolutePath, chatWith, fromId, toId, icon);
    //    }
    //    @Override
    //    public void reSendPic(int chatType, String msgId, String absolutePath, String chatWith, String fromId, String toId, String icon) {
    //        sendPicInner(chatType, msgId, absolutePath, chatWith, fromId, toId, icon);
    //    }
    //    private void sendPicInner(int chatType, String msgId, String absolutePath, String chatWith, String fromId, String toId, String icon) {
    //        getiModel().uploadPic(chatType, msgId, absolutePath, icon, chatWith, fromId, toId, new IGroupInfoModel.OnMsgSendListener() {
    //
    //            @Override
    //            public void onMsgSend(int code, String codeMsg, ChatData chatData) {
    //            }
    //        }, (chatData, result) -> {
    //            if (result != null && result.success()) {
    //                JsonUploadImage jsonUploadImage = (JsonUploadImage) result.get();
    //                String url = jsonUploadImage.getData().getUrl();
    //                chatData.setFileUrl(url);
    //                getiModel().sendPicMsg(chatData, null);
    //            } else {
    //                getiModel().updateStatus(chatData.getChatType(), chatData.getChatWith(), chatData.getMsgId(), PhotonIMMessage.SEND_FAILED);
    //                EventBus.getDefault().post(new ChatDataWrapper(chatData, ChatModel.MSG_ERROR_CODE_UPLOAD_PIC_FAILED, "上传图片失败"));
    //            }
    //        });
    //    }
    private fun sendPicMsgInner(chatDataTemp: ChatData) {
        getiModel()?.uploadPic(chatDataTemp,object : IChatModel.OnPicUploadListener{
            override fun onPicUpload(chatData: ChatData?, result: JsonResult<JsonRequestResult>) {
                if (result != null && result.success()) {
                    val jsonUploadImage: JsonUploadImage = result.get() as JsonUploadImage
                    val url: String = jsonUploadImage.getData().getUrl()
                    chatData?.fileUrl = (url)
                    getiModel()?.sendPicMsg(chatData, null)
                } else {
                    chatData?.chatType?.let {
                        getiModel()?.updateStatus(
                            it,
                            chatData?.chatWith,
                            chatData?.msgId,
                            PhotonIMMessage.SEND_FAILED
                        )
                    }
                    EventBus.getDefault().post(
                        chatData?.let {
                            ChatDataWrapper(
                                it,
                                ChatModel.MSG_ERROR_CODE_UPLOAD_PIC_FAILED,
                                "上传图片失败"
                            )
                        }
                    )
                }
            }


        })

    }

    //    @Override
    //    public void sendVoice(int chatType, String absolutePath, long time, String chatWith, String fromId, String toId, String icon) {
    //        sendVoice(chatType, null, absolutePath, time, chatWith, fromId, toId, icon);
    //    }
    //    @Override
    //    public void reSendVoice(int chatType, String msgId, String absolutePath, long time, String chatWith, String fromId, String toId, String icon) {
    //        sendVoice(chatType, msgId, absolutePath, time, chatWith, fromId, toId, icon);
    //    }
    //    private void sendVoice(int chatType, String msgId, String absolutePath, long time, String chatWith, String fromId, String toId, String icon) {
    //        getiModel().uploadVoiceFile(chatType, msgId, absolutePath, time, icon, chatWith, fromId, toId, null, (chatData, result) -> {
    //            if (result.success()) {
    //                JsonUploadVoice jsonUploadImage = (JsonUploadVoice) result.get();
    //                String url = jsonUploadImage.getData().getUrl();
    //                chatData.setFileUrl(url);
    //                getiModel().sendVoiceFileMsg(chatData, null);
    //            } else {
    //                getiModel().updateStatus(chatData.getChatType(), chatData.getChatWith(), chatData.getMsgId(), PhotonIMMessage.SEND_FAILED);
    //                EventBus.getDefault().post(new ChatDataWrapper(chatData, ChatModel.MSG_ERROR_CODE_UPLOAD_PIC_FAILED, "上传语音失败"));
    //            }
    //        });
    //    }
    private fun sendVoice(chatDataTemp: ChatData) {
        getiModel()?.uploadVoiceFile(chatDataTemp,object :
            IChatModel.OnVoiceUploadListener{
            override fun onVoiceFileUpload(
                chatData: ChatData?,
                result: JsonResult<JsonRequestResult>
            ) {
                if (result.success()) {
                    val jsonUploadImage: JsonUploadVoice = result.get() as JsonUploadVoice
                    val url: String = jsonUploadImage.getData().getUrl()
                    chatData?.fileUrl = (url)
                    getiModel()?.sendVoiceFileMsg(chatData, null)
                } else {
                    chatData?.chatType?.let {
                        getiModel()?.updateStatus(
                            it,
                            chatData?.chatWith,
                            chatData?.msgId,
                            PhotonIMMessage.SEND_FAILED
                        )
                    }
                    EventBus.getDefault().post(
                        chatData?.let {
                            ChatDataWrapper(
                                it,
                                ChatModel.MSG_ERROR_CODE_UPLOAD_PIC_FAILED,
                                "上传语音失败"
                            )
                        }
                    )
                }
            }

        })
    }

   override fun revertMsg(data: ChatData?) {
        getiModel()?.revertMsg(data,object : IChatModel.OnRevertListener{
            override fun onRevert(data: ChatData?, error: Int, msg: String?) {
                getIView()?.onRevertResult(
                    data,
                    error,
                    msg
                )
            }

        })
    }

    override fun sendReadMsg(messageData: ChatData?) {
        if (sendReadStatusSet == null) {
            sendReadStatusSet = HashSet()
        }
        if (sendReadStatusSet!!.contains(messageData?.msgId)) {
            LogUtils.log(
                TAG,
                java.lang.String.format(
                    "msg with id:%s read status sending",
                    messageData?.msgId
                )
            )
            return
        }
        messageData?.msgId?.let { sendReadStatusSet!!.add(it) }
        getiModel()?.sendReadMsg(messageData, object : IChatModel.OnSendReadListener {
            private val retryCount = 3
            private var retryNum = 0
            override fun onSendRead(data: ChatData?, error: Int, msg: String?) {
                sendReadStatusSet!!.remove(data?.msgId)
                if (error != ChatModel.MSG_ERROR_CODE_SUCCESS && retryNum < retryCount) {
                    LogUtils.log(
                        TAG,
                        String.format("sendread times:%d", ++retryNum)
                    )
                    getiModel()?.sendReadMsg(messageData, this)
                } else {
                    getIView()?.updateUnreadStatus(data)
                }
            }
        })
    }

    override fun startRecord(context: Context?): File {
        context?.let { init(it) }
        val file = File(
            Environment.getExternalStorageDirectory(),
            FileUtils.VOICE_PATH_SEND.toString() + String.format(
                "voice_%d",
                System.currentTimeMillis()
            )
        )
        FileUtils.createFile(file)
        voiceHelper?.record(file.absolutePath, object : OpusRecorder.OnRecordStatusListener {
            override fun onRecordStart() {
                voiceTimeStart = System.currentTimeMillis()
            }
        })
        return file
    }

    private fun init(context: Context) {
        if (voiceHelper != null) {
            return
        }
        voiceHelper = VoiceHelper(context, object : VoiceHelper.OnVoiceListener {
            override fun onRecordFinish() {
                getIView()?.onRecordFinish(System.currentTimeMillis() - voiceTimeStart)
            }

            override fun onRecordFailed() {
                getIView()?.onRecordFailed()
            }
        })
    }

    override fun stopRecord() {
        if (voiceHelper != null) {
            voiceHelper?.stopRecord()
        }
    }

    override fun play(context: Context?, fileName: String?) {
        if (context != null) {
            init(context)
        }
        voiceHelper?.play(fileName)
    }

    override fun cancelPlay() {
        if (voiceHelper != null) {
            voiceHelper?.cancelPlay()
        }
    }

    override fun cancelRecord() {
        if (voiceHelper != null) voiceHelper?.cancelRecord()
    }

    override fun getVoiceFile(chatData: ChatData?) {
        val fileUrlName = getFileUrlName(chatData?.fileUrl)
        if (fileUrlName == null) {
            LogUtils.log(
               TAG,
                "fileurl == null"
            )
            return
        }
        val file = File(
            Environment.getExternalStorageDirectory(),
            FileUtils.VOICE_PATH_RECEIVE.toString() + fileUrlName
        )
        FileUtils.createFile(file)
        getiModel()?.getVoiceFile(chatData, file.absolutePath, object : IChatModel.OnGetFileListener {
            override fun onGetFile(path: String?) {
                getIView()?.onGetChatVoiceFileResult(chatData, path)
            }
        })
    }

    override fun updateExtra(messageData: ChatData?) {
        getiModel()?.updateExtra(messageData)
    }

    override fun removeChat(chatType: Int, chatWith: String?, id: String?) {
        // TODO: 2019-08-16 线程安全
        getiModel()?.removeChat(chatType, chatWith, id)
    }

    override fun getInfo(chatData: ChatData?) {
        if (getUserInfoSet == null) {
            getUserInfoSet = HashSet()
        }
        if (getUserInfoSet!!.contains(chatData?.from)) {
            return
        }
        chatData?.from?.let { getUserInfoSet!!.add(it) }
        val businessListener: ImBaseBridge.BusinessListener? = ImBaseBridge.instance?.businessListener
        if (businessListener != null) {
            businessListener.getUserIcon(chatData?.from, object : ImBaseBridge.OnGetUserIconListener {
                override fun onGetUserIcon(iconUrl: String?, name: String?) {
                    getUserInfoSet!!.remove(chatData?.from)
                    getIView()?.onGetIcon(chatData, iconUrl, name)
                }
            })
        }
    }

    override fun destoryVoiceHelper() {
        if (voiceHelper != null) {
            voiceHelper?.destory()
        }
    }

    private fun getFileUrlName(fileUrl: String?): String? {
        if (fileUrl == null) {
            return null
        }
        val split = fileUrl.split("/").toTypedArray()
        return split[split.size - 1]
    }

    override fun generateIModel(): ChatModel {
        return ChatModel()
    }

    companion object {
        private const val TAG = "ChatSetPresenter"
    }


}
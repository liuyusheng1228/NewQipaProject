package com.qipa.qipaimbase

import android.os.Handler
import android.os.HandlerThread
import org.greenrobot.eventbus.EventBus

class IMReceiveHelper private constructor() {
    private val FREQUENT_UPDATE_DURATION = 500
    private val WHAT_UPDATE_SESSION = 1000
    private val WHAT_UPDATE_STATUS = 1001
    private val handlerThread: HandlerThread
    private val handler: Handler

    @Volatile
    private var tag = false
    fun start() {
//        PhotonIMClient.getInstance().setPhotonIMStateListener { state, errorCode, errorMsg ->
//            val statusMsg: String
//            Log.i(
//                TAG,
//                java.lang.String.format(
//                    "state:%d,threadId:%d",
//                    state,
//                    Thread.currentThread().id
//                )
//            )
//            statusMsg = when (state) {
//                PhotonIMClient.IM_STATE_CONNECTING -> "自动重连中"
//                PhotonIMClient.IM_STATE_AUTH_SUCCESS -> "鉴权成功"
//                PhotonIMClient.IM_STATE_AUTH_FAILED -> "鉴权失败"
//                PhotonIMClient.IM_STATE_KICK -> "服务器强制下线"
//                PhotonIMClient.IM_STATE_NET_UNAVAILABLE -> "网络不可用"
//                else -> "未知状态"
//            }
//            val msg = Message.obtain()
//            msg.what = WHAT_UPDATE_STATUS
//            msg.obj = IMStatus(state, statusMsg)
//            handler.sendMessage(msg)
//        }
//        LogUtils.log("startim", "setPhotonIMStateListener")
//        val businessListener: BusinessListener = ImBaseBridge.getInstance().getBusinessListener()
//        var loginUserId = ""
//        if (businessListener != null) {
//            loginUserId = businessListener.getUserId()
//        }
//        PhotonIMClient.getInstance().attachUserId(loginUserId)
//        LogUtils.log("startim", "attachUserId")
//        PhotonIMClient.getInstance().setDBMode(PhotonIMClient.DB_SYNC)
//        LogUtils.log("startim", "setDBMode")
//        PhotonIMClient.getInstance()
//            .setPhotonIMReSendCallback { code, msg, retTime, chatType, chatWith, msgId ->
//                LogUtils.log("pim_demo", "Recv DB PhotonIMReSendCallback $msgId")
//                if (code !== -1) {
//                    code = ChatModel.MSG_ERROR_CODE_SUCCESS
//                }
//                val chatData: ChatData =
//                    Builder().msgId(msg).chatType(chatType).chatWith(chatWith).build()
//                val chatDataWrapper = ChatDataWrapper(chatData, code, msg)
//                EventBus.getDefault().post(chatDataWrapper)
//            }
//        LogUtils.log("startim", "setPhotonIMReSendCallback")
//        PhotonIMClient.getInstance().setPhotonIMMessageReceiver { photonIMMessage, lt, lv ->
//            LogUtils.log(
//                TAG,
//                java.lang.String.format("msgStatus:%d", photonIMMessage.status)
//            )
//            LogUtils.log(
//                TAG,
//                "is receive is main thread :" + (Thread.currentThread()
//                    .id == Looper.getMainLooper().thread.id)
//            )
//            EventBus.getDefault().post(photonIMMessage)
//        }
//        LogUtils.log("startim", "setPhotonIMMessageReceiver")
//        PhotonIMClient.getInstance().setPhotonIMSyncEventListener { i ->
//            when (i) {
//                PhotonIMClient.SYNC_START -> LogUtils.log("pim_demo", "SYNC_START")
//                PhotonIMClient.SYNC_END -> LogUtils.log("pim_demo", "SYNC_END")
//                PhotonIMClient.SYNCT_IMEOUT ->                     // 这个不会打印，因为默认sync超时，sdk主动断开重连
//                    LogUtils.log("pim_demo", "SYNCT_IMEOUT")
//            }
//        }
//        LogUtils.log("startim", "setPhotonIMSyncEventListener")
//        PhotonIMDatabase.getInstance().addSessionDataChangeObserver(SessionDataChangeObserverImpl())
//        LogUtils.log("startim", "addSessionDataChangeObserver")
//        var tokenId = ""
//        if (businessListener != null) {
//            tokenId = businessListener.getTokenId()
//        }
//        PhotonIMClient.getInstance().login(loginUserId, tokenId, HashMap<K, V>())
//        LogUtils.log("startim", "login")
    }

//    inner class SessionDataChangeObserverImpl : PhotonIMDatabase.SessionDataChangeObserver {
//        private var lastChangeTime: Long = 0
//
//        /**
//         * 会话数据变化时回调，数据库中已更新
//         *
//         * @param event    0 新增，1 修改，2 删除
//         * @param chatType
//         * @param chatWith
//         */
//        fun onSessionChange(event: Int, chatType: Int, chatWith: String?) {
//            Log.i(TAG, "onSessionChange")
//            if (System.currentTimeMillis() - lastChangeTime < FREQUENT_UPDATE_DURATION) {
//                lastChangeTime = System.currentTimeMillis()
//                if (!tag) {
//                    tag = true
//                    val msg = Message.obtain()
//                    msg.what = WHAT_UPDATE_SESSION
//                    msg.obj = OnDBChanged(event, chatType, chatWith)
//                    handler.sendMessageDelayed(msg, FREQUENT_UPDATE_DURATION.toLong())
//                }
//            } else {
//                lastChangeTime = System.currentTimeMillis()
//                EventBus.getDefault().post(OnDBChanged(event, chatType, chatWith))
//            }
//        }
//    }

    fun stop() {}

    companion object {
        private const val TAG = "IMReceiveHelper"
        val instance = IMReceiveHelper()
    }

    init {
        handlerThread = HandlerThread("updateSession")
        handlerThread.start()
        handler = Handler(handlerThread.looper) { msg ->
            when (msg.what) {
                WHAT_UPDATE_SESSION -> {
                    EventBus.getDefault().post(msg.obj)
                    tag = false
                }
                WHAT_UPDATE_STATUS -> EventBus.getDefault().post(msg.obj)
            }
            true
        }
    }
}

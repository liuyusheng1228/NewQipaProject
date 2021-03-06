package com.qipa.newboxproject.app

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.TextUtils
import android.util.Log

import com.hyphenate.easeui.domain.EaseUser


import com.hyphenate.util.EMLog



import com.hyphenate.push.EMPushType

import com.hyphenate.push.EMPushHelper

import com.hyphenate.easeui.EaseIM

import com.hyphenate.easeui.domain.EaseAvatarOptions

import com.hyphenate.EMCallBack

import com.hyphenate.chat.*

import com.hyphenate.push.EMPushConfig

import com.hyphenate.push.PushListener

import com.hyphenate.easeui.provider.EaseUserProfileProvider

import com.hyphenate.easeui.domain.EaseEmojicon

import com.qipa.newboxproject.data.model.EmojiconExampleGroupData

import com.hyphenate.easeui.provider.EaseEmojiconInfoProvider

import com.hyphenate.easeui.provider.EaseSettingsProvider

import com.hyphenate.chat.EMConversation.EMConversationType

import com.hyphenate.easeui.delegate.EaseTextAdapterDelegate

import com.hyphenate.easeui.delegate.EaseCustomAdapterDelegate

import com.hyphenate.easeui.delegate.EaseVoiceAdapterDelegate

import com.hyphenate.easeui.delegate.EaseVideoAdapterDelegate

import com.hyphenate.easeui.delegate.EaseLocationAdapterDelegate

import com.hyphenate.easeui.delegate.EaseImageAdapterDelegate

import com.hyphenate.easeui.delegate.EaseFileAdapterDelegate

import com.hyphenate.easeui.delegate.EaseExpressionAdapterDelegate

import com.hyphenate.easeui.manager.EaseMessageTypeSetManager

import com.hyphenate.easeui.model.EaseNotifier
import com.qipa.newboxproject.app.manager.UserProfileManager
import com.qipa.newboxproject.app.util.FetchUserInfoList
import com.qipa.newboxproject.app.util.FetchUserRunnable
import com.qipa.newboxproject.app.util.PreferenceManager
import com.qipa.newboxproject.data.db.DemoDbHelper
import com.qipa.newboxproject.data.db.delegates.ChatNotificationAdapterDelegate
import com.qipa.newboxproject.data.db.delegates.ChatRecallAdapterDelegate
import com.qipa.newboxproject.data.model.DemoModel
import com.qipa.newboxproject.data.push.HeadsetReceiver
import java.util.*


/**
 * ??????hyphenate-sdk???????????????????????????sdk??????????????????????????????
 */
class ChatHelper private constructor() {
    /**
     * ??????SDK???????????????
     * @param init
     */
    var isSDKInit //SDK???????????????
            = false
    private var demoModel: DemoModel? = null
    private var contactList: Map<String, EaseUser>? = null
    private var userProManager: UserProfileManager? = null
//    private var callKitListener: EaseCallKitListener? = null
    private var mianContext: Context? = null
    private val tokenUrl = "http://a1.easemob.com/token/rtcToken/v1"
    private val uIdUrl = "http://a1.easemob.com/channel/mapper"
    private var fetchUserRunnable: FetchUserRunnable? = null
    private var fetchUserTread: Thread? = null
    private var fetchUserInfoList: FetchUserInfoList? = null
    fun init(context: Context) {
        demoModel = DemoModel(context)
        //?????????IM SDK
        if (initSDK(context)) {
            // debug mode, you'd better set it to false, if you want release your App officially.
            EMClient.getInstance().setDebugMode(true)
            // set Call options
            setCallOptions(context)
            //???????????????
            initPush(context)
            //??????call Receiver
            //initReceiver(context);
            //?????????ease ui??????
            initEaseUI(context)
            //??????????????????
            registerConversationType()

            //callKit?????????
            InitCallKit(context)

            //??????????????????????????????
            fetchUserInfoList = FetchUserInfoList.instance
            fetchUserRunnable = FetchUserRunnable()
            fetchUserTread = Thread(fetchUserRunnable)
            fetchUserTread!!.start()
        }
    }

    /**
     * callKit?????????
     * @param context
     */
    private fun InitCallKit(context: Context) {
//        val callKitConfig = EaseCallKitConfig()
//        //????????????????????????
//        callKitConfig.callTimeOut = (30 * 1000).toLong()
//        //????????????AgoraAppId
//        callKitConfig.agoraAppId = "15cb0d28b87b425ea613fc46f7c9f974"
//        callKitConfig.isEnableRTCToken = true
//        EaseCallKit.getInstance().init(context, callKitConfig)
        // Register the activities which you have registered in manifest
//        EaseCallKit.getInstance().registerVideoCallClass(VideoCallActivity::class.java)
//        EaseCallKit.getInstance().registerMultipleVideoClass(MultipleVideoActivity::class.java)
//        addCallkitListener()
    }

    /**
     * ?????????SDK
     * @param context
     * @return
     */
    private fun initSDK(context: Context): Boolean {
        // ?????????????????????SDK????????????
        val options = initChatOptions(context)
        //??????????????????rest server???im server
        //options.setRestServer("a1-hsb.easemob.com");
        //options.setIMServer("106.75.100.247");
        //options.setImPort(6717);
        // ?????????SDK
        isSDKInit = EaseIM.getInstance().init(context, options)
        //??????????????????????????????????????????
        demoModel?.userInfoTimeOut = (30 * 60 * 1000)
        //??????????????????????????????
        updateTimeoutUsers()
        mianContext = context
        return isSDKInit
    }

    /**
     * ??????????????????
     */
    private fun registerConversationType() {
        EaseMessageTypeSetManager.getInstance()
            .addMessageType(EaseExpressionAdapterDelegate::class.java) //???????????????
            .addMessageType(EaseFileAdapterDelegate::class.java) //??????
            .addMessageType(EaseImageAdapterDelegate::class.java) //??????
            .addMessageType(EaseLocationAdapterDelegate::class.java) //??????
            .addMessageType(EaseVideoAdapterDelegate::class.java) //??????
            .addMessageType(EaseVoiceAdapterDelegate::class.java) //??????
//            .addMessageType(ChatConferenceInviteAdapterDelegate::class.java) //????????????
            .addMessageType(ChatRecallAdapterDelegate::class.java) //????????????
//            .addMessageType(ChatVideoCallAdapterDelegate::class.java) //????????????
//            .addMessageType(ChatVoiceCallAdapterDelegate::class.java) //????????????
//            .addMessageType(ChatUserCardAdapterDelegate::class.java) //????????????
            .addMessageType(EaseCustomAdapterDelegate::class.java) //???????????????
            .addMessageType(ChatNotificationAdapterDelegate::class.java) //?????????????????????
            .setDefaultMessageType(EaseTextAdapterDelegate::class.java) //??????
    }

    /**
     * ???????????????????????????
     * @return
     */
    val isLoggedIn: Boolean
        get() = eMClient.isLoggedInBefore

    /**
     * ??????IM SDK????????????
     * @return
     */
    val eMClient: EMClient
        get() = EMClient.getInstance()

    /**
     * ??????contact manager
     * @return
     */
    val contactManager: EMContactManager
        get() = eMClient.contactManager()

    /**
     * ??????group manager
     * @return
     */
    val groupManager: EMGroupManager
        get() = eMClient.groupManager()

    /**
     * ??????chatroom manager
     * @return
     */
    val chatroomManager: EMChatRoomManager
        get() = eMClient.chatroomManager()

    /**
     * get EMChatManager
     * @return
     */
    val chatManager: EMChatManager
        get() = eMClient.chatManager()

    /**
     * get push manager
     * @return
     */
    val pushManager: EMPushManager
        get() = eMClient.pushManager()

    /**
     * get conversation
     * @param username
     * @param type
     * @param createIfNotExists
     * @return
     */
    fun getConversation(
        username: String?,
        type: EMConversationType?,
        createIfNotExists: Boolean
    ): EMConversation {
        return chatManager.getConversation(username, type, createIfNotExists)
    }

    val currentUser: String
        get() = eMClient.currentUser

    /**
     * ChatPresenter????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * @param context
     */
    private fun initEaseUI(context: Context) {
        //??????ChatPresenter,ChatPresenter???????????????????????????????????????
//        EaseIM.getInstance().addChatPresenter(ChatPresenter.getInstance())
        EaseIM.getInstance()
            .setSettingsProvider(object : EaseSettingsProvider {
                override fun isMsgNotifyAllowed(message: EMMessage): Boolean {
                    if (message == null) {
                        return demoModel?.settingMsgNotification!!
                    }
                    return if (demoModel?.settingMsgNotification!!) {
                        false
                    } else {
                        var chatUsename: String? = null
                        var notNotifyIds: List<String?>? = null
                        // get user or group id which was blocked to show message notifications
                        if (message.chatType == EMMessage.ChatType.Chat) {
                            chatUsename = message.from
                            notNotifyIds = demoModel?.getDisabledIds()
                        } else {
                            chatUsename = message.to
                            notNotifyIds = demoModel?.disabledGroups
                        }
                        if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
                            true
                        } else {
                            false
                        }
                    }
                }

                override fun isMsgSoundAllowed(message: EMMessage): Boolean {
                    return demoModel?.settingMsgSound!!
                }

                override fun isMsgVibrateAllowed(message: EMMessage): Boolean {
                    return demoModel?.settingMsgVibrate!!
                }

                override fun isSpeakerOpened(): Boolean {
                    return demoModel?.settingMsgSpeaker!!
                }
            })
            .setEmojiconInfoProvider(object : EaseEmojiconInfoProvider {
                override fun getEmojiconInfo(emojiconIdentityCode: String): EaseEmojicon? {
                    val data = EmojiconExampleGroupData.data
                    for (emojicon in data.emojiconList) {
                        if (emojicon.identityCode == emojiconIdentityCode) {
                            return emojicon
                        }
                    }
                    return null
                }

                override fun getTextEmojiconMapping(): Map<String, Any>? {
                    return null
                }
            })
            .setAvatarOptions(avatarOptions).userProvider =
            EaseUserProfileProvider { username -> getUserInfo(username)!! }
    }

    /**
     * ??????????????????
     * @return
     */
    private val avatarOptions: EaseAvatarOptions
        private get() {
            val avatarOptions = EaseAvatarOptions()
            avatarOptions.avatarShape = 1
            return avatarOptions
        }

    fun getUserInfo(username: String): EaseUser? {
        // To get instance of EaseUser, here we get it from the user list in memory
        // You'd better cache it if you get it from your server
        var user: EaseUser? = null
        if (username == EMClient.getInstance().currentUser) return userProfileManager?.currentUserInfo
        user = getContactList()[username]
        if (user == null) {
            //??????????????????????????? ????????????
            updateContactList()
            user = getContactList()[username]
            //?????????????????????????????????????????? ????????????UI????????????
            if (user == null) {
                fetchUserInfoList?.addUserId(username)
            }
        }
        return user
    }

    /**
     * ?????????????????????????????????
     * @param context
     * @return
     */
    private fun initChatOptions(context: Context): EMOptions {
        Log.d(TAG, "init HuanXin Options")
        val options = EMOptions()
        // ???????????????????????????????????????,?????????true
        options.acceptInvitationAlways = false
        // ???????????????????????????????????????
        options.requireAck = true
        // ???????????????????????????????????????,??????false
        options.requireDeliveryAck = false
        /**
         * NOTE:????????????????????????????????????????????????????????????????????????????????????
         */
        val builder = EMPushConfig.Builder(context)
        builder.enableVivoPush() // ?????????AndroidManifest.xml?????????appId???appKey
            .enableMeiZuPush("134952", "f00e7e8499a549e09731a60a4da399e3")
            .enableMiPush("2882303761517426801", "5381742660801")
            .enableOppoPush(
                "0bb597c5e9234f3ab9f821adbeceecdb",
                "cd93056d03e1418eaa6c3faf10fd7537"
            )
            .enableHWPush() // ?????????AndroidManifest.xml?????????appId
            .enableFCM("921300338324")
        options.pushConfig = builder.build()

        //set custom servers, commonly used in private deployment
        if (demoModel?.isCustomSetEnable()!!) {
            if (demoModel?.isCustomServerEnable()!! && demoModel?.getRestServer() != null && demoModel?.getIMServer() != null) {
                // ??????rest server??????
                options.restServer = demoModel?.getRestServer()
                // ??????im server??????
                options.setIMServer(demoModel?.getIMServer())
                //??????im server????????????????????????
                if (demoModel?.getIMServer()!!.contains(":")) {
                    options.setIMServer(demoModel?.getIMServer()!!.split(":").get(0))
                    // ??????im server ??????????????????443
                    options.imPort = Integer.valueOf(demoModel?.getIMServer()!!.split(":").get(1))
                } else {
                    //????????????????????????
                    if (demoModel?.getIMServerPort() !== 0) {
                        options.imPort = demoModel?.getIMServerPort()!!
                    }
                }
            }
        }
        if (demoModel?.isCustomAppkeyEnabled()!! && !TextUtils.isEmpty(demoModel?.getCutomAppkey())) {
            // ??????appkey
            options.appKey = demoModel?.getCutomAppkey()
        }
        val imServer = options.imServer
        val restServer = options.restServer

        // ???????????????????????????owner???????????????????????????????????????owner???????????????????????????
        options.allowChatroomOwnerLeave(demoModel?.isChatroomOwnerLeaveAllowed()!!)
        // ????????????(?????????????????????)?????????????????????????????????
        options.isDeleteMessagesAsExitGroup = demoModel?.isDeleteMessagesAsExitGroup()!!
        // ????????????????????????????????????
        options.isAutoAcceptGroupInvitation = demoModel?.isAutoAcceptGroupInvitation()!!
        // ???????????????????????????????????????????????????????????????True????????????????????????????????????
        options.autoTransferMessageAttachments = demoModel?.isSetTransferFileByUser()!!
        // ???????????????????????????????????????true???????????????
        options.setAutoDownloadThumbnail(demoModel?.isSetAutodownloadThumbnail()!!)
        return options
    }

    private fun setCallOptions(context: Context) {
        val headsetReceiver = HeadsetReceiver()
        val headsetFilter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
        context.registerReceiver(headsetReceiver, headsetFilter)
    }

    fun initPush(context: Context?) {
        if (EaseIM.getInstance().isMainProcess(context)) {
            //OPPO SDK?????????2.1.0????????????????????????
//            HeytapPushManager.init(context, true)
            //HMSPushHelper.getInstance().initHMSAgent(DemoApplication.getInstance());
            EMPushHelper.getInstance().setPushListener(object : PushListener() {
                override fun onError(pushType: EMPushType, errorCode: Long) {
                    // TODO: ?????????errorCode???9xx??????????????????????????????EMError?????????????????????????????????pushType???????????????????????????????????????
                    EMLog.e("PushClient", "Push client occur a error: $pushType - $errorCode")
                }

                override fun isSupportPush(
                    pushType: EMPushType,
                    pushConfig: EMPushConfig
                ): Boolean {
                    // ?????????????????????????????????????????????FCM??????
//                    if (pushType == EMPushType.FCM) {
//                        EMLog.d(
//                            "FCM",
//                            "GooglePlayServiceCode:" + GoogleApiAvailabilityLight.getInstance()
//                                .isGooglePlayServicesAvailable(context)
//                        )
//                        return demoModel.isUseFCM() && GoogleApiAvailabilityLight.getInstance()
//                            .isGooglePlayServicesAvailable(context) === ConnectionResult.SUCCESS
//                    }
                    return super.isSupportPush(pushType, pushConfig)
                }
            })
        }
    }

    /**
     * logout
     *
     * @param unbindDeviceToken
     * whether you need unbind your device token
     * @param callback
     * callback
     */
    fun logout(unbindDeviceToken: Boolean, callback: EMCallBack?) {
        Log.d(TAG, "logout: $unbindDeviceToken")
        if (fetchUserTread != null && fetchUserRunnable != null) {
            fetchUserRunnable?.setStop(true)
        }
        EMClient.getInstance().logout(unbindDeviceToken, object : EMCallBack {
            override fun onSuccess() {
                logoutSuccess()
                //reset();
                callback?.onSuccess()
            }

            override fun onProgress(progress: Int, status: String) {
                callback?.onProgress(progress, status)
            }

            override fun onError(code: Int, error: String) {
                Log.d(TAG, "logout: onSuccess")
                //reset();
                callback?.onError(code, error)
            }
        })
    }


    /**
     * ?????????????????????????????????????????????
     */
    fun logoutSuccess() {
        Log.d(TAG, "logout: onSuccess")
        autoLogin = false
//        DemoDbHelper.getInstance(DemoApplication.getInstance()).closeDb()
    }

    val easeAvatarOptions: EaseAvatarOptions
        get() = EaseIM.getInstance().avatarOptions
    val model: DemoModel?
        get() {
            if (demoModel == null) {
                demoModel = DemoModel(App.instance.applicationContext)
            }
            return demoModel
        }
    val currentLoginUser: String?
        get() = model?.currentUsername

    /**
     * get instance of EaseNotifier
     * @return
     */
    val notifier: EaseNotifier
        get() = EaseIM.getInstance().notifier
    /**
     * ???????????????????????????????????????
     * @return
     */
    /**
     * ???????????????????????????????????????
     * @param autoLogin
     */
    var autoLogin: Boolean?
        get() = PreferenceManager.instance?.autoLogin
        set(autoLogin) {
            if (autoLogin != null) {
                PreferenceManager.instance?.autoLogin = autoLogin
            }
        }

    /**
     * ???????????????????????????
     * @param object
     */
    fun insert(`object`: Any?) {
        demoModel?.insert(`object`)
    }

    /**
     * update
     * @param object
     */
    fun update(`object`: Any?) {
        demoModel?.update(`object`)
    }

    /**
     * update user list
     * @param users
     */
    fun updateUserList(users: List<EaseUser?>?) {
        demoModel?.updateContactList(users)
    }

    /**
     * ?????????????????????????????????
     */
    fun updateTimeoutUsers() {
        val userIds: List<String>? = demoModel?.selectTimeOutUsers()
        if (userIds != null && userIds.size > 0) {
            if (fetchUserInfoList != null) {
                for (i in userIds.indices) {
                    fetchUserInfoList?.addUserId(userIds[i])
                }
            }
        }
    }

    /**
     * get contact list
     *
     * @return
     */
    fun getContactList(): Map<String, EaseUser> {
        if (isLoggedIn && contactList == null) {
            updateTimeoutUsers()
            contactList = demoModel?.allUserList
        }

        // return a empty non-null object to avoid app crash
        return contactList ?: Hashtable<String, EaseUser>()
    }

    /**
     * update contact list
     */
    fun updateContactList() {
        if (isLoggedIn) {
            updateTimeoutUsers()
            contactList = demoModel?.contactList
        }
    }

    val userProfileManager: UserProfileManager?
        get() {
            if (userProManager == null) {
                userProManager = UserProfileManager()
            }
            return userProManager
        }

    /**
     * ????????????????????????
     */
    fun showNotificationPermissionDialog() {
        val pushType = EMPushHelper.getInstance().pushType
        // oppo
//        if (pushType == EMPushType.OPPOPUSH && HeytapPushManager.isSupportPush()) {
//            HeytapPushManager.requestNotificationPermission()
//        }
    }

    /**
     * ???????????????
     * @param username
     * @return
     */
    @Synchronized
    fun deleteContact(username: String?): Int {
        if (TextUtils.isEmpty(username)) {
            return 0
        }
        val helper: DemoDbHelper? = DemoDbHelper.getInstance(App.instance.applicationContext)
        if (helper?.userDao == null) {
            return 0
        }
        val num: Int = helper.userDao!!.deleteUser(username)
        if (helper.inviteMessageDao != null) {
            helper.inviteMessageDao?.deleteByFrom(username)
        }
        EMClient.getInstance().chatManager().deleteConversation(username, false)
        model?.deleteUsername(username, false)
        Log.e(TAG, "delete num = $num")
        return num
    }

    /**
     * ????????????????????????????????????
     * ????????????true, ????????????api???????????????????????????????????????false.
     * @return
     */
    val isFirstInstall: Boolean?
        get() = model?.isFirstInstall()

    /**
     * ??????????????????????????????????????????????????????????????????api?????????
     * ?????????????????????????????????????????????true
     */
    fun makeNotFirstInstall() {
        model?.makeNotFirstInstall()
    }

    /**
     * ????????????????????????????????????????????????
     * @return
     */
    val isConComeFromServer: Boolean?
        get() = model?.isConComeFromServer()

    /**
     * Determine if it is from the current user account of another device
     * @param username
     * @return
     */
    fun isCurrentUserFromOtherDevice(username: String): Boolean {
        if (TextUtils.isEmpty(username)) {
            return false
        }
        return if (username.contains("/") && username.contains(EMClient.getInstance().currentUser)) {
            true
        } else false
    }

    /**
     * ??????EaseCallkit??????
     *
     */
//    fun addCallkitListener() {
//        callKitListener = object : EaseCallKitListener {
//            override fun onInviteUsers(
//                context: Context,
//                userId: Array<String?>?,
//                ext: JSONObject?
//            ) {
////                val intent: Intent = Intent(context, ConferenceInviteActivity::class.java).addFlags(
////                    Intent.FLAG_ACTIVITY_NEW_TASK
////                )
////                var groupId: String? = null
////                if (ext != null && ext.length() > 0) {
////                    try {
////                        groupId = ext.getString("groupId")
////                    } catch (e: JSONException) {
////                        e.printStackTrace()
////                    }
////                }
////                intent.putExtra(DemoConstant.EXTRA_CONFERENCE_GROUP_ID, groupId)
////                intent.putExtra(DemoConstant.EXTRA_CONFERENCE_GROUP_EXIST_MEMBERS, userId)
////                context.startActivity(intent)
//            }
//
//            override fun onEndCallWithReason(
//                callType: EaseCallType,
//                channelName: String,
//                reason: EaseCallEndReason,
//                callTime: Long
//            ) {
//                EMLog.d(
//                    TAG,
//                    "onEndCallWithReason" + (callType?.name
//                        ?: " callType is null ") + " reason:" + reason + " time:" + callTime
//                )
//                val formatter = SimpleDateFormat("mm:ss")
//                formatter.setTimeZone(TimeZone.getTimeZone("UTC"))
//                var callString = "???????????? "
//                callString += formatter.format(callTime)
//                Toast.makeText(mianContext, callString, Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onGenerateToken(
//                userId: String,
//                channelName: String,
//                appKey: String,
//                callback: EaseCallKitTokenCallback
//            ) {
//                EMLog.d(
//                    TAG,
//                    "onGenerateToken userId:$userId channelName:$channelName appKey:$appKey"
//                )
//                var url = tokenUrl
//                url += "?"
//                url += "userAccount="
//                url += userId
//                url += "&channelName="
//                url += channelName
//                url += "&appkey="
//                url += appKey
//
//                //????????????Token
//                getRtcToken(url, callback)
//            }
//
//            override fun onReceivedCall(
//                callType: EaseCallType,
//                fromUserId: String,
//                ext: JSONObject?
//            ) {
//                //??????????????????
//                EMLog.d(TAG, "onRecivedCall" + callType.name + " fromUserId:" + fromUserId)
//            }
//
//            override fun onCallError(type: EaseCallError, errorCode: Int, description: String) {}
//            override fun onInViteCallMessageSent() {
////                LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(
////                    EaseEvent(
////                        DemoConstant.MESSAGE_CHANGE_CHANGE,
////                        EaseEvent.TYPE.MESSAGE
////                    )
////                )
//            }
//
//            override fun onRemoteUserJoinChannel(
//                channelName: String,
//                userName: String,
//                uid: Int,
//                callback: EaseGetUserAccountCallback
//            ) {
//                if (userName == null || userName === "") {
//                    var url = uIdUrl
//                    url += "?"
//                    url += "channelName="
//                    url += channelName
//                    url += "&userAccount="
//                    url += EMClient.getInstance().currentUser
//                    url += "&appkey="
//                    url += EMClient.getInstance().options.appKey
//                    getUserIdAgoraUid(uid, url, callback)
//                } else {
//                    //?????????????????? ??????
//                    setEaseCallKitUserInfo(userName)
//                    val account = EaseUserAccount(uid, userName)
//                    val accounts: MutableList<EaseUserAccount> = ArrayList()
//                    accounts.add(account)
//                    callback.onUserAccount(accounts)
//                }
//            }
//        }
//        EaseCallKit.getInstance().setCallKitListener(callKitListener)
//    }

    /**
     * ????????????Token
     *
     */
//    private fun getRtcToken(tokenUrl: String, callback: EaseCallKitTokenCallback) {
//        object : AsyncTask<String?, Void?, android.util.Pair<Int?, String?>?>() {
//            @SuppressLint("StaticFieldLeak")
//            protected override fun doInBackground(vararg str: String?): android.util.Pair<Int?, String?>? {
//                try {
//                    return EMHttpClient.getInstance()
//                        .sendRequestWithToken(tokenUrl, null, EMHttpClient.GET)
//                } catch (exception: HyphenateException) {
//                    exception.printStackTrace()
//                }
//                return null
//            }
//
//            protected fun onPostExecute(response: Pair<Int, String?>?) {
//                if (response != null) {
//                    try {
//                        val resCode = response.first
//                        if (resCode == 200) {
//                            val responseInfo = response.second
//                            if (responseInfo != null && responseInfo.length > 0) {
//                                try {
//                                    val `object` = JSONObject(responseInfo)
//                                    val token: String = `object`.getString("accessToken")
//                                    val uId: Int = `object`.getInt("agoraUserId")
//
//                                    //????????????????????????
//                                    setEaseCallKitUserInfo(EMClient.getInstance().currentUser)
//                                    callback.onSetToken(token, uId)
//                                } catch (e: Exception) {
//                                    e.stackTrace
//                                }
//                            } else {
//                                callback.onGetTokenError(response.first, response.second)
//                            }
//                        } else {
//                            callback.onGetTokenError(response.first, response.second)
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                } else {
//                    callback.onSetToken(null, 0)
//                }
//            }
//        }.execute(tokenUrl)
//    }

    /**
     * ??????channelName?????????uId???????????????????????????UserId
     * @param uId
     * @param url
     * @param callback
     */
//    private fun getUserIdAgoraUid(uId: Int, url: String, callback: EaseGetUserAccountCallback) {
//        object : AsyncTask<String?, Void?,  android.util.Pair<Int?, String?>?>() {
//            protected override fun doInBackground(vararg str: String?):  android.util.Pair<Int?, String?>?? {
//                try {
//                    return EMHttpClient.getInstance()
//                        .sendRequestWithToken(url, null, EMHttpClient.GET)
//                } catch (exception: HyphenateException) {
//                    exception.printStackTrace()
//                }
//                return null
//            }
//
//            protected fun onPostExecute(response: Pair<Int, String?>?) {
//                if (response != null) {
//                    try {
//                        val resCode = response.first
//                        if (resCode == 200) {
//                            val responseInfo = response.second
//                            val userAccounts: MutableList<EaseUserAccount> = ArrayList()
//                            if (responseInfo != null && responseInfo.length > 0) {
//                                try {
//                                    val `object` = JSONObject(responseInfo)
//                                    val resToken: JSONObject = `object`.getJSONObject("result")
//                                    val it: Iterator<*> = resToken.keys()
//                                    while (it.hasNext()) {
//                                        val uIdStr = it.next().toString()
//                                        var uid = 0
//                                        uid = Integer.valueOf(uIdStr).toInt()
//                                        val username: String = resToken.optString(uIdStr)
//                                        if (uid == uId) {
//                                            //????????????????????????userName ???????????????????????????
//                                            setEaseCallKitUserInfo(username)
//                                        }
//                                        userAccounts.add(EaseUserAccount(uid, username))
//                                    }
//                                    callback.onUserAccount(userAccounts)
//                                } catch (e: Exception) {
//                                    e.stackTrace
//                                }
//                            } else {
//                                callback.onSetUserAccountError(response.first, response.second)
//                            }
//                        } else {
//                            callback.onSetUserAccountError(response.first, response.second)
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                } else {
//                    callback.onSetUserAccountError(100, "response is null")
//                }
//            }
//        }.execute(url)
//    }

    /**
     * ??????callKit ??????????????????
     * @param userName
     */
    private fun setEaseCallKitUserInfo(userName: String) {
//        val user = getUserInfo(userName)
//        val userInfo = EaseCallUserInfo()
//        if (user != null) {
//            userInfo.nickName = user.nickname
//            userInfo.headImage = user.avatar
//        }
//        EaseCallKit.getInstance().callKitConfig.setUserInfo(userName, userInfo)
    }

    /**
     * data sync listener
     */
    interface DataSyncListener {
        /**
         * sync complete
         * @param success true???data sync successful???false: failed to sync data
         */
        fun onSyncComplete(success: Boolean)
    }

    companion object {
        private val TAG = ChatHelper::class.java.simpleName
        private var mInstance: ChatHelper? = null
        val instance: ChatHelper?
            get() {
                if (mInstance == null) {
                    synchronized(ChatHelper::class.java) {
                        if (mInstance == null) {
                            mInstance = ChatHelper()
                        }
                    }
                }
                return mInstance
            }
    }
}

package com.qipa.newboxproject.app.manager

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.hyphenate.chat.EMClient

import com.qipa.newboxproject.data.db.delegates.DemoConstant

import com.hyphenate.easeui.model.EaseEvent

import com.hyphenate.util.EMLog

import com.hyphenate.chat.adapter.EMAChatRoomManagerListener

import com.hyphenate.EMChatRoomChangeListener

import com.hyphenate.easeui.manager.EaseSystemMsgManager

import com.hyphenate.chat.EMMessage

import com.qipa.newboxproject.data.db.entity.InviteMessageStatus

import com.hyphenate.exceptions.HyphenateException

import com.hyphenate.EMMultiDeviceListener.GROUP_REMOVE_MUTE

import com.hyphenate.EMMultiDeviceListener.GROUP_ADD_MUTE

import com.hyphenate.EMMultiDeviceListener.GROUP_REMOVE_ADMIN

import com.hyphenate.EMMultiDeviceListener.GROUP_ADD_ADMIN

import com.hyphenate.EMMultiDeviceListener.GROUP_ASSIGN_OWNER

import com.hyphenate.EMMultiDeviceListener.GROUP_UNBLOCK

import com.hyphenate.EMMultiDeviceListener.GROUP_BLOCK

import com.hyphenate.EMMultiDeviceListener.GROUP_ALLOW

import com.hyphenate.EMMultiDeviceListener.GROUP_BAN

import com.hyphenate.EMMultiDeviceListener.GROUP_KICK

import com.hyphenate.EMMultiDeviceListener.GROUP_INVITE_DECLINE

import com.hyphenate.chat.EMTextMessageBody

import com.hyphenate.EMMultiDeviceListener.GROUP_INVITE_ACCEPT

import com.hyphenate.EMMultiDeviceListener.GROUP_INVITE

import com.hyphenate.EMMultiDeviceListener.GROUP_APPLY_DECLINE

import com.hyphenate.EMMultiDeviceListener.GROUP_APPLY_ACCEPT

import com.hyphenate.EMMultiDeviceListener.GROUP_APPLY

import com.hyphenate.EMMultiDeviceListener.GROUP_JOIN

import com.hyphenate.EMMultiDeviceListener.GROUP_DESTROY

import com.hyphenate.EMMultiDeviceListener.GROUP_CREATE

import com.qipa.newboxproject.data.db.entity.EmUserEntity

import com.qipa.newboxproject.data.db.DemoDbHelper

import com.hyphenate.EMMultiDeviceListener

import com.hyphenate.chat.EMUserInfo

import com.hyphenate.EMValueCallBack

import com.hyphenate.EMContactListener

import com.hyphenate.chat.EMMucSharedFile

import com.hyphenate.easeui.interfaces.EaseGroupListener

import com.hyphenate.EMError

import com.qipa.newboxproject.data.db.dao.EmUserDao

import com.hyphenate.easeui.domain.EaseUser

import com.hyphenate.chat.EMGroup

import com.hyphenate.EMConnectionListener

import com.hyphenate.EMConversationListener

import com.hyphenate.easeui.manager.EaseAtMessageHelper

import androidx.annotation.StringRes

import com.hyphenate.easeui.manager.EaseChatPresenter
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.App
import com.qipa.newboxproject.app.ChatHelper
import com.qipa.newboxproject.app.chat.interfaceOrImplement.ResultCallBack
import com.qipa.newboxproject.app.chat.repository.EMContactManagerRepository
import com.qipa.newboxproject.app.chat.repository.EMGroupManagerRepository
import com.qipa.newboxproject.app.chat.repository.EMPushManagerRepository
import com.qipa.newboxproject.data.db.LiveDataBus
import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue


/**
 * 主要用于chat过程中的全局监听，并对相应的事件进行处理
 * [.init]方法建议在登录成功以后进行调用
 */
class ChatPresenter private constructor() : EaseChatPresenter() {
    private var messageChangeLiveData: LiveDataBus? = null
    private var isGroupsSyncedWithServer = false
    private var isContactsSyncedWithServer = false
    private var isBlackListSyncedWithServer = false
    private var isPushConfigsWithServer = false
    private val appContext: Context
    protected var handler: Handler? = null
    var msgQueue: Queue<String> = ConcurrentLinkedQueue()

    /**
     * 将需要登录成功进入MainActivity中初始化的逻辑，放到此处进行处理
     */
    fun init() {}
    fun initHandler(looper: Looper?) {
        handler = object : Handler(looper!!) {
            override fun handleMessage(msg: Message) {
                val obj: Any = msg.obj
                when (msg.what) {
                    HANDLER_SHOW_TOAST -> if (obj is String) {
                        //ToastUtils.showToast(str);
                        Toast.makeText(appContext, obj, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        while (!msgQueue.isEmpty()) {
            showToast(msgQueue.remove())
        }
    }

    fun showToast(@StringRes mesId: Int) {
        showToast(context.getString(mesId))
    }

    fun showToast(message: String) {
        Log.d(TAG, "receive invitation to join the group：$message")
        if (handler != null) {
            val msg: Message = Message.obtain(handler, HANDLER_SHOW_TOAST, message)
            handler?.sendMessage(msg)
        } else {
            msgQueue.add(message)
        }
    }

    override fun onMessageReceived(messages: List<EMMessage>) {
        super.onMessageReceived(messages)
        val event = EaseEvent.create(DemoConstant.MESSAGE_CHANGE_RECEIVE, EaseEvent.TYPE.MESSAGE)
        messageChangeLiveData?.with(DemoConstant.MESSAGE_CHANGE_CHANGE)?.postValue(event)
        for (message in messages) {
            EMLog.d(TAG, "onMessageReceived id : " + message.msgId)
            EMLog.d(TAG, "onMessageReceived: " + message.type)
            // 如果设置群组离线消息免打扰，则不进行消息通知
            val disabledIds: List<String> =
                ChatHelper.instance?.pushManager?.getNoPushGroups() as List<String>
            if (disabledIds != null && disabledIds.contains(message.conversationId())) {
                return
            }
            // in background, do not refresh UI, notify it in notification bar
//            if (!App.instance.getLifecycleCallbacks().isFront()) {
//                notifier.notify(message)
//            }
            //notify new message
            notifier.vibrateAndPlayTone(message)
        }
    }

    /**
     * 判断是否已经启动了MainActivity
     * @return
     */
    @get:Synchronized
    private val isAppLaunchMain: Boolean
        private get() {
//            val activities: List<Activity> =
//                App.instance.getLifecycleCallbacks().getActivityList()
//            if (activities != null && !activities.isEmpty()) {
//                for (i in activities.indices.reversed()) {
//                    if (activities[i] is MainActivity) {
//                        return true
//                    }
//                }
//            }
            return false
        }

    override fun onCmdMessageReceived(messages: List<EMMessage>) {
        super.onCmdMessageReceived(messages)
        val event =
            EaseEvent.create(DemoConstant.MESSAGE_CHANGE_CMD_RECEIVE, EaseEvent.TYPE.MESSAGE)
        messageChangeLiveData?.with(DemoConstant.MESSAGE_CHANGE_CHANGE)?.postValue(event)
    }

    override fun onMessageRead(messages: List<EMMessage>) {
        super.onMessageRead(messages)
//        if (DemoApplication.getInstance().getLifecycleCallbacks().current() !is ChatActivity) {
            val event = EaseEvent.create(DemoConstant.MESSAGE_CHANGE_RECALL, EaseEvent.TYPE.MESSAGE)
            messageChangeLiveData?.with(DemoConstant.MESSAGE_CHANGE_CHANGE)?.postValue(event)
//        }
    }

    override fun onMessageRecalled(messages: List<EMMessage>) {
        val event = EaseEvent.create(DemoConstant.MESSAGE_CHANGE_RECALL, EaseEvent.TYPE.MESSAGE)
        messageChangeLiveData?.with(DemoConstant.MESSAGE_CHANGE_CHANGE)?.postValue(event)
        for (msg in messages) {
            if (msg.chatType == EMMessage.ChatType.GroupChat && EaseAtMessageHelper.get()
                    .isAtMeMsg(msg)
            ) {
                EaseAtMessageHelper.get().removeAtMeGroup(msg.to)
            }
            val msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT)
            val txtBody = EMTextMessageBody(
                String.format(
                    context.getString(R.string.msg_recall_by_user),
                    msg.from
                )
            )
            msgNotification.addBody(txtBody)
            msgNotification.from = msg.from
            msgNotification.to = msg.to
            msgNotification.isUnread = false
            msgNotification.msgTime = msg.msgTime
            msgNotification.setLocalTime(msg.msgTime)
            msgNotification.chatType = msg.chatType
            msgNotification.setAttribute(DemoConstant.MESSAGE_TYPE_RECALL, true)
            msgNotification.setStatus(EMMessage.Status.SUCCESS)
            EMClient.getInstance().chatManager().saveMessage(msgNotification)
        }
    }

    private inner class ChatConversationListener : EMConversationListener {
        override fun onCoversationUpdate() {}
        override fun onConversationRead(from: String, to: String) {
            val event = EaseEvent.create(DemoConstant.CONVERSATION_READ, EaseEvent.TYPE.MESSAGE)
            messageChangeLiveData?.with(DemoConstant.CONVERSATION_READ)?.postValue(event)
        }
    }

    private inner class ChatConnectionListener : EMConnectionListener {
        override fun onConnected() {
            EMLog.i(TAG, "onConnected")
            if (ChatHelper.instance?.isLoggedIn!!) {
                return
            }
            if (!isGroupsSyncedWithServer) {
                EMLog.i(TAG, "isGroupsSyncedWithServer")
                EMGroupManagerRepository().getAllGroups(object : ResultCallBack<List<EMGroup?>?>() {
                    override fun onSuccess(value: List<EMGroup?>?) {
                        //加载完群组信息后，刷新会话列表页面，保证展示群组名称
                        EMLog.i(TAG, "isGroupsSyncedWithServer success")
                        val event =
                            EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP)
                        messageChangeLiveData?.with(DemoConstant.GROUP_CHANGE)?.postValue(event)
                    }

                    override fun onError(error: Int, errorMsg: String?) {}
                })
                isGroupsSyncedWithServer = true
            }
            if (!isContactsSyncedWithServer) {
                EMLog.i(TAG, "isContactsSyncedWithServer")
                EMContactManagerRepository().getContactList(object :
                    ResultCallBack<List<EaseUser?>?>() {
                    override fun onSuccess(value: List<EaseUser?>?) {
                        val userDao: EmUserDao? =
                            DemoDbHelper.getInstance(App.instance.applicationContext)?.userDao
                        if (userDao != null) {
                            userDao.clearUsers()
                            userDao.insert(EmUserEntity.parseList(value as MutableList<EaseUser>))
                        }
                    }

                    override fun onError(error: Int, errorMsg: String?) {}
                })
                isContactsSyncedWithServer = true
            }
            if (!isBlackListSyncedWithServer) {
                EMLog.i(TAG, "isBlackListSyncedWithServer")
                EMContactManagerRepository().getBlackContactList(null)
                isBlackListSyncedWithServer = true
            }
            if (!isPushConfigsWithServer) {
                EMLog.i(TAG, "isPushConfigsWithServer")
                //首先获取push配置，否则获取push配置项会为空
                EMPushManagerRepository().fetchPushConfigsFromServer()
                isPushConfigsWithServer = true
            }
        }

        /**
         * 用来监听账号异常
         * @param error
         */
        override fun onDisconnected(error: Int) {
            EMLog.i(TAG, "onDisconnected =$error")
            var event: String? = null
            if (error == EMError.USER_REMOVED) {
                event = DemoConstant.ACCOUNT_REMOVED
            } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                event = DemoConstant.ACCOUNT_CONFLICT
            } else if (error == EMError.SERVER_SERVICE_RESTRICTED) {
                event = DemoConstant.ACCOUNT_FORBIDDEN
            } else if (error == EMError.USER_KICKED_BY_CHANGE_PASSWORD) {
                event = DemoConstant.ACCOUNT_KICKED_BY_CHANGE_PASSWORD
            } else if (error == EMError.USER_KICKED_BY_OTHER_DEVICE) {
                event = DemoConstant.ACCOUNT_KICKED_BY_OTHER_DEVICE
            }
            if (!TextUtils.isEmpty(event)) {
                LiveDataBus.get().with(DemoConstant.ACCOUNT_CHANGE)
                    .postValue(EaseEvent(event, EaseEvent.TYPE.ACCOUNT))
                EMLog.i(TAG, event)
            }
        }
    }

    private inner class ChatGroupListener : EaseGroupListener() {
        override fun onInvitationReceived(
            groupId: String,
            groupName: String,
            inviter: String,
            reason: String
        ) {
            var groupName = groupName
            super.onInvitationReceived(groupId, groupName, inviter, reason)
            //移除相同的请求
            val allMessages = EaseSystemMsgManager.getInstance().allMessages
            if (allMessages != null && !allMessages.isEmpty()) {
                for (message in allMessages) {
                    val ext = message.ext()
                    if (ext != null && ext.containsKey(DemoConstant.SYSTEM_MESSAGE_GROUP_ID) && TextUtils.equals(
                            groupId,
                            ext[DemoConstant.SYSTEM_MESSAGE_GROUP_ID] as String?
                        )
                        && ext.containsKey(DemoConstant.SYSTEM_MESSAGE_INVITER) && TextUtils.equals(
                            inviter,
                            ext[DemoConstant.SYSTEM_MESSAGE_INVITER] as String?
                        )
                    ) {
                        EaseSystemMsgManager.getInstance().removeMessage(message)
                    }
                }
            }
            groupName = if (TextUtils.isEmpty(groupName)) groupId else groupName
            val ext = EaseSystemMsgManager.getInstance().createMsgExt()
            ext[DemoConstant.SYSTEM_MESSAGE_FROM] = groupId
            ext[DemoConstant.SYSTEM_MESSAGE_GROUP_ID] = groupId
            ext[DemoConstant.SYSTEM_MESSAGE_REASON] = reason
            ext[DemoConstant.SYSTEM_MESSAGE_NAME] = groupName
            ext[DemoConstant.SYSTEM_MESSAGE_INVITER] = inviter
            ext[DemoConstant.SYSTEM_MESSAGE_STATUS] = InviteMessageStatus.GROUPINVITATION.name
            val message = EaseSystemMsgManager.getInstance()
                .createMessage(PushAndMessageHelper.getSystemMessage(ext), ext)
            notifyNewInviteMessage(message)
            val event =
                EaseEvent.create(DemoConstant.NOTIFY_GROUP_INVITE_RECEIVE, EaseEvent.TYPE.NOTIFY)
            messageChangeLiveData?.with(DemoConstant.NOTIFY_CHANGE)?.postValue(event)
            showToast(
                context.getString(
                    InviteMessageStatus.GROUPINVITATION.msgContent,
                    inviter,
                    groupName
                )
            )
            EMLog.i(
                TAG,
                context.getString(
                    InviteMessageStatus.GROUPINVITATION.msgContent,
                    inviter,
                    groupName
                )
            )
        }

        override fun onInvitationAccepted(groupId: String, invitee: String, reason: String) {
            super.onInvitationAccepted(groupId, invitee, reason)
            //user accept your invitation
            val groupName: String = GroupHelper.getGroupName(groupId)
            val ext = EaseSystemMsgManager.getInstance().createMsgExt()
            ext[DemoConstant.SYSTEM_MESSAGE_FROM] = groupId
            ext[DemoConstant.SYSTEM_MESSAGE_GROUP_ID] = groupId
            ext[DemoConstant.SYSTEM_MESSAGE_REASON] = reason
            ext[DemoConstant.SYSTEM_MESSAGE_NAME] = groupName
            ext[DemoConstant.SYSTEM_MESSAGE_INVITER] = invitee
            ext[DemoConstant.SYSTEM_MESSAGE_STATUS] =
                InviteMessageStatus.GROUPINVITATION_ACCEPTED.name
            val message = EaseSystemMsgManager.getInstance()
                .createMessage(PushAndMessageHelper.getSystemMessage(ext), ext)
            notifyNewInviteMessage(message)
            val event =
                EaseEvent.create(DemoConstant.NOTIFY_GROUP_INVITE_ACCEPTED, EaseEvent.TYPE.NOTIFY)
            messageChangeLiveData?.with(DemoConstant.NOTIFY_CHANGE)?.postValue(event)
            showToast(
                context.getString(
                    InviteMessageStatus.GROUPINVITATION_ACCEPTED.msgContent,
                    invitee
                )
            )
            EMLog.i(
                TAG,
                context.getString(InviteMessageStatus.GROUPINVITATION_ACCEPTED.msgContent, invitee)
            )
        }

        override fun onInvitationDeclined(groupId: String, invitee: String, reason: String) {
            super.onInvitationDeclined(groupId, invitee, reason)
            //user declined your invitation
            val groupName: String = GroupHelper.getGroupName(groupId)
            val ext = EaseSystemMsgManager.getInstance().createMsgExt()
            ext[DemoConstant.SYSTEM_MESSAGE_FROM] = groupId
            ext[DemoConstant.SYSTEM_MESSAGE_GROUP_ID] = groupId
            ext[DemoConstant.SYSTEM_MESSAGE_REASON] = reason
            ext[DemoConstant.SYSTEM_MESSAGE_NAME] = groupName
            ext[DemoConstant.SYSTEM_MESSAGE_INVITER] = invitee
            ext[DemoConstant.SYSTEM_MESSAGE_STATUS] =
                InviteMessageStatus.GROUPINVITATION_DECLINED.name
            val message = EaseSystemMsgManager.getInstance()
                .createMessage(PushAndMessageHelper.getSystemMessage(ext), ext)
            notifyNewInviteMessage(message)
            val event =
                EaseEvent.create(DemoConstant.NOTIFY_GROUP_INVITE_DECLINED, EaseEvent.TYPE.NOTIFY)
            messageChangeLiveData?.with(DemoConstant.NOTIFY_CHANGE)?.postValue(event)
            showToast(
                context.getString(
                    InviteMessageStatus.GROUPINVITATION_DECLINED.msgContent,
                    invitee
                )
            )
            EMLog.i(
                TAG,
                context.getString(InviteMessageStatus.GROUPINVITATION_DECLINED.msgContent, invitee)
            )
        }

        override fun onUserRemoved(groupId: String, groupName: String) {
            val easeEvent = EaseEvent(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP_LEAVE)
            easeEvent.message = groupId
            messageChangeLiveData?.with(DemoConstant.GROUP_CHANGE)?.postValue(easeEvent)
            showToast(context.getString(R.string.demo_group_listener_onUserRemoved, groupName))
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onUserRemoved, groupName))
        }

        override fun onGroupDestroyed(groupId: String, groupName: String) {
            val easeEvent = EaseEvent(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP_LEAVE)
            easeEvent.message = groupId
            messageChangeLiveData?.with(DemoConstant.GROUP_CHANGE)?.postValue(easeEvent)
            showToast(context.getString(R.string.demo_group_listener_onGroupDestroyed, groupName))
            EMLog.i(
                TAG,
                context.getString(R.string.demo_group_listener_onGroupDestroyed, groupName)
            )
        }

        override fun onRequestToJoinReceived(
            groupId: String,
            groupName: String,
            applicant: String,
            reason: String
        ) {
            super.onRequestToJoinReceived(groupId, groupName, applicant, reason)
            //移除相同的请求
            val allMessages = EaseSystemMsgManager.getInstance().allMessages
            if (allMessages != null && !allMessages.isEmpty()) {
                for (message in allMessages) {
                    val ext = message.ext()
                    if (ext != null && ext.containsKey(DemoConstant.SYSTEM_MESSAGE_GROUP_ID) && TextUtils.equals(
                            groupId,
                            ext[DemoConstant.SYSTEM_MESSAGE_GROUP_ID] as String?
                        )
                        && ext.containsKey(DemoConstant.SYSTEM_MESSAGE_FROM) && TextUtils.equals(
                            applicant,
                            ext[DemoConstant.SYSTEM_MESSAGE_FROM] as String?
                        )
                    ) {
                        EaseSystemMsgManager.getInstance().removeMessage(message)
                    }
                }
            }
            // user apply to join group
            val ext = EaseSystemMsgManager.getInstance().createMsgExt()
            ext[DemoConstant.SYSTEM_MESSAGE_FROM] = applicant
            ext[DemoConstant.SYSTEM_MESSAGE_GROUP_ID] = groupId
            ext[DemoConstant.SYSTEM_MESSAGE_REASON] = reason
            ext[DemoConstant.SYSTEM_MESSAGE_NAME] = groupName
            ext[DemoConstant.SYSTEM_MESSAGE_STATUS] = InviteMessageStatus.BEAPPLYED.name
            val message = EaseSystemMsgManager.getInstance()
                .createMessage(PushAndMessageHelper.getSystemMessage(ext), ext)
            notifyNewInviteMessage(message)
            val event =
                EaseEvent.create(DemoConstant.NOTIFY_GROUP_JOIN_RECEIVE, EaseEvent.TYPE.NOTIFY)
            messageChangeLiveData?.with(DemoConstant.NOTIFY_CHANGE)?.postValue(event)
            showToast(
                context.getString(
                    InviteMessageStatus.BEAPPLYED.msgContent,
                    applicant,
                    groupName
                )
            )
            EMLog.i(
                TAG,
                context.getString(InviteMessageStatus.BEAPPLYED.msgContent, applicant, groupName)
            )
        }

        override fun onRequestToJoinAccepted(groupId: String, groupName: String, accepter: String) {
            super.onRequestToJoinAccepted(groupId, groupName, accepter)
            // your application was accepted
            val msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT)
            msg.chatType = EMMessage.ChatType.GroupChat
            msg.from = accepter
            msg.to = groupId
            msg.msgId = UUID.randomUUID().toString()
            msg.setAttribute(DemoConstant.EM_NOTIFICATION_TYPE, true)
            msg.addBody(
                EMTextMessageBody(
                    context.getString(
                        R.string.demo_group_listener_onRequestToJoinAccepted,
                        accepter,
                        groupName
                    )
                )
            )
            msg.setStatus(EMMessage.Status.SUCCESS)
            // save accept message
            EMClient.getInstance().chatManager().saveMessage(msg)
            // notify the accept message
            notifier.vibrateAndPlayTone(msg)
            val event =
                EaseEvent.create(DemoConstant.MESSAGE_GROUP_JOIN_ACCEPTED, EaseEvent.TYPE.MESSAGE)
            messageChangeLiveData?.with(DemoConstant.MESSAGE_CHANGE_CHANGE)?.postValue(event)
            showToast(
                context.getString(
                    R.string.demo_group_listener_onRequestToJoinAccepted,
                    accepter,
                    groupName
                )
            )
            EMLog.i(
                TAG,
                context.getString(
                    R.string.demo_group_listener_onRequestToJoinAccepted,
                    accepter,
                    groupName
                )
            )
        }

        override fun onRequestToJoinDeclined(
            groupId: String,
            groupName: String,
            decliner: String,
            reason: String
        ) {
            super.onRequestToJoinDeclined(groupId, groupName, decliner, reason)
            showToast(
                context.getString(
                    R.string.demo_group_listener_onRequestToJoinDeclined,
                    decliner,
                    groupName
                )
            )
            EMLog.i(
                TAG,
                context.getString(
                    R.string.demo_group_listener_onRequestToJoinDeclined,
                    decliner,
                    groupName
                )
            )
        }

        override fun onAutoAcceptInvitationFromGroup(
            groupId: String,
            inviter: String,
            inviteMessage: String
        ) {
            super.onAutoAcceptInvitationFromGroup(groupId, inviter, inviteMessage)
            val groupName: String = GroupHelper.getGroupName(groupId)
            val msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT)
            msg.chatType = EMMessage.ChatType.GroupChat
            msg.from = inviter
            msg.to = groupId
            msg.msgId = UUID.randomUUID().toString()
            msg.setAttribute(DemoConstant.EM_NOTIFICATION_TYPE, true)
            msg.addBody(
                EMTextMessageBody(
                    context.getString(
                        R.string.demo_group_listener_onAutoAcceptInvitationFromGroup,
                        groupName
                    )
                )
            )
            msg.setStatus(EMMessage.Status.SUCCESS)
            // save invitation as messages
            EMClient.getInstance().chatManager().saveMessage(msg)
            // notify invitation message
            notifier.vibrateAndPlayTone(msg)
            val event =
                EaseEvent.create(DemoConstant.MESSAGE_GROUP_AUTO_ACCEPT, EaseEvent.TYPE.MESSAGE)
            messageChangeLiveData?.with(DemoConstant.MESSAGE_CHANGE_CHANGE)?.postValue(event)
            showToast(
                context.getString(
                    R.string.demo_group_listener_onAutoAcceptInvitationFromGroup,
                    groupName
                )
            )
            EMLog.i(
                TAG,
                context.getString(
                    R.string.demo_group_listener_onAutoAcceptInvitationFromGroup,
                    groupName
                )
            )
        }

        override fun onMuteListAdded(groupId: String, mutes: List<String>, muteExpire: Long) {
            super.onMuteListAdded(groupId, mutes, muteExpire)
            val content = getContentFromList(mutes)
            showToast(context.getString(R.string.demo_group_listener_onMuteListAdded, content))
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onMuteListAdded, content))
        }

        override fun onMuteListRemoved(groupId: String, mutes: List<String>) {
            super.onMuteListRemoved(groupId, mutes)
            val content = getContentFromList(mutes)
            showToast(context.getString(R.string.demo_group_listener_onMuteListRemoved, content))
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onMuteListRemoved, content))
        }

        override fun onWhiteListAdded(groupId: String, whitelist: List<String>) {
            val easeEvent = EaseEvent(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP)
            easeEvent.message = groupId
            messageChangeLiveData?.with(DemoConstant.GROUP_CHANGE)?.postValue(easeEvent)
            val content = getContentFromList(whitelist)
            showToast(context.getString(R.string.demo_group_listener_onWhiteListAdded, content))
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onWhiteListAdded, content))
        }

        override fun onWhiteListRemoved(groupId: String, whitelist: List<String>) {
            val easeEvent = EaseEvent(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP)
            easeEvent.message = groupId
            messageChangeLiveData?.with(DemoConstant.GROUP_CHANGE)?.postValue(easeEvent)
            val content = getContentFromList(whitelist)
            showToast(context.getString(R.string.demo_group_listener_onWhiteListRemoved, content))
            EMLog.i(
                TAG,
                context.getString(R.string.demo_group_listener_onWhiteListRemoved, content)
            )
        }

        override fun onAllMemberMuteStateChanged(groupId: String, isMuted: Boolean) {
            val easeEvent = EaseEvent(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP)
            easeEvent.message = groupId
            messageChangeLiveData?.with(DemoConstant.GROUP_CHANGE)?.postValue(easeEvent)
            showToast(context.getString(if (isMuted) R.string.demo_group_listener_onAllMemberMuteStateChanged_mute else R.string.demo_group_listener_onAllMemberMuteStateChanged_not_mute))
            EMLog.i(
                TAG,
                context.getString(if (isMuted) R.string.demo_group_listener_onAllMemberMuteStateChanged_mute else R.string.demo_group_listener_onAllMemberMuteStateChanged_not_mute)
            )
        }

        override fun onAdminAdded(groupId: String, administrator: String) {
            super.onAdminAdded(groupId, administrator)
            LiveDataBus.get().with(DemoConstant.GROUP_CHANGE)
                .postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP))
            showToast(context.getString(R.string.demo_group_listener_onAdminAdded, administrator))
            EMLog.i(
                TAG,
                context.getString(R.string.demo_group_listener_onAdminAdded, administrator)
            )
        }

        override fun onAdminRemoved(groupId: String, administrator: String) {
            LiveDataBus.get().with(DemoConstant.GROUP_CHANGE)
                .postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP))
            showToast(context.getString(R.string.demo_group_listener_onAdminRemoved, administrator))
            EMLog.i(
                TAG,
                context.getString(R.string.demo_group_listener_onAdminRemoved, administrator)
            )
        }

        override fun onOwnerChanged(groupId: String, newOwner: String, oldOwner: String) {
            LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(
                EaseEvent.create(
                    DemoConstant.GROUP_OWNER_TRANSFER,
                    EaseEvent.TYPE.GROUP
                )
            )
            showToast(
                context.getString(
                    R.string.demo_group_listener_onOwnerChanged,
                    oldOwner,
                    newOwner
                )
            )
            EMLog.i(
                TAG,
                context.getString(R.string.demo_group_listener_onOwnerChanged, oldOwner, newOwner)
            )
        }

        override fun onMemberJoined(groupId: String, member: String) {
            LiveDataBus.get().with(DemoConstant.GROUP_CHANGE)
                .postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP))
            showToast(context.getString(R.string.demo_group_listener_onMemberJoined, member))
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onMemberJoined, member))
        }

        override fun onMemberExited(groupId: String, member: String) {
            LiveDataBus.get().with(DemoConstant.GROUP_CHANGE)
                .postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP))
            showToast(context.getString(R.string.demo_group_listener_onMemberExited, member))
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onMemberExited, member))
        }

        override fun onAnnouncementChanged(groupId: String, announcement: String) {
            showToast(context.getString(R.string.demo_group_listener_onAnnouncementChanged))
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onAnnouncementChanged))
        }

        override fun onSharedFileAdded(groupId: String, sharedFile: EMMucSharedFile) {
            LiveDataBus.get().with(DemoConstant.GROUP_SHARE_FILE_CHANGE).postValue(
                EaseEvent.create(
                    DemoConstant.GROUP_SHARE_FILE_CHANGE,
                    EaseEvent.TYPE.GROUP
                )
            )
            showToast(
                context.getString(
                    R.string.demo_group_listener_onSharedFileAdded,
                    sharedFile.fileName
                )
            )
            EMLog.i(
                TAG,
                context.getString(
                    R.string.demo_group_listener_onSharedFileAdded,
                    sharedFile.fileName
                )
            )
        }

        override fun onSharedFileDeleted(groupId: String, fileId: String) {
            LiveDataBus.get().with(DemoConstant.GROUP_SHARE_FILE_CHANGE).postValue(
                EaseEvent.create(
                    DemoConstant.GROUP_SHARE_FILE_CHANGE,
                    EaseEvent.TYPE.GROUP
                )
            )
            showToast(context.getString(R.string.demo_group_listener_onSharedFileDeleted, fileId))
            EMLog.i(
                TAG,
                context.getString(R.string.demo_group_listener_onSharedFileDeleted, fileId)
            )
        }
    }

    private inner class ChatContactListener : EMContactListener {
        override fun onContactAdded(username: String) {
            EMLog.i("ChatContactListener", "onContactAdded")
            val userId = arrayOfNulls<String>(1)
            userId[0] = username
            EMClient.getInstance().userInfoManager()
                .fetchUserInfoByUserId(userId, object : EMValueCallBack<Map<String?, EMUserInfo?>> {
                    override fun onSuccess(value: Map<String?, EMUserInfo?>) {
                        val userInfo = value[username]
                        val entity = EmUserEntity()
                        entity.username = username
                        if (userInfo != null) {
                            entity.nickname = userInfo.nickName
                            entity.email = userInfo.email
                            entity.avatar = userInfo.avatarUrl
                            entity.birth = userInfo.birth
                            entity.gender = userInfo.gender
                            entity.ext = userInfo.ext
                            entity.contact = 0
                            entity.sign = userInfo.signature
                        }
                        ChatHelper.instance?.model?.insert(entity)
                        ChatHelper.instance?.updateContactList()
                        val event =
                            EaseEvent.create(DemoConstant.CONTACT_ADD, EaseEvent.TYPE.CONTACT)
                        event.message = username
                        messageChangeLiveData?.with(DemoConstant.CONTACT_ADD)?.postValue(event)
                        showToast(
                            context.getString(
                                R.string.demo_contact_listener_onContactAdded,
                                username
                            )
                        )
                        EMLog.i(
                            TAG,
                            context.getString(
                                R.string.demo_contact_listener_onContactAdded,
                                username
                            )
                        )
                    }

                    override fun onError(error: Int, errorMsg: String) {
                        EMLog.i(
                            TAG,
                            context.getString(R.string.demo_contact_get_userInfo_failed) + username + "error:" + error + " errorMsg:" + errorMsg
                        )
                    }
                })
        }

        override fun onContactDeleted(username: String) {
            EMLog.i("ChatContactListener", "onContactDeleted")
            val deleteUsername: Boolean? =
                ChatHelper.instance?.model?.isDeleteUsername(username)
            val num: Int? =  ChatHelper.instance?.deleteContact(username)
            ChatHelper.instance?.updateContactList()
            val event = EaseEvent.create(DemoConstant.CONTACT_DELETE, EaseEvent.TYPE.CONTACT)
            event.message = username
            messageChangeLiveData?.with(DemoConstant.CONTACT_DELETE)?.postValue(event)
            if (deleteUsername!! || num == 0) {
                showToast(
                    context.getString(
                        R.string.demo_contact_listener_onContactDeleted,
                        username
                    )
                )
                EMLog.i(
                    TAG,
                    context.getString(R.string.demo_contact_listener_onContactDeleted, username)
                )
            } else {
                //showToast(context.getString(R.string.demo_contact_listener_onContactDeleted_by_other, username));
                EMLog.i(
                    TAG,
                    context.getString(
                        R.string.demo_contact_listener_onContactDeleted_by_other,
                        username
                    )
                )
            }
        }

        override fun onContactInvited(username: String, reason: String) {
            EMLog.i("ChatContactListener", "onContactInvited")
            val allMessages = EaseSystemMsgManager.getInstance().allMessages
            if (allMessages != null && !allMessages.isEmpty()) {
                for (message in allMessages) {
                    val ext = message.ext()
                    if (ext != null && !ext.containsKey(DemoConstant.SYSTEM_MESSAGE_GROUP_ID)
                        && ext.containsKey(DemoConstant.SYSTEM_MESSAGE_FROM) && TextUtils.equals(
                            username,
                            ext[DemoConstant.SYSTEM_MESSAGE_FROM] as String?
                        )
                    ) {
                        EaseSystemMsgManager.getInstance().removeMessage(message)
                    }
                }
            }
            val ext = EaseSystemMsgManager.getInstance().createMsgExt()
            ext[DemoConstant.SYSTEM_MESSAGE_FROM] = username
            ext[DemoConstant.SYSTEM_MESSAGE_REASON] = reason
            ext[DemoConstant.SYSTEM_MESSAGE_STATUS] = InviteMessageStatus.BEINVITEED.name
            val message = EaseSystemMsgManager.getInstance()
                .createMessage(PushAndMessageHelper.getSystemMessage(ext), ext)
            notifyNewInviteMessage(message)
            val event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT)
            messageChangeLiveData?.with(DemoConstant.CONTACT_CHANGE)?.postValue(event)
            showToast(context.getString(InviteMessageStatus.BEINVITEED.msgContent, username))
            EMLog.i(TAG, context.getString(InviteMessageStatus.BEINVITEED.msgContent, username))
        }

        override fun onFriendRequestAccepted(username: String) {
            EMLog.i("ChatContactListener", "onFriendRequestAccepted")
            val allMessages = EaseSystemMsgManager.getInstance().allMessages
            if (allMessages != null && !allMessages.isEmpty()) {
                for (message in allMessages) {
                    val ext = message.ext()
                    if (ext != null && (ext.containsKey(DemoConstant.SYSTEM_MESSAGE_FROM)
                                && TextUtils.equals(
                            username,
                            ext[DemoConstant.SYSTEM_MESSAGE_FROM] as String?
                        ))
                    ) {
                        updateMessage(message)
                        return
                    }
                }
            }
            val ext = EaseSystemMsgManager.getInstance().createMsgExt()
            ext[DemoConstant.SYSTEM_MESSAGE_FROM] = username
            ext[DemoConstant.SYSTEM_MESSAGE_STATUS] = InviteMessageStatus.BEAGREED.name
            val message = EaseSystemMsgManager.getInstance()
                .createMessage(PushAndMessageHelper.getSystemMessage(ext), ext)
            notifyNewInviteMessage(message)
            val event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT)
            messageChangeLiveData?.with(DemoConstant.CONTACT_CHANGE)?.postValue(event)
            showToast(context.getString(InviteMessageStatus.BEAGREED.msgContent))
            EMLog.i(TAG, context.getString(InviteMessageStatus.BEAGREED.msgContent))
        }

        override fun onFriendRequestDeclined(username: String) {
            EMLog.i("ChatContactListener", "onFriendRequestDeclined")
            val ext = EaseSystemMsgManager.getInstance().createMsgExt()
            ext[DemoConstant.SYSTEM_MESSAGE_FROM] = username
            ext[DemoConstant.SYSTEM_MESSAGE_STATUS] = InviteMessageStatus.BEREFUSED.name
            val message = EaseSystemMsgManager.getInstance()
                .createMessage(PushAndMessageHelper.getSystemMessage(ext), ext)
            notifyNewInviteMessage(message)
            val event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT)
            messageChangeLiveData?.with(DemoConstant.CONTACT_CHANGE)?.postValue(event)
            showToast(context.getString(InviteMessageStatus.BEREFUSED.msgContent, username))
            EMLog.i(TAG, context.getString(InviteMessageStatus.BEREFUSED.msgContent, username))
        }
    }

    private fun updateMessage(message: EMMessage) {
        message.setAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS, InviteMessageStatus.BEAGREED.name)
        val body = EMTextMessageBody(PushAndMessageHelper.getSystemMessage(message.ext()))
        message.addBody(body)
        EaseSystemMsgManager.getInstance().updateMessage(message)
    }

    private inner class ChatMultiDeviceListener : EMMultiDeviceListener {
        override fun onContactEvent(event: Int, target: String, ext: String) {
            EMLog.i(TAG, "onContactEvent event$event")
            val dbHelper = DemoDbHelper.getInstance(App.instance.applicationContext)
            var message: String? = null
            when (event) {
                EMMultiDeviceListener.CONTACT_REMOVE -> {
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_REMOVE")
                    message = DemoConstant.CONTACT_REMOVE
                    if (dbHelper!!.userDao != null) {
                        dbHelper.userDao!!.deleteUser(target)
                    }
                    removeTargetSystemMessage(target, DemoConstant.SYSTEM_MESSAGE_FROM)
                    // TODO: 2020/1/16 0016 确认此处逻辑，是否是删除当前的target
                    ChatHelper.instance?.chatManager?.deleteConversation(target, false)
                    showToast("CONTACT_REMOVE")
                }
                EMMultiDeviceListener.CONTACT_ACCEPT -> {
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_ACCEPT")
                    message = DemoConstant.CONTACT_ACCEPT
                    val entity = EmUserEntity()
                    entity.username = target
                    if (dbHelper!!.userDao != null) {
                        dbHelper.userDao!!.insert(entity)
                    }
                    updateContactNotificationStatus(
                        target,
                        "",
                        InviteMessageStatus.MULTI_DEVICE_CONTACT_ACCEPT
                    )
                    showToast("CONTACT_ACCEPT")
                }
                EMMultiDeviceListener.CONTACT_DECLINE -> {
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_DECLINE")
                    message = DemoConstant.CONTACT_DECLINE
                    updateContactNotificationStatus(
                        target,
                        "",
                        InviteMessageStatus.MULTI_DEVICE_CONTACT_DECLINE
                    )
                    showToast("CONTACT_DECLINE")
                }
                EMMultiDeviceListener.CONTACT_BAN -> {
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_BAN")
                    message = DemoConstant.CONTACT_BAN
                    if (dbHelper!!.userDao != null) {
                        dbHelper.userDao!!.deleteUser(target)
                    }
                    removeTargetSystemMessage(target, DemoConstant.SYSTEM_MESSAGE_FROM)
                    ChatHelper.instance?.chatManager?.deleteConversation(target, false)
                    updateContactNotificationStatus(
                        target,
                        "",
                        InviteMessageStatus.MULTI_DEVICE_CONTACT_BAN
                    )
                    showToast("CONTACT_BAN")
                }
                EMMultiDeviceListener.CONTACT_ALLOW -> {
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_ALLOW")
                    message = DemoConstant.CONTACT_ALLOW
                    updateContactNotificationStatus(
                        target,
                        "",
                        InviteMessageStatus.MULTI_DEVICE_CONTACT_ALLOW
                    )
                    showToast("CONTACT_ALLOW")
                }
            }
            if (!TextUtils.isEmpty(message)) {
                val easeEvent = EaseEvent.create(message, EaseEvent.TYPE.CONTACT)
                message?.let { messageChangeLiveData?.with(it)?.postValue(easeEvent) }
            }
        }

        override fun onGroupEvent(event: Int, groupId: String, usernames: List<String>) {
            EMLog.i(TAG, "onGroupEvent event$event")
            var message: String? = null
            when (event) {
                GROUP_CREATE -> {
                    saveGroupNotification(
                        groupId,  /*groupName*/
                        "",  /*person*/
                        "",  /*reason*/
                        "",
                        InviteMessageStatus.MULTI_DEVICE_GROUP_CREATE
                    )
                    showToast("GROUP_CREATE")
                }
                GROUP_DESTROY -> {
                    removeTargetSystemMessage(groupId, DemoConstant.SYSTEM_MESSAGE_GROUP_ID)
                    saveGroupNotification(
                        groupId,  /*groupName*/
                        "",  /*person*/
                        "",  /*reason*/
                        "",
                        InviteMessageStatus.MULTI_DEVICE_GROUP_DESTROY
                    )
                    message = DemoConstant.GROUP_CHANGE
                    showToast("GROUP_DESTROY")
                }
                GROUP_JOIN -> {
                    saveGroupNotification(
                        groupId,  /*groupName*/
                        "",  /*person*/
                        "",  /*reason*/
                        "",
                        InviteMessageStatus.MULTI_DEVICE_GROUP_JOIN
                    )
                    message = DemoConstant.GROUP_CHANGE
                    showToast("GROUP_JOIN")
                }
                EMMultiDeviceListener.GROUP_LEAVE -> {
                    removeTargetSystemMessage(groupId, DemoConstant.SYSTEM_MESSAGE_GROUP_ID)
                    saveGroupNotification(
                        groupId,  /*groupName*/
                        "",  /*person*/
                        "",  /*reason*/
                        "",
                        InviteMessageStatus.MULTI_DEVICE_GROUP_LEAVE
                    )
                    message = DemoConstant.GROUP_CHANGE
                    showToast("GROUP_LEAVE")
                }
                GROUP_APPLY -> {
                    removeTargetSystemMessage(groupId, DemoConstant.SYSTEM_MESSAGE_GROUP_ID)
                    saveGroupNotification(
                        groupId,  /*groupName*/
                        "",  /*person*/
                        "",  /*reason*/
                        "",
                        InviteMessageStatus.MULTI_DEVICE_GROUP_APPLY
                    )
                    showToast("GROUP_APPLY")
                }
                GROUP_APPLY_ACCEPT -> {
                    removeTargetSystemMessage(
                        groupId, DemoConstant.SYSTEM_MESSAGE_GROUP_ID,
                        usernames[0], DemoConstant.SYSTEM_MESSAGE_FROM
                    )
                    // TODO: person, reason from ext
                    saveGroupNotification(
                        groupId,  /*groupName*/
                        "",  /*person*/
                        usernames[0],  /*reason*/
                        "",
                        InviteMessageStatus.MULTI_DEVICE_GROUP_APPLY_ACCEPT
                    )
                    showToast("GROUP_APPLY_ACCEPT")
                }
                GROUP_APPLY_DECLINE -> {
                    removeTargetSystemMessage(
                        groupId, DemoConstant.SYSTEM_MESSAGE_GROUP_ID,
                        usernames[0], DemoConstant.SYSTEM_MESSAGE_FROM
                    )
                    // TODO: person, reason from ext
                    saveGroupNotification(
                        groupId,  /*groupName*/
                        "",  /*person*/
                        usernames[0],  /*reason*/
                        "",
                        InviteMessageStatus.MULTI_DEVICE_GROUP_APPLY_DECLINE
                    )
                    showToast("GROUP_APPLY_DECLINE")
                }
                GROUP_INVITE -> {
                    // TODO: person, reason from ext
                    saveGroupNotification(
                        groupId,  /*groupName*/"",  /*person*/
                        usernames[0],  /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE
                    )
                    showToast("GROUP_INVITE")
                }
                GROUP_INVITE_ACCEPT -> {
                    val st3 = context.getString(R.string.Invite_you_to_join_a_group_chat)
                    val msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT)
                    msg.chatType = EMMessage.ChatType.GroupChat
                    // TODO: person, reason from ext
                    val from = ""
                    if (usernames != null && usernames.size > 0) {
                        msg.from = usernames[0]
                    }
                    msg.to = groupId
                    msg.msgId = UUID.randomUUID().toString()
                    msg.setAttribute(DemoConstant.EM_NOTIFICATION_TYPE, true)
                    msg.addBody(EMTextMessageBody(msg.from + " " + st3))
                    msg.setStatus(EMMessage.Status.SUCCESS)
                    // save invitation as messages
                    EMClient.getInstance().chatManager().saveMessage(msg)
                    removeTargetSystemMessage(groupId, DemoConstant.SYSTEM_MESSAGE_GROUP_ID)
                    // TODO: person, reason from ext
                    saveGroupNotification(
                        groupId,  /*groupName*/
                        "",  /*person*/
                        "",  /*reason*/
                        "",
                        InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE_ACCEPT
                    )
                    message = DemoConstant.GROUP_CHANGE
                    showToast("GROUP_INVITE_ACCEPT")
                }
                GROUP_INVITE_DECLINE -> {
                    removeTargetSystemMessage(groupId, DemoConstant.SYSTEM_MESSAGE_GROUP_ID)
                    // TODO: person, reason from ext
                    saveGroupNotification(
                        groupId,  /*groupName*/
                        "",  /*person*/
                        usernames[0],  /*reason*/
                        "",
                        InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE_DECLINE
                    )
                    showToast("GROUP_INVITE_DECLINE")
                }
                GROUP_KICK -> {
                    // TODO: person, reason from ext
                    saveGroupNotification(
                        groupId,  /*groupName*/"",  /*person*/
                        usernames[0],  /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_KICK
                    )
                    message = DemoConstant.GROUP_CHANGE
                    showToast("GROUP_KICK")
                }
                GROUP_BAN -> {
                    // TODO: person from ext
                    saveGroupNotification(
                        groupId,  /*groupName*/"",  /*person*/
                        usernames[0],  /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_BAN
                    )
                    message = DemoConstant.GROUP_CHANGE
                    showToast("GROUP_BAN")
                }
                GROUP_ALLOW -> {
                    // TODO: person from ext
                    saveGroupNotification(
                        groupId,  /*groupName*/"",  /*person*/
                        usernames[0],  /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_ALLOW
                    )
                    showToast("GROUP_ALLOW")
                }
                GROUP_BLOCK -> {
                    saveGroupNotification(
                        groupId,  /*groupName*/
                        "",  /*person*/
                        "",  /*reason*/
                        "",
                        InviteMessageStatus.MULTI_DEVICE_GROUP_BLOCK
                    )
                    showToast("GROUP_BLOCK")
                }
                GROUP_UNBLOCK -> {
                    // TODO: person from ext
                    saveGroupNotification(
                        groupId,  /*groupName*/
                        "",  /*person*/
                        "",  /*reason*/
                        "",
                        InviteMessageStatus.MULTI_DEVICE_GROUP_UNBLOCK
                    )
                    showToast("GROUP_UNBLOCK")
                }
                GROUP_ASSIGN_OWNER -> {
                    // TODO: person from ext
                    saveGroupNotification(
                        groupId,  /*groupName*/
                        "",  /*person*/
                        usernames[0],  /*reason*/
                        "",
                        InviteMessageStatus.MULTI_DEVICE_GROUP_ASSIGN_OWNER
                    )
                    showToast("GROUP_ASSIGN_OWNER")
                }
                GROUP_ADD_ADMIN -> {
                    // TODO: person from ext
                    saveGroupNotification(
                        groupId,  /*groupName*/
                        "",  /*person*/
                        usernames[0],  /*reason*/
                        "",
                        InviteMessageStatus.MULTI_DEVICE_GROUP_ADD_ADMIN
                    )
                    message = DemoConstant.GROUP_CHANGE
                    showToast("GROUP_ADD_ADMIN")
                }
                GROUP_REMOVE_ADMIN -> {
                    // TODO: person from ext
                    saveGroupNotification(
                        groupId,  /*groupName*/
                        "",  /*person*/
                        usernames[0],  /*reason*/
                        "",
                        InviteMessageStatus.MULTI_DEVICE_GROUP_REMOVE_ADMIN
                    )
                    message = DemoConstant.GROUP_CHANGE
                    showToast("GROUP_REMOVE_ADMIN")
                }
                GROUP_ADD_MUTE -> {
                    // TODO: person from ext
                    saveGroupNotification(
                        groupId,  /*groupName*/"",  /*person*/
                        usernames[0],  /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_ADD_MUTE
                    )
                    showToast("GROUP_ADD_MUTE")
                }
                GROUP_REMOVE_MUTE -> {
                    // TODO: person from ext
                    saveGroupNotification(
                        groupId,  /*groupName*/
                        "",  /*person*/
                        usernames[0],  /*reason*/
                        "",
                        InviteMessageStatus.MULTI_DEVICE_GROUP_REMOVE_MUTE
                    )
                    showToast("GROUP_REMOVE_MUTE")
                }
                else -> {
                }
            }
            if (!TextUtils.isEmpty(message)) {
                val easeEvent = EaseEvent.create(message, EaseEvent.TYPE.GROUP)
                message?.let { messageChangeLiveData?.with(it)?.postValue(easeEvent) }
            }
        }
    }

    /**
     * 移除目标所有的消息记录，如果目标被删除
     * @param target
     */
    private fun removeTargetSystemMessage(target: String, params: String) {
        val conversation = EaseSystemMsgManager.getInstance().conversation
        val messages = conversation.allMessages
        if (messages != null && !messages.isEmpty()) {
            for (message in messages) {
                var from: String? = null
                try {
                    from = message.getStringAttribute(params)
                } catch (e: HyphenateException) {
                    e.printStackTrace()
                }
                if (TextUtils.equals(from, target)) {
                    conversation.removeMessage(message.msgId)
                }
            }
        }
    }

    /**
     * 移除目标所有的消息记录，如果目标被删除
     * @param target1
     */
    private fun removeTargetSystemMessage(
        target1: String,
        params1: String,
        target2: String,
        params2: String
    ) {
        val conversation = EaseSystemMsgManager.getInstance().conversation
        val messages = conversation.allMessages
        if (messages != null && !messages.isEmpty()) {
            for (message in messages) {
                var targetParams1: String? = null
                var targetParams2: String? = null
                try {
                    targetParams1 = message.getStringAttribute(params1)
                    targetParams2 = message.getStringAttribute(params2)
                } catch (e: HyphenateException) {
                    e.printStackTrace()
                }
                if (TextUtils.equals(targetParams1, target1) && TextUtils.equals(
                        targetParams2,
                        target2
                    )
                ) {
                    conversation.removeMessage(message.msgId)
                }
            }
        }
    }

    private fun notifyNewInviteMessage(msg: EMMessage?) {
        // notify there is new message
        notifier.vibrateAndPlayTone(null)
    }

    private fun updateContactNotificationStatus(
        from: String,
        reason: String,
        status: InviteMessageStatus
    ) {
        var msg: EMMessage? = null
        val conversation = EaseSystemMsgManager.getInstance().conversation
        val allMessages = conversation.allMessages
        if (allMessages != null && !allMessages.isEmpty()) {
            for (message in allMessages) {
                val ext = message.ext()
                if (ext != null && (ext.containsKey(DemoConstant.SYSTEM_MESSAGE_FROM)
                            && TextUtils.equals(
                        from,
                        ext[DemoConstant.SYSTEM_MESSAGE_FROM] as String?
                    ))
                ) {
                    msg = message
                }
            }
        }
        if (msg != null) {
            msg.setAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS, status.name)
            EaseSystemMsgManager.getInstance().updateMessage(msg)
        } else {
            // save invitation as message
            val ext = EaseSystemMsgManager.getInstance().createMsgExt()
            ext[DemoConstant.SYSTEM_MESSAGE_FROM] = from
            ext[DemoConstant.SYSTEM_MESSAGE_REASON] = reason
            ext[DemoConstant.SYSTEM_MESSAGE_STATUS] = status.name
            msg = EaseSystemMsgManager.getInstance()
                .createMessage(PushAndMessageHelper.getSystemMessage(ext), ext)
            notifyNewInviteMessage(msg)
        }
    }

    private fun saveGroupNotification(
        groupId: String,
        groupName: String,
        inviter: String,
        reason: String,
        status: InviteMessageStatus
    ) {
        val ext = EaseSystemMsgManager.getInstance().createMsgExt()
        ext[DemoConstant.SYSTEM_MESSAGE_FROM] = groupId
        ext[DemoConstant.SYSTEM_MESSAGE_GROUP_ID] = groupId
        ext[DemoConstant.SYSTEM_MESSAGE_REASON] = reason
        ext[DemoConstant.SYSTEM_MESSAGE_NAME] = groupName
        ext[DemoConstant.SYSTEM_MESSAGE_INVITER] = inviter
        ext[DemoConstant.SYSTEM_MESSAGE_STATUS] = status.name
        val message = EaseSystemMsgManager.getInstance()
            .createMessage(PushAndMessageHelper.getSystemMessage(ext), ext)
        notifyNewInviteMessage(message)
    }

    private inner class ChatRoomListener : EMChatRoomChangeListener {
        override fun onChatRoomDestroyed(roomId: String, roomName: String) {
            setChatRoomEvent(roomId, EaseEvent.TYPE.CHAT_ROOM_LEAVE)
            showToast(
                context.getString(
                    R.string.demo_chat_room_listener_onChatRoomDestroyed,
                    roomName
                )
            )
            EMLog.i(
                TAG,
                context.getString(R.string.demo_chat_room_listener_onChatRoomDestroyed, roomName)
            )
        }

        override fun onMemberJoined(roomId: String, participant: String) {
            setChatRoomEvent(roomId, EaseEvent.TYPE.CHAT_ROOM)
            showToast(
                context.getString(
                    R.string.demo_chat_room_listener_onMemberJoined,
                    participant
                )
            )
            EMLog.i(
                TAG,
                context.getString(R.string.demo_chat_room_listener_onMemberJoined, participant)
            )
        }

        override fun onMemberExited(roomId: String, roomName: String, participant: String) {
            setChatRoomEvent(roomId, EaseEvent.TYPE.CHAT_ROOM)
            showToast(
                context.getString(
                    R.string.demo_chat_room_listener_onMemberExited,
                    participant
                )
            )
            EMLog.i(
                TAG,
                context.getString(R.string.demo_chat_room_listener_onMemberExited, participant)
            )
        }

        override fun onRemovedFromChatRoom(
            reason: Int,
            roomId: String,
            roomName: String,
            participant: String
        ) {
            if (TextUtils.equals(ChatHelper.instance?.currentUser, participant)) {
                setChatRoomEvent(roomId, EaseEvent.TYPE.CHAT_ROOM)
                if (reason == EMAChatRoomManagerListener.BE_KICKED) {
                    showToast(R.string.quiting_the_chat_room)
                    showToast(R.string.quiting_the_chat_room)
                } else {
                    showToast(
                        context.getString(
                            R.string.demo_chat_room_listener_onRemovedFromChatRoom,
                            participant
                        )
                    )
                    EMLog.i(
                        TAG,
                        context.getString(
                            R.string.demo_chat_room_listener_onRemovedFromChatRoom,
                            participant
                        )
                    )
                }
            }
        }

        override fun onMuteListAdded(chatRoomId: String, mutes: List<String>, expireTime: Long) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM)
            val content = getContentFromList(mutes)
            showToast(context.getString(R.string.demo_chat_room_listener_onMuteListAdded, content))
            EMLog.i(
                TAG,
                context.getString(R.string.demo_chat_room_listener_onMuteListAdded, content)
            )
        }

        override fun onMuteListRemoved(chatRoomId: String, mutes: List<String>) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM)
            val content = getContentFromList(mutes)
            showToast(
                context.getString(
                    R.string.demo_chat_room_listener_onMuteListRemoved,
                    content
                )
            )
            EMLog.i(
                TAG,
                context.getString(R.string.demo_chat_room_listener_onMuteListRemoved, content)
            )
        }

        override fun onWhiteListAdded(chatRoomId: String, whitelist: List<String>) {
            val content = getContentFromList(whitelist)
            showToast(context.getString(R.string.demo_chat_room_listener_onWhiteListAdded, content))
            EMLog.i(
                TAG,
                context.getString(R.string.demo_chat_room_listener_onWhiteListAdded, content)
            )
        }

        override fun onWhiteListRemoved(chatRoomId: String, whitelist: List<String>) {
            val content = getContentFromList(whitelist)
            showToast(
                context.getString(
                    R.string.demo_chat_room_listener_onWhiteListRemoved,
                    content
                )
            )
            EMLog.i(
                TAG,
                context.getString(R.string.demo_chat_room_listener_onWhiteListRemoved, content)
            )
        }

        override fun onAllMemberMuteStateChanged(chatRoomId: String, isMuted: Boolean) {
            showToast(context.getString(if (isMuted) R.string.demo_chat_room_listener_onAllMemberMuteStateChanged_mute else R.string.demo_chat_room_listener_onAllMemberMuteStateChanged_note_mute))
            EMLog.i(
                TAG,
                context.getString(if (isMuted) R.string.demo_chat_room_listener_onAllMemberMuteStateChanged_mute else R.string.demo_chat_room_listener_onAllMemberMuteStateChanged_note_mute)
            )
        }

        override fun onAdminAdded(chatRoomId: String, admin: String) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM)
            showToast(context.getString(R.string.demo_chat_room_listener_onAdminAdded, admin))
            EMLog.i(TAG, context.getString(R.string.demo_chat_room_listener_onAdminAdded, admin))
        }

        override fun onAdminRemoved(chatRoomId: String, admin: String) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM)
            showToast(context.getString(R.string.demo_chat_room_listener_onAdminRemoved, admin))
            EMLog.i(TAG, context.getString(R.string.demo_chat_room_listener_onAdminRemoved, admin))
        }

        override fun onOwnerChanged(chatRoomId: String, newOwner: String, oldOwner: String) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM)
            showToast(
                context.getString(
                    R.string.demo_chat_room_listener_onOwnerChanged,
                    oldOwner,
                    newOwner
                )
            )
            EMLog.i(
                TAG,
                context.getString(
                    R.string.demo_chat_room_listener_onOwnerChanged,
                    oldOwner,
                    newOwner
                )
            )
        }

        override fun onAnnouncementChanged(chatRoomId: String, announcement: String) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM)
            showToast(context.getString(R.string.demo_chat_room_listener_onAnnouncementChanged))
            EMLog.i(TAG, context.getString(R.string.demo_chat_room_listener_onAnnouncementChanged))
        }
    }

    private fun setChatRoomEvent(roomId: String, type: EaseEvent.TYPE) {
        val easeEvent = EaseEvent(DemoConstant.CHAT_ROOM_CHANGE, type)
        easeEvent.message = roomId
        messageChangeLiveData?.with(DemoConstant.CHAT_ROOM_CHANGE)?.postValue(easeEvent)
    }

    private fun getContentFromList(members: List<String>): String {
        val sb = StringBuilder()
        for (member in members) {
            if (!TextUtils.isEmpty(sb.toString().trim { it <= ' ' })) {
                sb.append(",")
            }
            sb.append(member)
        }
        var content = sb.toString()
        if (content.contains(EMClient.getInstance().currentUser)) {
            content = "您"
        }
        return content
    }

    companion object {
        private val TAG = ChatPresenter::class.java.simpleName
        private const val HANDLER_SHOW_TOAST = 0
        var instance: ChatPresenter? = null
            get() {
                if (field == null) {
                    synchronized(ChatPresenter::class.java) {
                        if (field == null) {
                            field = ChatPresenter()
                        }
                    }
                }
                return field
            }
            private set
    }

    init {
        appContext = App.instance.applicationContext
        initHandler(appContext.getMainLooper())
        messageChangeLiveData = LiveDataBus.get()
        //添加网络连接状态监听
        ChatHelper.instance?.eMClient?.addConnectionListener(ChatConnectionListener())
        //添加多端登录监听
        ChatHelper.instance?.eMClient?.addMultiDeviceListener(ChatMultiDeviceListener())
        //添加群组监听
        ChatHelper.instance?.groupManager?.addGroupChangeListener(ChatGroupListener())
        //添加联系人监听
        ChatHelper.instance?.contactManager?.setContactListener(ChatContactListener())
        //添加聊天室监听
        ChatHelper.instance?.chatroomManager?.addChatRoomChangeListener(ChatRoomListener())
        //添加对会话的监听（监听已读回执）
        ChatHelper.instance?.chatManager?.addConversationListener(ChatConversationListener())
    }
}

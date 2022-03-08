package com.qipa.newboxproject.ui.fragment.chat

import android.app.Activity
import android.app.Dialog
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hyphenate.easeui.modules.chat.EaseChatFragment
import com.hyphenate.chat.EMGroup

import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMCustomMessageBody
import com.hyphenate.chat.EMMessage


import com.hyphenate.easeui.constants.EaseConstant
import com.hyphenate.easeui.domain.EaseUser
import com.hyphenate.easeui.model.EaseEvent

import com.hyphenate.easeui.modules.chat.interfaces.OnRecallMessageResultListener
import com.hyphenate.easeui.modules.menu.EasePopupWindowHelper
import com.qipa.newboxproject.R
import com.qipa.newboxproject.data.model.EmojiconExampleGroupData
import com.hyphenate.easeui.modules.menu.MenuItemBean
import com.hyphenate.util.EMFileHelper
import com.hyphenate.util.EMLog
import com.qipa.newboxproject.app.ChatHelper
import com.qipa.newboxproject.data.db.LiveDataBus
import com.qipa.newboxproject.data.db.delegates.DemoConstant
import com.qipa.newboxproject.viewmodel.state.MessageViewModel
import java.util.HashMap

class ChatFragment : EaseChatFragment(), OnRecallMessageResultListener {
    private var viewModel: MessageViewModel? = null
    protected var clipboard: ClipboardManager? = null
    private var infoListener: OnFragmentInfoListener? = null
    private var dialog: Dialog? = null
    override fun initView() {
        super.initView()
        clipboard = mContext!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        viewModel = ViewModelProvider(this).get(MessageViewModel::class.java)

        //获取到聊天列表控件
        //EaseChatMessageListLayout messageListLayout = chatLayout.getChatMessageListLayout();
        //设置聊天列表背景
        //messageListLayout.setBackground(new ColorDrawable(Color.parseColor("#DA5A4D")));
        //设置默认头像
        //messageListLayout.setAvatarDefaultSrc(ContextCompat.getDrawable(mContext, R.drawable.ease_default_avatar));
        //设置头像形状
        //messageListLayout.setAvatarShapeType(1);
        //设置文本字体大小
        //messageListLayout.setItemTextSize((int) EaseCommonUtils.sp2px(mContext, 18));
        //设置文本字体颜色
        //messageListLayout.setItemTextColor(ContextCompat.getColor(mContext, R.color.red));
        //设置时间线的背景
        //messageListLayout.setTimeBackground(ContextCompat.getDrawable(mContext, R.color.gray_normal));
        //设置时间线的文本大小
        //messageListLayout.setTimeTextSize((int) EaseCommonUtils.sp2px(mContext, 18));
        //设置时间线的文本颜色
        //messageListLayout.setTimeTextColor(ContextCompat.getColor(mContext, R.color.black));
        //设置聊天列表样式：两侧及均位于左侧
        //messageListLayout.setItemShowType(EaseChatMessageListLayout.ShowType.LEFT);

        //获取到菜单输入父控件
        //EaseChatInputMenu chatInputMenu = chatLayout.getChatInputMenu();
        //获取到菜单输入控件
        //IChatPrimaryMenu primaryMenu = chatInputMenu.getPrimaryMenu();
        //if(primaryMenu != null) {
        //设置菜单样式为不可用语音模式
        //    primaryMenu.setMenuShowType(EaseInputMenuStyle.ONLY_TEXT);
        //}
    }

    private fun addItemMenuAction() {
//        val itemMenu =
//            MenuItemBean(0, R.id.action_chat_forward, 11, getString(R.string.action_forward))
//        itemMenu.resourceId = R.drawable.ease_chat_item_menu_forward
//        chatLayout.addItemMenu(itemMenu)
    }

    private fun resetChatExtendMenu() {
        val chatExtendMenu = chatLayout.chatInputMenu.chatExtendMenu
        chatExtendMenu.clear()
        chatExtendMenu.registerMenuItem(
            R.string.attach_picture,
            R.drawable.ease_chat_image_selector,
            R.id.extend_item_picture
        )
        chatExtendMenu.registerMenuItem(
            R.string.attach_take_pic,
            R.drawable.ease_chat_takepic_selector,
            R.id.extend_item_take_picture
        )
        chatExtendMenu.registerMenuItem(
            R.string.attach_video,
            R.drawable.em_chat_video_selector,
            R.id.extend_item_video
        )


        chatExtendMenu.registerMenuItem(
            R.string.attach_location,
            R.drawable.ease_chat_location_selector,
            R.id.extend_item_location
        )
        chatExtendMenu.registerMenuItem(
            R.string.attach_file,
            R.drawable.em_chat_file_selector,
            R.id.extend_item_file
        )

        //群组类型，开启消息回执，且是owner
        if (chatType == EaseConstant.CHATTYPE_GROUP && EMClient.getInstance().options.requireAck) {
            val group: EMGroup? = ChatHelper.instance?.groupManager?.getGroup(conversationId)
//            if (GroupHelper.isOwner(group)) {
//                chatExtendMenu.registerMenuItem(
//                    R.string.em_chat_group_delivery_ack,
//                    R.drawable.demo_chat_delivery_selector,
//                    R.id.extend_item_delivery
//                )
//            }
        }
        //添加扩展表情
        chatLayout.chatInputMenu.emojiconMenu.addEmojiconGroup(EmojiconExampleGroupData.data)
    }

    override fun initListener() {
        super.initListener()
        chatLayout.setOnRecallMessageResultListener(this)
    }

    override fun initData() {
        super.initData()
        resetChatExtendMenu()
        addItemMenuAction()
        chatLayout.chatInputMenu.primaryMenu.editText.setText(unSendMsg)
        ChatHelper.instance?.model?.isShowMsgTyping()?.let { chatLayout.turnOnTypingMonitor(it) }
        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE)
            .postValue(EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE))
        LiveDataBus.get().with(DemoConstant.MESSAGE_CALL_SAVE, Boolean::class.java).observe(
            viewLifecycleOwner
        ) { event ->
            if (event == null) {
                return@observe
            }
            if (event) {
                chatLayout.chatMessageListLayout.refreshToLatest()
            }
        }
        LiveDataBus.get().with(DemoConstant.CONVERSATION_DELETE, EaseEvent::class.java).observe(
            viewLifecycleOwner
        ) { event ->
            if (event == null) {
                return@observe
            }
            if (event.isMessageChange()) {
                chatLayout.chatMessageListLayout.refreshMessages()
            }
        }
        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent::class.java).observe(
            viewLifecycleOwner
        ) { event ->
            if (event == null) {
                return@observe
            }
            if (event.isMessageChange()) {
                chatLayout.chatMessageListLayout.refreshToLatest()
            }
        }
        LiveDataBus.get().with(DemoConstant.CONVERSATION_READ, EaseEvent::class.java).observe(
            viewLifecycleOwner
        ) { event ->
            if (event == null) {
                return@observe
            }
            if (event.isMessageChange()) {
                chatLayout.chatMessageListLayout.refreshMessages()
            }
        }

        //更新用户属性刷新列表
        LiveDataBus.get().with(DemoConstant.CONTACT_ADD, EaseEvent::class.java).observe(
            viewLifecycleOwner
        ) { event ->
            if (event == null) {
                return@observe
            }
            if (event != null) {
                chatLayout.chatMessageListLayout.refreshMessages()
            }
        }
        LiveDataBus.get().with(DemoConstant.CONTACT_UPDATE, EaseEvent::class.java).observe(
            viewLifecycleOwner
        ) { event ->
            if (event == null) {
                return@observe
            }
            if (event != null) {
                chatLayout.chatMessageListLayout.refreshMessages()
            }
        }
    }


    private fun showDeliveryDialog() {
//        Builder(mContext as BaseActivity)
//            .setTitle(R.string.em_chat_group_read_ack)
//            .setOnConfirmClickListener(
//                R.string.em_chat_group_read_ack_send,
//                object : OnSaveClickListener() {
//                    fun onSaveClick(view: View?, content: String?) {
//                        chatLayout.sendTextMessage(content, true)
//                    }
//                })
//            .setConfirmColor(R.color.em_color_brand)
//            .setHint(R.string.em_chat_group_read_ack_hint)
//            .show()
    }

    private fun showSelectDialog() {
//        Builder(mContext as BaseActivity) //.setTitle(R.string.em_single_call_type)
//            .setData(calls)
//            .setCancelColorRes(R.color.black)
//            .setWindowAnimations(R.style.animate_dialog)
//            .setOnItemClickListener(object : OnDialogItemClickListener() {
//                fun OnItemClick(view: View?, position: Int) {
//                    when (position) {
//                        0 -> EaseCallKit.getInstance().startSingleCall(
//                            EaseCallType.SINGLE_VIDEO_CALL, conversationId, null,
//                            VideoCallActivity::class.java
//                        )
//                        1 -> EaseCallKit.getInstance().startSingleCall(
//                            EaseCallType.SINGLE_VOICE_CALL, conversationId, null,
//                            VideoCallActivity::class.java
//                        )
//                    }
//                }
//            })
//            .show()
    }

    override fun onUserAvatarClick(username: String) {
        if (!TextUtils.equals(username, ChatHelper.instance?.currentUser)) {
            var user: EaseUser? = ChatHelper.instance?.getUserInfo(username)
            if (user == null) {
                user = EaseUser(username)
            }
            val isFriend: Boolean? = ChatHelper.instance?.model?.isContact(username)
            if (isFriend!!) {
                user.contact = 0
            } else {
                user.contact = 3
            }
        } else {
        }
    }

    override fun onUserAvatarLongClick(username: String) {}
    override fun onBubbleLongClick(v: View, message: EMMessage): Boolean {
        return false
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (!chatLayout.chatMessageListLayout.isGroupChat) {
            return
        }
        if (count == 1 && "@" == s[start].toString()) {
//            PickAtUserActivity.actionStartForResult(
//                this@ChatFragment,
//                conversationId,
//                REQUEST_CODE_SELECT_AT_USER
//            )
        }
    }

    override fun selectVideoFromLocal() {
        super.selectVideoFromLocal()
//        val intent = Intent(
//            activity,
//            ImageGridActivity::class.java
//        )
//        startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO)
    }

    override fun onBubbleClick(message: EMMessage): Boolean {
        return false
    }

    override fun onChatExtendMenuItemClick(view: View, itemId: Int) {
        super.onChatExtendMenuItemClick(view, itemId)
        when (itemId) {
//            R.id.extend_item_video_call -> showSelectDialog()
//            R.id.extend_item_conference_call -> {
//                val intent = Intent(
//                    context,
//                    ConferenceInviteActivity::class.java
//                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                intent.putExtra(DemoConstant.EXTRA_CONFERENCE_GROUP_ID, conversationId)
//                context!!.startActivity(intent)
//            }
//            R.id.extend_item_delivery -> showDeliveryDialog()
//            R.id.extend_item_user_card -> {
//                EMLog.d(TAG, "select user card")
//                val userCardIntent = Intent(
//                    this.context,
//                    SelectUserCardActivity::class.java
//                )
//                userCardIntent.putExtra("toUser", conversationId)
//                startActivityForResult(userCardIntent, REQUEST_CODE_SELECT_USER_CARD)
//            }
        }
    }

    override fun onChatError(code: Int, errorMsg: String) {
        if (infoListener != null) {
            infoListener!!.onChatError(code, errorMsg)
        }
    }

    override fun onOtherTyping(action: String) {
        if (infoListener != null) {
            infoListener!!.onOtherTyping(action)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_SELECT_AT_USER -> if (data != null) {
                    val username = data.getStringExtra("username")
                    chatLayout.inputAtUsername(username, false)
                }
                REQUEST_CODE_SELECT_VIDEO -> if (data != null) {
                    val duration = data.getIntExtra("dur", 0)
                    val videoPath = data.getStringExtra("path")
                    val uriString = data.getStringExtra("uri")
                    EMLog.d(TAG, "path = $videoPath uriString = $uriString")
                    if (!TextUtils.isEmpty(videoPath)) {
                        chatLayout.sendVideoMessage(Uri.parse(videoPath), duration)
                    } else {
                        val videoUri = EMFileHelper.getInstance().formatInUri(uriString)
                        chatLayout.sendVideoMessage(videoUri, duration)
                    }
                }
                REQUEST_CODE_SELECT_USER_CARD -> if (data != null) {
                    val user = data.getSerializableExtra("user") as EaseUser?
                    user?.let { sendUserCardMessage(it) }
                }
            }
        }
    }

    /**
     * Send user card message
     * @param user
     */
    private fun sendUserCardMessage(user: EaseUser) {
        val message = EMMessage.createSendMessage(EMMessage.Type.CUSTOM)
        val body = EMCustomMessageBody(DemoConstant.USER_CARD_EVENT)
        val params: MutableMap<String, String> = HashMap()
        params[DemoConstant.USER_CARD_ID] = user.username
        params[DemoConstant.USER_CARD_NICK] = user.nickname
        params[DemoConstant.USER_CARD_AVATAR] = user.avatar
        body.params = params
        message.body = body
        message.to = conversationId
        chatLayout.sendMessage(message)
    }

    override fun onStop() {
        super.onStop()
        //保存未发送的文本消息内容
        if (mContext != null && mContext.isFinishing) {
            if (chatLayout.chatInputMenu != null) {
                saveUnSendMsg(chatLayout.inputContent)
                LiveDataBus.get().with(DemoConstant.MESSAGE_NOT_SEND).postValue(true)
            }
        }
    }
    //================================== for video and voice start ====================================
    /**
     * 保存未发送的文本消息内容
     * @param content
     */
    private fun saveUnSendMsg(content: String) {
        ChatHelper.instance?.model?.saveUnSendMsg(conversationId, content)
    }

    private val unSendMsg: String?
        private get() = ChatHelper.instance?.model?.getUnSendMsg(conversationId)

    override fun onPreMenu(helper: EasePopupWindowHelper, message: EMMessage) {
        //默认两分钟后，即不可撤回
        if (System.currentTimeMillis() - message.msgTime > 2 * 60 * 1000) {
            helper.findItemVisible(R.id.action_chat_recall, false)
        }
        val type = message.type
//        helper.findItemVisible(R.id.action_chat_forward, false)
//        when (type) {
//            EMMessage.Type.TXT -> if (!message.getBooleanAttribute(
//                    DemoConstant.MESSAGE_ATTR_IS_VIDEO_CALL,
//                    false
//                )
//                && !message.getBooleanAttribute(DemoConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)
//            ) {
//                helper.findItemVisible(R.id.action_chat_forward, true)
//            }
//            EMMessage.Type.IMAGE -> helper.findItemVisible(R.id.action_chat_forward, true)
//        }
//        if (chatType == DemoConstant.CHATTYPE_CHATROOM) {
//            helper.findItemVisible(R.id.action_chat_forward, true)
//        }
    }

    override fun onMenuItemClick(item: MenuItemBean, message: EMMessage): Boolean {
        when (item.itemId) {
//            R.id.action_chat_forward -> {
//                ForwardMessageActivity.actionStart(mContext, message.msgId)
//                return true
//            }
            R.id.action_chat_delete -> {
                showDeleteDialog(message)
                return true
            }
            R.id.action_chat_recall -> {
                showProgressBar()
                chatLayout.recallMessage(message)
                return true
            }
        }
        return false
    }

    private fun showProgressBar() {
//        val view = View.inflate(mContext, R.layout.demo_layout_progress_recall, null)
//        dialog = Dialog(mContext, R.style.dialog_recall)
//        val layoutParams = ViewGroup.LayoutParams(
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//        dialog!!.setContentView(view, layoutParams)
//        dialog!!.setCancelable(false)
//        dialog!!.setCanceledOnTouchOutside(true)
//        dialog!!.show()
    }

    private fun showDeleteDialog(message: EMMessage) {
//        Builder(mContext as BaseActivity)
//            .setTitle(getString(R.string.em_chat_delete_title))
//            .setConfirmColor(R.color.red)
//            .setOnConfirmClickListener(
//                getString(R.string.delete),
//                object : OnConfirmClickListener() {
//                    fun onConfirmClick(view: View?) {
//                        chatLayout.deleteMessage(message)
//                    }
//                })
//            .showCancelButton(true)
//            .show()
    }

    fun setOnFragmentInfoListener(listener: OnFragmentInfoListener?) {
        infoListener = listener
    }

    override fun recallSuccess(message: EMMessage) {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    override fun recallFail(code: Int, errorMsg: String) {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    interface OnFragmentInfoListener {
        fun onChatError(code: Int, errorMsg: String?)
        fun onOtherTyping(action: String?)
    }

    companion object {
        private val TAG = ChatFragment::class.java.simpleName
        private const val REQUEST_CODE_SELECT_USER_CARD = 20
        private const val REQUEST_CODE_SELECT_AT_USER = 15
        private val calls = arrayOf("视频通话", "语音通话")
    }
}
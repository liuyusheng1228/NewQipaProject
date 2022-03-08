package com.qipa.qipaimbase.chat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.*
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.widget.*
import androidx.annotation.Nullable
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qipa.qipaimbase.ImBaseBridge
import com.qipa.qipaimbase.R
import com.qipa.qipaimbase.chat.adapter.ChatAdapter
import com.qipa.qipaimbase.chat.adapter.ChatNormalLeftItem
import com.qipa.qipaimbase.chat.emoji.EmojiContainerFragment
import com.qipa.qipaimbase.chat.ichat.IChatView
import com.qipa.qipaimbase.image.ImageCheckActivity
import com.qipa.qipaimbase.session.PhotonIMMessage
import com.qipa.qipaimbase.utils.*
import com.qipa.qipaimbase.utils.event.AlertEvent
import com.qipa.qipaimbase.utils.event.ChatDataWrapper
import com.qipa.qipaimbase.utils.event.ClearUnReadStatus
import com.qipa.qipaimbase.utils.event.IMStatus
import com.qipa.qipaimbase.utils.looperexecute.CustomRunnable
import com.qipa.qipaimbase.utils.looperexecute.MainLooperExecuteUtil
import com.qipa.qipaimbase.utils.mvpbase.IModel
import com.qipa.qipaimbase.utils.mvpbase.IPresenter
import com.qipa.qipaimbase.utils.mvpbase.IView
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.RvBaseAdapter
import com.qipa.qipaimbase.utils.recycleadapter.RvListenerImpl
import com.qipa.qipaimbase.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

abstract class ChatBaseActivity : IChatView() {
    private val REQUEST_IMAGE_CODE = 1001

    private var titleBar: TitleBar? = null

    private var recyclerViews: RecyclerView? = null

    private var ivVoice: ImageView? = null

    var etInput: AtEditText? = null

    private var ivEmoji: ImageView? = null

    private var ivExtra: ImageView? = null

    private var tvVoice: VoiceTextView? = null

    private var tvSendMsg: TextView? = null

    private var llExtra: LinearLayout? = null

    private var llPic: LinearLayout? = null

    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    private var vsEmoji: ViewStub? = null

    private var emojiRoot: FrameLayout? = null
    private var chatPopupWindow: ChatPopupWindow? = null
    private var chatAdapter: ChatAdapter? = null
    private var chatMsg: ArrayList<ChatData>? = null
    private var chatMsgMap //key:msgId
            : HashMap<String?, ChatData?>? = null
    private var chatType = 0
    protected var chatWith: String? = null
    private var firstLoad = true
    protected var myIcon: String? = null
    protected var singleChatUserIcon: String? = null
    private var name: String? = null
    private var voiceFile: File? = null
    private val lastPosition = 0
    private var igoreAlert = false
    private var mImageUri: Uri? = null
    private var imageFile: File? = null
    private var fragment: EmojiContainerFragment? = null
    private var lastLoadHistoryFromRemote = false

    /*
     * for test
     * */
    private var llTestRoot: LinearLayout? = null
    private var etInterval: EditText? = null
    private var etSendNum: EditText? = null
    private var tvFirstTime: TextView? = null
    private var tvLastTime: TextView? = null
    private var tvSuccessFail: TextView? = null
    private var tvStart: TextView? = null
    private var etCustomContent: EditText? = null
    private var tvAuthSuccess: TextView? = null
    private var tvAuthFailed: TextView? = null
    private var loginUserId: String? = null
    private var llTakePic : LinearLayout? = null
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        recyclerViews = findViewById(R.id.recyclerViews)
        titleBar = findViewById(R.id.titleBar)
        ivVoice = findViewById(R.id.ivVoice)
        etInput = findViewById(R.id.etInput)
        ivEmoji = findViewById(R.id.ivEmoji)
        ivExtra = findViewById(R.id.ivExtra)
        tvVoice = findViewById(R.id.tvVoice)
        tvSendMsg = findViewById(R.id.tvSendMsg)
        llExtra = findViewById(R.id.llExtra)
        vsEmoji = findViewById(R.id.vsEmoji)
        llPic = findViewById(R.id.llPic)
        llTakePic = findViewById(R.id.llTakePic)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        chatType = getIntent().getIntExtra(EXTRA_CHATTYPE, 0)
        chatWith = getIntent().getStringExtra(EXTRA_CHATWITH)
        myIcon = ImBaseBridge.instance?.myIcon
        singleChatUserIcon = getIntent().getStringExtra(EXTRA_OTHERICON)
        name = getIntent().getStringExtra(EXTRA_NAME)
        igoreAlert = getIntent().getBooleanExtra(EXTRA_IGOREALERT, false)
        val businessListener: ImBaseBridge.BusinessListener? = ImBaseBridge.instance?.businessListener
        if (businessListener != null) {
            loginUserId = businessListener.userId
        }
        getHistory(false, 0L, "")
        initView()
        initData()
    }

    fun initData(){
        ivVoice?.setOnClickListener {
            onVoiceClick()
        }
        ivEmoji?.setOnClickListener {
            onEmojiClick()
        }
        etInput?.setOnClickListener {
            onInputClick()
        }
        ivExtra?.setOnClickListener {
            onExtraClick()
        }
        llPic?.setOnClickListener {
            onPicClick()
        }
        llTakePic?.setOnClickListener {
            onTakePic()
        }
        tvSendMsg?.setOnClickListener {
            onSendMsgClick()
        }
    }

    protected override fun onResume() {
        super.onResume()
    }

    //    @Override
    //    protected void onPause() {
    //        // TODO: 2019-08-13 记录位置
    ////        lastPosition = recyclerView.get
    //        EventBus.getDefault().post(new ClearUnReadStatus(chatType, chatWith));
    //        super.onPause();
    //    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (!hasFocus) {
            EventBus.getDefault().post(chatWith?.let { ClearUnReadStatus(chatType, it) })
        }
        super.onWindowFocusChanged(hasFocus)
    }

    private fun getHistory(loadFromRemote: Boolean, endTimeStamp: Long, anchorMsgId: String) {
        if (loadFromRemote) {
            lastLoadHistoryFromRemote = true
            //            chatPresenter.loadAllHistory(chatType, chatWith, PAGE_ONE, beginTimeStamp);
            chatPresenter?.loadAllHistory(chatType, chatWith, PAGE_ONE, 0, endTimeStamp)
        } else {
            lastLoadHistoryFromRemote = false
            chatPresenter?.loadLocalHistory(
                chatType, chatWith, anchorMsgId, true, false,
                PAGE_ONE, loginUserId
            )
        }
    }

    private fun initView() {
        titleBar?.setTitle(if (TextUtils.isEmpty(name)) "" else name, object : View.OnClickListener {
            private val count = 2
            private var num = 0
            private var lastTime: Long = 0
            override fun onClick(v: View) {
                if (System.currentTimeMillis() - lastTime < 500) {
                    num++
                } else {
                    num = 0
                }
                lastTime = System.currentTimeMillis()
                if (num >= count) {
                    num = 0
                    showTestView()
                }
            }
        })
        titleBar?.setLeftImageEvent(R.drawable.arrow_left) { v -> this@ChatBaseActivity.finish() }
        titleBar?.setRightImageEvent(R.drawable.chat_more) { v -> onInfoClick() }
        etInput?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (etInput?.getText().toString().trim().length > 0) {
                    tvSendMsg?.visibility = View.VISIBLE
                    ivExtra?.visibility = View.GONE
                    llExtra?.visibility = View.GONE
                } else {
                    tvSendMsg?.visibility = View.GONE
                    ivExtra?.visibility = View.VISIBLE
                }
            }
        })
        etInput?.setFilters(arrayOf<InputFilter>(EditFilter()))
        etInput?.setOnFocusChangeListener { v, hasFocus ->
            llExtra?.visibility = View.GONE
            if (emojiRoot != null) {
                emojiRoot?.visibility = View.GONE
            }
        }
        etInput?.setOnAtInputListener(object : AtEditText.OnAtInputListener {
            override fun onAtCharacterInput() {
                this@ChatBaseActivity.onAtCharacterInput()
            }
        })
        swipeRefreshLayout?.setOnRefreshListener {
            if (chatMsg!!.size > 0) {
                val chatData = chatMsg!![0]
                if (!isGroup && chatMsg!!.size >= LIMIT_LOADREMOTE) { //单人消息从本地拉取LIMIT_LOADREMOTE条之后，从服务器拉取
                    chatData?.msgId?.let { getHistory(true, chatData.time, it) }
                } else {
                    chatData?.msgId?.let {
                        getHistory(chatData?.isRemainHistory, chatData.time,
                            it
                        )
                    }
                }
            } else {
                getHistory(false, 0L, "")
            }
        }
        tvVoice?.setTimeOut(VOICE_MAX_LENGTH)
        tvVoice?.setOnEventUpListener(object : VoiceTextView.OnEventUpListener {
            override fun canHandle(): Boolean {
                return CheckAudioPermission.isHasPermission(this@ChatBaseActivity)
            }

            override fun onEventDown() {
                Log.i("Api","onEventDown ")
                ChatToastUtils.showChatVoice()
                voiceFile = chatPresenter?.startRecord(this@ChatBaseActivity)
                setControlEnable(false)
            }

            override fun onEventCancel() {
                Log.i("Api","cancle ")
                chatPresenter?.cancelRecord()
                setControlEnable(true)
            }

            override fun onEventUp() {
                Log.i("Api","onEventUp ")
                chatPresenter?.stopRecord()
                setControlEnable(true)
            }

            override fun onTimeout() {
                ToastUtils.showText(this@ChatBaseActivity, "超时自动发送")
                chatPresenter?.stopRecord()
                setControlEnable(true)
            }
        })
        (recyclerViews as TouchRecycleView).setOnRecycleViewClickListener(object :
            TouchRecycleView.OnRecycleViewClickListener{
            override fun onRecycleViewClick() {
                etInput?.let {
                    Utils.keyBoard(
                        this@ChatBaseActivity,
                        it,
                        false
                    )
                }
            }

        })

    }

    private fun showTestView() {
        if (llTestRoot == null) {
            val vsTest: ViewStub = findViewById(R.id.vsTest)
            vsTest.inflate()
            llTestRoot = findViewById(R.id.llTestRoot)
            etInterval = findViewById(R.id.etInterval)
            etSendNum = findViewById(R.id.etSendNum)
            tvFirstTime = findViewById(R.id.tvFirstTime)
            tvAuthSuccess = findViewById(R.id.tvAuthSuccess)
            tvAuthFailed = findViewById(R.id.tvAuthFailed)
            tvLastTime = findViewById(R.id.tvLastTime)
            tvSuccessFail = findViewById(R.id.tvSuccessFail)
            tvStart = findViewById(R.id.tvStart)
            etCustomContent = findViewById(R.id.etCustomContent)
            tvStart?.setOnClickListener {
                if (beginTest) {
                    stopTest()
                } else {
                    tvStart?.text = resources.getString(R.string.stop)
                    testSendStart()
                }
            }
        } else {
            if (llTestRoot?.visibility == View.VISIBLE) {
                llTestRoot?.visibility = View.GONE
            } else {
                llTestRoot?.visibility = View.VISIBLE
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // for test start
    ///////////////////////////////////////////////////////////////////////////
    private var sendNum = 0
    private var customRunnable: CustomRunnable? = null
    private var lastChatData: ChatData? = null
    private var currentContent = 0
    private var successNum = 0
    private var failedNum = 0
    private var beginTest = false
    private var authSuccess = 0
    private var authFailed = 0
    private fun testSendStart() {
        tvFirstTime?.text = ""
        tvLastTime?.text = ""
        tvAuthSuccess?.text = "0"
        tvAuthFailed?.text = "0"
        lastChatData = null
        currentContent = 0
        successNum = 0
        failedNum = 0
        authFailed = 0
        authSuccess = 0
        val interval: Int
        try {
            interval = Integer.valueOf(etInterval!!.text.toString())
            sendNum = Integer.valueOf(etSendNum!!.text.toString())
        } catch (e: NumberFormatException) {
            ToastUtils.showText("输入有误，请检查")
            return
        }
        beginTest = true
        customRunnable = CustomRunnable()
        customRunnable?.setRunnable(runnable)
        customRunnable?.setDelayTime(interval)
        customRunnable?.setRepeated(true)
        MainLooperExecuteUtil.instance?.post(customRunnable)
    }

    private val runnable = Runnable {
        if (currentContent >= sendNum) {
            stopTest()
            return@Runnable
        }
        val chatDataBuilder: ChatData.Builder = ChatData.Builder()
            .content(etCustomContent!!.text.toString() + ++currentContent)
            .icon(myIcon)
            .msgType(PhotonIMMessage.TEXT)
            .chatWith(chatWith)
            .chatType(chatType)
            .from(loginUserId)
            .to(chatWith)
        if (currentContent == 1) {
            tvFirstTime!!.text = System.currentTimeMillis().toString() + ""
        }
        val chatData: ChatData? = chatPresenter?.sendMsg(chatDataBuilder)
        if (currentContent == sendNum) {
            lastChatData = chatData
        }
    }

    private fun stopTest() {
        customRunnable?.setRepeated(false)
        beginTest = false
        tvStart!!.text = resources.getString(R.string.start)
        sendNum = currentContent
    }

    private val time: String
        private get() = SimpleDateFormat(TIME_FORMAT_HOUR, Locale.CHINA).format(Date())

    ///////////////////////////////////////////////////////////////////////////
    // for test end
    ///////////////////////////////////////////////////////////////////////////
    private fun setControlEnable(enable: Boolean) {
        ivVoice?.isEnabled = enable
        ivEmoji?.isEnabled = enable
        ivExtra?.isEnabled = enable
        tvSendMsg?.isEnabled = enable
    }

    private class EditFilter : InputFilter {
        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            return null
        }
    }

    protected abstract fun onInfoClick()
    protected abstract fun getChatIcon(msg: PhotonIMMessage?): String?
    protected open fun onAtCharacterInput() {}
    protected abstract val isGroup: Boolean

    override fun onRecordFinish(duration: Long) {
        if (duration < 1000) {
            ChatToastUtils.showChatTimeWarn()
            chatPresenter?.cancelRecord()
            return
        }
        val chatDataBuilder: ChatData.Builder = ChatData.Builder()
            .msgStatus(PhotonIMMessage.SENDING)
            .icon(myIcon)
            .localFile(voiceFile?.absolutePath)
            .msgType(PhotonIMMessage.AUDIO)
            .chatType(chatType)
            .voiceDuration(duration / 1000)
            .chatWith(chatWith)
            .from(loginUserId)
            .to(chatWith)
        chatPresenter?.sendMsg(chatDataBuilder)
    }

    override fun onRecordFailed() {
        ToastUtils.showText(this, "录制失败，请重试")
        chatPresenter?.stopRecord()
    }

    override fun updateUnreadStatus(data: ChatData?) {
        data?.msgStatus = (PhotonIMMessage.RECV_READ)
        data?.listPostion?.let { chatAdapter?.notifyItemChanged(it) }
    }

    override fun onGetIcon(chatData: ChatData?, url: String?, name: String?) {
        chatData?.icon = (url)
        chatData?.fromName = (name)
        chatData?.listPostion?.let { chatAdapter?.notifyItemChanged(it) }
    }

    override fun onloadHistoryResult(
        chatData: List<ChatData>?,
        chatDataMap: Map<String?, ChatData>?
    ) {
        if (lastLoadHistoryFromRemote) { //服务器没有历史消息了需要加载本地
            if (chatMsg!!.size > 0) {
                val chatDataTemp = chatMsg!![0]
                chatDataTemp.msgId?.let { getHistory(false, 0L, it) }
            } else {
                getHistory(false, 0L, "")
            }
            return
        }
        if (swipeRefreshLayout?.isRefreshing()!!) {
            swipeRefreshLayout?.setRefreshing(false)
        }
        if (CollectionUtils.isEmpty(chatData)) {
//            chatPresenter.loadRemoteHistory();
            ToastUtils.showText(
                this,
                getResources().getString(R.string.chat_msg_nomore)
            )
        } else {
            if (chatData != null) {
                chatMsg?.addAll(0, chatData)
            }
            chatMsgMap = chatDataMap as HashMap<String?, ChatData?>?
            chatData?.size?.let { chatAdapter?.notifyItemRangeInserted(0, it) }
            if (firstLoad) {
                firstLoad = false
                if (chatData != null) {
                    recyclerViews?.scrollToPosition(chatData?.size - 1)
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAuthSuccess(imStatus: IMStatus) {
        when (imStatus.status) {
            //待修改
//            PhotonIMClient.IM_STATE_AUTH_SUCCESS -> if (sendNum != 0 && successNum + failedNum != sendNum) {
//                authSuccess++
//                tvAuthSuccess!!.text = authSuccess.toString() + ""
//            }
//            PhotonIMClient.IM_STATE_AUTH_FAILED -> if (sendNum != 0 && successNum + failedNum != sendNum) {
//                authFailed++
//                tvAuthFailed!!.text = authFailed.toString() + ""
//            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSendChatData(chatDataWrapper: ChatDataWrapper) {
        ///////////////////////////////////////////////////////////////////////////
        // for test start
        ///////////////////////////////////////////////////////////////////////////
        //        if (beginTest) {
        if (tvSuccessFail != null) {
            when (chatDataWrapper.code) {
                ChatModel.MSG_ERROR_CODE_SUCCESS, ChatModel.MSG_ERROR_CODE_SERVER_ERROR, ChatModel.MSG_ERROR_CODE_TEXT_ILLEGAL, ChatModel.MSG_ERROR_CODE_FREQUENCY -> successNum++
                ChatModel.MSG_ERROR_CODE_TIME_OUT -> failedNum++
            }
            tvSuccessFail?.text = String.format("%d/%d", successNum, failedNum)
            if (successNum + failedNum == sendNum) {
                tvLastTime?.text = System.currentTimeMillis().toString() + ""
            }
        }
        //        }
        if (!chatDataWrapper.chatData.chatWith.equals(chatWith)) {
            return
        }
        when (chatDataWrapper.code) {
            PhotonIMMessage.SENDING -> addNewMsg(chatDataWrapper.chatData)
            ChatModel.MSG_ERROR_CODE_CANT_REVOKE -> ToastUtils.showText(
                this,
                getResources().getString(R.string.chat_revoke_failed)
            )
            else -> chatDataWrapper.msg?.let {
                changeDataStatus(
                    chatDataWrapper.chatData,
                    chatDataWrapper.code,
                    it
                )
            }
        }
    }

    override fun onRevertResult(data: ChatData?, error: Int, msg: String?) {
        if (error == ChatModel.MSG_ERROR_CODE_SUCCESS) {
            data?.msgStatus = (PhotonIMMessage.RECALL)
            data?.setContent(msg)
            data?.listPostion?.let { chatAdapter?.notifyItemChanged(it) }
            return
        }
        ToastUtils.showText(this, String.format("操作失败：%s", msg))
    }

   override fun onGetChatVoiceFileResult(data: ChatData?, path: String?) {
        data?.localFile = (path)
       data?.listPostion?.let { chatAdapter?.notifyItemChanged(it) }
    }

    private fun addNewMsg(chatData: ChatData) {
        chatData.timeContent = (getTimeContent(chatData.time))
        chatMsg?.add(chatData)
        //        chatData.setListPostion(chatMsg.size() - 1);
        chatMsgMap!![chatData.msgId] = chatData
        chatAdapter?.notifyItemInserted(chatMsg!!.size)
        val mLayoutManager: LinearLayoutManager =
            recyclerViews?.getLayoutManager() as LinearLayoutManager
        mLayoutManager.scrollToPosition(chatMsg!!.size - 1)
    }

    private fun changeDataStatus(chatData: ChatData, code: Int, msg: String) {
        val temp = chatMsgMap!![chatData.msgId]
        if (temp == null) {
            LogUtils.log(TAG, "chatData is null")
            return
        }
        when (code) {
            ChatModel.MSG_ERROR_CODE_SUCCESS -> temp.msgStatus = (PhotonIMMessage.SENT)
            ChatModel.MSG_ERROR_CODE_UPLOAD_PIC_FAILED, ChatModel.MSG_ERROR_CODE_SERVER_ERROR -> {
                temp.msgStatus = (PhotonIMMessage.SEND_FAILED)
                if (!TextUtils.isEmpty(msg)) {
                    temp.notic = (msg)
                }
                ChatToastUtils.showChatSendFailedWarn()
            }
            ChatModel.MSG_ERROR_CODE_TEXT_ILLEGAL, ChatModel.MSG_ERROR_CODE_PIC_ILLEGAL, ChatModel.MSG_ERROR_CODE_SERVER_NO_GROUP_MEMBER, ChatModel.MSG_ERROR_CODE_GROUP_CLOSED -> temp.notic = (
                msg
            )
            ChatModel.MSG_ERROR_CODE_FREQUENCY -> ToastUtils.showText(
                this,
                getResources().getString(R.string.chat_send_failed_frequency)
            )
            ChatModel.MSG_ERROR_CODE_TIME_OUT -> temp.msgStatus = (PhotonIMMessage.SEND_FAILED)
            else -> temp.notic = (msg)
        }
        //需要从chatMsgMap读取：有可能是退出聊天再次进入
        chatAdapter?.notifyItemChanged(temp.listPostion)
    }

    fun onVoiceClick() {
        if (tvVoice?.getVisibility() === View.VISIBLE) {
            tvVoice?.setVisibility(View.GONE)
            etInput?.setVisibility(View.VISIBLE)
        } else {
            tvVoice?.setVisibility(View.VISIBLE)
            tvVoice?.setText(resources.getString(R.string.hold_to_talk))
            etInput?.setVisibility(View.GONE)
        }
    }

    fun onEmojiClick() {
        etInput?.let { Utils.keyBoard(this, it, false) }
        llExtra?.visibility = View.GONE
        if (fragment == null) {
            vsEmoji?.inflate()
            emojiRoot = findViewById(R.id.emojiRoot)
            fragment =
                getSupportFragmentManager().findFragmentById(R.id.emojiContainerFragment) as EmojiContainerFragment
            fragment?.setOnDelListener(object : EmojiContainerFragment.OnDelListener {
                override fun onDelClick() {
                    val content: String = etInput?.getText().toString()
                    if (TextUtils.isEmpty(content.trim { it <= ' ' })) {
                        return
                    }
                    val contentLength: Int = etInput?.getText().toString().length
                    etInput?.setText(content.substring(0, contentLength - 1))
                }
            })

//            fragment.setOnSendListener(new EmojiContainerFragment.OnSendListener() {
//                @Override
//                public void onEmojiSend() {
//                    if (TextUtils.isEmpty(etInput.getText().toString().trim())) {
//                        return;
//                    }
//                    chatPresenter.sendText(etInput.getText().toString().trim(), chatWith, MyApplication.getApplication().getUserId(), chatWith, myIcon);
//                }
//            });
            fragment?.setOnEmojiClickListener(object : EmojiContainerFragment.OnEmojiClickListener {
                override fun onEmojiClick(content: String?) {
                    if (tvVoice?.getVisibility() === View.GONE) {
                        etInput?.append(content)
                    }
                }
            })
        } else {
            if (emojiRoot?.visibility == View.GONE) {
                emojiRoot?.visibility = View.VISIBLE
            } else {
                emojiRoot?.visibility = View.GONE
            }
        }
    }

    fun onInputClick() {
        if (emojiRoot != null) {
            emojiRoot?.visibility = View.GONE
        }
        llExtra?.visibility = View.GONE
    }

    fun onExtraClick() {
        etInput?.let { Utils.keyBoard(this, it, false) }
        if (llExtra?.visibility == View.VISIBLE) {
            dismissExtraLayout()
        } else {
            showExtraLayout()
        }
    }

    private fun dismissExtraLayout() {
        llExtra?.visibility = View.GONE
    }

    private fun showExtraLayout() {
        llExtra?.visibility = View.VISIBLE
        if (emojiRoot != null) {
            emojiRoot!!.visibility = View.GONE
        }
    }

    fun onPicClick() {
        val intent = Intent(Intent.ACTION_PICK, null)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED)
        startActivityForResult(intent, REQUEST_IMAGE_CODE)
    }

    fun onTakePic() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //打开相机的Intent
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) { //这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
            imageFile = FileUtils.createImageFile(this) //创建用来保存照片的文件
            if (imageFile != null) {
                mImageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    /*7.0以上要通过FileProvider将File转化为Uri*/
                    FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, imageFile!!)
                } else {
                    /*7.0以下则直接使用Uri的fromFile方法将File转化为Uri*/
                    Uri.fromFile(imageFile)
                }
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri) //将用于输出的文件Uri传递给相机
                startActivityForResult(takePhotoIntent, REQUEST_CAMERA) //打开相机
            }
        }
    }

    fun onSendMsgClick() {
        val chatDataBuilder: ChatData.Builder = ChatData.Builder()
            .content(etInput?.getText().toString())
            .icon(myIcon)
            .msgType(PhotonIMMessage.TEXT)
            .chatWith(chatWith)
            .chatType(chatType)
            .from(loginUserId)
            .to(chatWith)
        getAtStatus(chatDataBuilder)
        chatPresenter?.sendMsg(chatDataBuilder)
        etInput?.setText("")
        etInput?.clearAtStatus()
    }

    private fun getAtStatus(chatDataBuilder: ChatData.Builder) {
        val atList: ArrayList<AtEditText.Entity> = etInput?.getAtList()!!
        if (CollectionUtils.isEmpty(atList)) {
            return
        }
        val atMsgList: MutableList<String> = ArrayList(atList.size)
        for (entity in atList) {
            entity.id?.let { atMsgList.add(it) }
            if (entity.name.equals(AT_ALL_CONTENT)) {
                chatDataBuilder.atType(PhotonIMMessage.MSG_AT_ALL)
                return
            }
        }
        chatDataBuilder.atType(PhotonIMMessage.MSG_NO_AT_ALL).msgAtList(atMsgList)
    }

    override fun getIPresenter(): IPresenter<in IView, in IModel>?{
        return ChatPresenter(this) as IPresenter<in IView, in IModel>
    }


    override fun getRecycleView(): RecyclerView? {
        recyclerViews = findViewById(R.id.recyclerViews)
        return recyclerViews
    }

    override fun getAdapter(): RvBaseAdapter<ItemData> {
        if (chatAdapter == null) {
            chatMsg =  ArrayList()
            chatMsgMap = HashMap()
            chatAdapter = ChatAdapter(chatMsg, object : ChatNormalLeftItem.OnGetVoiceFileListener{
                override fun onGetVoice(data: ChatData?) {
                    chatPresenter?.getVoiceFile(data)
                }

            },object : ChatNormalLeftItem.OnReceiveReadListener {
                override fun onReceiveRead(data: ChatData?) {
                    chatPresenter?.sendReadMsg(data)
                }

            }, object : ChatNormalLeftItem.OnGetIconListener{
                override fun onGetUserInfo(chatData: ChatData?) {
                    chatPresenter?.getInfo(chatData)
                }

            }, isGroup)
            chatAdapter!!.setRvListener(object : RvListenerImpl() {
               override fun onClick(view: View?, data: ItemData, position: Int) {
                    val chatData = data as ChatData
                    val viewId = view?.id
                    if (viewId == R.id.llVoice) {
                        chatPresenter?.cancelPlay()
                        if (chatData.from.equals(loginUserId)) {
                            chatPresenter?.play(this@ChatBaseActivity, data.localFile)
                        } else {
                            if (TextUtils.isEmpty(data.localFile)) {
                                ToastUtils.showText(this@ChatBaseActivity, "请稍后")
                                return
                            }
                            chatPresenter?.play(this@ChatBaseActivity, data.localFile)
                        }
                    } else if (viewId == R.id.ivWarn) {
                        chatMsg?.removeAt(chatData.listPostion)
                        chatMsgMap?.remove(chatData.msgId)
                        chatAdapter?.notifyDataSetChanged()
                        //                        chatAdapter.notifyItemRemoved(chatData.getListPostion());
                        resendMsg(chatData)
                    } else if (viewId == R.id.ivPic) {
                        // TODO: 2019-08-19 图片的获取移到其他位置获取
                        var chatDataTemp: ChatData
                        var currentPosition = 0
                        val urls = java.util.ArrayList<String>()
                        for (i in chatMsg!!.indices) {
                            chatDataTemp = chatMsg!![i]
                            if (chatDataTemp.msgType !== PhotonIMMessage.IMAGE) {
                                continue
                            }
                            if (TextUtils.isEmpty(chatDataTemp.localFile)) chatDataTemp.fileUrl else chatDataTemp.localFile?.let {
                                urls.add(
                                    it
                                )
                            }
                            if (chatData.msgId.equals(chatDataTemp.msgId)) {
                                currentPosition = urls.size - 1
                            }
                        }
                        ImageCheckActivity.startActivity(
                            this@ChatBaseActivity,
                            urls,
                            currentPosition
                        )
                    }
                }

                override fun onLongClick(view: View?, data: ItemData, position: Int) {
                    val chatData = data as ChatData?
                    val i = view?.id
                    if (i == R.id.tvContent || i == R.id.llVoice || i == R.id.ivPic) {
                        showPopupMenu(chatData!!, view)
                    }
                }
            })
        }
        return this.chatAdapter!!
    }

    private fun resendMsg(chatData: ChatData) {
        chatPresenter?.removeChat(
            chatData.chatType,
            chatData.chatWith,
            chatData.msgId
        )
        chatPresenter?.reSendMsg(chatData)
    }

    private fun showPopupMenu(data: ChatData, view: View) {
        if (chatPopupWindow != null) {
            chatPopupWindow?.dismiss()
        }
        val showRevert = (data.from.equals(loginUserId)
                && System.currentTimeMillis() - data.time < 1000 * 60 * 2 && data.msgStatus !== PhotonIMMessage.SEND_FAILED)
        val showCopy = data.msgType === PhotonIMMessage.TEXT
        chatPopupWindow =
            ChatPopupWindow(showCopy, showRevert, this@ChatBaseActivity, object : ChatPopupWindow.OnMenuClick {
                override fun onCopyClick() {
                    // TODO: 2019-09-23 如果有at？
                    Utils.copyToClipBoard(this@ChatBaseActivity, data.getContent())
                }

                override fun onRelayClick() {
                    val businessListener: ImBaseBridge.BusinessListener? =
                        ImBaseBridge.instance?.businessListener
                    if (businessListener != null) {
                        businessListener.onRelayClick(this@ChatBaseActivity, data)
                    }
                }

                override fun onRevertClick() {
                    chatPresenter?.revertMsg(data)
                }
            })
        recyclerViews?.let {
            (recyclerViews as TouchRecycleView?)?.lastPoint?.let { it1 ->
                chatPopupWindow?.show(
                    it1,
                    it
                )
            }
        }
    }

    // TODO: 2019-08-18 移到presenter
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnReceiveMsg(msg: PhotonIMMessage) {
        if (!msg.chatWith.equals(chatWith)) {
            return
        }
        if (msg.status === PhotonIMMessage.RECV_READ && msg.msgId != null) {
            for (msgId in msg.msgId) {
                val chatData = chatMsgMap!![msgId]
                if (chatData == null) {
                    LogUtils.log(TAG, "update status :chatData is null")
                    continue
                }
                chatData.msgStatus = (PhotonIMMessage.SENT_READ)
                chatAdapter?.notifyItemChanged(chatData.listPostion)
            }
            return
        }
        // 删除demo层消息去重，防止和sdk2.0混淆
//        if (chatMsgMap.get(msg.id) != null) {
//            LogUtils.log(TAG, String.format("消息重复：%s", msg.id));
//            return;
//        }
        // 删除demo层消息去重，防止和sdk2.0混淆
//        if (chatMsgMap.get(msg.id) != null) {
//            LogUtils.log(TAG, String.format("消息重复：%s", msg.id));
//            return;
//        }
        if (msg.status === PhotonIMMessage.RECALL) {
            for (msgId in msg.msgId) {
                val chatData = chatMsgMap!![msgId]
                if (chatData == null) {
                    LogUtils.log(TAG, "update status :chatData is null")
                    continue
                }
                chatData.setContent(msg.notic)
                chatData.msgStatus = (PhotonIMMessage.RECALL)
                chatAdapter!!.notifyItemChanged(chatData.listPostion)
            }
            return
        }
        val messageData: ChatData
        messageData = ChatData.Builder()
            .chatWith(msg.chatWith)
            .content(msg.content)
            .localFile(msg.localFile)
            .to(msg.to)
            .from(msg.from)
            .time(msg.time)
            .itemType(if (msg.from.equals(loginUserId)) Constants.ITEM_TYPE_CHAT_NORMAL_RIGHT else Constants.ITEM_TYPE_CHAT_NORMAL_LEFT)
            .fileUrl(msg.fileUrl)
            .msgStatus(msg.status)
            .icon(getChatIcon(msg))
            .msgId(msg.id)
            .timeContent(getTimeContent(msg.time))
            .msgType(msg.messageType)
            .voiceDuration(msg.mediaTime)
            .chatType(msg.chatType)
            .build()
        chatMsg?.add(messageData)
        chatMsgMap!![msg.id] = messageData
        chatAdapter?.notifyItemInserted(chatMsg!!.size - 1)
        val mLayoutManager: LinearLayoutManager =
            recyclerViews?.getLayoutManager() as LinearLayoutManager
        mLayoutManager.scrollToPosition(chatMsg!!.size - 1)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAlertEvent(event: AlertEvent) {
        igoreAlert = event.igoreAlert
    }

    protected override fun onDestroy() {
        MainLooperExecuteUtil.instance?.cancelRunnable(customRunnable)
        EventBus.getDefault().post(chatWith?.let { ClearUnReadStatus(chatType, it) })
        EventBus.getDefault().unregister(this)
        chatPresenter?.cancelPlay()
        chatPresenter?.destoryVoiceHelper()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (llExtra?.visibility == View.VISIBLE) {
            dismissExtraLayout()
        } else {
            super.onBackPressed()
        }
    }

    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            val originalUri = data?.data // 获得图片的uri
            val filePath: String? = originalUri?.let { Utils.getFilePath(this, it) }
            if (File(filePath).length() >= IMAGE_MAX_SIZE) {
                ToastUtils.showText(this, "仅支持发送10M以内的图片")
                return
            }
            val chatDataBuild: ChatData.Builder = ChatData.Builder()
                .icon(myIcon)
                .localFile(filePath)
                .msgType(PhotonIMMessage.IMAGE)
                .chatType(chatType)
                .chatWith(chatWith)
                .from(loginUserId)
                .to(chatWith)
            chatPresenter?.sendMsg(chatDataBuild)
        } else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            if (File(imageFile!!.absolutePath).length() >= IMAGE_MAX_SIZE) {
                ToastUtils.showText(this, "仅支持发送10M以内的图片")
                return
            }
            val chatDataBuild: ChatData.Builder = ChatData.Builder()
                .icon(myIcon)
                .localFile(imageFile!!.absolutePath)
                .msgType(PhotonIMMessage.IMAGE)
                .chatType(chatType)
                .chatWith(chatWith)
                .from(loginUserId)
                .to(chatWith)
            chatPresenter?.sendMsg(chatDataBuild)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getTimeContent(curTime: Long): String? {
        return TimeUtils.getTimeContent(
            curTime,
            if (CollectionUtils.isEmpty(chatMsg)) 0 else chatMsg!![chatMsg!!.size - 1].time
        )
    }

    companion object {
        private const val LIMIT_LOADREMOTE = 200
        private const val IMAGE_UNSPECIFIED = "image/*"
        public const val AT_ALL_CONTENT = "所有人 "
        private const val REQUEST_CAMERA = 1000
        private const val SEND_COUNT_LIMIT = 480
        private const val VOICE_MAX_LENGTH = 3 * 60 * 1000
        private const val IMAGE_MAX_SIZE = 10 * 1024 * 1024
        private const val FILE_PROVIDER_AUTHORITY = "com.qipa.newboxproject.fileprovider"
        private const val TAG = "ChatActivityTAG"
        private const val EXTRA_CHATTYPE = "EXTRA_CHATTYPE"
        private const val EXTRA_CHATWITH = "EXTRA_CHATWITH"
        private const val EXTRA_MyICON = "EXTRA_MyICON"
        private const val EXTRA_IGOREALERT = "EXTRA_IGOREALERT"
        private const val EXTRA_NAME = "EXTRA_NAME"
        private const val EXTRA_OTHERICON = "EXTRA_OTHERICON"
        private const val PAGE_ONE = 50
        fun startActivity(
            from: Activity, chatType: Int, chatWith: String?, myIcon: String?,
            name: String?, otherIcon: String?, igoreAlert: Boolean
        ) {
            val intent: Intent
            intent = if (chatType == PhotonIMMessage.SINGLE) {
                Intent(from, ChatSingleActivity::class.java)
            } else {
                Intent(from, ChatGroupActivity::class.java)
            }
            intent.putExtra(EXTRA_CHATTYPE, chatType)
            intent.putExtra(EXTRA_CHATWITH, chatWith)
            intent.putExtra(EXTRA_MyICON, myIcon)
            intent.putExtra(EXTRA_NAME, name)
            intent.putExtra(EXTRA_OTHERICON, otherIcon)
            intent.putExtra(EXTRA_IGOREALERT, igoreAlert)
            from.startActivity(intent)
        }

        private const val TIME_FORMAT_HOUR = "mm:ss:SSS"
    }
}
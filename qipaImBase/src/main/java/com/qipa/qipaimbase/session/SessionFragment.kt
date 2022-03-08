package com.qipa.qipaimbase.session

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import com.qipa.qipaimbase.R
import com.qipa.qipaimbase.R2
import com.qipa.qipaimbase.chat.ChatBaseActivity
import com.qipa.qipaimbase.session.isession.ISessionView
import com.qipa.qipaimbase.utils.CollectionUtils
import com.qipa.qipaimbase.utils.LocalRestoreUtils
import com.qipa.qipaimbase.utils.LogUtils
import com.qipa.qipaimbase.utils.event.AllUnReadCount
import com.qipa.qipaimbase.utils.event.ClearUnReadStatus
import com.qipa.qipaimbase.utils.event.OnDBChanged
import com.qipa.qipaimbase.utils.http.jsons.JsonGroupProfile
import com.qipa.qipaimbase.utils.http.jsons.JsonOtherInfoMulti
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import com.qipa.qipaimbase.utils.mvpbase.IModel
import com.qipa.qipaimbase.utils.mvpbase.IPresenter
import com.qipa.qipaimbase.utils.mvpbase.IView
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.RvBaseAdapter
import com.qipa.qipaimbase.utils.recycleadapter.RvListenerImpl
import com.qipa.qipaimbase.view.ProcessDialogFragment
import com.qipa.qipaimbase.view.SessionDialogFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList

class SessionFragment : ISessionView(), SessionItem.UpdateOtherInfoListener {
    var recyclerView: RecyclerView? = null

    lateinit var llNoMsg: LinearLayout
    private var baseDataList: MutableList<SessionData?>? = null

    //    private Map<String, SessionData> baseDataMap;//key 为chatWith
    private var sessionAdapter: SessionAdapter? = null
    private val processDialogFragment: ProcessDialogFragment? = null
    private var sessionDialogFragment: SessionDialogFragment? = null
    private var isVisibleToUser = false
    private var isFirstLoad = false
    @Nullable
    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_main_message, null)
        isFirstLoad = LocalRestoreUtils.firstLoadSession!!
        llNoMsg = view.findViewById(R.id.llNoMsg)
        recyclerView = view.findViewById(R.id.recyclerView)
        iSessionPresenter?.loadHistoryData()
        iSessionPresenter?.allUnReadCount
        // 删除demo层的重发机制，防止和sdk内部重发逻辑混淆
//        iSessionPresenter.resendSendingStatusMsgs();
        return view
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        this.isVisibleToUser = isVisibleToUser
        //        if (isVisibleToUser && iSessionPresenter != null) {
//            iSessionPresenter.loadHistoryData(LoginInfo.getInstance().getSessenId(), LoginInfo.getInstance().getUserId());
//        }
        super.setUserVisibleHint(isVisibleToUser)
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    //    @Subscribe(threadMode = ThreadMode.MAIN)
    //    public void onReceiveMsg(PhotonIMMessage msg) {
    //        if (msg.status == PhotonIMMessage.SENT_READ) {
    //            return;
    //        }
    //        SessionData sessionData = getSessionData(msg.chatWith);
    //        String content;
    //        switch (msg.messageType) {
    //            case PhotonIMMessage.AUDIO:
    //                content = "[语音]";
    //                break;
    //            case PhotonIMMessage.IMAGE:
    //                content = "[图片]";
    //                break;
    //            case PhotonIMMessage.VIDEO:
    //                content = "[视频]";
    //                break;
    //            case PhotonIMMessage.TEXT:
    //                content = msg.content;
    //                break;
    //            default:
    //                content = "未知消息";
    //        }
    //        ChatToastUtils.showText(getContext(), content);
    //        if (sessionData == null) {
    //            if (llNoMsg.getVisibility() == View.VISIBLE) {
    //                llNoMsg.setVisibility(View.GONE);
    //                recyclerView.setVisibility(View.VISIBLE);
    //            }
    //            sessionData = new SessionData(msg);
    //            sessionData.setLastMsgContent(content);
    //            iSessionPresenter.saveSession(sessionData);
    //            baseDataList.add(sessionData);
    ////            baseDataMap.put(msg.chatWith, sessionData);
    //            sessionAdapter.notifyItemInserted(baseDataList.size() - 1);
    //        } else {
    //            sessionData.setUnreadCount(sessionData.getUnreadCount() + 1);
    //            sessionData.setLastMsgContent(content);
    //
    //            int itemPosition = getItemPosition(sessionData.getChatWith());
    //            if (itemPosition < 0) {
    //                LogUtils.log(TAG, "未找到要更新的item");
    //                return;
    //            }
    //            sessionAdapter.notifyItemChanged(itemPosition);
    //        }
    //    }
    private fun getSessionData(chatWith: String): SessionData? {
        for (sessionData in baseDataList!!) {
            if (sessionData?.chatWith.equals(chatWith)) return sessionData
        }
        return null
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOnDBChanged(onDBChanged: OnDBChanged) {
//        if (!isVisibleToUser) {
//            return;
//        }
        when (onDBChanged.event) {
            0, 1, 2 -> //                iSessionPresenter.getSessionUnRead(onDBChanged.chatType,onDBChanged.chatWith);
                iSessionPresenter?.loadHistoryData()
        }
        iSessionPresenter?.allUnReadCount
    }

    @Subscribe
    fun onClearUnReadStatus(event: ClearUnReadStatus) {
//        int itemPosition = getItemPosition(event.chatWith);
//        if (itemPosition < 0) {
//            LogUtils.log(TAG, "未找到要更新的item");
//            return;
//        }
//        iSessionPresenter.updateUnRead(event.chatWith);
//        baseDataList.get(itemPosition).setUnreadCount(0);
//        sessionAdapter.notifyItemChanged(itemPosition);
        iSessionPresenter?.clearSesionUnReadCount(event.chatType, event.chatWith)
    }

    private fun getItemPosition(chatWith: String): Int {
        val size = baseDataList!!.size
        for (i in 0 until size) {
            if (chatWith == baseDataList!![i]!!.chatWith) {
                return i
            }
        }
        return -1
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun getRecycleView(): RecyclerView? {
        return recyclerView
    }

    override fun getAdapter(): RvBaseAdapter<ItemData> {
        if (sessionAdapter == null) {
            baseDataList = ArrayList()
            //            baseDataMap = new HashMap<>();
            sessionAdapter = SessionAdapter(baseDataList, this)
            sessionAdapter!!.setRvListener(object : RvListenerImpl() {
                override fun onClick(view: View?, data: ItemData, position: Int) {
                    val sessionData = data as SessionData
                    if (sessionData.isShowAtTip) {
                        iSessionPresenter!!.updateSessionAtType(sessionData)
                    }
                    this@SessionFragment.activity?.let {
                        ChatBaseActivity.startActivity(
                            it,
                            sessionData.chatType,
                            sessionData.chatWith,
                            null,
                            sessionData.nickName,
                            sessionData.icon,
                            false
                        )
                    }
                }

               override fun onLongClick(view: View?, data: ItemData, position: Int) {
                    if (sessionDialogFragment != null) {
                        sessionDialogFragment!!.dismiss()
                    }
                    sessionDialogFragment =
                        SessionDialogFragment.getInstance(object : SessionDialogFragment.OnHandleListener {
                            override fun onDelete() {
                                iSessionPresenter!!.deleteSession(data as SessionData?)
                            }

                            override fun onClearContent() {
                                iSessionPresenter!!.clearSession(data as SessionData?)
                            }
                        })
                    sessionDialogFragment!!.show(fragmentManager!!, "sessiondialog")
                }
            })
        }
        return this.sessionAdapter as RvBaseAdapter<ItemData>
    }

    //    @Override
    //    public void showDialog() {
    //        processDialogFragment = ProcessDialogFragment.getInstance();
    //        processDialogFragment.show(getFragmentManager(), "");
    //    }
    //
    //    @Override
    //    public void hideDialog() {
    //        if (processDialogFragment != null) {
    //            processDialogFragment.dismiss();
    //            processDialogFragment = null;
    //        }
    //    }
    //    @Override
    //    public void showDialog() {
    //        processDialogFragment = ProcessDialogFragment.getInstance();
    //        processDialogFragment.show(getFragmentManager(), "");
    //    }
    //
    //    @Override
    //    public void hideDialog() {
    //        if (processDialogFragment != null) {
    //            processDialogFragment.dismiss();
    //            processDialogFragment = null;
    //        }
    //    }
    override fun getIPresenter(): IPresenter<in IView, in IModel>?{
        return SessionPresenter(this) as IPresenter<in IView, in IModel>
    }

    override fun onLoadHistory(sessionData: List<SessionData>?) {
        if (CollectionUtils.isEmpty(sessionData)) {
            if (isFirstLoad) {
                iSessionPresenter?.loadHistoryFromRemote()
                isFirstLoad = false
            }
            recyclerView?.setVisibility(View.GONE)
            llNoMsg!!.visibility = View.VISIBLE
            return
        }
        isFirstLoad = false
        recyclerView?.setVisibility(View.VISIBLE)
        llNoMsg!!.visibility = View.GONE
        baseDataList!!.clear()
        //        baseDataMap.clear();
        if (sessionData != null) {
            baseDataList!!.addAll(sessionData as Collection<SessionData?>)
        }
        sessionAdapter!!.notifyDataSetChanged()
    }

    override fun onLoadHistoryFromRemote(sessionData: List<SessionData>?) {
        if (CollectionUtils.isEmpty(sessionData)) {
            llNoMsg!!.visibility = View.VISIBLE
            recyclerView?.setVisibility(View.GONE)
            return
        }
        recyclerView?.setVisibility(View.VISIBLE)
        llNoMsg!!.visibility = View.GONE
        baseDataList!!.clear()
        //        baseDataMap.clear();
        baseDataList!!.addAll(sessionData!!)
        sessionAdapter!!.notifyDataSetChanged()
    }

    override fun onGetOtherInfoResult(
        result: JsonResult<in JsonRequestResult>,
        sessionData: SessionData?
    ) {
        if (result == null || !result.success()) {
            LogUtils.log(TAG, "获取Session item info failed")
            return
        }
        if (sessionData?.chatType === PhotonIMMessage.SINGLE) {
            val jsonRequestResult: JsonOtherInfoMulti = result.get() as JsonOtherInfoMulti
            if (CollectionUtils.isEmpty(jsonRequestResult.getData().getLists())) {
                return
            }
            val listsBean: JsonOtherInfoMulti.DataBean.ListsBean =
                jsonRequestResult.getData().getLists().get(0)
            sessionData.icon = (listsBean.getAvatar())
            sessionData.nickName = (listsBean.getNickname())
        } else {
            val jr: Any? = result.get()
            if (jr is JsonOtherInfoMulti) {
                val jsonRequestResult: JsonOtherInfoMulti = result.get() as JsonOtherInfoMulti
                if (jsonRequestResult.getData().getLists().size === 0) {
                    return
                }
                sessionData?.lastMsgFrName = (
                    jsonRequestResult.getData().getLists().get(0).getNickname()
                )
                sessionData?.isUpdateFromInfo = (false)
            } else {
                val jsonGroupProfile: JsonGroupProfile = result.get() as JsonGroupProfile
                val profile: JsonGroupProfile.DataBean.ProfileBean =
                    jsonGroupProfile.getData().getProfile()
                sessionData?.icon = (profile.getAvatar())
                sessionData?.nickName = (profile.getName())
            }
        }
        sessionData?.itemPosition?.let { sessionAdapter!!.notifyItemChanged(it) }
    }

    override fun onDeleteSession(data: SessionData?) {
        if (sessionDialogFragment != null) {
            sessionDialogFragment?.dismiss()
        }
        baseDataList!!.remove(data)
        sessionAdapter!!.notifyDataSetChanged()
        if (baseDataList!!.size == 0) {
            recyclerView?.setVisibility(View.GONE)
            llNoMsg!!.visibility = View.VISIBLE
        } else {
            recyclerView?.setVisibility(View.VISIBLE)
            llNoMsg!!.visibility = View.GONE
        }
    }

    override fun onClearSession(data: SessionData?) {
        if (sessionDialogFragment != null) {
            sessionDialogFragment?.dismiss()
        }
    }

    override fun onNewSession(sessionData: SessionData?) {
        // TODO: 2019-08-12 置顶
        baseDataList!!.add(0, sessionData)
        sessionAdapter!!.notifyItemInserted(0)
    }

    override fun onGetAllUnReadCount(result: Int) {
        EventBus.getDefault().post(AllUnReadCount(result))
    }

    override fun onUpdateOtherInfo(sessionData: SessionData?) {
        iSessionPresenter?.getOthersInfo(sessionData)
    }

    companion object {
        private const val TAG = "SessionFragment"
    }
}
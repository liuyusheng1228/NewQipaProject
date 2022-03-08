package com.qipa.newboxproject.ui.fragment.chat.groupinfo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.FrameLayout
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.util.AppMetaDataHelper
import com.qipa.newboxproject.ui.fragment.chat.groupinfo.adapter.GroupInfoMemberAdapter
import com.qipa.newboxproject.ui.fragment.chat.groupinfo.groupmember.GroupMemberActivity
import com.qipa.newboxproject.ui.fragment.chat.groupinfo.igroupinfo.IGroupInfoView
import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.GroupMembersData
import com.qipa.qipaimbase.utils.CollectionUtils
import com.qipa.qipaimbase.utils.ToastUtils
import com.qipa.qipaimbase.utils.http.jsons.JsonGetGroupIgnoreInfo
import com.qipa.qipaimbase.utils.http.jsons.JsonGroupProfile
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import com.qipa.qipaimbase.utils.mvpbase.IModel
import com.qipa.qipaimbase.utils.mvpbase.IPresenter
import com.qipa.qipaimbase.utils.mvpbase.IView
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.RvBaseAdapter
import com.qipa.qipaimbase.view.TitleBar
import java.util.ArrayList

class GroupInfoActivity : IGroupInfoView() {
    var titleBar: TitleBar? = null

    var swipeRefreshLayout: SwipeRefreshLayout? = null

    var recyclerView: RecyclerView? = null

    var tvGroupName: TextView? = null

    var tvNotic: TextView? = null

    var tvMemberCount: TextView? = null

    var flMemberRoot : FrameLayout? = null

    var sIgnore: Switch? = null
    private var gid: String? = null
    private var groupInfoDataList: MutableList<GroupMembersData?>? = null
    private var groupInfoMemberAdapter: GroupInfoMemberAdapter? = null
    private var getGroupInfo = false
    private var getGroupMemberInfo = false
    private var getGroupStatusInfo = false
    protected override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groupinfo)
        gid = getIntent().getStringExtra(EXTRA_GID)
        initView()
        getGroupInfo()
        initData()
    }
    override fun attachBaseContext(newBase: Context?) {
        val context: Context? =
            AppMetaDataHelper.instance?.getConfigurationContext(newBase)
        super.attachBaseContext(context)
    }


    private fun initData(){
        sIgnore?.setOnClickListener {
            onIgoreClick()
        }
        flMemberRoot?.setOnClickListener {
            onflMemberRootClick()
        }
    }

    private fun getGroupInfo() {
        iGroupInfoPresenter?.getGroupInfo(gid)
        iGroupInfoPresenter?.getGroupMembers(gid)
        iGroupInfoPresenter?.getGroupIgnoreStatus(gid)
    }

    private fun initView() {
        titleBar = findViewById(R.id.titleBar)
        swipeRefreshLayout = findViewById(R.id.refreshLayout)
        recyclerView = findViewById(R.id.recyclerView)
        tvGroupName = findViewById(R.id.tvGroupName)
        tvNotic = findViewById(R.id.tvNotic)
        tvMemberCount = findViewById(R.id.tvMemberCount)
        sIgnore = findViewById(R.id.sIgnore)
        flMemberRoot = findViewById(R.id.flMemberRoot)
        titleBar?.setTitle(resources.getString(R.string.message_settings))
        titleBar?.setLeftImageEvent(R.drawable.arrow_left) { v -> this@GroupInfoActivity.finish() }
        swipeRefreshLayout?.setOnRefreshListener {
            getGroupInfo = false
            getGroupMemberInfo = false
            getGroupStatusInfo = false
            getGroupInfo()
        }
    }

    override fun getIPresenter(): IPresenter<in IView, in IModel>?  {
        return GroupInfoPresenter(this) as IPresenter<in IView, in IModel>
    }

    override fun getRecycleView(): RecyclerView? {
        recyclerView = findViewById(R.id.recyclerView)
        return recyclerView
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager? {
        return LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration? {
        return DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
    }
    override fun getAdapter(): RvBaseAdapter<ItemData> {
        if (groupInfoMemberAdapter == null) {
            groupInfoDataList = ArrayList()
            groupInfoMemberAdapter = GroupInfoMemberAdapter(groupInfoDataList)
        }
        return groupInfoMemberAdapter as  RvBaseAdapter<ItemData>
    }


    override fun onGetGroupInfoResult(jsonResult: JsonResult<JsonRequestResult>) {
        getGroupInfo = true
        if (jsonResult.success()) {
            val jsonGroupProfile: JsonGroupProfile = jsonResult.get() as JsonGroupProfile
            if (!TextUtils.isEmpty(jsonGroupProfile.getData().getProfile().getName())) {
                tvGroupName?.setText(jsonGroupProfile.getData().getProfile().getName())
            }
        } else {
            ToastUtils.showText("获取群信息失败")
        }
        updateSwipeLayoutStatus()
    }

    override fun onGetGroupMemberResult(jsonResult: List<GroupMembersData?>?) {
        getGroupMemberInfo = true
        if (CollectionUtils.isEmpty(jsonResult)) {
            tvMemberCount?.text = "0人"
            ToastUtils.showText("获取群成员失败")
        } else {
            tvMemberCount?.text = String.format("%d人", jsonResult?.size)
            groupInfoDataList!!.clear()
            jsonResult?.let { groupInfoDataList!!.addAll(it) }
            groupInfoMemberAdapter?.notifyDataSetChanged()
        }
        updateSwipeLayoutStatus()
    }

    override fun onGetGroupIgnoreStatusResult(jsonResult: JsonResult<JsonRequestResult>) {
        getGroupStatusInfo = true
        if (jsonResult.success()) {
            val jsonGetGroupIgnoreInfo: JsonGetGroupIgnoreInfo =
                jsonResult.get() as JsonGetGroupIgnoreInfo
            sIgnore?.isChecked = jsonGetGroupIgnoreInfo.getData().getSwitchX() === 0
        } else {
            ToastUtils.showText("获取群消息免打扰状态失败")
            sIgnore?.isChecked = !sIgnore!!.isChecked
        }
        updateSwipeLayoutStatus()
    }

    override fun onChangeGroupIgnoreStatusResult(jsonResult: JsonResult<JsonRequestResult>) {
        if (jsonResult.success()) {
            ToastUtils.showText("设置成功")
        } else {
            ToastUtils.showText("设置失败")
        }
    }

    fun onIgoreClick() {
        iGroupInfoPresenter?.changeGroupIgnoreStatus(gid, sIgnore!!.isChecked)
    }

    fun onflMemberRootClick() {
        GroupMemberActivity.start(this, gid)
    }

    private fun updateSwipeLayoutStatus() {
        if (getGroupInfo && getGroupMemberInfo && getGroupStatusInfo) {
            if (swipeRefreshLayout?.isRefreshing()!!) {
                swipeRefreshLayout?.setRefreshing(false)
            }
        }
    }

    companion object {
        private const val EXTRA_GID = "EXTRA_GID"
        fun startActivity(activity: Activity, gid: String?) {
            val intent = Intent(
                activity,
                GroupInfoActivity::class.java
            )
            intent.putExtra(EXTRA_GID, gid)
            activity.startActivity(intent)
        }
    }
}
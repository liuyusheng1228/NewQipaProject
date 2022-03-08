package com.qipa.newboxproject.ui.fragment.chat.groupinfo.groupmember

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qipa.newboxproject.R
import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.GroupMemberSelectPresenter
import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.GroupMembersData
import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.adapter.GroupMemberAdapter
import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.igroupmember.IGroupMemeberView

import com.qipa.qipaimbase.utils.LogUtils
import com.qipa.qipaimbase.utils.mvpbase.IModel
import com.qipa.qipaimbase.utils.mvpbase.IPresenter
import com.qipa.qipaimbase.utils.mvpbase.IView
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.RvBaseAdapter
import com.qipa.qipaimbase.utils.recycleadapter.RvListenerImpl
import com.qipa.qipaimbase.view.TitleBar
import java.util.ArrayList

class GroupMemberActivity : IGroupMemeberView() {
    var titleBar: TitleBar? = null

    var recyclerView: RecyclerView? = null

    var llNoMsg: LinearLayout? = null

    var refreshLayout: SwipeRefreshLayout? = null
    private var gId: String? = null
    private var groupMemberData: MutableList<GroupMembersData?>? = null
    private var groupMemberAdapter: GroupMemberAdapter? = null
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groupmember)
        initView()
    }

    private fun initView() {
        titleBar = findViewById(R.id.titleBar)
        recyclerView = findViewById(R.id.recyclerView)
        llNoMsg = findViewById(R.id.llNoMsg)
        refreshLayout = findViewById(R.id.refreshLayout)
        gId = getIntent().getStringExtra(EXTRA_GID)
        titleBar?.setLeftImageEvent(R.drawable.arrow_left) { view -> this@GroupMemberActivity.finish() }
        titleBar?.setTitle("组成员")
        if (!TextUtils.isEmpty(gId)) {
            iGroupPresenter?.getGroupMembers(gId, true, false)
        } else {
            LogUtils.log(TAG, "gid is null")
        }
        refreshLayout?.setOnRefreshListener { iGroupPresenter?.getGroupMembers(gId, true, false) }
    }

    override fun onGetGroupMembersResult(result: List<GroupMembersData?>?) {
        titleBar?.setTitle(String.format("组成员（%d人）", result?.size))
        if (refreshLayout?.isRefreshing()!!) {
            refreshLayout?.setRefreshing(false)
        }
        llNoMsg!!.visibility = View.GONE
        recyclerView?.setVisibility(View.VISIBLE)
        groupMemberData!!.clear()
        result?.let { groupMemberData!!.addAll(it) }
        groupMemberAdapter?.notifyDataSetChanged()
    }

    override fun showMembersEmptyView() {
        if (refreshLayout?.isRefreshing()!!) {
            refreshLayout?.setRefreshing(false)
        }
        llNoMsg!!.visibility = View.VISIBLE
        recyclerView?.setVisibility(View.GONE)
    }

    override fun getRecycleView(): RecyclerView? {
        recyclerView= findViewById(R.id.recyclerView)
        return recyclerView
    }

    override fun getAdapter(): RvBaseAdapter<ItemData>? {
        if (groupMemberAdapter == null) {
            groupMemberData = ArrayList()
            groupMemberAdapter = GroupMemberAdapter(groupMemberData)
            groupMemberAdapter!!.setRvListener(object : RvListenerImpl() {
                override fun onClick(view: View?, data: ItemData, position: Int) {
                    when (view?.id) {
                    }
                }

                override fun onLongClick(view: View?, data: ItemData, position: Int) {}
            })
        }
        return groupMemberAdapter
    }

    override fun getIPresenter(): IPresenter<in IView, in IModel>? {
        return GroupMemberSelectPresenter(this) as IPresenter<in IView, in IModel>
    }




    companion object {
        private const val EXTRA_GID = "EXTRA_GID"
        private const val ACTIVITY_REQUEST_CODE = 123
        private const val TAG = "GroupMemberSelectActivity"
        fun start(activity: Activity, gId: String?) {
            val intent = Intent(
                activity,
                GroupMemberActivity::class.java
            )
            intent.putExtra(EXTRA_GID, gId)
            activity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
        }
    }
}

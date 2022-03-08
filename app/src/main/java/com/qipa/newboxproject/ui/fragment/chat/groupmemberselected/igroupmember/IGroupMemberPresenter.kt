package com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.igroupmember

import androidx.recyclerview.widget.RecyclerView
import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.GroupMembersData
import com.qipa.qipaimbase.utils.mvpbase.IModel
import com.qipa.qipaimbase.utils.mvpbase.IPresenter
import com.qipa.qipaimbase.utils.mvpbase.IView
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.RvBaseAdapter

abstract class IGroupMemberPresenter<V : IGroupMemeberView?, M : IGroupMemberModel?>(iView: V) :
    IPresenter<V, M>(iView) {
    abstract fun getGroupMembers(gid: String?)
    abstract fun getGroupMembers(gid: String?, containSelf: Boolean, showCb: Boolean)
    override fun getEmptyView(): V {
        return object : IGroupMemeberView() {
            override fun onGetGroupMembersResult(result: List<GroupMembersData?>?) {}
            override fun showMembersEmptyView() {}
            override fun getRecycleView(): RecyclerView? {
                return null
            }

            override fun getAdapter(): RvBaseAdapter<ItemData>? {
                return null
            }

            override fun getIPresenter(): IPresenter<in IView, in IModel>? {
                return null
            }


        } as V
    }
}

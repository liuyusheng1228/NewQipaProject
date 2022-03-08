package com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.adapter

import android.widget.TextView
import com.qipa.newboxproject.R
import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.GroupMembersData
import com.qipa.qipaimbase.utils.Constants
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.ItemTypeAbstract
import com.qipa.qipaimbase.utils.recycleadapter.RvViewHolder

class GroupMemberAllItem : ItemTypeAbstract() {
    override fun openClick(): Boolean {
        return true
    }

    override val type: Int
        get() = Constants.ITEM_TYPE_GROUP_MEMBER_SELECTED_ALL


    override val layout: Int
        get() = R.layout.item_group_member_selected_all

    override fun fillContent(rvViewHolder: RvViewHolder?, position: Int, data: ItemData?) {
        val groupMembersData = data as GroupMembersData
        groupMembersData.position = (position)
        val view = rvViewHolder?.getView(R.id.ivIcon)
        (rvViewHolder?.getView(R.id.tvNickName) as TextView).setText(groupMembersData.name)
    }

    override val onClickViews: IntArray
        get() = intArrayOf(R.id.llRoot)
}

package com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.adapter

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.qipa.newboxproject.R
import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.GroupMembersData
import com.qipa.qipaimbase.utils.Constants
import com.qipa.qipaimbase.utils.image.ImageLoaderUtils
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.ItemTypeAbstract
import com.qipa.qipaimbase.utils.recycleadapter.RvViewHolder

class GroupMemberItem : ItemTypeAbstract() {
    override fun openClick(): Boolean {
        return true
    }

    override val type: Int
        get() = Constants.ITEM_TYPE_GROUP_MEMBER_SELECTED
    override val layout: Int
        get() = R.layout.item_group_member_selected



    override fun fillContent(rvViewHolder: RvViewHolder?, position: Int, data: ItemData?) {
        val groupMembersData = data as GroupMembersData
        groupMembersData.position = (position)
        if (groupMembersData.isShowCb) {
            val cb = rvViewHolder?.getView(R.id.checkbox) as CheckBox
            if (groupMembersData.id.equals(
                   " LoginInfo.getInstance().getUserId()"
                ) //                || groupMembersData.getId().equals(igoreId)
            ) {
                cb.visibility = View.INVISIBLE
            } else {
                cb.visibility = View.VISIBLE
                cb.isChecked = groupMembersData.isSelected
            }
        } else {
            rvViewHolder?.getView(R.id.checkbox)?.visibility = View.GONE
        }
        val view = rvViewHolder?.getView(R.id.tvNickName) as TextView
        ImageLoaderUtils.getInstance().loadImage(
            view.context,
            groupMembersData.icon,
            R.drawable.head_placeholder,
            rvViewHolder.getView(R.id.ivIcon) as ImageView
        )
        view.setText(groupMembersData.name)
    }

    override val onClickViews: IntArray
        get() = intArrayOf(R.id.checkbox)
}

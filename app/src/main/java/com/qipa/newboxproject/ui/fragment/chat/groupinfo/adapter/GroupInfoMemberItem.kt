package com.qipa.newboxproject.ui.fragment.chat.groupinfo.adapter

import android.widget.ImageView
import android.widget.TextView
import com.qipa.newboxproject.R
import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.GroupMembersData
import com.qipa.qipaimbase.utils.Constants
import com.qipa.qipaimbase.utils.image.ImageLoaderUtils
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.ItemTypeAbstract
import com.qipa.qipaimbase.utils.recycleadapter.RvViewHolder

class GroupInfoMemberItem : ItemTypeAbstract() {
    override fun openClick(): Boolean {
        return false
    }

    override val type: Int
        get() = Constants.ITEM_TYPE_GROUP_MEMBER_INFO
    override val layout: Int
        get() = R.layout.item_groupinfo_type

    override fun fillContent(rvViewHolder: RvViewHolder?, position: Int, data: ItemData?) {
        val groupInfoData: GroupMembersData = data as GroupMembersData
        val view = rvViewHolder?.getView(R.id.tvName) as TextView
        view.setText(groupInfoData.name)
        ImageLoaderUtils.getInstance().loadImage(
            view.context,
            groupInfoData.icon,
            R.drawable.head_placeholder,
            rvViewHolder.getView(R.id.ivIcon) as ImageView
        )
    }

    override val onClickViews: IntArray
        get() = IntArray(0)
}

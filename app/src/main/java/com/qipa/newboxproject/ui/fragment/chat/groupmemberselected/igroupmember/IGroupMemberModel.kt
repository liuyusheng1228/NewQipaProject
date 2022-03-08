package com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.igroupmember

import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.GroupMembersData
import com.qipa.qipaimbase.utils.mvpbase.IModel

abstract class IGroupMemberModel : IModel {
    abstract fun getGroupMembers(
        gid: String?,
        containSelf: Boolean,
        onGetGroupMemberListener: OnGetGroupMemberListener?
    )

    abstract fun getGroupMembers(
        gid: String?,
        containSelf: Boolean,
        showCb: Boolean,
        onGetGroupMemberListener: OnGetGroupMemberListener?
    )

    abstract fun getGroupMembers(
        itemType: Int,
        gid: String?,
        onGetGroupMemberListener: OnGetGroupMemberListener?
    )

    abstract fun getGroupMembers(
        itemType: Int,
        gid: String?,
        containSelf: Boolean,
        onGetGroupMemberListener: OnGetGroupMemberListener?
    )

    abstract fun getGroupMembers(
        itemType: Int,
        gid: String?,
        containSelf: Boolean,
        showCb: Boolean,
        onGetGroupMemberListener: OnGetGroupMemberListener?
    )

    interface OnGetGroupMemberListener {
        fun onGetGroupMembers(result: List<GroupMembersData?>?)
    }
}

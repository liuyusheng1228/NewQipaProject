package com.qipa.newboxproject.ui.fragment.chat.groupinfo.groupmember.igroupmember

import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.GroupMembersData
import com.qipa.qipaimbase.utils.mvpbase.IModel

abstract class IGroupMemberModel : IModel {
    abstract fun getGroupMembers(gid: String?, onGetGroupMemberListener: OnGetGroupMemberListener?)
    interface OnGetGroupMemberListener {
        fun onGetGroupMembers(result: List<GroupMembersData>?)
    }
}

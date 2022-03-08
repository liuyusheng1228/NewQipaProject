package com.qipa.newboxproject.ui.fragment.chat.groupmemberselected

import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.igroupmember.IGroupMemberModel
import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.igroupmember.IGroupMemberPresenter
import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.igroupmember.IGroupMemeberView
import com.qipa.qipaimbase.utils.CollectionUtils

class GroupMemberSelectPresenter(iView: IGroupMemeberView?) :
    IGroupMemberPresenter<IGroupMemeberView?, IGroupMemberModel?>(iView) {
    override fun getGroupMembers(gid: String?) {
        getGroupMembers(gid, false, true)
    }

    override fun generateIModel(): IGroupMemberModel {
        return GroupMemberSelectModel()
    }

    override fun getGroupMembers(gid: String?, containSelf: Boolean, showCb: Boolean) {
        containSelf?.let {
            showCb?.let { it1 ->
                getiModel()?.getGroupMembers(gid, it, it1, object :
                    IGroupMemberModel.OnGetGroupMemberListener {

                    override fun onGetGroupMembers(result: List<GroupMembersData?>?) {
                        if (CollectionUtils.isEmpty(result)) {
                            getIView()?.showMembersEmptyView()
                        } else {
                            getIView()?.onGetGroupMembersResult(result)
                        }
                    }

                })
            }
        }
    }
}

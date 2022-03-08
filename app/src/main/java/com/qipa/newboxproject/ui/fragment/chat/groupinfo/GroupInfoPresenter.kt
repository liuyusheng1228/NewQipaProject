package com.qipa.newboxproject.ui.fragment.chat.groupinfo

import com.qipa.newboxproject.ui.fragment.chat.groupinfo.igroupinfo.IGroupInfoModel
import com.qipa.newboxproject.ui.fragment.chat.groupinfo.igroupinfo.IGroupInfoPresenter
import com.qipa.newboxproject.ui.fragment.chat.groupinfo.igroupinfo.IGroupInfoView
import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.GroupMemberSelectModel
import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.GroupMembersData
import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.igroupmember.IGroupMemberModel
import com.qipa.qipaimbase.utils.Constants
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult

class GroupInfoPresenter(iView: IGroupInfoView?) :
    IGroupInfoPresenter<IGroupInfoView?, IGroupInfoModel?>(iView) {
    private val groupMemberSelectModel: GroupMemberSelectModel
   override fun getGroupInfo(gid: String?) {
        getiModel()?.getGroupInfo(
            "LoginInfo.getInstance().getSessionId()", "LoginInfo.getInstance().getUserId()", gid
        ,object : IGroupInfoModel.OnGetGroupInfoListener{
                override fun onGetGroupInfoResult(jsonResult: JsonResult<JsonRequestResult>) {
                    getIView()?.onGetGroupInfoResult(jsonResult)
                }

            })
    }

    override fun getGroupMembers(gid: String?) {
        groupMemberSelectModel.getGroupMembers(
            Constants.ITEM_TYPE_GROUP_MEMBER_INFO,
            gid
        ,object : IGroupMemberModel.OnGetGroupMemberListener{
                override fun onGetGroupMembers(result: List<GroupMembersData?>?) {
                    getIView()?.onGetGroupMemberResult(
                        result
                    )
                }

            })
    }

    override fun getGroupIgnoreStatus(gid: String?) {
        getiModel()?.getGroupIgnoreStatus(
            "LoginInfo.getInstance().getSessionId()",
            "LoginInfo.getInstance().getUserId()", gid
        ,object : IGroupInfoModel.OnGetGroupIgnoreStatusListener{
                override fun onGetGroupIgnoreStatus(jsonResult: JsonResult<JsonRequestResult>) {
                    getIView()?.onGetGroupIgnoreStatusResult(jsonResult)
                }

            })
    }

    override fun changeGroupIgnoreStatus(gid: String?, igonre: Boolean) {
        // 0（开启勿扰）1(关闭勿扰）
        val switchX = if (igonre) 0 else 1
        getiModel()?.changeGroupIgnoreStatus(
            "LoginInfo.getInstance().getSessionId()",
            "LoginInfo.getInstance().getUserId()", gid, switchX
        ,object : IGroupInfoModel.OnChangeGroupIgnoreStatusListener{
                override fun onChangeGroupIgnoreStatus(jsonResult: JsonResult<JsonRequestResult>) {
                    getIView()?.onChangeGroupIgnoreStatusResult(jsonResult)
                }

            })
    }

    override fun generateIModel(): IGroupInfoModel {
        return GroupInfoModel()
    }

    init {
        groupMemberSelectModel = GroupMemberSelectModel()
    }
}

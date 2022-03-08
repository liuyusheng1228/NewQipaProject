package com.qipa.newboxproject.ui.fragment.chat.groupmemberselected

import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.igroupmember.IGroupMemberModel
import com.qipa.qipaimbase.utils.CollectionUtils
import com.qipa.qipaimbase.utils.Constants
import com.qipa.qipaimbase.utils.http.HttpUtils
import com.qipa.qipaimbase.utils.http.jsons.JsonGroupMembers
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import com.qipa.qipaimbase.utils.task.AsycTaskUtil
import com.qipa.qipaimbase.utils.task.TaskExecutor
import java.util.ArrayList

class GroupMemberSelectModel : IGroupMemberModel() {
    override fun getGroupMembers(
        gid: String?,
        containSelf: Boolean,
        onGetGroupMemberListener: OnGetGroupMemberListener?
    ) {
        getGroupMembers(
            Constants.ITEM_TYPE_GROUP_MEMBER_SELECTED,
            gid,
            containSelf,
            onGetGroupMemberListener
        )
    }

    override fun getGroupMembers(
        gid: String?,
        containSelf: Boolean,
        showCb: Boolean,
        onGetGroupMemberListener: OnGetGroupMemberListener?
    ) {
        getGroupMembers(
            Constants.ITEM_TYPE_GROUP_MEMBER_SELECTED,
            gid,
            containSelf,
            showCb,
            onGetGroupMemberListener
        )
    }

    override fun getGroupMembers(
        itemType: Int,
        gid: String?,
        onGetGroupMemberListener: OnGetGroupMemberListener?
    ) {
        getGroupMembers(itemType, gid, true, onGetGroupMemberListener)
    }

    override fun getGroupMembers(
        itemType: Int,
        gid: String?,
        containSelf: Boolean,
        onGetGroupMemberListener: OnGetGroupMemberListener?
    ) {
        getGroupMembers(itemType, gid, containSelf, true, onGetGroupMemberListener)
    }

    override fun getGroupMembers(
        itemType: Int,
        gid: String?,
        containSelf: Boolean,
        showCb: Boolean,
        onGetGroupMemberListener: OnGetGroupMemberListener?
    ) {
        TaskExecutor.instance.createAsycTask({
            getGroupMembersInner(
                "LoginInfo.getInstance().getSessionId()",
                "LoginInfo.getInstance().getUserId()",
                gid,
                itemType,
                containSelf,
                showCb
            )
        },object : AsycTaskUtil.OnTaskListener{
            override fun onTaskFinished(result: Any?) {
                if (onGetGroupMemberListener != null) {
                    onGetGroupMemberListener.onGetGroupMembers(result as List<GroupMembersData?>?)
                }
            }

        })
    }

    companion object {
        fun getGroupMembersInner(
            sessionId: String?,
            userID: String?,
            gid: String?,
            itemType: Int,
            containSelf: Boolean,
            showCb: Boolean
        ): Any? {
            val jsonResult: JsonResult<JsonRequestResult> =
                HttpUtils.getInstance().getGroupMembers(sessionId, userID, gid)
            return if (jsonResult.success()) {
                val jsonGroupMembers: JsonGroupMembers = jsonResult.get() as JsonGroupMembers
                val lists: List<JsonGroupMembers.DataBean.ListsBean> =
                    jsonGroupMembers.getData().getLists()
                if (CollectionUtils.isEmpty(lists)) {
                    null
                } else {
                    val result: MutableList<GroupMembersData> = ArrayList(lists.size)
                    for (list in lists) {
                        if (!containSelf && list.getUserId()
                                .equals("LoginInfo.getInstance().getUserId()")
                        ) {
                            continue
                        }
                        result.add(
                            GroupMembersData(
                                list.getAvatar(),
                                list.getNickname(),
                                list.getUserId(),
                                showCb,
                                itemType
                            )
                        )
                    }
                    result
                }
            } else {
                null
            }
        }
    }
}

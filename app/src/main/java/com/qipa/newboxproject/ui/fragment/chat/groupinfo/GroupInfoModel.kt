package com.qipa.newboxproject.ui.fragment.chat.groupinfo

import com.qipa.newboxproject.ui.fragment.chat.groupinfo.igroupinfo.IGroupInfoModel
import com.qipa.qipaimbase.utils.http.HttpUtils
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import com.qipa.qipaimbase.utils.task.AsycTaskUtil
import com.qipa.qipaimbase.utils.task.TaskExecutor

class GroupInfoModel : IGroupInfoModel() {
    override fun getGroupInfo(
        sessionId: String?,
        userId: String?,
        gid: String?,
        onGetGroupInfoListener: OnGetGroupInfoListener?
    ) {
        TaskExecutor.instance.createAsycTask({
            HttpUtils.getInstance().getGroupProfile(sessionId, userId, gid)
        },object : AsycTaskUtil.OnTaskListener{
            override fun onTaskFinished(result: Any?) {
                if (onGetGroupInfoListener != null) {
                    onGetGroupInfoListener.onGetGroupInfoResult(result as JsonResult<JsonRequestResult>)
                }
            }

        })
    }

    override fun getGroupIgnoreStatus(
        sessionId: String?,
        userId: String?,
        gid: String?,
        onGetGroupIgnoreStatusListener: OnGetGroupIgnoreStatusListener?
    ) {
        TaskExecutor.instance.createAsycTask({
            HttpUtils.getInstance().getGroupIgnoreStatus(sessionId, userId, gid)
        },object :  AsycTaskUtil.OnTaskListener{
            override fun onTaskFinished(result: Any?) {
                if (onGetGroupIgnoreStatusListener != null) {
                    onGetGroupIgnoreStatusListener.onGetGroupIgnoreStatus(result as JsonResult<JsonRequestResult>)
                }
            }

        })
    }

    override fun changeGroupIgnoreStatus(
        sessionId: String?,
        userId: String?,
        gid: String?,
        switchX: Int,
        onChangeGroupIgnoreStatusListener: OnChangeGroupIgnoreStatusListener?
    ) {
        TaskExecutor.instance.createAsycTask({
            sessionId?.let {
                userId?.let { it1 ->
                    gid?.let { it2 ->
                        changeGroupIgnoreStatusInner(
                            it,
                            it1,
                            it2,
                            switchX
                        )
                    }
                }
            }
        },object :  AsycTaskUtil.OnTaskListener{
            override fun onTaskFinished(result: Any?) {
                if (onChangeGroupIgnoreStatusListener != null) {
                    onChangeGroupIgnoreStatusListener.onChangeGroupIgnoreStatus(result as JsonResult<JsonRequestResult>)
                }
            }

        })
    }

    private fun changeGroupIgnoreStatusInner(
        sessionId: String,
        userId: String,
        gid: String,
        switchX: Int
    ): Any {
        val jsonResult: JsonResult<JsonRequestResult> =
            HttpUtils.getInstance().setGroupIgnoreStatus(sessionId, userId, gid, switchX)
        if (jsonResult.success()) {
            //待修改
//            PhotonIMDatabase.getInstance()
//                .updateSessionIgnoreAlert(PhotonIMMessage.GROUP, gid, switchX == 0)
        }
        return jsonResult
    }
}

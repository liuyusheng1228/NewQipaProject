package com.qipa.newboxproject.ui.fragment.chat.groupinfo.igroupinfo

import androidx.recyclerview.widget.RecyclerView
import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.GroupMembersData
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import com.qipa.qipaimbase.utils.mvpbase.IModel
import com.qipa.qipaimbase.utils.mvpbase.IPresenter
import com.qipa.qipaimbase.utils.mvpbase.IView
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.RvBaseAdapter

abstract class IGroupInfoPresenter<V : IGroupInfoView?, M : IGroupInfoModel?>(iView: V) :
    IPresenter<V, M>(iView) {
    override fun getEmptyView(): V {
        return object : IGroupInfoView() {
            override fun onGetGroupInfoResult(jsonResult: JsonResult<JsonRequestResult>) {}
            override fun onGetGroupMemberResult(jsonResult: List<GroupMembersData?>?) {}
            override fun onGetGroupIgnoreStatusResult(jsonResult: JsonResult<JsonRequestResult>) {}
            override fun onChangeGroupIgnoreStatusResult(jsonResult: JsonResult<JsonRequestResult>) {}
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

    abstract fun getGroupInfo(gid: String?)
    abstract fun getGroupMembers(gid: String?)
    abstract fun getGroupIgnoreStatus(gid: String?)
    abstract fun changeGroupIgnoreStatus(gid: String?, igonre: Boolean)
}
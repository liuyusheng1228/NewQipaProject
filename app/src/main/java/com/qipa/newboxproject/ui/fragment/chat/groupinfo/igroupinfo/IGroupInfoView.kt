package com.qipa.newboxproject.ui.fragment.chat.groupinfo.igroupinfo

import android.os.Bundle
import androidx.annotation.Nullable
import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.GroupMembersData
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import com.qipa.qipaimbase.utils.mvpbase.IView
import com.qipa.qipaimbase.utils.recycleadapter.actiivty.RvBaseActivity

abstract class IGroupInfoView : RvBaseActivity(), IView {
    protected var iGroupInfoPresenter: IGroupInfoPresenter<*, *>? = null
    protected override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iGroupInfoPresenter = getIPresenter() as IGroupInfoPresenter<*, *>?
        checkNotNull(iGroupInfoPresenter) { "chatPresenter is null" }
    }

    abstract fun onGetGroupInfoResult(jsonResult: JsonResult<JsonRequestResult>)
    abstract fun onGetGroupMemberResult(jsonResult: List<GroupMembersData?>?)
    abstract fun onGetGroupIgnoreStatusResult(jsonResult: JsonResult<JsonRequestResult>)
    abstract fun onChangeGroupIgnoreStatusResult(jsonResult: JsonResult<JsonRequestResult>)
}

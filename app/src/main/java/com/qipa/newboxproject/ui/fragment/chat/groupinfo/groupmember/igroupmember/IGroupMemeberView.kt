package com.qipa.newboxproject.ui.fragment.chat.groupinfo.groupmember.igroupmember

import android.os.Bundle
import androidx.annotation.Nullable
import com.qipa.newboxproject.ui.fragment.chat.groupmemberselected.GroupMembersData
import com.qipa.qipaimbase.utils.mvpbase.IView
import com.qipa.qipaimbase.utils.recycleadapter.actiivty.RvBaseActivity

abstract class IGroupMemeberView : RvBaseActivity(), IView {
    protected var iGroupPresenter: IGroupMemberPresenter<*, *>? = null
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iGroupPresenter = getIPresenter() as IGroupMemberPresenter<*, *>?
        checkNotNull(iGroupPresenter) { "iForwardPresenter is null" }
    }

    abstract fun onGetGroupMembersResult(result: List<GroupMembersData?>?)
    abstract fun showMembersEmptyView()
}

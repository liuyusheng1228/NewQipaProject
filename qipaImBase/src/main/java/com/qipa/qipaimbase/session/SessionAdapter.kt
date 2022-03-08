package com.qipa.qipaimbase.session

import com.qipa.qipaimbase.utils.recycleadapter.RvBaseAdapter

class SessionAdapter(
    baseDataList: List<SessionData?>?,
    updateOtherInfoListener: SessionItem.UpdateOtherInfoListener?
) :
    RvBaseAdapter<SessionData?>(baseDataList) {
    init {
        addItemType(SessionItem(updateOtherInfoListener))
    }
}
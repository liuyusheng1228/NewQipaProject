package com.qipa.newboxproject.ui.fragment.chat.groupinfo.adapter

import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.RvBaseAdapter

class GroupInfoMemberAdapter(baseDataList: List<*>?) :
    RvBaseAdapter<ItemData>(baseDataList as List<ItemData>?) {
    init {
        addItemType(GroupInfoMemberItem())
    }
}

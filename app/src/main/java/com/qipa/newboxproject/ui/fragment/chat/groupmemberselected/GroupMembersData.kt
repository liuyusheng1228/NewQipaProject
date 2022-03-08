package com.qipa.newboxproject.ui.fragment.chat.groupmemberselected

import com.qipa.qipaimbase.utils.Constants
import com.qipa.qipaimbase.utils.recycleadapter.ItemData

class GroupMembersData : ItemData {
    var isSelected = false
    var icon: String
    var name: String
    var id: String
    var isShowCb = true
    override var itemType: Int = Constants.ITEM_TYPE_GROUP_MEMBER_SELECTED
    var position = 0

    constructor(icon: String, name: String, id: String) {
        this.icon = icon
        this.name = name
        this.id = id
    }

    constructor(icon: String, name: String, id: String, itemType: Int) {
        this.icon = icon
        this.name = name
        this.id = id
        this.itemType = itemType
    }

    constructor(icon: String, name: String, id: String, showCb: Boolean, itemType: Int) {
        this.icon = icon
        this.name = name
        this.id = id
        isShowCb = showCb
        this.itemType = itemType
    }
}

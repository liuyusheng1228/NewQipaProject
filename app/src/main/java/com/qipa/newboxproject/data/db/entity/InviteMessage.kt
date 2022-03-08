package com.qipa.newboxproject.data.db.entity

import androidx.room.Entity
import com.qipa.newboxproject.app.App
import com.qipa.newboxproject.data.db.DemoDbHelper
import com.qipa.newboxproject.data.db.dao.MsgTypeManageDao
import java.io.Serializable
import java.lang.IllegalArgumentException


@Entity(tableName = "em_invite_message", primaryKeys = ["time"])
class InviteMessage : Serializable {
    var id = 0
    var from: String? = null
    var time: Long = 0
    var reason: String? = null
    private var type: String = MsgTypeManageEntity.msgType.NOTIFICATION.name
    private var status: String? = null
    var groupId: String? = null
    var groupName: String? = null
    var groupInviter: String? = null
    var isUnread //是否已读
            = false
    val statusEnum: InviteMessageStatus?
        get() {
            var status: InviteMessageStatus? = null
            try {
                status = this.status?.let { InviteMessageStatus.valueOf(it) }
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
            return status
        }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: InviteMessageStatus) {
        this.status = status.name
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    val typeEnum: MsgTypeManageEntity.msgType?
        get() {
            var type: MsgTypeManageEntity.msgType? = null
            try {
                type = MsgTypeManageEntity.msgType.valueOf(this.type)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
            return type
        }

    fun getType(): String {
        return type
    }

    fun setType(type: MsgTypeManageEntity.msgType) {
        //保存相应类型的MsgTypeManageEntity
        val entity = MsgTypeManageEntity()
        entity.type = (type.name)
        val msgTypeManageDao: MsgTypeManageDao? =
            DemoDbHelper.getInstance(App.instance.applicationContext)?.msgTypeManageDao
        if (msgTypeManageDao != null) {
            msgTypeManageDao.insert(entity)
        }
        this.type = type.name
    }

    fun setType(type: String) {
        this.type = type
    }
}

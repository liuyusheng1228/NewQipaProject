package com.qipa.newboxproject.data.db.entity

import android.text.TextUtils
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import com.qipa.newboxproject.app.App
import com.qipa.newboxproject.data.db.DemoDbHelper
import com.qipa.newboxproject.data.db.dao.InviteMessageDao
import java.io.Serializable


@Entity(
    tableName = "em_msg_type",
    primaryKeys = ["id"],
    indices = [Index(value = ["type"], unique = true)]
)
class MsgTypeManageEntity : Serializable {
    var id = 0
    var type: String? = null
    var extField: String? = null

    @get:Ignore
    val lastMsg: Any?
        get() {
            if (TextUtils.equals(type, msgType.NOTIFICATION.name)) {
                val inviteMessageDao: InviteMessageDao? =
                    DemoDbHelper.getInstance(App.instance.applicationContext)?.inviteMessageDao
                return if (inviteMessageDao == null) null else inviteMessageDao.lastInviteMessage()
            }
            return null
        }
    val unReadCount: Int
        get() {
            if (TextUtils.equals(type, msgType.NOTIFICATION.name)) {
                val inviteMessageDao: InviteMessageDao? =
                    DemoDbHelper.getInstance(App.instance.applicationContext)?.inviteMessageDao
                return if (inviteMessageDao == null) 0 else inviteMessageDao.queryUnreadCount()
            }
            return 0
        }

    enum class msgType {
        /**
         * 通知
         */
        NOTIFICATION
    }
}

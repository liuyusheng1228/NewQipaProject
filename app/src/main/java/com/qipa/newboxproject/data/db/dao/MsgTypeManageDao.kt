package com.qipa.newboxproject.data.db.dao

import androidx.room.*
import com.qipa.newboxproject.data.db.entity.MsgTypeManageEntity


@Dao
interface MsgTypeManageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg entities: MsgTypeManageEntity?): List<Long?>?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg entities: MsgTypeManageEntity?): Int

    @Query("select * from em_msg_type")
    fun loadAllMsgTypeManage(): List<MsgTypeManageEntity?>?

    @Query("select * from em_msg_type where type = :type")
    fun loadMsgTypeManage(type: String?): MsgTypeManageEntity?

    @Delete
    fun delete(vararg entities: MsgTypeManageEntity?)
}

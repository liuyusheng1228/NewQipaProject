package com.qipa.newboxproject.data.db

import androidx.room.RoomDatabase

import androidx.room.TypeConverters

import androidx.room.Database
import com.qipa.newboxproject.data.converter.DateConverter
import com.qipa.newboxproject.data.db.dao.*
import com.qipa.newboxproject.data.db.entity.AppKeyEntity
import com.qipa.newboxproject.data.db.entity.EmUserEntity
import com.qipa.newboxproject.data.db.entity.InviteMessage
import com.qipa.newboxproject.data.db.entity.MsgTypeManageEntity


@Database(
    entities = [EmUserEntity::class, InviteMessage::class, MsgTypeManageEntity::class, AppKeyEntity::class,ApkDownloadDao::class],
    version = 17
)
@TypeConverters(
    DateConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): EmUserDao?
    abstract fun inviteMessageDao(): InviteMessageDao?
    abstract fun msgTypeManageDao(): MsgTypeManageDao?
    abstract fun appKeyDao(): AppKeyDao?
    abstract fun apkDownloadDao() : ApkDownloadDao?
}

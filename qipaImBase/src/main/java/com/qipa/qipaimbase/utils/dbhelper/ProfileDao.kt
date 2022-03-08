package com.qipa.qipaimbase.utils.dbhelper

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(profile: Profile?): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(profiles: List<Profile?>?)

    @Query("select * from profile where userId = :userId")
    fun find(userId: String?): Profile?
}
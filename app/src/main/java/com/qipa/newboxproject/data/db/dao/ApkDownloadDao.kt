package com.qipa.newboxproject.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.qipa.newboxproject.data.db.entity.AppKeyEntity
import com.qipa.newboxproject.data.db.entity.DownloadApkEntity

@Dao
interface ApkDownloadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg keys: DownloadApkEntity?): List<Long?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(keys: List<DownloadApkEntity?>?): List<Long?>?

    @Query("select * from em_download_apk  order by timestamp asc")
    fun loadAllDownApp(): List<DownloadApkEntity?>?

    @Query("delete from em_download_apk where apk_url = :arg0")
    fun deleteAppDown(arg0: String?)

    @Query("select * from em_download_apk where apk_url = :arg0")
    fun queryDown(arg0: String?): List<DownloadApkEntity?>?
}
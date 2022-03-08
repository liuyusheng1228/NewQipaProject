package com.qipa.newboxproject.data.db.entity

import androidx.room.Entity
import java.io.Serializable

@Entity(tableName = "em_download_apk", primaryKeys = ["apk_url"])
class DownloadApkEntity : Serializable {
    var apk_url : String? = null
    var apk_name : String? = null
    var apk_down_time : Long = 0
    var apk_down_size : Int =0
    var apk_pic_url : String? = null
    var timestamp: Double

    init {
        timestamp = System.currentTimeMillis().toDouble()
    }
}
package com.qipa.qipaimbase.utils.dbhelper

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
class Profile {
    @NonNull
    @PrimaryKey
    var userId: String? = null
    var icon: String? = null
    var name: String? = null
}

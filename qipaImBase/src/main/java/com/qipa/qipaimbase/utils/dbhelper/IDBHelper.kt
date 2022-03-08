package com.qipa.qipaimbase.utils.dbhelper

interface IDBHelper {
    fun saveProfile(userId: String?, icon: String?, name: String?)
    fun findProfile(userId: String?): Profile?
    fun saveProfiles(profiles: List<Profile?>?)
}

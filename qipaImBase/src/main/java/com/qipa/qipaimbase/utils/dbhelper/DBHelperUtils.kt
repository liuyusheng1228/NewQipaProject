package com.qipa.qipaimbase.utils.dbhelper


class DBHelperUtils private constructor() {
    private val idbHelper: IDBHelper
    fun saveProfile(userId: String?, icon: String?, name: String?) {
        idbHelper.saveProfile(userId, icon, name)
    }

    fun saveProfiles(map: List<Profile?>?) {
        idbHelper.saveProfiles(map)
    }

    fun findProfile(userId: String?): Profile? {
        return idbHelper.findProfile(userId)
    }

    companion object {
        val instance = DBHelperUtils()
    }

    init {
        idbHelper = DBHelperImpl()
    }
}

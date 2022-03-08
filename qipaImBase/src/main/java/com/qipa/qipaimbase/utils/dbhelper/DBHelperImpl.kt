package com.qipa.qipaimbase.utils.dbhelper

import com.qipa.qipaimbase.ImBaseBridge
import com.qipa.qipaimbase.utils.CollectionUtils

class DBHelperImpl : IDBHelper {
    override fun saveProfile(userId: String?, icon: String?, name: String?) {
        val profile = Profile()
        profile.icon = (icon)
        profile.userId = (userId)
        profile.name = (name)
        ImBaseBridge.instance?.application?.let { ProfileDB.getInstance(it) }!!.profileDao()!!
            .insert(profile)
    }

    override fun saveProfiles(profiles: List<Profile?>?) {
        if (CollectionUtils.isEmpty(profiles)) {
            return
        }
        ImBaseBridge.instance?.application?.let { ProfileDB.getInstance(it) }!!.profileDao()!!
            .insertList(profiles)
    }

    override fun findProfile(userId: String?): Profile? {
        return ImBaseBridge.instance?.application?.let { ProfileDB.getInstance(it) }!!.profileDao()!!
            .find(userId)
    }
}

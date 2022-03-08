package com.qipa.qipaimbase.utils

import android.content.Context
import android.content.SharedPreferences
import com.qipa.qipaimbase.ImBaseBridge

object LocalRestoreUtils {
    private const val AUTH = "auth"
    private const val TOKEN = "token"
    private const val USERID = "userId"
    private const val SESSIONID = "sessionId"
    private const val FRISTLOADSESSION = "FRISTLOADSESSION"
    fun saveAuth(tokenId: String?, userId: String?, sessionId: String?) {
        val preferences: SharedPreferences? =
            ImBaseBridge.instance?.application?.getSharedPreferences(
                AUTH, Context.MODE_PRIVATE
            )
        val edit = preferences?.edit()
        edit?.putString(TOKEN, tokenId)
        edit?.putString(USERID, userId)
        edit?.putString(SESSIONID, sessionId)
        edit?.apply()
    }

    val auth: Array<String?>
        get() {
            val preferences: SharedPreferences? =
                ImBaseBridge.instance?.application?.getSharedPreferences(
                    AUTH, Context.MODE_PRIVATE
                )
            return arrayOf(
                preferences?.getString(TOKEN, ""),
                preferences?.getString(USERID, ""),
                preferences?.getString(
                    SESSIONID, ""
                )
            )
        }

    fun removeAuth() {
        val preferences: SharedPreferences? =
            ImBaseBridge.instance?.application?.getSharedPreferences(
                AUTH, Context.MODE_PRIVATE
            )
        val edit = preferences?.edit()
        edit?.putString(TOKEN, "")
        edit?.putString(USERID, "")
        edit?.putString(SESSIONID, "")
        edit?.apply()
    }

    val firstLoadSession: Boolean?
        get() {
            val preferences: SharedPreferences? =
                ImBaseBridge.instance?.application?.getSharedPreferences(
                    AUTH, Context.MODE_PRIVATE
                )
            val businessListener: ImBaseBridge.BusinessListener? =
                ImBaseBridge.instance?.businessListener
            var userId = ""
            if (businessListener != null) {
                userId = businessListener.userId.toString()
            }
            val key = FRISTLOADSESSION + "_" + userId
            val aBoolean = preferences?.getBoolean(key, true)
            val edit = preferences?.edit()
            edit?.putBoolean(key, false)
            edit?.apply()
            return aBoolean
        }
}

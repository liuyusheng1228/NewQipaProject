package com.qipa.newboxproject.app.manager

import android.content.Context
import com.hyphenate.util.EMLog

import com.hyphenate.chat.EMClient

import com.hyphenate.easeui.domain.EaseUser

import com.hyphenate.EMValueCallBack

import com.hyphenate.easeui.utils.EaseCommonUtils
import com.parse.*
import com.qipa.newboxproject.app.ChatHelper
import java.lang.Exception


class ParseManager private constructor() {
    fun onInit(context: Context) {
        val appContext: Context = context.getApplicationContext()
        Parse.enableLocalDatastore(appContext)
        //		Parse.initialize(context, ParseAppID, ParseClientKey);
        Parse.initialize(
            Parse.Configuration.Builder(appContext)
                .applicationId(ParseAppID)
                .server(parseServer)
                .build()
        )
    }

    fun updateParseNickName(nickname: String?): Boolean {
        val username = EMClient.getInstance().currentUser
        val pQuery: ParseQuery<ParseObject> = ParseQuery.getQuery(CONFIG_TABLE_NAME)
        pQuery.whereEqualTo(CONFIG_USERNAME, username)
        var pUser: ParseObject? = null
        try {
            pUser = pQuery.getFirst()
            if (pUser == null) {
                return false
            }
            pUser.put(CONFIG_NICK, nickname)
            pUser.save()
            return true
        } catch (e: ParseException) {
            if (e.getCode() === ParseException.OBJECT_NOT_FOUND) {
                pUser = ParseObject(CONFIG_TABLE_NAME)
                pUser.put(CONFIG_USERNAME, username)
                pUser.put(CONFIG_NICK, nickname)
                try {
                    pUser.save()
                    return true
                } catch (e1: ParseException) {
                    e1.printStackTrace()
                    EMLog.e(TAG, "parse error " + e1.message)
                }
            }
            e.printStackTrace()
            EMLog.e(TAG, "parse error " + e.message)
        } catch (e: Exception) {
            EMLog.e(TAG, "updateParseNickName error")
            e.printStackTrace()
        }
        return false
    }

    fun getContactInfos(usernames: List<String?>?, callback: EMValueCallBack<List<EaseUser?>?>) {
        val pQuery: ParseQuery<ParseObject> = ParseQuery.getQuery(CONFIG_TABLE_NAME)
        pQuery.whereContainedIn(CONFIG_USERNAME, usernames)
        pQuery.findInBackground(object : FindCallback<ParseObject?> {
            override fun done(arg0: List<ParseObject?>?, arg1: ParseException) {
                if (arg0 != null) {
                    val mList: MutableList<EaseUser> = ArrayList()
                    for (pObject in arg0) {
                        val user = pObject?.getString(CONFIG_USERNAME)?.let { EaseUser(it) }
                        val parseFile: ParseFile? = pObject?.getParseFile(CONFIG_AVATAR)
                        if (parseFile != null) {
                            user?.avatar = parseFile.getUrl()
                        }
                        user?.nickname = pObject?.getString(CONFIG_NICK)
                        EaseCommonUtils.setUserInitialLetter(user)
                        if (user != null) {
                            mList.add(user)
                        }
                    }
                    callback.onSuccess(mList)
                } else {
                    callback.onError(arg1.getCode(), arg1.message)
                }
            }
        })
    }

    fun asyncGetCurrentUserInfo(callback: EMValueCallBack<EaseUser?>) {
        val username = EMClient.getInstance().currentUser
        asyncGetUserInfo(username, object : EMValueCallBack<EaseUser?> {
            override fun onSuccess(value: EaseUser?) {
                callback.onSuccess(value)
            }

            override fun onError(error: Int, errorMsg: String) {
                if (error == ParseException.OBJECT_NOT_FOUND) {
                    val pUser = ParseObject(CONFIG_TABLE_NAME)
                    pUser.put(CONFIG_USERNAME, username)
                    pUser.saveInBackground(object : SaveCallback {
                        override fun done(arg0: ParseException?) {
                            if (arg0 == null) {
                                callback.onSuccess(EaseUser(username))
                            }
                        }
                    })
                } else {
                    callback.onError(error, errorMsg)
                }
            }
        })
    }

    fun asyncGetUserInfo(username: String?, callback: EMValueCallBack<EaseUser?>?) {
        val pQuery: ParseQuery<ParseObject> = ParseQuery.getQuery(CONFIG_TABLE_NAME)
        pQuery.whereEqualTo(CONFIG_USERNAME, username)
        pQuery.getFirstInBackground(object : GetCallback<ParseObject?> {
            override fun done(pUser: ParseObject?, e: ParseException) {
                if (pUser != null) {
                    val nick: String = pUser.getString(CONFIG_NICK)
                    val pFile: ParseFile = pUser.getParseFile(CONFIG_AVATAR)
                    if (callback != null) {
                        var user: EaseUser? = ChatHelper.instance?.getContactList()?.get(username)
                        if (user != null) {
                            user.nickname = nick
                            if (pFile != null && pFile.getUrl() != null) {
                                user.avatar = pFile.getUrl()
                            }
                        } else {
                            user = EaseUser(username!!)
                            user.nickname = nick
                            if (pFile != null && pFile.getUrl() != null) {
                                user.avatar = pFile.getUrl()
                            }
                        }
                        callback.onSuccess(user)
                    }
                } else {
                    callback?.onError(e.getCode(), e.message)
                }
            }
        })
    }

    fun uploadParseAvatar(data: ByteArray?): String? {
        val username = EMClient.getInstance().currentUser
        val pQuery: ParseQuery<ParseObject> = ParseQuery.getQuery(CONFIG_TABLE_NAME)
        pQuery.whereEqualTo(CONFIG_USERNAME, username)
        var pUser: ParseObject? = null
        try {
            pUser = pQuery.getFirst()
            if (pUser == null) {
                pUser = ParseObject(CONFIG_TABLE_NAME)
                pUser.put(CONFIG_USERNAME, username)
            }
            val pFile = ParseFile(data)
            pUser.put(CONFIG_AVATAR, pFile)
            pUser.save()
            return pFile.getUrl()
        } catch (e: ParseException) {
            if (e.getCode() === ParseException.OBJECT_NOT_FOUND) {
                try {
                    pUser = ParseObject(CONFIG_TABLE_NAME)
                    pUser.put(CONFIG_USERNAME, username)
                    val pFile = ParseFile(data)
                    pUser.put(CONFIG_AVATAR, pFile)
                    pUser.save()
                    return pFile.getUrl()
                } catch (e1: ParseException) {
                    e1.printStackTrace()
                    EMLog.e(TAG, "parse error " + e1.message)
                }
            } else {
                e.printStackTrace()
                EMLog.e(TAG, "parse error " + e.message)
            }
        } catch (e: Exception) {
            EMLog.e(TAG, "uploadParseAvatar error")
            e.printStackTrace()
        }
        return null
    }

    companion object {
        private val TAG = ParseManager::class.java.simpleName
        private const val ParseAppID = "UUL8TxlHwKj7ZXEUr2brF3ydOxirCXdIj9LscvJs"
        private const val ParseClientKey = "B1jH9bmxuYyTcpoFfpeVslhmLYsytWTxqYqKQhBJ"

        //	private static final String ParseAppID = "task";
        //	private static final String ParseClientKey = "123456789";
        private const val CONFIG_TABLE_NAME = "hxuser"
        private const val CONFIG_USERNAME = "username"
        private const val CONFIG_NICK = "nickname"
        private const val CONFIG_AVATAR = "avatar"
        private const val parseServer = "http://parse.easemob.com/parse/"
        val instance = ParseManager()
    }
}

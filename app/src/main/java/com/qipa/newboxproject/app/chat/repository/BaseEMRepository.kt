package com.qipa.newboxproject.app.chat.repository

import android.content.Context
import com.hyphenate.easeui.manager.EaseThreadManager

import com.qipa.newboxproject.data.db.DemoDbHelper

import com.qipa.newboxproject.data.db.dao.InviteMessageDao

import com.qipa.newboxproject.data.db.dao.MsgTypeManageDao

import com.qipa.newboxproject.data.db.dao.EmUserDao

import com.hyphenate.chat.EMPushManager

import com.hyphenate.chat.EMChatRoomManager

import com.hyphenate.chat.EMGroupManager

import com.hyphenate.chat.EMContactManager

import com.hyphenate.chat.EMChatManager

import com.hyphenate.chat.EMClient

import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.LiveData
import com.qipa.newboxproject.app.App
import com.qipa.newboxproject.app.ChatHelper


open class BaseEMRepository {


    /**
     * return a new liveData
     * @param item
     * @param <T>
     * @return
    </T> */
    open fun <T> createLiveData(item: T): LiveData<T>? {
        return MutableLiveData(item)
    }

    /**
     * login before
     * @return
     */
    val isLoggedIn: Boolean
        get() = EMClient.getInstance().isLoggedInBefore

    /**
     * 获取本地标记，是否自动登录
     * @return
     */
    val isAutoLogin: Boolean?
        get() = ChatHelper.instance?.autoLogin

    /**
     * 获取当前用户
     * @return
     */
    val currentUser: String?
        get() = ChatHelper.instance?.currentUser

    /**
     * EMChatManager
     * @return
     */
    val chatManager: EMChatManager?
        get() = ChatHelper.instance?.eMClient?.chatManager()

    /**
     * EMContactManager
     * @return
     */
    val contactManager: EMContactManager?
        get() = ChatHelper.instance?.contactManager

    /**
     * EMGroupManager
     * @return
     */
    val groupManager: EMGroupManager?
        get() = ChatHelper.instance?.eMClient?.groupManager()

    /**
     * EMChatRoomManager
     * @return
     */
    val chatRoomManager: EMChatRoomManager?
        get() = ChatHelper.instance?.chatroomManager

    /**
     * EMPushManager
     * @return
     */
    val pushManager: EMPushManager?
        get() = ChatHelper.instance?.pushManager

    /**
     * init room
     */
    fun initDb() {
        DemoDbHelper.getInstance(App.instance.applicationContext)!!.initDb(currentUser)
    }

    /**
     * EmUserDao
     * @return
     */
    val userDao: EmUserDao?
        get() = DemoDbHelper.getInstance(App.instance.applicationContext)?.userDao

    /**
     * get MsgTypeManageDao
     * @return
     */
    val msgTypeManageDao: MsgTypeManageDao?
        get() = DemoDbHelper.getInstance(App.instance.applicationContext)?.msgTypeManageDao

    /**
     * get invite message dao
     * @return
     */
    val inviteMessageDao: InviteMessageDao?
        get() = DemoDbHelper.getInstance(App.instance.applicationContext)?.inviteMessageDao

    /**
     * 在主线程执行
     * @param runnable
     */
    fun runOnMainThread(runnable: Runnable?) {
        EaseThreadManager.getInstance().runOnMainThread(runnable)
    }

    /**
     * 在异步线程
     * @param runnable
     */
    fun runOnIOThread(runnable: Runnable?) {
        EaseThreadManager.getInstance().runOnIOThread(runnable)
    }

    val context: Context
        get() = App.instance.getApplicationContext()
}

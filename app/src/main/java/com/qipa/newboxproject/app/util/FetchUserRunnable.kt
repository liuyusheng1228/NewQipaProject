package com.qipa.newboxproject.app.util

import com.hyphenate.util.EMLog

//import com.hyphenate.easecallkit.utils.EaseCallKitUtils
//
//import com.hyphenate.easecallkit.livedatas.EaseLiveDataBus
//
//import com.hyphenate.easecallkit.base.EaseCallUserInfo

import com.hyphenate.easeui.domain.EaseUser

import com.hyphenate.easeui.utils.EaseCommonUtils

import com.hyphenate.chat.EMUserInfo

import com.hyphenate.EMValueCallBack

import com.hyphenate.chat.EMClient
import com.qipa.newboxproject.app.ChatHelper


class FetchUserRunnable : Runnable {
    private val infoList: FetchUserInfoList

    // 轮询时间
    private val SLEEP_TIME = 1000

    // 是否停止
    @Volatile
    private var isStop = false
    override fun run() {
        while (!isStop) {
            var size: Int = infoList.userSize
            if (size > 0) {
                //判断长度是否大于100 最多能一次性获取100个用户属性
                if (size > 100) {
                    size = 100
                }
                val userIds = arrayOfNulls<String>(size)
                for (i in 0 until size) {
                    userIds[i] = infoList.userId
                }
                EMLog.i(TAG, "FetchUserRunnable exec  userId:$userIds")
                EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(
                    userIds,
                    object : EMValueCallBack<Map<String, EMUserInfo>?> {
                        override fun onSuccess(userInfos: Map<String, EMUserInfo>?) {
                            EMLog.i(
                                TAG,
                                "fetchUserInfoByUserId userInfo:" + userInfos!!.keys.toString()
                            )
                            if (userInfos != null && userInfos.size > 0) {
                                //更新本地数据库 同时刷新UI列表
                                warpEMUserInfo(userInfos)
                            } else {
                                EMLog.e(TAG, "fetchUserInfoByUserId userInfo is null")
                            }
                        }

                        override fun onError(error: Int, errorMsg: String) {
                            EMLog.e(
                                TAG,
                                "fetchUserInfoByUserId  error$error  errorMsg$errorMsg"
                            )
                        }
                    })
            } else {
                try {
                    Thread.sleep(SLEEP_TIME.toLong())
                } catch (e: InterruptedException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
            }
        }
        if (isStop) {
            //清空列表缓存
            infoList.init()
        }
    }

    /**
     * @param isStop
     * the isStop to set
     */
    fun setStop(isStop: Boolean) {
        this.isStop = isStop
    }

    private fun warpEMUserInfo(userInfos: Map<String, EMUserInfo>) {
        val it_user = userInfos.keys.iterator()
        val userEntities: MutableList<EaseUser> = ArrayList()
        var refreshContact = false
        val exitUsers: Map<String, EaseUser>? = ChatHelper.instance?.getContactList()
        while (it_user.hasNext()) {
            val userId = it_user.next()
            val userInfo = userInfos[userId]
            if (userInfo != null) {
                EMLog.e(TAG, "start warpEMUserInfo userId:" + userInfo.userId)
                val userEntity = EaseUser()
                userEntity.username = userInfo.userId
                userEntity.nickname = userInfo.nickName
                userEntity.email = userInfo.email
                userEntity.avatar = userInfo.avatarUrl
                userEntity.birth = userInfo.birth
                userEntity.gender = userInfo.gender
                userEntity.ext = userInfo.ext
                userEntity.sign = userInfo.signature
                EaseCommonUtils.setUserInitialLetter(userEntity)
                //判断当前更新的是否为好友关系
                if (exitUsers?.containsKey(userInfo.userId)!!) {
                    val user = exitUsers?.get(userInfo.userId)
                    if (user != null) {
                        if (user.contact == 0 || user.contact == 1) {
                            refreshContact = true
                        }
                        userEntity.contact = user.contact
                    } else {
                        userEntity.contact = 3
                    }
                } else {
                    userEntity.contact = 3
                }
                userEntities.add(userEntity)

                //通知callKit更新头像昵称
//                val info = EaseCallUserInfo(userInfo.nickName, userInfo.avatarUrl)
//                info.userId = userInfo.userId
//                EaseLiveDataBus.get().with(EaseCallKitUtils.UPDATE_USERINFO).postValue(info)
            }
        }

        //更新本地数据库信息
        ChatHelper.instance?.updateUserList(userEntities)

        //更新本地联系人列表
        ChatHelper.instance?.updateContactList()
        if (refreshContact) {
            //通知UI刷新列表
//            val event = EaseEvent.create(DemoConstant.CONTACT_UPDATE, EaseEvent.TYPE.CONTACT)
//            event.message = userInfos.keys.toString()

            //发送联系人更新事件
//            LiveDataBus.get().with(DemoConstant.CONTACT_UPDATE).postValue(event)
        }
        EMLog.e(TAG, " warpEMUserInfo userId:" + userInfos.keys.toString() + "  end")
    }

    companion object {
        private val TAG = FetchUserRunnable::class.java.simpleName
    }

    init {
        infoList = FetchUserInfoList.instance!!
    }
}

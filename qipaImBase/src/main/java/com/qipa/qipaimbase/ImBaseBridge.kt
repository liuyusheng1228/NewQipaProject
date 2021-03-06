package com.qipa.qipaimbase

import android.app.Activity
import android.app.Application
import android.content.Context
import com.qipa.qipaimbase.chat.ChatData
import com.qipa.qipaimbase.chat.emoji.EmojiUtils
import com.qipa.qipaimbase.utils.http.jsons.JsonContactRecent
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import java.util.ArrayList

class ImBaseBridge private constructor() {
    var application: Application? = null
        private set
    var businessListener: BusinessListener? = null
        private set
    var myIcon: String? = null
    private var joinedGids: MutableList<String>? = null


    fun setGids(joinedGids: MutableList<String>?) {
        this.joinedGids = joinedGids
    }

    fun getJoinedGids(): List<String>? {
        return joinedGids
    }

    fun addJoindGId(groupID: String) {
        if (joinedGids == null) {
            joinedGids = ArrayList()
        }
        if (!joinedGids!!.contains(groupID)) {
            joinedGids!!.add(groupID)
        }
    }
    companion object {
        var instance: ImBaseBridge? = null
            get() {
                if (field == null) {
                    field = ImBaseBridge()
                }
                return field
            }
        @Synchronized
        fun get(): ImBaseBridge{
            return instance!!
        }
    }




    fun init(builder: Builder) {
        application = builder.application
        //        PhotonIMClient.getInstance().supportGroup();
        businessListener = builder.businessListener
//        PhotonIMClient.getInstance().openDebugLog()
//        PhotonIMClient.getInstance().init(application, builder.appId)
        // TODO: 2019-08-18 maybe should change position
        EmojiUtils.instance?.init()
    }

    fun logout() {
//        LocalRestoreUtils.removeAuth()
//        TaskExecutor.getInstance().createAsycTask {
//            PhotonIMClient.getInstance().logout()
//            PhotonIMClient.getInstance().detachUserId()
//            null
//        }
    }

    fun startIm() {
//        IMReceiveHelper.getInstance().start()
    }

    fun stopIm(context: Context?) {
//        IMReceiveHelper.getInstance().stop()
    }

    interface OnGetUserIconListener {
        fun onGetUserIcon(iconUrl: String?, name: String?)
    }

    interface BusinessListener {
        //??????
        fun onRelayClick(activity: Activity?, chatData: ChatData?)

        //????????????icon
        fun getUserIcon(userId: String?, onGetUserIconListener: OnGetUserIconListener?)

        //??????@??????
        fun onAtListener(activity: Activity?, gid: String?)

        //?????????????????????
        fun onKickUser(activity: Activity?)

        //??????????????????
        fun onGroupInfoClick(activity: Activity?, gId: String?)

        //??????????????????
        fun getOthersInfo(ids: Array<String?>?): JsonResult<in JsonRequestResult>

        //?????????????????????
        fun getGroupProfile(groupId: String?): JsonResult<in JsonRequestResult>

        //?????????????????????
        val recentUser: JsonContactRecent?

        //??????????????????
        fun setIgnoreStatus(remoteId: String?, igoreAlert: Boolean): JsonResult<in JsonRequestResult>

        //??????????????????
        fun getIgnoreStatus(otherId: String?): JsonResult<in JsonRequestResult>

        //??????????????????
        fun sendVoiceFile(localFile: String?): JsonResult<in JsonRequestResult>

        //????????????
        fun sendPic(localFile: String?): JsonResult<in JsonRequestResult>

        //????????????id
        val userId: String?

        //??????tokenId
        val tokenId: String?
    }

    class Builder {
        var application: Application? = null
        private var appId: String? = null
        var businessListener: BusinessListener? = null
        fun application(`val`: Application?): Builder {
            application = `val`
            return this
        }

        fun appId(`val`: String?): Builder {
            appId = `val`
            return this
        }

        fun addListener(listener: BusinessListener?): Builder {
            businessListener = listener
            return this
        }
    }

}


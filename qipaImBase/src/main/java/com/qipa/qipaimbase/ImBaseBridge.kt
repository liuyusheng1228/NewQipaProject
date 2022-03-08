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
        //转发
        fun onRelayClick(activity: Activity?, chatData: ChatData?)

        //获取用户icon
        fun getUserIcon(userId: String?, onGetUserIconListener: OnGetUserIconListener?)

        //群聊@成员
        fun onAtListener(activity: Activity?, gid: String?)

        //收到服务器踢人
        fun onKickUser(activity: Activity?)

        //获取群组信息
        fun onGroupInfoClick(activity: Activity?, gId: String?)

        //获取他人信息
        fun getOthersInfo(ids: Array<String?>?): JsonResult<in JsonRequestResult>

        //获取群组信息呢
        fun getGroupProfile(groupId: String?): JsonResult<in JsonRequestResult>

        //获取最近联系人
        val recentUser: JsonContactRecent?

        //设置勿扰状态
        fun setIgnoreStatus(remoteId: String?, igoreAlert: Boolean): JsonResult<in JsonRequestResult>

        //获取勿扰状态
        fun getIgnoreStatus(otherId: String?): JsonResult<in JsonRequestResult>

        //上传语音文件
        fun sendVoiceFile(localFile: String?): JsonResult<in JsonRequestResult>

        //上传图片
        fun sendPic(localFile: String?): JsonResult<in JsonRequestResult>

        //返回用户id
        val userId: String?

        //返回tokenId
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


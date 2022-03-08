package com.qipa.newboxproject.app.manager

import com.qipa.newboxproject.app.util.AppMetaDataHelper
import com.qipa.newboxproject.app.util.PreferenceManager

class OptionsHelper private constructor() {
    var defAppkey = ""
        private set
    private val defaultAppkey: Unit
        private get() {
            defAppkey = AppMetaDataHelper.instance?.getPlaceholderValue("EASEMOB_APPKEY").toString()
        }

    /**
     * 自定义配置是否可用
     * @return
     */
    val isCustomSetEnable: Boolean
        get() = PreferenceManager.instance?.isCustomSetEnable == true

    /**
     * 自定义配置是否可用
     * @param enable
     */
    fun enableCustomSet(enable: Boolean) {
        PreferenceManager.instance?.enableCustomSet(enable)
    }

    /**
     * 自定义服务器是否可用
     * @return
     */
    val isCustomServerEnable: Boolean?
        get() = PreferenceManager.instance?.isCustomServerEnable

    /**
     * 这是自定义服务器是否可用
     * @param enable
     */
    fun enableCustomServer(enable: Boolean) {
        PreferenceManager.instance?.enableCustomServer(enable)
    }
    /**
     * 获取闲置服务器
     * @return
     */
    /**
     * 设置闲置服务器
     * @param restServer
     */
    var restServer: String?
        get() = PreferenceManager.instance?.restServer
        set(restServer) {
            PreferenceManager.instance?.restServer = restServer
        }
    /**
     * 获取IM服务器
     * @return
     */
    /**
     * 设置IM服务器
     * @param imServer
     */
    var iMServer: String?
        get() = PreferenceManager.instance?.iMServer
        set(imServer) {
            PreferenceManager.instance?.iMServer = imServer
        }

    /**
     * 设置端口号
     * @param port
     */
    var iMServerPort: Int?
        get() = PreferenceManager.instance?.iMServerPort
        set(port) {
            if (port != null) {
                PreferenceManager.instance?.iMServerPort = port
            }
        }

    /**
     * 设置自定义appkey是否可用
     * @param enable
     */
    fun enableCustomAppkey(enable: Boolean) {
        PreferenceManager.instance?.enableCustomAppkey(enable)
    }

    /**
     * 获取自定义appkey是否可用
     * @return
     */
    val isCustomAppkeyEnabled: Boolean?
        get() = PreferenceManager.instance?.isCustomAppkeyEnabled
    /**
     * 获取自定义appkey
     * @return
     */
    /**
     * 设置自定义appkey
     * @param appkey
     */
    var customAppkey: String?
        get() = PreferenceManager.instance?.customAppkey
        set(appkey) {
            PreferenceManager.instance?.customAppkey = appkey
        }
    /**
     * 获取是否只使用Https
     * @return
     */
    /**
     * 设置是否只使用Https
     * @param usingHttpsOnly
     */
    var usingHttpsOnly: Boolean?
        get() = PreferenceManager.instance?.usingHttpsOnly
        set(usingHttpsOnly) {
            if (usingHttpsOnly != null) {
                PreferenceManager.instance?.usingHttpsOnly = usingHttpsOnly
            }
        }

    /**
     * 设置是否允许聊天室owner离开并删除会话记录，意味着owner再不会受到任何消息
     * @param value
     */
    fun allowChatroomOwnerLeave(value: Boolean) {
        PreferenceManager.instance?.settingAllowChatroomOwnerLeave = value
    }

    /**
     * 获取聊天室owner离开时的设置
     * @return
     */
    val isChatroomOwnerLeaveAllowed: Boolean?
        get() = PreferenceManager.instance?.settingAllowChatroomOwnerLeave
    /**
     * 获取退出(主动和被动退出)群组时是否删除聊天消息
     * @return
     */
    /**
     * 设置退出(主动和被动退出)群组时是否删除聊天消息
     * @param value
     */
    var isDeleteMessagesAsExitGroup: Boolean?
        get() = PreferenceManager.instance?.isDeleteMessagesAsExitGroup
        set(value) {
            if (value != null) {
                PreferenceManager.instance?.isDeleteMessagesAsExitGroup = value
            }
        }
    var isDeleteMessagesAsExitChatRoom: Boolean?
        get() = PreferenceManager.instance?.isDeleteMessagesAsExitChatRoom
        set(value) {
            if (value != null) {
                PreferenceManager.instance?.isDeleteMessagesAsExitChatRoom = value
            }
        }
    /**
     * 获取是否自动接受加群邀请
     * @return
     */
    /**
     * 设置是否自动接受加群邀请
     * @param value
     */
    var isAutoAcceptGroupInvitation: Boolean?
        get() = PreferenceManager.instance?.isAutoAcceptGroupInvitation
        set(value) {
            if (value != null) {
                PreferenceManager.instance?.isAutoAcceptGroupInvitation = value
            }
        }

    /**
     * 设置是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载
     * @param value
     */
    fun setTransfeFileByUser(value: Boolean) {
        PreferenceManager.instance?.setTransferFileByUser(value)
    }

    /**
     * 获取是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载
     * @return
     */
    val isSetTransferFileByUser: Boolean?
        get() = PreferenceManager.instance?.isSetTransferFileByUser

    /**
     * 是否自动下载缩略图，默认是true为自动下载
     * @param autodownload
     */
    fun setAutodownloadThumbnail(autodownload: Boolean) {
        PreferenceManager.instance?.setAudodownloadThumbnail(autodownload)
    }

    /**
     * 获取是否自动下载缩略图
     * @return
     */
    val isSetAutodownloadThumbnail: Boolean?
        get() = PreferenceManager.instance?.isSetAutodownloadThumbnail
    var isSortMessageByServerTime: Boolean?
        get() = PreferenceManager.instance?.isSortMessageByServerTime
        set(sortByServerTime) {
            if (sortByServerTime != null) {
                PreferenceManager.instance?.isSortMessageByServerTime = sortByServerTime
            }
        }

//    /**
//     * 获取服务设置
//     * @return
//     */
//    val serverSet: DemoServerSetBean
//        get() {
//            val bean = DemoServerSetBean()
//            bean.setAppkey(customAppkey)
//            bean.setCustomServerEnable(isCustomServerEnable)
//            bean.setHttpsOnly(usingHttpsOnly)
//            bean.setImServer(iMServer)
//            bean.setRestServer(restServer)
//            return bean
//        }
//
//    /**
//     * 获取默认服务设置
//     * @return
//     */
//    val defServerSet: DemoServerSetBean
//        get() {
//            val bean = DemoServerSetBean()
//            bean.setAppkey(defAppkey)
//            bean.setRestServer(defRestServer)
//            bean.setImServer(defImServer)
//            bean.setImPort(defImPort)
//            bean.setHttpsOnly(usingHttpsOnly)
//            bean.setCustomServerEnable(isCustomServerEnable)
//            return bean
//        }

    companion object {
        val defImServer : String = "msync-im1.sandbox.easemob.com"
        val defImPort : Int  = 6717
        val defRestServer : String  = "a1.sdb.easemob.com"
        var instance: OptionsHelper? = null
            get() {
                if (field == null) {
                    synchronized(OptionsHelper::class.java) {
                        if (field == null) {
                            field = OptionsHelper()
                        }
                    }
                }
                return field
            }
            private set
    }

    init {
        defaultAppkey
    }
}
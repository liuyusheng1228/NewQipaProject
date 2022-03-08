package com.qipa.newboxproject.data.model
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.hyphenate.chat.EMChatRoom
import com.hyphenate.chat.EMClient

import com.hyphenate.easeui.domain.EaseUser
import com.hyphenate.easeui.manager.EasePreferenceManager
import com.qipa.newboxproject.app.App
import com.qipa.newboxproject.app.manager.OptionsHelper
import com.qipa.newboxproject.app.util.PreferenceManager
import com.qipa.newboxproject.data.db.DemoDbHelper
import com.qipa.newboxproject.data.db.dao.AppKeyDao
import com.qipa.newboxproject.data.db.dao.EmUserDao
import com.qipa.newboxproject.data.db.entity.*
import java.util.ArrayList
import java.util.HashMap


/**
 * DemoModel主要用于SP存取及一些数据库的存取
 */
class DemoModel(ctx: Context?) {
    var dao: EmUserDao? = null
    protected var context: Context? = null
    protected var valueCache: MutableMap<Key, Any?> = HashMap()
    var chatRooms: List<EMChatRoom>? = null
    var userInfoTimeOut: Long = 0
        get() = Companion.userInfoTimeOut
        set(userInfoTimeOut) {
            if (userInfoTimeOut > 0) {
                field = userInfoTimeOut
            }
        }

    fun updateContactList(contactList: List<EaseUser?>?): Boolean {
        val userEntities: List<EmUserEntity> = EmUserEntity.parseList(contactList as List<EaseUser>?)
        val dao: EmUserDao? = context?.let { DemoDbHelper.getInstance(it)?.userDao }
        if (dao != null) {
            dao.insert(userEntities)
            return true
        }
        return false
    }

    val contactList: Map<String, EaseUser>
        get() {
            val dao: EmUserDao = context?.let { DemoDbHelper.getInstance(it)?.userDao }
                ?: return HashMap()
            val map: MutableMap<String, EaseUser> = HashMap<String, EaseUser>()
            val users: List<EaseUser> = dao.loadAllContactUsers() as List<EaseUser>
            if (users != null && !users.isEmpty()) {
                for (user in users) {
                    map[user.username] = user
                }
            }
            return map
        }
    val allUserList: Map<String, EaseUser>
        get() {
            val dao: EmUserDao = context?.let { DemoDbHelper.getInstance(it)?.userDao }
                ?: return HashMap()
            val map: MutableMap<String, EaseUser> = HashMap<String, EaseUser>()
            val users: List<EaseUser> = dao.loadAllEaseUsers() as List<EaseUser>
            if (users != null && !users.isEmpty()) {
                for (user in users) {
                    map[user.username] = user
                }
            }
            return map
        }
    val friendContactList: Map<String, EaseUser>
        get() {
            val dao: EmUserDao = context?.let { DemoDbHelper.getInstance(it)?.userDao }
                ?: return HashMap()
            val map: MutableMap<String, EaseUser> = HashMap<String, EaseUser>()
            val users: List<EaseUser> = dao.loadContacts() as List<EaseUser>
            if (users != null && !users.isEmpty()) {
                for (user in users) {
                    map[user.username] = user
                }
            }
            return map
        }

    /**
     * 判断是否是联系人
     * @param userId
     * @return
     */
    fun isContact(userId: String): Boolean {
        val contactList = friendContactList
        return contactList.keys.contains(userId)
    }

    fun saveContact(user: EaseUser?) {
        val dao: EmUserDao = context?.let { DemoDbHelper.getInstance(it)?.userDao } ?: return
        dao.insert(user?.let { EmUserEntity.parseParent(it) })
    }

    val appKeys: List<AppKeyEntity?>?
        get() {
            val dao: AppKeyDao = context?.let { DemoDbHelper.getInstance(it)?.appKeyDao }
                ?: return ArrayList()
            val defAppkey: String? =
                OptionsHelper.instance?.defAppkey
            val appKey = EMClient.getInstance().options.appKey
            if (!TextUtils.equals(defAppkey, appKey)) {
                val appKeys: List<AppKeyEntity> = dao.queryKey(appKey) as List<AppKeyEntity>
                if (appKeys == null || appKeys.isEmpty()) {
                    dao.insert(AppKeyEntity(appKey))
                }
            }
            return dao.loadAllAppKeys()
        }

    /**
     * 保存appKey
     * @param appKey
     */
    fun saveAppKey(appKey: String?) {
        val dao: AppKeyDao = context?.let { DemoDbHelper.getInstance(it)?.appKeyDao } ?: return
        val entity = appKey?.let { AppKeyEntity(it) }
        dao.insert(entity)
    }

    fun deleteAppKey(appKey: String?) {
        val dao: AppKeyDao = context?.let { DemoDbHelper.getInstance(it)?.appKeyDao } ?: return
        dao.deleteAppKey(appKey)
    }

    /**
     * get DemoDbHelper
     * @return
     */
    val dbHelper: DemoDbHelper?
        get() = DemoDbHelper.getInstance(App.instance.applicationContext)

    /**
     * 向数据库中插入数据
     * @param object
     */
    fun insert(`object`: Any?) {
        val dbHelper: DemoDbHelper? = dbHelper
        if (`object` is InviteMessage) {
            if (dbHelper?.inviteMessageDao != null) {
                dbHelper?.inviteMessageDao!!.insert(`object` as InviteMessage?)
            }
        } else if (`object` is MsgTypeManageEntity) {
            if (dbHelper?.msgTypeManageDao != null) {
                dbHelper?.msgTypeManageDao!!.insert(`object` as MsgTypeManageEntity?)
            }
        } else if (`object` is EmUserEntity) {
            if (dbHelper?.userDao != null) {
                dbHelper?.userDao!!.insert(`object` as EmUserEntity?)
            }
        }else if (`object` is DownloadApkEntity) {
            if (dbHelper?.apkDownloadDao != null) {
                dbHelper?.apkDownloadDao!!.insert(`object` as DownloadApkEntity?)
            }
        }
    }

    /**
     * update
     * @param object
     */
    fun update(`object`: Any?) {
        val dbHelper: DemoDbHelper? = dbHelper
        if (`object` is InviteMessage) {
            if (dbHelper?.inviteMessageDao != null) {
                dbHelper?.inviteMessageDao!!.update(`object` as InviteMessage?)
            }
        } else if (`object` is MsgTypeManageEntity) {
            if (dbHelper?.msgTypeManageDao != null) {
                dbHelper?.msgTypeManageDao!!.update(`object` as MsgTypeManageEntity?)
            }
        } else if (`object` is EmUserEntity) {
            if (dbHelper?.userDao != null) {
                dbHelper?.userDao!!.insert(`object` as EmUserEntity?)
            }
        }
    }

    /**
     * 查找有关用户用户属性过期的用户ID
     *
     */
    fun selectTimeOutUsers(): List<String>? {
        val dbHelper: DemoDbHelper? = dbHelper
        var users: List<String>? = null
        if (dbHelper?.userDao != null) {
            users = dbHelper?.userDao!!
                .loadTimeOutEaseUsers(Companion.userInfoTimeOut, System.currentTimeMillis()) as List<String>?
        }
        return users
    }

    /**
     * save current username
     * @param username
     */
    fun setCurrentUserName(username: String?) {
        PreferenceManager.instance?.currentUsername = username
    }

    val currentUsername: String?
        get() = PreferenceManager.instance?.currentUsername

    /**
     * 保存是否删除联系人的状态
     * @param username
     * @param isDelete
     */
    fun deleteUsername(username: String?, isDelete: Boolean) {
        val sp = context!!.getSharedPreferences("save_delete_username_status", Context.MODE_PRIVATE)
        val edit = sp.edit()
        edit.putBoolean(username, isDelete)
        edit.commit()
    }

    /**
     * 查看联系人是否删除
     * @param username
     * @return
     */
    fun isDeleteUsername(username: String?): Boolean {
        val sp = context!!.getSharedPreferences("save_delete_username_status", Context.MODE_PRIVATE)
        return sp.getBoolean(username, false)
    }

    /**
     * 保存当前用户密码
     * 此处保存密码是为了查看多端设备登录是，调用接口不再输入用户名及密码，实际开发中，不可在本地保存密码！
     * 注：实际开发中不可进行此操作！！！
     * @param pwd
     */
    var currentUserPwd: String?
        get() = PreferenceManager.instance?.currentUserPwd
        set(pwd) {
            PreferenceManager.instance?.currentUserPwd = pwd
        }

    /**
     * 设置昵称
     * @param nickname
     */
    var currentUserNick: String?
        get() = PreferenceManager.instance?.currentUserNick
        set(nickname) {
            PreferenceManager.instance?.currentUserNick = nickname
        }

    /**
     * 设置头像
     * @param avatar
     */
    private var currentUserAvatar: String?
        private get() = PreferenceManager.instance?.currentUserAvatar
        private set(avatar) {
            PreferenceManager.instance?.currentUserAvatar = avatar
        }
    var settingMsgNotification: Boolean
        get() {
            var `val` = valueCache[Key.VibrateAndPlayToneOn]
            if (`val` == null) {
                `val` = PreferenceManager.instance?.settingMsgNotification
                valueCache[Key.VibrateAndPlayToneOn] = `val`
            }
            return (`val` ?: true) as Boolean
        }
        set(paramBoolean) {
            PreferenceManager.instance?.settingMsgNotification = paramBoolean
            valueCache[Key.VibrateAndPlayToneOn] = paramBoolean
        }
    var settingMsgSound: Boolean
        get() {
            var `val` = valueCache[Key.PlayToneOn]
            if (`val` == null) {
                `val` = PreferenceManager.instance?.settingMsgSound
                valueCache[Key.PlayToneOn] = `val`
            }
            return (`val` ?: true) as Boolean
        }
        set(paramBoolean) {
            PreferenceManager.instance?.settingMsgSound=paramBoolean
            valueCache[Key.PlayToneOn] = paramBoolean
        }
    var settingMsgVibrate: Boolean
        get() {
            var `val` = valueCache[Key.VibrateOn]
            if (`val` == null) {
                `val` = PreferenceManager.instance?.settingMsgVibrate
                valueCache[Key.VibrateOn] = `val`
            }
            return (`val` ?: true) as Boolean
        }
        set(paramBoolean) {
            PreferenceManager.instance?.settingMsgVibrate=paramBoolean
            valueCache[Key.VibrateOn] = paramBoolean
        }
    var settingMsgSpeaker: Boolean
        get() {
            var `val` = valueCache[Key.SpakerOn]
            if (`val` == null) {
                `val` = PreferenceManager.instance?.settingMsgSpeaker
                valueCache[Key.SpakerOn] = `val`
            }
            return (`val` ?: true) as Boolean
        }
        set(paramBoolean) {
            PreferenceManager.instance?.settingMsgSpeaker=paramBoolean
            valueCache[Key.SpakerOn] = paramBoolean
        }

    //        if(dao == null){
//            dao = new UserDao(context);
//        }
//
//        List<String> list = new ArrayList<String>();
//        list.addAll(groups);
//        for(int i = 0; i < list.size(); i++){
//            if(EaseAtMessageHelper.get().getAtMeGroups().contains(list.get(i))){
//                list.remove(i);
//                i--;
//            }
//        }
//
//        dao.setDisabledGroups(list);
//        valueCache.put(Key.DisabledGroups, list);
    //        if(dao == null){
//            dao = new UserDao(context);
//        }
//
//        if(val == null){
//            val = dao.getDisabledGroups();
//            valueCache.put(Key.DisabledGroups, val);
//        }
    var disabledGroups: List<String>? = null
        get() {
            val `val` = valueCache[Key.DisabledGroups]

            //        if(dao == null){
            //            dao = new UserDao(context);
            //        }
            //
            //        if(val == null){
            //            val = dao.getDisabledGroups();
            //            valueCache.put(Key.DisabledGroups, val);
            //        }
            return `val` as List<String>?
        }

    fun setDisabledIds(ids: List<String?>?) {
//        if(dao == null){
//            dao = new UserDao(context);
//        }
//
//        dao.setDisabledIds(ids);
//        valueCache.put(Key.DisabledIds, ids);
    }

    fun getDisabledIds(): List<String>? {
        val `val` = valueCache[Key.DisabledIds]

//        if(dao == null){
//            dao = new UserDao(context);
//        }
//
//        if(val == null){
//            val = dao.getDisabledIds();
//            valueCache.put(Key.DisabledIds, val);
//        }
        return `val` as List<String>?
    }

    fun setGroupsSynced(synced: Boolean) {
        PreferenceManager.instance?.isGroupsSynced = synced
    }

    fun isGroupsSynced(): Boolean? {
        return PreferenceManager.instance?.isGroupsSynced
    }

    fun setContactSynced(synced: Boolean) {
        PreferenceManager.instance?.isContactSynced = synced
    }

    fun isContactSynced(): Boolean? {
        return PreferenceManager.instance?.isContactSynced
    }

    fun setBlacklistSynced(synced: Boolean) {
        PreferenceManager.instance?.setBlacklistSynced(synced)
    }

    fun isBacklistSynced(): Boolean? {
        return PreferenceManager.instance?.isBacklistSynced
    }

    fun setAdaptiveVideoEncode(value: Boolean) {
        PreferenceManager.instance?.isAdaptiveVideoEncode = value
    }

    fun isAdaptiveVideoEncode(): Boolean? {
        return PreferenceManager.instance?.isAdaptiveVideoEncode
    }

    fun setPushCall(value: Boolean) {
        PreferenceManager.instance?.isPushCall = value
    }

    fun isPushCall(): Boolean? {
        return PreferenceManager.instance?.isPushCall
    }

    fun isMsgRoaming(): Boolean? {
        return PreferenceManager.instance?.isMsgRoaming
    }

    fun setMsgRoaming(roaming: Boolean) {
        PreferenceManager.instance?.isMsgRoaming = roaming
    }

    fun isShowMsgTyping(): Boolean? {
        return PreferenceManager.instance?.isShowMsgTyping
    }

    fun showMsgTyping(show: Boolean) {
        PreferenceManager.instance?.showMsgTyping(show)
    }

//    /**
//     * 获取默认的服务器设置
//     * @return
//     */
//    fun getDefServerSet(): DemoServerSetBean {
//        return OptionsHelper.instance.getDefServerSet()
//    }

    /**
     * 设置是否使用google推送
     * @param useFCM
     */
    fun setUseFCM(useFCM: Boolean) {
        PreferenceManager.instance?.isUseFCM = useFCM
    }

    /**
     * 获取设置，是否设置google推送
     * @return
     */
    fun isUseFCM(): Boolean? {
        return PreferenceManager.instance?.isUseFCM
    }

    /**
     * 自定义服务器是否可用
     * @return
     */
    fun isCustomServerEnable(): Boolean? {
        return OptionsHelper.instance?.isCustomServerEnable
    }

    /**
     * 这是自定义服务器是否可用
     * @param enable
     */
    fun enableCustomServer(enable: Boolean) {
        OptionsHelper.instance?.enableCustomServer(enable)
    }

    /**
     * 自定义配置是否可用
     * @return
     */
    fun isCustomSetEnable(): Boolean? {
        return OptionsHelper.instance?.isCustomSetEnable
    }

    /**
     * 自定义配置是否可用
     * @param enable
     */
    fun enableCustomSet(enable: Boolean) {
        OptionsHelper.instance?.enableCustomSet(enable)
    }

    /**
     * 设置闲置服务器
     * @param restServer
     */
    fun setRestServer(restServer: String?) {
        OptionsHelper.instance?.restServer = restServer
    }

    /**
     * 获取闲置服务器
     * @return
     */
    fun getRestServer(): String? {
        return OptionsHelper.instance?.restServer
    }

    /**
     * 设置IM服务器
     * @param imServer
     */
    fun setIMServer(imServer: String?) {
        OptionsHelper.instance?.iMServer = imServer
    }

    /**
     * 获取IM服务器
     * @return
     */
    fun getIMServer(): String? {
        return OptionsHelper.instance?.iMServer
    }

    /**
     * 设置端口号
     * @param port
     */
    fun setIMServerPort(port: Int) {
        OptionsHelper.instance?.iMServerPort = port
    }

    fun getIMServerPort(): Int? {
        return OptionsHelper.instance?.iMServerPort
    }

    /**
     * 设置自定义appkey是否可用
     * @param enable
     */
    fun enableCustomAppkey(enable: Boolean) {
        OptionsHelper.instance?.enableCustomAppkey(enable)
    }

    /**
     * 获取自定义appkey是否可用
     * @return
     */
    fun isCustomAppkeyEnabled(): Boolean? {
        return OptionsHelper.instance?.isCustomAppkeyEnabled
    }

    /**
     * 设置自定义appkey
     * @param appkey
     */
    fun setCustomAppkey(appkey: String?) {
        OptionsHelper.instance?.customAppkey = appkey
    }

    /**
     * 获取自定义appkey
     * @return
     */
    fun getCutomAppkey(): String? {
        return OptionsHelper.instance?.customAppkey
    }

    /**
     * 设置是否允许聊天室owner离开并删除会话记录，意味着owner再不会受到任何消息
     * @param value
     */
    fun allowChatroomOwnerLeave(value: Boolean) {
        OptionsHelper.instance?.allowChatroomOwnerLeave(value)
    }

    /**
     * 获取聊天室owner离开时的设置
     * @return
     */
    fun isChatroomOwnerLeaveAllowed(): Boolean? {
        return OptionsHelper.instance?.isChatroomOwnerLeaveAllowed
    }

    /**
     * 设置退出(主动和被动退出)群组时是否删除聊天消息
     * @param value
     */
    fun setDeleteMessagesAsExitGroup(value: Boolean) {
        OptionsHelper.instance?.isDeleteMessagesAsExitGroup = value
    }

    /**
     * 获取退出(主动和被动退出)群组时是否删除聊天消息
     * @return
     */
    fun isDeleteMessagesAsExitGroup(): Boolean? {
        return OptionsHelper.instance?.isDeleteMessagesAsExitGroup
    }

    /**
     * 设置退出（主动和被动）聊天室时是否删除聊天信息
     * @param value
     */
    fun setDeleteMessagesAsExitChatRoom(value: Boolean) {
        OptionsHelper.instance?.isDeleteMessagesAsExitChatRoom = value
    }

    /**
     * 获取退出(主动和被动退出)聊天室时是否删除聊天消息
     * @return
     */
    fun isDeleteMessagesAsExitChatRoom(): Boolean? {
        return OptionsHelper.instance?.isDeleteMessagesAsExitChatRoom
    }

    /**
     * 设置是否自动接受加群邀请
     * @param value
     */
    fun setAutoAcceptGroupInvitation(value: Boolean) {
        OptionsHelper.instance?.isAutoAcceptGroupInvitation = value
    }

    /**
     * 获取是否自动接受加群邀请
     * @return
     */
    fun isAutoAcceptGroupInvitation(): Boolean? {
        return OptionsHelper.instance?.isAutoAcceptGroupInvitation
    }

    /**
     * 设置是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载
     * @param value
     */
    fun setTransfeFileByUser(value: Boolean) {
        OptionsHelper.instance?.setTransfeFileByUser(value)
    }

    /**
     * 获取是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载
     * @return
     */
    fun isSetTransferFileByUser(): Boolean? {
        return OptionsHelper.instance?.isSetTransferFileByUser
    }

    /**
     * 是否自动下载缩略图，默认是true为自动下载
     * @param autodownload
     */
    fun setAutodownloadThumbnail(autodownload: Boolean) {
        OptionsHelper.instance?.setAutodownloadThumbnail(autodownload)
    }

    /**
     * 获取是否自动下载缩略图
     * @return
     */
    fun isSetAutodownloadThumbnail(): Boolean? {
        return OptionsHelper.instance?.isSetAutodownloadThumbnail
    }

    /**
     * 设置是否只使用Https
     * @param usingHttpsOnly
     */
    fun setUsingHttpsOnly(usingHttpsOnly: Boolean) {
        OptionsHelper.instance?.usingHttpsOnly = usingHttpsOnly
    }

    /**
     * 获取是否只使用Https
     * @return
     */
    fun getUsingHttpsOnly(): Boolean? {
        return OptionsHelper.instance?.usingHttpsOnly
    }

    fun setSortMessageByServerTime(sortByServerTime: Boolean) {
        OptionsHelper.instance?.isSortMessageByServerTime=sortByServerTime
    }

    fun isSortMessageByServerTime(): Boolean? {
        return OptionsHelper.instance?.isSortMessageByServerTime
    }

    /**
     * 是否允许token登录
     * @param isChecked
     */
    fun setEnableTokenLogin(isChecked: Boolean) {
        PreferenceManager.instance?.isEnableTokenLogin = isChecked
    }

    fun isEnableTokenLogin(): Boolean? {
        return PreferenceManager.instance?.isEnableTokenLogin
    }

    /**
     * 保存未发送的文本消息内容
     * @param toChatUsername
     * @param content
     */
    fun saveUnSendMsg(toChatUsername: String?, content: String?) {
        EasePreferenceManager.getInstance().saveUnSendMsgInfo(toChatUsername, content)
    }

    fun getUnSendMsg(toChatUsername: String?): String {
        return EasePreferenceManager.getInstance().getUnSendMsgInfo(toChatUsername)
    }

    /**
     * 检查是否是第一次安装登录
     * 默认值是true, 需要在用api拉取完会话列表后，就其置为false.
     * @return
     */
    fun isFirstInstall(): Boolean {
        val preferences: SharedPreferences = App.instance
            .getSharedPreferences("first_install", Context.MODE_PRIVATE)
        return preferences.getBoolean("is_first_install", true)
    }

    /**
     * 将状态置为非第一次安装，在调用获取会话列表的api后调用
     * 并将会话列表是否来自服务器置为true
     */
    fun makeNotFirstInstall() {
        val preferences: SharedPreferences = App.instance
            .getSharedPreferences("first_install", Context.MODE_PRIVATE)
        preferences.edit().putBoolean("is_first_install", false).apply()
        preferences.edit().putBoolean("is_conversation_come_from_server", true).apply()
    }

    /**
     * 检查会话列表是否从服务器返回数据
     * @return
     */
    fun isConComeFromServer(): Boolean {
        val preferences: SharedPreferences =App.instance
            .getSharedPreferences("first_install", Context.MODE_PRIVATE)
        return preferences.getBoolean("is_conversation_come_from_server", false)
    }

    /**
     * 将会话列表从服务器取数据的状态置为false，即后面应该采用本地数据库数据。
     */
    fun modifyConComeFromStatus() {
        val preferences: SharedPreferences = App.instance
            .getSharedPreferences("first_install", Context.MODE_PRIVATE)
        preferences.edit().putBoolean("is_conversation_come_from_server", false).apply()
    }

    enum class Key {
        VibrateAndPlayToneOn, VibrateOn, PlayToneOn, SpakerOn, DisabledGroups, DisabledIds
    }

    companion object {
        //用户属性数据过期时间设置
        var userInfoTimeOut = (7 * 24 * 60 * 60 * 1000).toLong()
    }

    init {
        context = ctx
        context?.let { PreferenceManager.init(it) }
    }
}

package com.qipa.newboxproject.app.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import java.lang.RuntimeException

class PreferenceManager @SuppressLint("CommitPrefEdits") private constructor(cxt: Context) {
    private val SHARED_KEY_SETTING_NOTIFICATION = "shared_key_setting_notification"
    private val SHARED_KEY_SETTING_SOUND = "shared_key_setting_sound"
    private val SHARED_KEY_SETTING_VIBRATE = "shared_key_setting_vibrate"
    private val SHARED_KEY_SETTING_SPEAKER = "shared_key_setting_speaker"
    var settingMsgNotification: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_SETTING_NOTIFICATION, true)
        set(paramBoolean) {
            editor.putBoolean(SHARED_KEY_SETTING_NOTIFICATION, paramBoolean)
            editor.apply()
        }
    var settingMsgSound: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_SETTING_SOUND, true)
        set(paramBoolean) {
            editor.putBoolean(SHARED_KEY_SETTING_SOUND, paramBoolean)
            editor.apply()
        }
    var settingMsgVibrate: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_SETTING_VIBRATE, true)
        set(paramBoolean) {
            editor.putBoolean(SHARED_KEY_SETTING_VIBRATE, paramBoolean)
            editor.apply()
        }
    var settingMsgSpeaker: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_SETTING_SPEAKER, true)
        set(paramBoolean) {
            editor.putBoolean(SHARED_KEY_SETTING_SPEAKER, paramBoolean)
            editor.apply()
        }
    var settingAllowChatroomOwnerLeave: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_SETTING_CHATROOM_OWNER_LEAVE, true)
        set(value) {
            editor.putBoolean(SHARED_KEY_SETTING_CHATROOM_OWNER_LEAVE, value)
            editor.apply()
        }
    var isDeleteMessagesAsExitGroup: Boolean
        get() = mSharedPreferences.getBoolean(
            SHARED_KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_GROUP,
            true
        )
        set(value) {
            editor.putBoolean(SHARED_KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_GROUP, value)
            editor.apply()
        }
    var isDeleteMessagesAsExitChatRoom: Boolean
        get() = mSharedPreferences.getBoolean(
            SHARED_KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_CHAT_ROOM, true
        )
        set(value) {
            editor.putBoolean(SHARED_KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_CHAT_ROOM, value)
            editor.apply()
        }

    fun setTransferFileByUser(value: Boolean) {
        editor.putBoolean(SHARED_KEY_SETTING_TRANSFER_FILE_BY_USER, value)
        editor.apply()
    }

    val isSetTransferFileByUser: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_SETTING_TRANSFER_FILE_BY_USER, true)

    fun setAudodownloadThumbnail(autodownload: Boolean) {
        editor.putBoolean(SHARED_KEY_SETTING_AUTODOWNLOAD_THUMBNAIL, autodownload)
        editor.apply()
    }

    val isSetAutodownloadThumbnail: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_SETTING_AUTODOWNLOAD_THUMBNAIL, true)
    var isAutoAcceptGroupInvitation: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_SETTING_AUTO_ACCEPT_GROUP_INVITATION, true)
        set(value) {
            editor.putBoolean(SHARED_KEY_SETTING_AUTO_ACCEPT_GROUP_INVITATION, value)
            editor.commit()
        }
    var isAdaptiveVideoEncode: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_SETTING_ADAPTIVE_VIDEO_ENCODE, false)
        set(value) {
            editor.putBoolean(SHARED_KEY_SETTING_ADAPTIVE_VIDEO_ENCODE, value)
            editor.apply()
        }
    var isPushCall: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_SETTING_OFFLINE_PUSH_CALL, true)
        set(value) {
            editor.putBoolean(SHARED_KEY_SETTING_OFFLINE_PUSH_CALL, value)
            editor.apply()
        }
    var isRecordOnServer: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_SETTING_RECORD_ON_SERVER, false)
        set(value) {
            editor.putBoolean(SHARED_KEY_SETTING_RECORD_ON_SERVER, value)
            editor.apply()
        }
    var isMergeStream: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_SETTING_MERGE_STREAM, false)
        set(value) {
            editor.putBoolean(SHARED_KEY_SETTING_MERGE_STREAM, value)
            editor.apply()
        }
    var isGroupsSynced: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_SETTING_GROUPS_SYNCED, false)
        set(synced) {
            editor.putBoolean(SHARED_KEY_SETTING_GROUPS_SYNCED, synced)
            editor.apply()
        }
    var isContactSynced: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_SETTING_CONTACT_SYNCED, false)
        set(synced) {
            editor.putBoolean(SHARED_KEY_SETTING_CONTACT_SYNCED, synced)
            editor.apply()
        }

    fun setBlacklistSynced(synced: Boolean) {
        editor.putBoolean(SHARED_KEY_SETTING_BALCKLIST_SYNCED, synced)
        editor.apply()
    }

    val isBacklistSynced: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_SETTING_BALCKLIST_SYNCED, false)
    var currentUserNick: String?
        get() = mSharedPreferences.getString(SHARED_KEY_CURRENTUSER_NICK, null)
        set(nick) {
            editor.putString(SHARED_KEY_CURRENTUSER_NICK, nick)
            editor.apply()
        }
    var currentUserAvatar: String?
        get() = mSharedPreferences.getString(SHARED_KEY_CURRENTUSER_AVATAR, null)
        set(avatar) {
            editor.putString(SHARED_KEY_CURRENTUSER_AVATAR, avatar)
            editor.apply()
        }

    fun setCurrentUserName(username: String?) {
        editor.putString(SHARED_KEY_CURRENTUSER_USERNAME, username)
        editor.apply()
    }

    var currentUsername: String? = null
        get() = mSharedPreferences.getString(SHARED_KEY_CURRENTUSER_USERNAME, null)
    var currentUserPwd: String?
        get() = mSharedPreferences.getString(SHARED_KEY_CURRENTUSER_USER_PASSWORD, null)
        set(pwd) {
            editor.putString(SHARED_KEY_CURRENTUSER_USER_PASSWORD, pwd)
            editor.apply()
        }
    var restServer: String?
        get() = mSharedPreferences.getString(SHARED_KEY_REST_SERVER, null)
        set(restServer) {
            editor.putString(SHARED_KEY_REST_SERVER, restServer).commit()
            editor.commit()
        }
    var iMServer: String?
        get() = mSharedPreferences.getString(SHARED_KEY_IM_SERVER, null)
        set(imServer) {
            editor.putString(SHARED_KEY_IM_SERVER, imServer)
            editor.commit()
        }

    fun enableCustomServer(enable: Boolean) {
        editor.putBoolean(SHARED_KEY_ENABLE_CUSTOM_SERVER, enable)
        editor.commit()
    }

    val isCustomServerEnable: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_ENABLE_CUSTOM_SERVER, false)
    val isCustomSetEnable: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_ENABLE_CUSTOM_SET, false)

    fun enableCustomSet(enable: Boolean) {
        editor.putBoolean(SHARED_KEY_ENABLE_CUSTOM_SET, enable)
        editor.commit()
    }

    fun enableCustomAppkey(enable: Boolean) {
        editor.putBoolean(SHARED_KEY_ENABLE_CUSTOM_APPKEY, enable)
        editor.commit()
    }

    val isCustomAppkeyEnabled: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_ENABLE_CUSTOM_APPKEY, true)
    var customAppkey: String?
        get() = mSharedPreferences.getString(SHARED_KEY_CUSTOM_APPKEY, "")
        set(appkey) {
            editor.putString(SHARED_KEY_CUSTOM_APPKEY, appkey)
            editor.commit()
        }

    fun removeCurrentUserInfo() {
        editor.remove(SHARED_KEY_CURRENTUSER_NICK)
        editor.remove(SHARED_KEY_CURRENTUSER_AVATAR)
        editor.apply()
    }

    var isMsgRoaming: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_MSG_ROAMING, false)
        set(isRoaming) {
            editor.putBoolean(SHARED_KEY_MSG_ROAMING, isRoaming)
            editor.apply()
        }
    val isShowMsgTyping: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_SHOW_MSG_TYPING, false)

    fun showMsgTyping(show: Boolean) {
        editor.putBoolean(SHARED_KEY_SHOW_MSG_TYPING, show)
        editor.apply()
    }
    /**
     * 获取是否是自动登录
     * @return
     */
    /**
     * 设置是否自动登录,只有登录成功后，此值才能设置为true
     * @param autoLogin
     */
    var autoLogin: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_AUTO_LOGIN, false)
        set(autoLogin) {
            editor.putBoolean(SHARED_KEY_AUTO_LOGIN, autoLogin)
            editor.commit()
        }
    /**
     * get if using Https only
     * @return
     */
    /**
     * using Https only
     * @param usingHttpsOnly
     */
    var usingHttpsOnly: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_HTTPS_ONLY, false)
        set(usingHttpsOnly) {
            editor.putBoolean(SHARED_KEY_HTTPS_ONLY, usingHttpsOnly)
            editor.commit()
        }
    /**
     * ----------------------------------------- Call Option -----------------------------------------
     */
    /**
     * Min Video kbps
     * if no value was set, return -1
     * @return
     */
    var callMinVideoKbps: Int
        get() = mSharedPreferences.getInt(SHARED_KEY_CALL_MIN_VIDEO_KBPS, -1)
        set(minBitRate) {
            editor.putInt(SHARED_KEY_CALL_MIN_VIDEO_KBPS, minBitRate)
            editor.apply()
        }

    /**
     * Max Video kbps
     * if no value was set, return -1
     * @return
     */
    var callMaxVideoKbps: Int
        get() = mSharedPreferences.getInt(SHARED_KEY_CALL_MAX_VIDEO_KBPS, -1)
        set(maxBitRate) {
            editor.putInt(SHARED_KEY_CALL_MAX_VIDEO_KBPS, maxBitRate)
            editor.apply()
        }

    /**
     * Max frame rate
     * if no value was set, return -1
     * @return
     */
    var callMaxFrameRate: Int
        get() = mSharedPreferences.getInt(SHARED_KEY_CALL_MAX_FRAME_RATE, -1)
        set(maxFrameRate) {
            editor.putInt(SHARED_KEY_CALL_MAX_FRAME_RATE, maxFrameRate)
            editor.apply()
        }

    /**
     * audio sample rate
     * if no value was set, return -1
     * @return
     */
    var callAudioSampleRate: Int
        get() = mSharedPreferences.getInt(SHARED_KEY_CALL_AUDIO_SAMPLE_RATE, -1)
        set(audioSampleRate) {
            editor.putInt(SHARED_KEY_CALL_AUDIO_SAMPLE_RATE, audioSampleRate)
            editor.apply()
        }

    /**
     * back camera resolution
     * format: 320x240
     * if no value was set, return ""
     */
    var callBackCameraResolution: String?
        get() = mSharedPreferences.getString(SHARED_KEY_CALL_BACK_CAMERA_RESOLUTION, "")
        set(resolution) {
            editor.putString(SHARED_KEY_CALL_BACK_CAMERA_RESOLUTION, resolution)
            editor.apply()
        }

    /**
     * front camera resolution
     * format: 320x240
     * if no value was set, return ""
     */
    var callFrontCameraResolution: String?
        get() = mSharedPreferences.getString(SHARED_KEY_CALL_FRONT_CAMERA_RESOLUTION, "")
        set(resolution) {
            editor.putString(SHARED_KEY_CALL_FRONT_CAMERA_RESOLUTION, resolution)
            editor.apply()
        }

    /**
     * fixed video sample rate
     * if no value was set, return false
     * @return
     */
    var isCallFixedVideoResolution: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_CALL_FIX_SAMPLE_RATE, false)
        set(enable) {
            editor.putBoolean(SHARED_KEY_CALL_FIX_SAMPLE_RATE, enable)
            editor.apply()
        }
    var isExternalAudioInputResolution: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_EXTERNAL_INPUT_AUDIO_RESOLUTION, false)
        set(enable) {
            editor.putBoolean(SHARED_KEY_EXTERNAL_INPUT_AUDIO_RESOLUTION, enable)
            editor.apply()
        }
    var isWatermarkResolution: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_WATER_MARK_RESOLUTION, false)
        set(enable) {
            editor.putBoolean(SHARED_KEY_WATER_MARK_RESOLUTION, enable)
            editor.apply()
        }
    var isUseFCM: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_PUSH_USE_FCM, false)
        set(useFCM) {
            editor.putBoolean(SHARED_KEY_PUSH_USE_FCM, useFCM)
            editor.apply()
        }

    /**
     * set the server port
     * @param port
     */
    var iMServerPort: Int
        get() = mSharedPreferences.getInt(SHARED_KEY_IM_SERVER_PORT, 0)
        set(port) {
            editor.putInt(SHARED_KEY_IM_SERVER_PORT, port)
        }
    var isSortMessageByServerTime: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_SORT_MESSAGE_BY_SERVER_TIME, true)
        set(sortByServerTime) {
            editor.putBoolean(SHARED_KEY_SORT_MESSAGE_BY_SERVER_TIME, sortByServerTime)
            editor.apply()
        }

    /**
     * 是否允许token登录
     * @param isChecked
     */
    var isEnableTokenLogin: Boolean
        get() = mSharedPreferences.getBoolean(SHARED_KEY_ENABLE_TOKEN_LOGIN, false)
        set(isChecked) {
            editor.putBoolean(SHARED_KEY_ENABLE_TOKEN_LOGIN, isChecked)
            editor.apply()
        }

    companion object {
        /**
         * name of preference
         */
        const val PREFERENCE_NAME = "saveInfo"
        private lateinit var mSharedPreferences: SharedPreferences
        private var mPreferencemManager: PreferenceManager? = null
        private lateinit var editor: SharedPreferences.Editor
        private const val SHARED_KEY_SETTING_CHATROOM_OWNER_LEAVE =
            "shared_key_setting_chatroom_owner_leave"
        private const val SHARED_KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_GROUP =
            "shared_key_setting_delete_messages_when_exit_group"
        private const val SHARED_KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_CHAT_ROOM =
            "shared_key_setting_delete_messages_when_exit_chat_room"
        private const val SHARED_KEY_SETTING_TRANSFER_FILE_BY_USER =
            "shared_key_setting_transfer_file_by_user"
        private const val SHARED_KEY_SETTING_AUTODOWNLOAD_THUMBNAIL =
            "shared_key_setting_autodownload_thumbnail"
        private const val SHARED_KEY_SETTING_AUTO_ACCEPT_GROUP_INVITATION =
            "shared_key_setting_auto_accept_group_invitation"
        private const val SHARED_KEY_SETTING_ADAPTIVE_VIDEO_ENCODE =
            "shared_key_setting_adaptive_video_encode"
        private const val SHARED_KEY_SETTING_OFFLINE_PUSH_CALL =
            "shared_key_setting_offline_push_call"
        private const val SHARED_KEY_SETTING_RECORD_ON_SERVER =
            "shared_key_setting_record_on_server"
        private const val SHARED_KEY_SETTING_MERGE_STREAM = "shared_key_setting_merge_stream"
        private const val SHARED_KEY_SETTING_OFFLINE_LARGE_CONFERENCE_MODE =
            "shared_key_setting_offline_large_conference_mode"
        private const val SHARED_KEY_SETTING_GROUPS_SYNCED = "SHARED_KEY_SETTING_GROUPS_SYNCED"
        private const val SHARED_KEY_SETTING_CONTACT_SYNCED = "SHARED_KEY_SETTING_CONTACT_SYNCED"
        private const val SHARED_KEY_SETTING_BALCKLIST_SYNCED =
            "SHARED_KEY_SETTING_BALCKLIST_SYNCED"
        private const val SHARED_KEY_CURRENTUSER_USERNAME = "SHARED_KEY_CURRENTUSER_USERNAME"
        private const val SHARED_KEY_CURRENTUSER_USER_PASSWORD =
            "SHARED_KEY_CURRENTUSER_USER_PASSWORD"
        private const val SHARED_KEY_CURRENTUSER_NICK = "SHARED_KEY_CURRENTUSER_NICK"
        private const val SHARED_KEY_CURRENTUSER_AVATAR = "SHARED_KEY_CURRENTUSER_AVATAR"
        private const val SHARED_KEY_REST_SERVER = "SHARED_KEY_REST_SERVER"
        private const val SHARED_KEY_IM_SERVER = "SHARED_KEY_IM_SERVER"
        private const val SHARED_KEY_IM_SERVER_PORT = "SHARED_KEY_IM_SERVER_PORT"
        private const val SHARED_KEY_ENABLE_CUSTOM_SERVER = "SHARED_KEY_ENABLE_CUSTOM_SERVER"
        private const val SHARED_KEY_ENABLE_CUSTOM_SET = "SHARED_KEY_ENABLE_CUSTOM_SET"
        private const val SHARED_KEY_ENABLE_CUSTOM_APPKEY = "SHARED_KEY_ENABLE_CUSTOM_APPKEY"
        private const val SHARED_KEY_CUSTOM_APPKEY = "SHARED_KEY_CUSTOM_APPKEY"
        private const val SHARED_KEY_MSG_ROAMING = "SHARED_KEY_MSG_ROAMING"
        private const val SHARED_KEY_SHOW_MSG_TYPING = "SHARED_KEY_SHOW_MSG_TYPING"
        private const val SHARED_KEY_CALL_MIN_VIDEO_KBPS = "SHARED_KEY_CALL_MIN_VIDEO_KBPS"
        private const val SHARED_KEY_CALL_MAX_VIDEO_KBPS = "SHARED_KEY_CALL_Max_VIDEO_KBPS"
        private const val SHARED_KEY_CALL_MAX_FRAME_RATE = "SHARED_KEY_CALL_MAX_FRAME_RATE"
        private const val SHARED_KEY_CALL_AUDIO_SAMPLE_RATE = "SHARED_KEY_CALL_AUDIO_SAMPLE_RATE"
        private const val SHARED_KEY_CALL_BACK_CAMERA_RESOLUTION =
            "SHARED_KEY_CALL_BACK_CAMERA_RESOLUTION"
        private const val SHARED_KEY_CALL_FRONT_CAMERA_RESOLUTION =
            "SHARED_KEY_FRONT_CAMERA_RESOLUTIOIN"
        private const val SHARED_KEY_CALL_FIX_SAMPLE_RATE = "SHARED_KEY_CALL_FIX_SAMPLE_RATE"
        private const val SHARED_KEY_EXTERNAL_INPUT_AUDIO_RESOLUTION =
            "SHARED_KEY_EXTERNAL_INPUT_AUDIO_RESOLUTION"
        private const val SHARED_KEY_WATER_MARK_RESOLUTION = "SHARED_KEY_WATER_MARK_RESOLUTION"
        private const val SHARED_KEY_PUSH_USE_FCM = "shared_key_push_use_fcm"
        private const val SHARED_KEY_AUTO_LOGIN = "shared_key_auto_login"
        private const val SHARED_KEY_HTTPS_ONLY = "shared_key_https_only"
        private const val SHARED_KEY_SORT_MESSAGE_BY_SERVER_TIME = "sort_message_by_server_time"
        private const val SHARED_KEY_ENABLE_TOKEN_LOGIN = "enable_token_login"
        @Synchronized
        fun init(cxt: Context) {
            if (mPreferencemManager == null) {
                mPreferencemManager = PreferenceManager(cxt)
            }
        }

        /**
         * get instance of PreferenceManager
         *
         * @param
         * @return
         */
        @get:Synchronized
        val instance: PreferenceManager?
            get() {
                if (mPreferencemManager == null) {
                    throw RuntimeException("please init first!")
                }
                return mPreferencemManager
            }
    }

    init {
        mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        editor = mSharedPreferences.edit()
    }
}
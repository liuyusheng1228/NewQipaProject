package com.qipa.newboxproject.data.push

import android.content.Context
import android.util.Log

import cn.jpush.android.api.JPushMessage


/**
 * 处理tagalias相关的逻辑
 */
class TagAliasOperatorHelper private constructor() {
    private var context: Context? = null
    fun init(context: Context?) {
        if (context != null) {
            this.context = context.getApplicationContext()
        }
    }

    fun onTagOperatorResult(context: Context?, jPushMessage: JPushMessage) {
        val sequence = jPushMessage.sequence
        Log.i(
            TAG,
            "action - onTagOperatorResult, sequence:" + sequence + ",tags:" + jPushMessage.tags
        )
        Log.i(TAG, "tags size:" + jPushMessage.tags.size)
        init(context)
        if (jPushMessage.errorCode == 0) {
            Log.i(
                TAG,
                "action - modify tag Success,sequence:$sequence"
            )
//            ToastHelper.showOk(context, "modify success")
        } else {
            var logs = "Failed to modify tags"
            if (jPushMessage.errorCode == 6018) {
                //tag数量超过限制,需要先清除一部分再add
                logs += ", tags is exceed limit need to clean"
            }
            logs += ", errorCode:" + jPushMessage.errorCode
            Log.e(TAG, logs)
//            ToastHelper.showOther(context, logs)
        }
    }

    fun onCheckTagOperatorResult(context: Context?, jPushMessage: JPushMessage) {
        val sequence = jPushMessage.sequence
        Log.i(
            TAG,
            "action - onCheckTagOperatorResult, sequence:" + sequence + ",checktag:" + jPushMessage.checkTag
        )
        init(context)
        if (jPushMessage.errorCode == 0) {
            val logs =
                "modify tag " + jPushMessage.checkTag + " bind state success,state:" + jPushMessage.tagCheckStateResult
            Log.i(TAG, logs)
//            ToastHelper.showOk(context, "modify success")
        } else {
            val logs = "Failed to modify tags, errorCode:" + jPushMessage.errorCode
            Log.e(TAG, logs)
//            ToastHelper.showOther(context, logs)
        }
    }

    fun onAliasOperatorResult(context: Context?, jPushMessage: JPushMessage) {
        val sequence = jPushMessage.sequence
        Log.i(
            TAG,
            "action - onAliasOperatorResult, sequence:" + sequence + ",alias:" + jPushMessage.alias
        )
        init(context)
        if (jPushMessage.errorCode == 0) {
            Log.i(
                TAG,
                "action - modify alias Success,sequence:$sequence"
            )
//            ToastHelper.showOk(context, "modify success")
        } else {
            val logs = "Failed to modify alias, errorCode:" + jPushMessage.errorCode
            Log.e(TAG, logs)
//            ToastHelper.showOther(context, logs)
//            MMKV.defaultMMKV().putString(AdvActivity.ALIAS_DATA, "")
        }
    }

    //设置手机号码回调
    fun onMobileNumberOperatorResult(context: Context?, jPushMessage: JPushMessage) {
        val sequence = jPushMessage.sequence
        Log.i(
            TAG,
            "action - onMobileNumberOperatorResult, sequence:" + sequence + ",mobileNumber:" + jPushMessage.mobileNumber
        )
        init(context)
        if (jPushMessage.errorCode == 0) {
            Log.i(
                TAG,
                "action - set mobile number Success,sequence:$sequence"
            )
//            ToastHelper.showOk(context, "modify success")
        } else {
            val logs = "Failed to set mobile number, errorCode:" + jPushMessage.errorCode
            Log.e(TAG, logs)
//            ToastHelper.showOther(context, logs)
//            MMKV.defaultMMKV().putString(AdvActivity.MN_DATA, "")
        }
    }

    companion object {
        private const val TAG = "JIGUANG-TagAliasHelper"
        private var mInstance: TagAliasOperatorHelper? = null
        val instance: TagAliasOperatorHelper?
            get() {
                if (mInstance == null) {
                    synchronized(TagAliasOperatorHelper::class.java) {
                        if (mInstance == null) {
                            mInstance = TagAliasOperatorHelper()
                        }
                    }
                }
                return mInstance
            }
    }
}
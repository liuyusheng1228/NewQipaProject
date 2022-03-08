package com.qipa.newboxproject.data.db

import android.content.Context
import android.text.TextUtils
import com.hyphenate.util.EMLog

import androidx.lifecycle.LiveData

import androidx.room.Room

import androidx.lifecycle.MutableLiveData
import com.qipa.newboxproject.app.util.MD5
import com.qipa.newboxproject.data.db.dao.*


class DemoDbHelper private constructor(context: Context) {
    private val mContext: Context
    private var currentUser: String? = null
    private var mDatabase: AppDatabase? = null
    private val mIsDatabaseCreated = MutableLiveData<Boolean>()

    /**
     * 初始化数据库
     * @param user
     */
    fun initDb(user: String?) {
        if (currentUser != null) {
            if (TextUtils.equals(currentUser, user)) {
                EMLog.i(TAG, "you have opened the db")
                return
            }
            closeDb()
        }
        currentUser = user
        val userMd5: String? = user?.let { MD5.encrypt2MD5(it) }
        // 以下数据库升级设置，为升级数据库将清掉之前的数据，如果要保留数据，慎重采用此种方式
        // 可以采用addMigrations()的方式，进行数据库的升级
        val dbName = String.format("em_%1\$s.db", userMd5)
        EMLog.i(TAG, "db name = $dbName")
        mDatabase = Room.databaseBuilder(mContext, AppDatabase::class.java, dbName)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
        mIsDatabaseCreated.postValue(true)
    }

    val databaseCreatedObservable: LiveData<Boolean>
        get() = mIsDatabaseCreated

    /**
     * 关闭数据库
     */
    fun closeDb() {
        if (mDatabase != null) {
            mDatabase!!.close()
            mDatabase = null
        }
        currentUser = null
    }

    val apkDownloadDao : ApkDownloadDao?
        get() {
            if (mDatabase != null) {
                return mDatabase!!.apkDownloadDao()
            }
            EMLog.i(TAG, "get userDao failed, should init db first")
            return null
        }

    val userDao: EmUserDao?
        get() {
            if (mDatabase != null) {
                return mDatabase!!.userDao()
            }
            EMLog.i(TAG, "get userDao failed, should init db first")
            return null
        }
    val inviteMessageDao: InviteMessageDao?
        get() {
            if (mDatabase != null) {
                return mDatabase!!.inviteMessageDao()
            }
            EMLog.i(TAG, "get inviteMessageDao failed, should init db first")
            return null
        }
    val msgTypeManageDao: MsgTypeManageDao?
        get() {
            if (mDatabase != null) {
                return mDatabase!!.msgTypeManageDao()
            }
            EMLog.i(TAG, "get msgTypeManageDao failed, should init db first")
            return null
        }
    val appKeyDao: AppKeyDao?
        get() {
            if (mDatabase != null) {
                return mDatabase!!.appKeyDao()
            }
            EMLog.i(TAG, "get appKeyDao failed, should init db first")
            return null
        }

    companion object {
        private const val TAG = "DemoDbHelper"
        private var instance: DemoDbHelper? = null
        fun getInstance(context: Context): DemoDbHelper? {
            if (instance == null) {
                synchronized(DemoDbHelper::class.java) {
                    if (instance == null) {
                        instance = DemoDbHelper(context)
                    }
                }
            }
            return instance
        }
    }

    init {
        mContext = context.getApplicationContext()
    }
}
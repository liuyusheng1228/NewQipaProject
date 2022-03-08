package com.qipa.jetpackmvvm.util.cache

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import com.qipa.jetpackmvvm.util.AppLogMessageMgr
import com.qipa.jetpackmvvm.util.FileUtils
import java.io.File
import java.text.DecimalFormat


/**
 * 主要功能： 提供App数据清理工作的类
 * @Company:
 * @version: 1.0.0
 */
@SuppressLint("SdCardPath")
object AppCleanMgr {
    /**
     * 清除本应用内部缓存数据(/data/data/com.xxx.xxx/cache)
     * @param context 上下文
     * @return void
     */
    fun cleanInternalCache(context: Context) {
        FileUtils.deleteFilesByDirectory(context.cacheDir)
        AppLogMessageMgr.i(
            "AppCleanMgr->>cleanInternalCache",
            "清除本应用内部缓存(/data/data/" + context.packageName + "/cache)"
        )
    }

    /**
     * 清除本应用外部缓存数据(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     * @param context 上下文
     * @return void
     */
    fun cleanExternalCache(context: Context) {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            FileUtils.deleteFilesByDirectory(context.externalCacheDir)
            AppLogMessageMgr.i(
                "AppCleanMgr->>cleanExternalCache",
                "清除本应用外部缓存数据(/mnt/sdcard/android/data/" + context.packageName + "/cache)"
            )
        }
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases)
     * @param context 上下文
     * @return void
     */
    fun cleanDatabases(context: Context) {
        FileUtils.deleteFilesByDirectory(File("/data/data/" + context.packageName + "/databases"))
        AppLogMessageMgr.i("AppCleanMgr->>cleanDatabases", "清除本应用所有数据库")
    }

    /**
     * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs)
     * @param context 上下文
     * @return void
     */
    fun cleanSharedPreference(context: Context) {
        FileUtils.deleteFilesByDirectory(File("/data/data/" + context.packageName + "/shared_prefs"))
        AppLogMessageMgr.i("AppCleanMgr->>cleanSharedPreference", "清除本应用cleanSharedPreference数据信息")
    }

    /**
     * 根据名字清除本应用数据库
     * @param context 上下文
     * @param dbName
     * @return void
     */
    fun cleanDatabaseByName(context: Context, dbName: String?) {
        context.deleteDatabase(dbName)
        AppLogMessageMgr.i("AppCleanMgr->>cleanDatabaseByName", "根据名字清除本应用数据库")
    }

    /**
     * 清除本应用files文件(/data/data/com.xxx.xxx/files)
     * @param context 上下文
     * @return void
     */
    fun cleanFiles(context: Context) {
        FileUtils.deleteFilesByDirectory(context.filesDir)
        AppLogMessageMgr.i(
            "AppCleanMgr->>cleanFiles",
            "清除data/data/" + context.packageName + "/files下的内容信息"
        )
    }

    /**
     * 清除本应用所有的数据
     * @param context 上下文
     * @return int
     */
    fun cleanApplicationData(context: Context): Int {
        //清除本应用内部缓存数据
        cleanInternalCache(context)
        //清除本应用外部缓存数据
        cleanExternalCache(context)
        //清除本应用SharedPreference
        cleanSharedPreference(context)
        //清除本应用files文件
        cleanFiles(context)
        AppLogMessageMgr.i("AppCleanMgr->>cleanApplicationData", "清除本应用所有的数据")
        return 1
    }

    /**
     * 获取App应用缓存的大小
     * @param context 上下文
     * @return String
     */
    fun getAppClearSize(context: Context): String {
        var clearSize: Long = 0
        var fileSizeStr = ""
        val df = DecimalFormat("0.00")
        //获得应用内部缓存大小
        clearSize = FileUtils.getFileSize(context.cacheDir)
        //获得应用SharedPreference缓存数据大小
        clearSize += FileUtils.getFileSize(File("/data/data/" + context.packageName + "/shared_prefs"))
        //获得应用data/data/com.xxx.xxx/files下的内容文件大小
        clearSize += FileUtils.getFileSize(context.filesDir)
        //获取应用外部缓存大小
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            clearSize += FileUtils.getFileSize(context.externalCacheDir!!)
        }
        if (clearSize > 5000) {
            //转换缓存大小Byte为MB
            fileSizeStr = df.format(clearSize.toDouble() / 1048576) + "MB"
        }
        AppLogMessageMgr.i("AppCleanMgr->>getAppClearSize", "获取App应用缓存的大小")
        return fileSizeStr
    }
}

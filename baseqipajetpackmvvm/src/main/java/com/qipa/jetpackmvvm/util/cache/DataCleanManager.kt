package com.qipa.jetpackmvvm.util.cache

import android.content.Context
import android.os.Environment
import java.io.File
import java.lang.Exception
import java.math.BigDecimal


/**
 * @Description:主要功能:app缓存清理文件清理管理器
 * @Company:
 * @version: 1.0.0
 */
object DataCleanManager {
    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache)
     *
     * @param context
     */
    fun cleanInternalCache(context: Context) {
        deleteFilesByDirectory(context.cacheDir)
        //    ImageLoader.getInstance().clearMemoryCache();  // 清除内存缓存
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases)
     *
     * @param context
     */
    fun cleanDatabases(context: Context) {
        deleteFilesByDirectory(
            File(
                "/data/data/"
                        + context.packageName + "/databases"
            )
        )
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs)
     *
     * @param context
     */
    fun cleanSharedPreference(context: Context) {
        deleteFilesByDirectory(
            File(
                ("/data/data/"
                        + context.packageName + "/shared_prefs")
            )
        )
    }

    /**
     * * 按名字清除本应用数据库 *
     * * @param context *
     *
     * @param dbName
     */
    fun cleanDatabaseByName(context: Context, dbName: String?) {
        context.deleteDatabase(dbName)
    }

    /**
     * * 清除/data/data/com.xxx.xxx/files下的内容 *
     *
     * @param context
     */
    fun cleanFiles(context: Context) {
        deleteFilesByDirectory(context.filesDir)
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     * * @param
     * context
     */
    fun cleanExternalCache(context: Context) {
        if ((Environment.getExternalStorageState() ==
                    Environment.MEDIA_MOUNTED)
        ) {
            deleteFilesByDirectory(context.externalCacheDir)
        }
    }

    /**
     * * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除
     * * @param filePath
     */
    fun cleanCustomCache(filePath: String?) {
        deleteFilesByDirectory(File(filePath))
    }

    /**
     * * 清除本应用所有的数据 *
     * * @param context *
     * *@param filepath
     */
    fun cleanApplicationData(context: Context, vararg filepath: String?) {
        cleanInternalCache(context)
        cleanExternalCache(context)
        cleanDatabases(context)
        cleanSharedPreference(context)
        cleanFiles(context)
        for (filePath: String? in filepath) {
            cleanCustomCache(filePath)
        }
    }

    /**
     * * 清除本应用数据 *
     * * @param context *
     * *@param filepath
     */
    fun cleanApplicationData(context: Context) {
        cleanInternalCache(context)
        cleanExternalCache(context)
        cleanDatabases(context)
        cleanSharedPreference(context)
        cleanFiles(context)
        //        ImageLoader.getInstance().clearDiskCache();  // 清除本地缓存
    }

    /**
     * * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 *
     * * @param directory
     */
    private fun deleteFilesByDirectory(directory: File?) {
        if ((directory != null) && directory.exists() && directory.isDirectory) {
            val files = directory.listFiles()
            if (files != null) {
                val length = files.size
                if (length > 0) {
                    for (item: File in files) {
                        item.delete()
                    }
                }
            }
        }
    }

    /**
     * 获取总缓存大小
     * @param context
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getTotalCacheSize(context: Context): String {
        var cacheSize = getFolderSize(context.cacheDir)
        if ((Environment.getExternalStorageState() ==
                    Environment.MEDIA_MOUNTED)
        ) {
            cacheSize += getFolderSize(context.externalCacheDir)
        }
        return getFormatSize(cacheSize.toDouble())
    }

    // 获取文件
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    @Throws(Exception::class)
    fun getFolderSize(file: File?): Long {
        var size: Long = 0
        if (file != null) {
            try {
                val fileList = file.listFiles()
                if (fileList != null) {
                    val length = fileList.size
                    if (length > 0) {
                        for (i in 0 until length) {
                            // 如果下面还有文件
                            if (fileList[i].isDirectory) {
                                size = size + getFolderSize(fileList[i])
                            } else {
                                size = size + fileList[i].length()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return size
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    fun getFormatSize(size: Double): String {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
            return "0K"
        }
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(java.lang.Double.toString(kiloByte))
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "KB"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(java.lang.Double.toString(megaByte))
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "MB"
        }
        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(java.lang.Double.toString(gigaByte))
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)
        return (result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB")
    }
}

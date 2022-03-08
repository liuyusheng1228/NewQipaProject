package com.qipa.jetpackmvvm.util

import android.annotation.TargetApi
import android.os.Build
import android.os.Environment
import android.os.StatFs
import java.io.*
import java.lang.Exception
import java.util.ArrayList


/**
 * @Description:主要功能:SD卡片工具
 * @Company:
 * @version: 1.0.0
 */
class SDCardUtils {
    /**
     * is sd card available.
     * @return true if available
     */
    val isSdCardAvailable: Boolean
        get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()

    /**
     * see more [StatFs]
     */
    class SDCardInfo {
        var isExist = false
        var totalBlocks: Long = 0
        var freeBlocks: Long = 0
        var availableBlocks: Long = 0
        var blockByteSize: Long = 0
        var totalBytes: Long = 0
        var freeBytes: Long = 0
        var availableBytes: Long = 0
        override fun toString(): String {
            return "SDCardInfo{" +
                    "isExist=" + isExist +
                    ", totalBlocks=" + totalBlocks +
                    ", freeBlocks=" + freeBlocks +
                    ", availableBlocks=" + availableBlocks +
                    ", blockByteSize=" + blockByteSize +
                    ", totalBytes=" + totalBytes +
                    ", freeBytes=" + freeBytes +
                    ", availableBytes=" + availableBytes +
                    '}'
        }
    }

    companion object {
        /**
         * Get [StatFs].
         */
        fun getStatFs(path: String?): StatFs {
            return StatFs(path)
        }

        /**
         * Get phone data path.
         */
        val dataPath: String
            get() = Environment.getDataDirectory().path

        /**
         * Get SD card path.
         */
        val normalSDCardPath: String
            get() = Environment.getExternalStorageDirectory().path// p.exitValue()==0表示正常结束，1：非正常结束// 启动另一个进程来执行命令// 返回与当前 Java 应用程序相关的运行时对象

        /**
         * Get SD card path by CMD.
         */
        val sDCardPath: String?
            get() {
                val cmd = "cat /proc/mounts"
                var sdcard: String? = null
                val run = Runtime.getRuntime() // 返回与当前 Java 应用程序相关的运行时对象
                var bufferedReader: BufferedReader? = null
                try {
                    val p = run.exec(cmd) // 启动另一个进程来执行命令
                    bufferedReader =
                        BufferedReader(InputStreamReader(BufferedInputStream(p.inputStream)))
                    var lineStr: String
                    while (bufferedReader.readLine().also { lineStr = it } != null) {
                        if (lineStr.contains("sdcard")
                            && lineStr.contains(".android_secure")
                        ) {
                            val strArray = lineStr.split(" ".toRegex()).toTypedArray()
                            if (strArray.size >= 5) {
                                sdcard = strArray[1].replace("/.android_secure", "")
                                return sdcard
                            }
                        }
                        if (p.waitFor() != 0 && p.exitValue() == 1) {
                            // p.exitValue()==0表示正常结束，1：非正常结束
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        bufferedReader?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                sdcard = Environment.getExternalStorageDirectory().path
                return sdcard
            }

        /**
         * Get SD card path list.
         */
        val sDCardPathEx: ArrayList<String>
            get() {
                val list = ArrayList<String>()
                try {
                    val runtime = Runtime.getRuntime()
                    val proc = runtime.exec("mount")
                    val `is` = proc.inputStream
                    val isr = InputStreamReader(`is`)
                    var line: String
                    val br = BufferedReader(isr)
                    while (br.readLine().also { line = it } != null) {
                        if (line.contains("secure")) {
                            continue
                        }
                        if (line.contains("asec")) {
                            continue
                        }
                        if (line.contains("fat")) {
                            val columns = line.split(" ".toRegex()).toTypedArray()
                            if (columns.size > 1) {
                                list.add("*" + columns[1])
                            }
                        } else if (line.contains("fuse")) {
                            val columns = line.split(" ".toRegex()).toTypedArray()
                            if (columns.size > 1) {
                                list.add(columns[1])
                            }
                        }
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return list
            }

        /**
         * Get available size of SD card.
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        fun getAvailableSize(path: String?): Long {
            try {
                val base = File(path)
                val stat = StatFs(base.path)
                return stat.blockSizeLong * stat.availableBlocksLong
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 0
        }

        /**
         * Get SD card info detail.
         */
        @get:TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        val sDCardInfo: SDCardInfo
            get() {
                val sd = SDCardInfo()
                val state = Environment.getExternalStorageState()
                if (Environment.MEDIA_MOUNTED == state) {
                    sd.isExist = true
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        val sdcardDir = Environment.getExternalStorageDirectory()
                        val sf = StatFs(sdcardDir.path)
                        sd.totalBlocks = sf.blockCountLong
                        sd.blockByteSize = sf.blockSizeLong
                        sd.availableBlocks = sf.availableBlocksLong
                        sd.availableBytes = sf.availableBytes
                        sd.freeBlocks = sf.freeBlocksLong
                        sd.freeBytes = sf.freeBytes
                        sd.totalBytes = sf.totalBytes
                    }
                }
                return sd
            }

        /**
         * 获取指定路径所在空间的剩余可用容量字节数(byte)
         * @param filePath
         * @return 容量字节 SDCard可用空间，内部存储可用空间
         */
        fun getFreeBytes(filePath: String?): Long {
            // 如果是sd卡的下的路径，则获取sd卡可用容量
            var filePath = filePath
            filePath = if (filePath!!.startsWith(sDCardPath!!)) {
                sDCardPath
            } else { // 如果是内部存储的路径，则获取内存存储的可用容量
                Environment.getDataDirectory().absolutePath
            }
            val stat = StatFs(filePath)
            val availableBlocks = stat.availableBlocks.toLong() - 4
            return stat.blockSize * availableBlocks
        }

        /**
         * 获取系统存储路径
         */
        val rootDirectoryPath: String
            get() = Environment.getRootDirectory().absolutePath
    }
}

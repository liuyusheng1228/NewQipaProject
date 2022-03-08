package com.qipa.jetpackmvvm.util.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Process
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * 主要功能:缓存工具类
 *
 * @Company:
 * @version: 1.0.0
 */
class AppACache private constructor(cacheDir: File, max_size: Long, max_count: Int) {
    private val mCache: ACacheManager

    /**
     * Provides a means to save a cached file before the data are available.
     * Since writing about the file is complete, and its close method is called,
     * its contents will be registered in the cache. Example of use:
     *
     * ACache cache = new ACache(this) try { OutputStream stream =
     * cache.put("myFileName") stream.write("some bytes".getBytes()); // now
     * update cache! stream.close(); } catch(FileNotFoundException e){
     * e.printStackTrace() }
     */
    internal inner class xFileOutputStream(var file: File) : FileOutputStream(
        file
    ) {
        @Throws(IOException::class)
        override fun close() {
            super.close()
            mCache.put(file)
        }
    }
    // =======================================
    // ============ String数据 读写 ==============
    // =======================================
    /**
     * 保存 String数据 到 缓存中
     *
     * @param key
     * 保存的key
     * @param value
     * 保存的String数据
     */
    fun put(key: String, value: String?) {
        val file = mCache.newFile(key)
        var out: BufferedWriter? = null
        try {
            out = BufferedWriter(FileWriter(file), 1024)
            out.write(value)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (out != null) {
                try {
                    out.flush()
                    out.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            mCache.put(file)
        }
    }

    /**
     * 保存 String数据 到 缓存中
     *
     * @param key
     * 保存的key
     * @param value
     * 保存的String数据
     * @param saveTime
     * 保存的时间，单位：秒
     */
    fun put(key: String, value: String, saveTime: Int) {
        put(key, Utils.newStringWithDateInfo(saveTime, value))
    }

    /**
     * 读取 String数据
     *
     * @param key
     * @return String 数据
     */
    fun getAsString(key: String): String? {
        val file = mCache.get(key)
        if (!file.exists()) return null
        var removeFile = false
        var `in`: BufferedReader? = null
        try {
            `in` = BufferedReader(FileReader(file))
            var readString = ""
            var currentLine: String
            while (`in`.readLine().also { currentLine = it } != null) {
                readString += currentLine
            }
            if (!Utils.isDue(readString)) {
                return Utils.clearDateInfo(readString)
            } else {
                removeFile = true
                return null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (removeFile) remove(key)
        }
    }
    // =======================================
    // ============= JSONObject 数据 读写 ==============
    // =======================================
    /**
     * 保存 JSONObject数据 到 缓存中
     *
     * @param key
     * 保存的key
     * @param value
     * 保存的JSON数据
     */
    fun put(key: String, value: JSONObject) {
        put(key, value.toString())
    }

    /**
     * 保存 JSONObject数据 到 缓存中
     *
     * @param key
     * 保存的key
     * @param value
     * 保存的JSONObject数据
     * @param saveTime
     * 保存的时间，单位：秒
     */
    fun put(key: String, value: JSONObject, saveTime: Int) {
        put(key, value.toString(), saveTime)
    }

    /**
     * 读取JSONObject数据
     *
     * @param key
     * @return JSONObject数据
     */
    fun getAsJSONObject(key: String): JSONObject? {
        val JSONString = getAsString(key)
        try {
            return JSONObject(JSONString)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    // =======================================
    // ============ JSONArray 数据 读写 =============
    // =======================================
    /**
     * 保存 JSONArray数据 到 缓存中
     *
     * @param key
     * 保存的key
     * @param value
     * 保存的JSONArray数据
     */
    fun put(key: String, value: JSONArray) {
        put(key, value.toString())
    }

    /**
     * 保存 JSONArray数据 到 缓存中
     *
     * @param key
     * 保存的key
     * @param value
     * 保存的JSONArray数据
     * @param saveTime
     * 保存的时间，单位：秒
     */
    fun put(key: String, value: JSONArray, saveTime: Int) {
        put(key, value.toString(), saveTime)
    }

    /**
     * 读取JSONArray数据
     *
     * @param key
     * @return JSONArray数据
     */
    fun getAsJSONArray(key: String): JSONArray? {
        val JSONString = getAsString(key)
        try {
            return JSONArray(JSONString)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    // =======================================
    // ============== byte 数据 读写 =============
    // =======================================
    /**
     * 保存 byte数据 到 缓存中
     *
     * @param key
     * 保存的key
     * @param value
     * 保存的数据
     */
    fun put(key: String, value: ByteArray?) {
        val file = mCache.newFile(key)
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(file)
            out.write(value)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (out != null) {
                try {
                    out.flush()
                    out.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            mCache.put(file)
        }
    }

    /**
     * Cache for a stream
     *
     * @param key
     * the file name.
     * @return OutputStream stream for writing data.
     * @throws FileNotFoundException
     * if the file can not be created.
     */
    @Throws(FileNotFoundException::class)
    fun put(key: String): OutputStream {
        return xFileOutputStream(mCache.newFile(key))
    }

    /**
     *
     * @param key
     * the file name.
     * @return (InputStream or null) stream previously saved in cache.
     * @throws FileNotFoundException
     * if the file can not be opened
     */
    @Throws(FileNotFoundException::class)
    operator fun get(key: String): InputStream? {
        val file = mCache.get(key)
        return if (!file.exists()) null else FileInputStream(file)
    }

    /**
     * 保存 byte数据 到 缓存中
     *
     * @param key
     * 保存的key
     * @param value
     * 保存的数据
     * @param saveTime
     * 保存的时间，单位：秒
     */
    fun put(key: String, value: ByteArray?, saveTime: Int) {
        put(key, Utils.newByteArrayWithDateInfo(saveTime, value))
    }

    /**
     * 获取 byte 数据
     *
     * @param key
     * @return byte 数据
     */
    fun getAsBinary(key: String): ByteArray? {
        var RAFile: RandomAccessFile? = null
        var removeFile = false
        try {
            val file = mCache.get(key)
            if (!file.exists()) return null
            RAFile = RandomAccessFile(file, "r")
            val byteArray = ByteArray(RAFile.length().toInt())
            RAFile.read(byteArray)
            if (!Utils.isDue(byteArray)) {
                return Utils.clearDateInfo(byteArray)
            } else {
                removeFile = true
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            if (RAFile != null) {
                try {
                    RAFile.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (removeFile) remove(key)
        }
    }
    /**
     * 保存 Serializable数据到 缓存中
     *
     * @param key
     * 保存的key
     * @param value
     * 保存的value
     * @param saveTime
     * 保存的时间，单位：秒
     */
    // =======================================
    // ============= 序列化 数据 读写 ===============
    // =======================================
    /**
     * 保存 Serializable数据 到 缓存中
     *
     * @param key
     * 保存的key
     * @param value
     * 保存的value
     */
    @JvmOverloads
    fun put(key: String, value: Serializable?, saveTime: Int = -1) {
        var baos: ByteArrayOutputStream? = null
        var oos: ObjectOutputStream? = null
        try {
            baos = ByteArrayOutputStream()
            oos = ObjectOutputStream(baos)
            oos.writeObject(value)
            val data = baos.toByteArray()
            if (saveTime != -1) {
                put(key, data, saveTime)
            } else {
                put(key, data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                oos!!.close()
            } catch (e: IOException) {
            }
        }
    }

    /**
     * 读取 Serializable数据
     *
     * @param key
     * @return Serializable 数据
     */
    fun getAsObject(key: String): Any? {
        val data = getAsBinary(key)
        if (data != null) {
            var bais: ByteArrayInputStream? = null
            var ois: ObjectInputStream? = null
            try {
                bais = ByteArrayInputStream(data)
                ois = ObjectInputStream(bais)
                return ois.readObject()
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            } finally {
                try {
                    bais?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    ois?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }
    // =======================================
    // ============== bitmap 数据 读写 =============
    // =======================================
    /**
     * 保存 bitmap 到 缓存中
     *
     * @param key
     * 保存的key
     * @param value
     * 保存的bitmap数据
     */
    fun put(key: String, value: Bitmap?) {
        put(key, Utils.Bitmap2Bytes(value))
    }

    /**
     * 保存 bitmap 到 缓存中
     *
     * @param key
     * 保存的key
     * @param value
     * 保存的 bitmap 数据
     * @param saveTime
     * 保存的时间，单位：秒
     */
    fun put(key: String, value: Bitmap?, saveTime: Int) {
        put(key, Utils.Bitmap2Bytes(value), saveTime)
    }

    /**
     * 读取 bitmap 数据
     *
     * @param key
     * @return bitmap 数据
     */
    fun getAsBitmap(key: String): Bitmap? {
        return if (getAsBinary(key) == null) {
            null
        } else Utils.Bytes2Bimap(getAsBinary(key))
    }
    // =======================================
    // ============= drawable 数据 读写 =============
    // =======================================
    /**
     * 保存 drawable 到 缓存中
     *
     * @param key
     * 保存的key
     * @param value
     * 保存的drawable数据
     */
    fun put(key: String, value: Drawable?) {
        put(key, Utils.drawable2Bitmap(value))
    }

    /**
     * 保存 drawable 到 缓存中
     *
     * @param key
     * 保存的key
     * @param value
     * 保存的 drawable 数据
     * @param saveTime
     * 保存的时间，单位：秒
     */
    fun put(key: String, value: Drawable?, saveTime: Int) {
        put(key, Utils.drawable2Bitmap(value), saveTime)
    }

    /**
     * 读取 Drawable 数据
     *
     * @param key
     * @return Drawable 数据
     */
    fun getAsDrawable(key: String): Drawable? {
        return if (getAsBinary(key) == null) {
            null
        } else Utils.bitmap2Drawable(Utils.Bytes2Bimap(getAsBinary(key)))
    }

    /**
     * 获取缓存文件
     *
     * @param key
     * @return value 缓存的文件
     */
    fun file(key: String): File? {
        val f = mCache.newFile(key)
        return if (f.exists()) f else null
    }

    /**
     * 移除某个key
     *
     * @param key
     * @return 是否移除成功
     */
    fun remove(key: String): Boolean {
        return mCache.remove(key)
    }

    /**
     * 清除所有数据
     */
    fun clear() {
        mCache.clear()
    }

    /**
     * @title 缓存管理器
     * @version 1.0
     */
    inner class ACacheManager(
        protected var cacheDir: File,
        private val sizeLimit: Long,
        private val countLimit: Int
    ) {
        private val cacheSize: AtomicLong
        private val cacheCount: AtomicInteger
        private val lastUsageDates = Collections
            .synchronizedMap(HashMap<File, Long>())

        /**
         * 计算 cacheSize和cacheCount
         */
        private fun calculateCacheSizeAndCacheCount() {
            Thread {
                var size = 0
                var count = 0
                val cachedFiles = cacheDir.listFiles()
                if (cachedFiles != null) {
                    for (cachedFile: File in cachedFiles) {
                        size += calculateSize(cachedFile).toInt()
                        count += 1
                        lastUsageDates[cachedFile] = cachedFile.lastModified()
                    }
                    cacheSize.set(size.toLong())
                    cacheCount.set(count)
                }
            }.start()
        }

        fun put(file: File) {
            var curCacheCount = cacheCount.get()
            while (curCacheCount + 1 > countLimit) {
                val freedSize = removeNext()
                cacheSize.addAndGet(-freedSize)
                curCacheCount = cacheCount.addAndGet(-1)
            }
            cacheCount.addAndGet(1)
            val valueSize = calculateSize(file)
            var curCacheSize = cacheSize.get()
            while (curCacheSize + valueSize > sizeLimit) {
                val freedSize = removeNext()
                curCacheSize = cacheSize.addAndGet(-freedSize)
            }
            cacheSize.addAndGet(valueSize)
            val currentTime = System.currentTimeMillis()
            file.setLastModified(currentTime)
            lastUsageDates[file] = currentTime
        }

        operator fun get(key: String): File {
            val file = newFile(key)
            val currentTime = System.currentTimeMillis()
            file.setLastModified(currentTime)
            lastUsageDates[file] = currentTime
            return file
        }

        fun newFile(key: String): File {
            return File(cacheDir, key.hashCode().toString() + "")
        }

        fun remove(key: String): Boolean {
            val image = get(key)
            return image.delete()
        }

        fun clear() {
            lastUsageDates.clear()
            cacheSize.set(0)
            val files = cacheDir.listFiles()
            if (files != null) {
                for (f: File in files) {
                    f.delete()
                }
            }
        }

        /**
         * 移除旧的文件
         *
         * @return
         */
        private fun removeNext(): Long {
            if (lastUsageDates.isEmpty()) {
                return 0
            }
            var oldestUsage: Long? = null
            var mostLongUsedFile: File? = null
            val entries: Set<Map.Entry<File, Long>> = lastUsageDates.entries
            synchronized(lastUsageDates) {
                for (entry: Map.Entry<File, Long> in entries) {
                    if (mostLongUsedFile == null) {
                        mostLongUsedFile = entry.key
                        oldestUsage = entry.value
                    } else {
                        val lastValueUsage: Long = entry.value
                        if (lastValueUsage < (oldestUsage)!!) {
                            oldestUsage = lastValueUsage
                            mostLongUsedFile = entry.key
                        }
                    }
                }
            }
            val fileSize = calculateSize(mostLongUsedFile)
            if (mostLongUsedFile!!.delete()) {
                lastUsageDates.remove(mostLongUsedFile)
            }
            return fileSize
        }

        private fun calculateSize(file: File?): Long {
            return file!!.length()
        }

        init {
            cacheSize = AtomicLong()
            cacheCount = AtomicInteger()
            calculateCacheSizeAndCacheCount()
        }
    }

    /**
     * @title 时间计算工具类
     * @version 1.0
     */
    private object Utils {
        /**
         * 判断缓存的String数据是否到期
         *
         * @param str
         * @return true：到期了 false：还没有到期
         */
        fun isDue(str: String): Boolean {
            return isDue(str.toByteArray())
        }

        /**
         * 判断缓存的byte数据是否到期
         *
         * @param data
         * @return true：到期了 false：还没有到期
         */
        fun isDue(data: ByteArray): Boolean {
            val strs = getDateInfoFromDate(data)
            if (strs != null && strs.size == 2) {
                var saveTimeStr = strs[0]
                while (saveTimeStr.startsWith("0")) {
                    saveTimeStr = saveTimeStr
                        .substring(1, saveTimeStr.length)
                }
                val saveTime = java.lang.Long.valueOf(saveTimeStr)
                val deleteAfter = java.lang.Long.valueOf(strs[1])
                if (System.currentTimeMillis() > saveTime + deleteAfter * 1000) {
                    return true
                }
            }
            return false
        }

        fun newStringWithDateInfo(second: Int, strInfo: String): String {
            return createDateInfo(second) + strInfo
        }

        fun newByteArrayWithDateInfo(second: Int, data2: ByteArray?): ByteArray {
            val data1 = createDateInfo(second).toByteArray()
            val retdata = ByteArray(data1.size + data2!!.size)
            System.arraycopy(data1, 0, retdata, 0, data1.size)
            System.arraycopy(data2, 0, retdata, data1.size, data2.size)
            return retdata
        }

        fun clearDateInfo(strInfo: String): String? {
            var strInfo: String? = strInfo
            if (strInfo != null && hasDateInfo(strInfo.toByteArray())) {
                strInfo = strInfo.substring(
                    strInfo.indexOf(mSeparator) + 1,
                    strInfo.length
                )
            }
            return strInfo
        }

        fun clearDateInfo(data: ByteArray): ByteArray {
            return if (hasDateInfo(data)) {
                copyOfRange(
                    data, indexOf(data, mSeparator) + 1,
                    data.size
                )
            } else data
        }

        private fun hasDateInfo(data: ByteArray?): Boolean {
            return data != null && data.size > 15 && data[13].equals('-')&& indexOf(
                data,
                mSeparator
            ) > 14
        }

        private fun getDateInfoFromDate(data: ByteArray): Array<String>? {
            if (hasDateInfo(data)) {
                val saveDate = String(copyOfRange(data, 0, 13))
                val deleteAfter = String(
                    copyOfRange(
                        data, 14,
                        indexOf(data, mSeparator)
                    )
                )
                return arrayOf(saveDate, deleteAfter)
            }
            return null
        }

        private fun indexOf(data: ByteArray, c: Char): Int {
            for (i in data.indices) {
                if (data[i].equals(c)) {
                    return i
                }
            }
            return -1
        }

        private fun copyOfRange(original: ByteArray, from: Int, to: Int): ByteArray {
            val newLength = to - from
            if (newLength < 0) throw IllegalArgumentException("$from > $to")
            val copy = ByteArray(newLength)
            System.arraycopy(
                original, from, copy, 0,
                Math.min(original.size - from, newLength)
            )
            return copy
        }

        private val mSeparator = ' '
        private fun createDateInfo(second: Int): String {
            var currentTime = System.currentTimeMillis().toString() + ""
            while (currentTime.length < 13) {
                currentTime = "0$currentTime"
            }
            return currentTime + "-" + second + mSeparator
        }

        /*
         * Bitmap → byte[]
         */
        fun Bitmap2Bytes(bm: Bitmap?): ByteArray? {
            if (bm == null) {
                return null
            }
            val baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
            return baos.toByteArray()
        }

        /*
         * byte[] → Bitmap
         */
        fun Bytes2Bimap(b: ByteArray?): Bitmap? {
            return if (b!!.size == 0) {
                null
            } else BitmapFactory.decodeByteArray(b, 0, b.size)
        }

        /*
         * Drawable → Bitmap
         */
        fun drawable2Bitmap(drawable: Drawable?): Bitmap? {
            if (drawable == null) {
                return null
            }
            // 取 drawable 的长宽
            val w = drawable.intrinsicWidth
            val h = drawable.intrinsicHeight
            // 取 drawable 的颜色格式
            val config =
                if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
            // 建立对应 bitmap
            val bitmap = Bitmap.createBitmap(w, h, config)
            // 建立对应 bitmap 的画布
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, w, h)
            // 把 drawable 内容画到画布中
            drawable.draw(canvas)
            return bitmap
        }

        /*
         * Bitmap → Drawable
         */
        fun bitmap2Drawable(bm: Bitmap?): Drawable? {
            return if (bm == null) {
                null
            } else BitmapDrawable(bm)
        }
    }

    companion object {
        // 用法例子
        // ACache mCache = ACache.get(this); // 初始化，一般放在基类里
        // mCache.put("test_key1","test value");
        // mCache.put("test_key2", "test value", 10);// 保存10秒，如果超过10秒去获取这个key，将为null
        // mCache.put("test_key3", "test value", 2 ACache.TIME_DAY);// 保存两天，如果超过两天去获取这个key，将为null
        // String value = mCache.getAsString("test_key1");// 获取数据
        val TIME_HOUR = 60 * 60
        val TIME_DAY = TIME_HOUR * 24
        private val MAX_SIZE = 1000 * 1000 * 50 // 50 mb
        private val MAX_COUNT = Int.MAX_VALUE // 不限制存放数据的数量
        private val mInstanceMap: MutableMap<String, AppACache> = HashMap()

        @JvmOverloads
        operator fun get(ctx: Context, cacheName: String? = "ACache"): AppACache {
            val f = File(ctx.cacheDir, cacheName)
            return Companion[f, MAX_SIZE.toLong(), MAX_COUNT]
        }

        operator fun get(ctx: Context, max_zise: Long, max_count: Int): AppACache {
            val f = File(ctx.cacheDir, "ACache")
            return Companion[f, max_zise, max_count]
        }

        @JvmOverloads
        operator fun get(
            cacheDir: File,
            max_zise: Long = MAX_SIZE.toLong(),
            max_count: Int = MAX_COUNT
        ): AppACache {
            var manager = mInstanceMap[cacheDir.absoluteFile.toString() + myPid()]
            if (manager == null) {
                manager = AppACache(cacheDir, max_zise, max_count)
                mInstanceMap[cacheDir.absolutePath + myPid()] =
                    manager
            }
            return manager
        }

        private fun myPid(): String {
            return "_" + Process.myPid()
        }
    }

    init {
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            throw RuntimeException(
                "can't make dirs in "
                        + cacheDir.absolutePath
            )
        }
        mCache = ACacheManager(cacheDir, max_size, max_count)
    }
}
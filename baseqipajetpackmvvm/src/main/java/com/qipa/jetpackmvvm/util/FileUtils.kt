package com.qipa.jetpackmvvm.util
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.os.StatFs
import android.text.format.Formatter
import android.util.Log
import com.qipa.jetpackmvvm.network.utils.rsa.StringUtils
import com.unionpay.WebViewJavascriptBridge.convertStreamToString


import com.unionpay.WebViewJavascriptBridge
import java.io.*
import java.lang.Exception
import java.lang.UnsupportedOperationException
import java.util.ArrayList
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream


/**
 * @Description:主要功能:文件管理
 * @Prject: CommonUtilLibrary
 * @Package: com.jingewenku.abrahamcaijin.commonutil
 * @author: AbrahamCaiJin
 * @date: 2017年05月16日 15:30
 * @Copyright: 个人版权所有
 * @Company:
 * @version: 1.0.0
 */
class FileUtils private constructor() {
    companion object {
        private const val TAG = "FileUtils"
        private val ROOT_PATH = (Environment.getExternalStorageDirectory()
            .toString() + File.separator
                + "pache")
        private const val CACHE = "/cache/"

        /**
         * 在初始化时创建APP所需要的基础文件夹
         * 在6.0以上版本时需要进行权限申请
         * @param context 上下文
         */
        fun init(context: Context) {
//        LogUtils.d(TAG, "Root path is " + ROOT_PATH);
            createFileDir(context, CACHE)
        }

        /*
     * 创建目录
     *
     * @param context
     * @param dirName 文件夹名称
     * @return
     */
        fun createFileDir(context: Context, dirName: String): File {
            val filePath: String
            // 如SD卡已存在，则存储；反之存在data目录下
            filePath = if (isMountedSDCard) {
                // SD卡路径
                Environment.getExternalStorageDirectory().toString() + File.separator + dirName
            } else {
                context.cacheDir.path + File.separator + dirName
            }
            val destDir = File(filePath)
            if (!destDir.exists()) {
                val isCreate = destDir.mkdirs()
                Log.i("FileUtils", "$filePath has created. $isCreate")
            }
            return destDir
        }

        /**
         * 删除文件（若为目录，则递归删除子目录和文件）
         *
         * @param file
         * @param delThisPath true代表删除参数指定file，false代表保留参数指定file
         */
        fun delFile(file: File, delThisPath: Boolean) {
            if (!file.exists()) {
                return
            }
            if (file.isDirectory) {
                val subFiles = file.listFiles()
                if (subFiles != null) {
                    val num = subFiles.size
                    // 删除子目录和文件
                    for (i in 0 until num) {
                        delFile(subFiles[i], true)
                    }
                }
            }
            if (delThisPath) {
                file.delete()
            }
        }

        /**
         * 获取文件大小，单位为byte（若为目录，则包括所有子目录和文件）
         *
         * @param file
         * @return
         */
        fun getFileSize(file: File): Long {
            var size: Long = 0
            if (file.exists()) {
                if (file.isDirectory) {
                    val subFiles = file.listFiles()
                    if (subFiles != null) {
                        val num = subFiles.size
                        for (i in 0 until num) {
                            size += getFileSize(subFiles[i])
                        }
                    }
                } else {
                    size += file.length()
                }
            }
            return size
        }

        fun saveBitmapFile(
            context: Context,
            dirName: String,
            fileName: String?,
            bitmap: Bitmap?
        ): File? {
            val file = File(createFileDir(context, dirName), fileName)
            if (bitmap == null) {
                return null
            }
            try {
                file.createNewFile()
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return file
        }

        /**
         * 保存Bitmap到指定目录
         *
         * @param dir      目录
         * @param fileName 文件名
         * @param bitmap
         * @throws IOException
         */
        fun saveBitmap(dir: File?, fileName: String?, bitmap: Bitmap?) {
            if (bitmap == null) {
                return
            }
            val file = File(dir, fileName)
            try {
                file.createNewFile()
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        /**
         * 判断某目录下文件是否存在
         *
         * @param dir      目录
         * @param fileName 文件名
         * @return
         */
        fun isFileExists(dir: File?, fileName: String?): Boolean {
            return File(dir, fileName).exists()
        }

        /**
         * 检查是否已挂载SD卡镜像（是否存在SD卡）
         *
         * @return
         */
        val isMountedSDCard: Boolean
            get() = if (Environment.MEDIA_MOUNTED == Environment
                    .getExternalStorageState()
            ) {
                true
            } else {
                Log.e(TAG, "SDCARD is not MOUNTED !")
                false
            }

        /**
         * 获取SD卡剩余容量（单位Byte）
         *
         * @return
         */
        fun gainSDFreeSize(): Long {
            return if (isMountedSDCard) {
                // 取得SD卡文件路径
                val path = Environment.getExternalStorageDirectory()
                val sf = StatFs(path.path)
                // 获取单个数据块的大小(Byte)
                val blockSize = sf.blockSize.toLong()
                // 空闲的数据块的数量
                val freeBlocks = sf.availableBlocks.toLong()

                // 返回SD卡空闲大小
                freeBlocks * blockSize // 单位Byte
            } else {
                0
            }
        }

        /**
         * 获取SD卡总容量（单位Byte）
         *
         * @return
         */
        fun gainSDAllSize(): Long {
            return if (isMountedSDCard) {
                // 取得SD卡文件路径
                val path = Environment.getExternalStorageDirectory()
                val sf = StatFs(path.path)
                // 获取单个数据块的大小(Byte)
                val blockSize = sf.blockSize.toLong()
                // 获取所有数据块数
                val allBlocks = sf.blockCount.toLong()
                // 返回SD卡大小（Byte）
                allBlocks * blockSize
            } else {
                0
            }
        }

        /**
         * 获取可用的SD卡路径（若SD卡不没有挂载则返回""）
         *
         * @return
         */
        fun gainSDCardPath(): String {
            if (isMountedSDCard) {
                val sdcardDir = Environment.getExternalStorageDirectory()
                if (!sdcardDir.canWrite()) {
                    Log.w(TAG, "SDCARD can not write !")
                }
                return sdcardDir.path
            }
            return ""
        }

        /**
         * 以行为单位读取文件内容，一次读一整行，常用于读面向行的格式化文件
         *
         * @param filePath
         * 文件路径
         */
        @Throws(IOException::class)
        fun readFileByLines(filePath: String?): String {
            var reader: BufferedReader? = null
            val sb = StringBuffer()
            try {
                reader = BufferedReader(
                    InputStreamReader(
                        FileInputStream(filePath),
                        System.getProperty("file.encoding")
                    )
                )
                var tempString: String? = null
                while (reader.readLine().also { tempString = it } != null) {
                    sb.append(tempString)
                    sb.append("\n")
                }
                reader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                reader?.close()
            }
            return sb.toString()
        }

        /**
         * 以行为单位读取文件内容，一次读一整行，常用于读面向行的格式化文件
         *
         * @param filePath
         * 文件路径
         * @param encoding
         * 写文件编码
         */
        @Throws(IOException::class)
        fun readFileByLines(filePath: String?, encoding: String?): String {
            var reader: BufferedReader? = null
            val sb = StringBuffer()
            try {
                reader = BufferedReader(
                    InputStreamReader(
                        FileInputStream(filePath), encoding
                    )
                )
                var tempString: String? = null
                while (reader.readLine().also { tempString = it } != null) {
                    sb.append(tempString)
                    sb.append("\n")
                }
                reader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                reader?.close()
            }
            return sb.toString()
        }
        /**
         * 指定编码保存内容
         *
         * @param filePath
         * 文件路径
         * @param content
         * 保存的内容
         * @param encoding
         * 写文件编码
         * @throws IOException
         */
        /**
         * 保存内容
         *
         * @param filePath
         * 文件路径
         * @param content
         * 保存的内容
         * @throws IOException
         */
        @JvmOverloads
        @Throws(IOException::class)
        fun saveToFile(
            filePath: String?, content: String?,
            encoding: String? = System.getProperty("file.encoding")
        ) {
            var writer: BufferedWriter? = null
            val file = File(filePath)
            try {
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                writer = BufferedWriter(
                    OutputStreamWriter(
                        FileOutputStream(file, false), encoding
                    )
                )
                writer.write(content)
            } finally {
                writer?.close()
            }
        }
        /**
         * 追加文本
         *
         * @param content
         * 需要追加的内容
         * @param file
         * 待追加文件源
         * @param encoding
         * 文件编码
         * @throws IOException
         */
        /**
         * 追加文本
         *
         * @param content
         * 需要追加的内容
         * @param file
         * 待追加文件源
         * @throws IOException
         */
        @JvmOverloads
        @Throws(IOException::class)
        fun appendToFile(
            content: String?,
            file: File,
            encoding: String? = System.getProperty("file.encoding")
        ) {
            var writer: BufferedWriter? = null
            try {
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                writer = BufferedWriter(
                    OutputStreamWriter(
                        FileOutputStream(file, true), encoding
                    )
                )
                writer.write(content)
            } finally {
                writer?.close()
            }
        }

        /**
         * 判断文件是否存在
         *
         * @param filePath
         * 文件路径
         * @return 是否存在
         * @throws Exception
         */
        fun isExsit(filePath: String?): Boolean {
            var flag = false
            try {
                val file = File(filePath)
                if (file.exists()) {
                    flag = true
                }
            } catch (e: Exception) {
                Log.e(TAG, "判断文件失败-->" + e.message)
            }
            return flag
        }

        /**
         * 快速读取程序应用包下的文件内容
         *
         * @param context
         * 上下文
         * @param filename
         * 文件名称
         * @return 文件内容
         * @throws IOException
         */
        @Throws(IOException::class)
        fun read(context: Context, filename: String?): String {
            val inStream = context.openFileInput(filename)
            val outStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var len = 0
            while (inStream.read(buffer).also { len = it } != -1) {
                outStream.write(buffer, 0, len)
            }
            val data = outStream.toByteArray()
            return String(data)
        }

        /**
         * 读取指定目录文件的文件内容
         *
         * @param fileName
         * 文件名称
         * @return 文件内容
         * @throws Exception
         */
        @Throws(IOException::class)
        fun read(fileName: String?): String {
            val inStream = FileInputStream(fileName)
            val outStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var len = 0
            while (inStream.read(buffer).also { len = it } != -1) {
                outStream.write(buffer, 0, len)
            }
            val data = outStream.toByteArray()
            return String(data)
        }

        /***
         * 以行为单位读取文件内容，一次读一整行，常用于读面向行的格式化文件
         *
         * @param fileName
         * 文件名称
         * @param encoding
         * 文件编码
         * @return 字符串内容
         * @throws IOException
         */
        @Throws(IOException::class)
        fun read(fileName: String?, encoding: String?): String {
            var reader: BufferedReader? = null
            val sb = StringBuffer()
            try {
                reader = BufferedReader(
                    InputStreamReader(
                        FileInputStream(fileName), encoding
                    )
                )
                var tempString: String? = null
                while (reader.readLine().also { tempString = it } != null) {
                    sb.append(tempString)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                reader?.close()
            }
            return sb.toString()
        }

        /**
         * 读取raw目录的文件内容
         *
         * @param context
         * 内容上下文
         * @param rawFileId
         * raw文件名id
         * @return
         */
        fun readRawValue(context: Context, rawFileId: Int): String {
            var result = ""
            try {
                val `is` = context.resources.openRawResource(rawFileId)
                val len = `is`.available()
                val buffer = ByteArray(len)
                `is`.read(buffer)
                result = String(buffer)
                `is`.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        /**
         * 读取assets目录的文件内容
         *
         * @param context
         * 内容上下文
         * @param fileName
         * 文件名称，包含扩展名
         * @return
         */
        fun readAssetsValue(context: Context, fileName: String?): String {
            var result = ""
            try {
                val `is` = context.resources.assets.open(fileName!!)
                val len = `is`.available()
                val buffer = ByteArray(len)
                `is`.read(buffer)
                result = String(buffer)
                `is`.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        /**
         * 读取assets目录的文件内容
         *
         * @param context
         * 内容上下文
         * @param fileName
         * 文件名称，包含扩展名
         * @return
         */
        fun readAssetsListValue(
            context: Context,
            fileName: String?
        ): List<String?> {
            val list: MutableList<String?> = ArrayList()
            try {
                val `in` = context.resources.assets.open(fileName!!)
                val br = BufferedReader(
                    InputStreamReader(
                        `in`,
                        "UTF-8"
                    )
                )
                var str: String? = null
                while (br.readLine().also { str = it } != null) {
                    list.add(str)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return list
        }

        /**
         * 获取SharedPreferences文件内容
         *
         * @param context
         * 上下文
         * @param fileNameNoExt
         * 文件名称（不用带后缀名）
         * @return
         */
        fun readShrePerface(
            context: Context,
            fileNameNoExt: String?
        ): Map<String, *> {
            val preferences = context.getSharedPreferences(
                fileNameNoExt, Context.MODE_PRIVATE
            )
            return preferences.all
        }

        /**
         * 写入SharedPreferences文件内容
         *
         * @param context
         * 上下文
         * @param fileNameNoExt
         * 文件名称（不用带后缀名）
         * @param values
         * 需要写入的数据Map(String,Boolean,Float,Long,Integer)
         * @return
         */
        fun writeShrePerface(
            context: Context, fileNameNoExt: String?,
            values: Map<String?, *>
        ) {
            try {
                val preferences = context.getSharedPreferences(
                    fileNameNoExt, Context.MODE_PRIVATE
                )
                val editor = preferences.edit()
                val iterator: Iterator<*> = values.entries.iterator()
                while (iterator
                        .hasNext()
                ) {
                    val entry = iterator
                        .next() as Map.Entry<String, *>
                    if (entry.value is String) {
                        editor.putString(entry.key, entry.value as String?)
                    } else if (entry.value is Boolean) {
                        editor.putBoolean(
                            entry.key,
                            (entry.value as Boolean?)!!
                        )
                    } else if (entry.value is Float) {
                        editor.putFloat(entry.key, (entry.value as Float?)!!)
                    } else if (entry.value is Long) {
                        editor.putLong(entry.key, (entry.value as Long?)!!)
                    } else if (entry.value is Int) {
                        editor.putInt(entry.key, (entry.value as Int?)!!)
                    }
                }
                editor.commit()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * 写入应用程序包files目录下文件
         *
         * @param context
         * 上下文
         * @param fileName
         * 文件名称
         * @param content
         * 文件内容
         */
        fun write(context: Context, fileName: String?, content: String) {
            try {
                val outStream = context.openFileOutput(
                    fileName,
                    Context.MODE_PRIVATE
                )
                outStream.write(content.toByteArray())
                outStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * 写入应用程序包files目录下文件
         *
         * @param context
         * 上下文
         * @param fileName
         * 文件名称
         * @param content
         * 文件内容
         */
        fun write(context: Context, fileName: String?, content: ByteArray?) {
            try {
                val outStream = context.openFileOutput(
                    fileName,
                    Context.MODE_PRIVATE
                )
                outStream.write(content)
                outStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * 写入应用程序包files目录下文件
         *
         * @param context
         * 上下文
         * @param fileName
         * 文件名称
         * @param modeType
         * 文件写入模式（Context.MODE_PRIVATE、Context.MODE_APPEND、Context.
         * MODE_WORLD_READABLE、Context.MODE_WORLD_WRITEABLE）
         * @param content
         * 文件内容
         */
        fun write(
            context: Context, fileName: String?, content: ByteArray?,
            modeType: Int
        ) {
            try {
                val outStream = context.openFileOutput(
                    fileName,
                    modeType
                )
                outStream.write(content)
                outStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * 指定编码将内容写入目标文件
         *
         * @param target
         * 目标文件
         * @param content
         * 文件内容
         * @param encoding
         * 写入文件编码
         * @throws Exception
         */
        @Throws(IOException::class)
        fun write(target: File, content: String?, encoding: String?) {
            var writer: BufferedWriter? = null
            try {
                if (!target.parentFile.exists()) {
                    target.parentFile.mkdirs()
                }
                writer = BufferedWriter(
                    OutputStreamWriter(
                        FileOutputStream(target, false), encoding
                    )
                )
                writer.write(content)
            } finally {
                writer?.close()
            }
        }

        /**
         * 指定目录写入文件内容
         *
         * @param filePath
         * 文件路径+文件名
         * @param content
         * 文件内容
         * @throws IOException
         */
        @Throws(IOException::class)
        fun write(filePath: String?, content: ByteArray?) {
            var fos: FileOutputStream? = null
            try {
                val file = File(filePath)
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                fos = FileOutputStream(file)
                fos.write(content)
                fos.flush()
            } finally {
                fos?.close()
            }
        }

        /**
         * 写入文件
         *
         * @param inputStream 下载文件的字节流对象
         * @param filePath 文件的存放路径
         * (带文件名称)
         * @throws IOException
         */
        @Throws(IOException::class)
        fun write(inputStream: InputStream, filePath: String?): File {
            var outputStream: OutputStream? = null
            // 在指定目录创建一个空文件并获取文件对象
            val mFile = File(filePath)
            if (!mFile.parentFile.exists()) mFile.parentFile.mkdirs()
            return try {
                outputStream = FileOutputStream(mFile)
                val buffer = ByteArray(4 * 1024)
                var lenght = 0
                while (inputStream.read(buffer).also { lenght = it } > 0) {
                    outputStream.write(buffer, 0, lenght)
                }
                outputStream.flush()
                mFile
            } catch (e: IOException) {
                Log.e(TAG, "写入文件失败，原因：" + e.message)
                throw e
            } finally {
                try {
                    inputStream.close()
                    if (outputStream != null) {
                        outputStream.close()
                        outputStream = null
                    }
                } catch (e: IOException) {
                }
            }
        }

        /**
         * 指定目录写入文件内容
         *
         * @param filePath
         * 文件路径+文件名
         * @param bitmap
         * 文件内容
         * @throws IOException
         */
        @Throws(IOException::class)
        fun saveAsJPEG(bitmap: Bitmap, filePath: String?) {
            var fos: FileOutputStream? = null
            try {
                val file = File(filePath)
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
            } finally {
                fos?.close()
            }
        }

        /**
         * 指定目录写入文件内容
         *
         * @param filePath
         * 文件路径+文件名
         * @param bitmap
         * 文件内容
         * @throws IOException
         */
        @Throws(IOException::class)
        fun saveAsPNG(bitmap: Bitmap, filePath: String?) {
            var fos: FileOutputStream? = null
            try {
                val file = File(filePath)
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
            } finally {
                fos?.close()
            }
        }

        /**
         * 将文件转成字符串
         *
         * @param file
         * 文件
         * @return
         * @throws Exception
         */
        @Throws(Exception::class)
        fun getStringFromFile(file: File?): String {
            val fin = FileInputStream(file)
            val ret = convertStreamToString(fin)
            // Make sure you close all streams.
            fin.close()
            return ret
        }

        /**
         * 复制文件
         * @param in
         * @param out
         */
        fun copyFile(`in`: InputStream, out: OutputStream) {
            try {
                val b = ByteArray(2 * 1024 * 1024) //2M memory
                var len = -1
                while (`in`.read(b).also { len = it } > 0) {
                    out.write(b, 0, len)
                    out.flush()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
//            closeIO(in, out);
            }
        }

        /**
         * 分享文件
         * @param context
         * @param title
         * @param filePath
         */
        fun shareFile(context: Context, title: String?, filePath: String) {
            val intent = Intent(Intent.ACTION_SEND)
            val uri = Uri.parse("file://$filePath")
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            context.startActivity(Intent.createChooser(intent, title))
        }

        /**
         * 压缩
         * @param is
         * @param os
         */
        fun zip(`is`: InputStream, os: OutputStream?) {
            var gzip: GZIPOutputStream? = null
            try {
                gzip = GZIPOutputStream(os)
                val buf = ByteArray(1024)
                var len: Int
                while (`is`.read(buf).also { len = it } != -1) {
                    gzip.write(buf, 0, len)
                    gzip.flush()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
//            closeIO(is, gzip);
            }
        }

        /**
         * 解压
         * @param is
         * @param os
         */
        fun unzip(`is`: InputStream?, os: OutputStream) {
            var gzip: GZIPInputStream? = null
            try {
                gzip = GZIPInputStream(`is`)
                val buf = ByteArray(1024)
                var len: Int
                while (gzip.read(buf).also { len = it } != -1) {
                    os.write(buf, 0, len)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
//            closeIO(gzip, os);
            }
        }

        /**
         * 格式化文件大小
         * @param context
         * @param size
         * @return
         */
        fun formatFileSize(context: Context?, size: Long): String {
            return Formatter.formatFileSize(context, size)
        }

        /**
         * 将输入流写入到文件
         * @param is
         * @param file
         */
        fun Stream2File(`is`: InputStream, file: File?) {
            val b = ByteArray(1024)
            var len: Int
            var os: FileOutputStream? = null
            try {
                os = FileOutputStream(file)
                while (`is`.read(b).also { len = it } != -1) {
                    os.write(b, 0, len)
                    os.flush()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
//            closeIO(is, os);
            }
        }

        /**
         * 创建文件夹(支持覆盖已存在的同名文件夹)
         * @param filePath
         * @param recreate
         * @return
         */
        fun createFolder(filePath: String?, recreate: Boolean): Boolean {
            val folderName = getFolderName(filePath)
            if (folderName == null || folderName.length == 0 || folderName.trim { it <= ' ' }.length == 0) {
                return false
            }
            val folder = File(folderName)
            return if (folder.exists()) {
                if (recreate) {
                    deleteFile(folderName)
                    folder.mkdirs()
                } else {
                    true
                }
            } else {
                folder.mkdirs()
            }
        }

        fun deleteFile(filename: String?): Boolean {
            return File(filename).delete()
        }

        /**
         * 获取文件名
         * @param filePath
         * @return
         */
//        fun getFileName(filePath: String): String {
//            if (StringUtils.isEmpty(filePath)) {
//                return filePath
//            }
//            val filePosi = filePath.lastIndexOf(File.separator)
//            return if (filePosi == -1) filePath else filePath.substring(filePosi + 1)
//        }

        /**
         * 重命名文件\文件夹
         * @param filepath
         * @param newName
         * @return
         */
        fun rename(filepath: String?, newName: String?): Boolean {
            val file = File(filepath)
            return file.exists() && file.renameTo(File(newName))
        }

        /**
         * 获取文件夹名称
         * @param filePath
         * @return
         */
        fun getFolderName(filePath: String?): String? {
            if (filePath == null || filePath.length == 0 || filePath.trim { it <= ' ' }.length == 0) {
                return filePath
            }
            val filePos = filePath.lastIndexOf(File.separator)
            return if (filePos == -1) "" else filePath.substring(0, filePos)
        }

        /**
         * 获取文件夹下所有文件
         * @param path
         * @return
         */
        fun getFilesArray(path: String?): ArrayList<File> {
            val file = File(path)
            val files = file.listFiles()
            val listFile = ArrayList<File>()
            if (files != null) {
                for (i in files.indices) {
                    if (files[i].isFile) {
                        listFile.add(files[i])
                    }
                    if (files[i].isDirectory) {
                        listFile.addAll(getFilesArray(files[i].toString()))
                    }
                }
            }
            return listFile
        }

        /**
         * 打开图片
         * @param mContext
         * @param imagePath
         */
        fun openImage(mContext: Context, imagePath: String?) {
            val intent = Intent("android.intent.action.VIEW")
            intent.addCategory("android.intent.category.DEFAULT")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri = Uri.fromFile(File(imagePath))
            intent.setDataAndType(uri, "image/*")
            mContext.startActivity(intent)
        }

        /**
         * 打开视频
         * @param mContext
         * @param videoPath
         */
        fun openVideo(mContext: Context, videoPath: String?) {
            val intent = Intent("android.intent.action.VIEW")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("oneshot", 0)
            intent.putExtra("configchange", 0)
            val uri = Uri.fromFile(File(videoPath))
            intent.setDataAndType(uri, "video/*")
            mContext.startActivity(intent)
        }

        /**
         * 打开URL
         * @param mContext
         * @param url
         */
        fun openURL(mContext: Context, url: String?) {
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            mContext.startActivity(intent)
        }

        /**
         * 下载文件
         * @param context
         * @param fileurl
         */
        fun downloadFile(context: Context, fileurl: String) {
            val request = DownloadManager.Request(Uri.parse(fileurl))
            request.setDestinationInExternalPublicDir(
                "/Download/",
                fileurl.substring(fileurl.lastIndexOf("/") + 1)
            )
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
        }



        /**
         * 在没有sdcard时获取内部存储路径
         * @return
         */
        fun getInternalPath(context: Context): String {
//        LogUtils.d(TAG, "internal path is " + context.getFilesDir().getPath());
            return context.filesDir.path + context.packageName
        }

        /**
         * 检测是否SDCard是否存在
         * @return true：存在 false：不存在
         */
        val isExistSDCard: Boolean
            get() = (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)

        /**
         * 删除目录下所有文件
         * @param Path    路径
         */
        fun deleteAllFile(Path: String?) {

            // 删除目录下所有文件
            val path = File(Path)
            val files = path.listFiles()
            if (files != null) {
                for (tfi in files) {
                    if (tfi.isDirectory) {
                        println(tfi.name)
                    } else {
                        tfi.delete()
                    }
                }
            }
        }

        /**
         * 删除方法, 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理
         * @param directory
         * @return void
         */
        fun deleteFilesByDirectory(directory: File?) {
            if (directory != null && directory.exists() && directory.isDirectory) {
                for (file in directory.listFiles()) {
                    if (file.isDirectory) deleteFilesByDirectory(file)
                    file.delete()
                }
            } else {
                Log.i("", "This directory is file, not execute delete")
            }
        }
    }

    init {
        throw UnsupportedOperationException("cannot be instantiated")
    }
}

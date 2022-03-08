package com.qipa.jetpackmvvm.util

import java.io.*
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.channels.ReadableByteChannel


/**
 * 类描述：
 * 创建人：Administrator
 * 修改备注：
 * 使用nio以提高性能
 */
object NioFileUtiles {
    @Throws(IOException::class)
    fun writeToFileNio(dataIns: InputStream, target: File?) {
        var fo: FileOutputStream? = null
        var src: ReadableByteChannel? = null
        var out: FileChannel? = null
        try {
            val len = dataIns.available()
            src = Channels.newChannel(dataIns)
            fo = FileOutputStream(target)
            out = fo.channel
            out.transferFrom(src, 0, len.toLong())
        } finally {
            fo?.close()
            src?.close()
            out?.close()
        }
    }

    @Throws(IOException::class)
    fun writeToFileNio(data: ByteArray, target: File?) {
        var fo: FileOutputStream? = null
        var src: ReadableByteChannel? = null
        var out: FileChannel? = null
        try {
            src = Channels.newChannel(ByteArrayInputStream(data))
            fo = FileOutputStream(target)
            out = fo.channel
            out.transferFrom(src, 0, data.size.toLong())
        } finally {
            fo?.close()
            src?.close()
            out?.close()
        }
    }

    /**
     * 复制文件
     *
     * @param source - 源文件
     * @param target - 目标文件
     */
    fun copyFileNio(source: File?, target: File?) {
        var fi: FileInputStream? = null
        var fo: FileOutputStream? = null
        var `in`: FileChannel? = null
        var out: FileChannel? = null
        try {
            fi = FileInputStream(source)
            fo = FileOutputStream(target)
            `in` = fi.channel // 得到对应的文件通道
            out = fo.channel // 得到对应的文件通道
            `in`.transferTo(0, `in`.size(), out) // 连接两个通道，并且从in通道读取，然后写入out通道
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fi!!.close()
                `in`!!.close()
                fo!!.close()
                out!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    fun writeToFileIO(dataIns: InputStream, target: File?) {
        val BUFFER = 1024
        val bos = BufferedOutputStream(
            FileOutputStream(target)
        )
        var count: Int
        val data = ByteArray(BUFFER)
        while (dataIns.read(data, 0, BUFFER).also { count = it } != -1) {
            bos.write(data, 0, count)
        }
        bos.close()
    }
}

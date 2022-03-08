package com.qipa.qipaimbase.utils

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {
    const val IM_DEOM_ROOT = "imdemo"
    const val VOICE_PATH_SEND = IM_DEOM_ROOT + "/" + "voice_send/"
    const val VOICE_PATH_RECEIVE = IM_DEOM_ROOT + "/" + "voice_receive/"
    fun createFile(file: File?): Boolean {
        requireNotNull(file) { "file is null" }
        if (file.exists()) {
            return true
        }
        val parentFile = file.parentFile
        return if (!parentFile.exists() && !parentFile.mkdirs()) {
            false
        } else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun removeFile(fileName: String?): Boolean {
        if (fileName == null) {
            return false
        }
        val file = File(fileName)
        if (!file.exists()) {
            return false
        }
        return if (file.isFile) {
            file.delete()
        } else false
    }

    fun createImageFile(context: Context): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var imageFile: File? = null
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return imageFile
    }
}

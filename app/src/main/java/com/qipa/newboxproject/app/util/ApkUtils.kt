package com.qipa.newboxproject.app.util

import android.content.Context
import android.content.Intent

import com.blankj.utilcode.util.AppUtils

import android.os.Build

import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File


object ApkUtils {
    fun installApk(context: Context, downloadApk: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        val file = File(downloadApk)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val apkUri: Uri = FileProvider.getUriForFile(
                context,
                AppUtils.getAppPackageName() + ".fileprovider",
                file
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val uri: Uri = Uri.fromFile(file)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
        }
        context.startActivity(intent)
    }
}
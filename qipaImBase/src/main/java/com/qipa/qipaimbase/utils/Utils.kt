package com.qipa.qipaimbase.utils

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentUris
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.graphics.drawable.DrawableCompat
import com.qipa.qipaimbase.ImBaseBridge
import java.lang.Exception


object Utils {
    fun keyBoard(context: Context, view: View, show: Boolean) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (show) {
            imm.showSoftInputFromInputMethod(view.windowToken, 0)
        } else {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun tintDrawable(drawable: Drawable?, colors: ColorStateList?): Drawable? {
        val wrappedDrawable: Drawable? = drawable?.let { DrawableCompat.wrap(it) }
        if (wrappedDrawable != null) {
            DrawableCompat.setTintList(wrappedDrawable, colors)
        }
        return wrappedDrawable
    }

    fun dimenToPix(context: Context, dimenRes: Int): Int {
        return context.resources.getDimensionPixelSize(dimenRes)
    }

    fun getFilePath(context: Context?, uri: Uri): String? {
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val sdkVersion = Build.VERSION.SDK_INT
            return if (sdkVersion >= 19) { // api >= 19
                context?.let { getRealPathFromUriAboveApi19(it, uri) }
            } else { // api < 19
                context?.let { getRealPathFromUriBelowAPI19(it, uri) }
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * ??????api19??????(?????????api19),??????uri???????????????????????????
     *
     * @param context ???????????????
     * @param uri     ?????????Uri
     * @return ??????Uri?????????????????????, ????????????????????????????????????, ????????????null
     */
    private fun getRealPathFromUriBelowAPI19(context: Context, uri: Uri): String? {
        return getDataColumn(context, uri, null, null)
    }

    /**
     * ???????????????????????? _data ???????????????Uri?????????????????????
     *
     * @return
     */
    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var path: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(projection[0])
                path = cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            cursor?.close()
        }
        return path
    }

    /**
     * ??????api19?????????,??????uri???????????????????????????
     *
     * @param context ???????????????
     * @param uri     ?????????Uri
     * @return ??????Uri?????????????????????, ????????????????????????????????????, ????????????null
     */
    private fun getRealPathFromUriAboveApi19(context: Context, uri: Uri): String? {
        var filePath: String? = null
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ?????????document????????? uri, ?????????document id???????????????
            val documentId = DocumentsContract.getDocumentId(uri)
            if (isMediaDocument(uri)) { // MediaProvider
                // ??????':'??????
                val id = documentId.split(":").toTypedArray()[1]
                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(id)
                filePath = getDataColumn(
                    context,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    selection,
                    selectionArgs
                )
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(documentId)
                )
                filePath = getDataColumn(
                    context,
                    contentUri,
                    null,
                    null
                )
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // ????????? content ????????? Uri
            filePath =
                getDataColumn(context, uri, null, null)
        } else if ("file" == uri.scheme) {
            // ????????? file ????????? Uri,?????????????????????????????????
            filePath = uri.path
        }
        return filePath
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun getScreenSize(context: Context): IntArray {
        val wm = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width = wm.defaultDisplay.width
        val height = wm.defaultDisplay.height
        return intArrayOf(width, height)
    }

    fun copyToClipBoard(context: Context, textContent: String?) {
        //???????????????????????????
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // ?????????????????????ClipData
        val mClipData = ClipData.newPlainText("Label", textContent)
        // ???ClipData?????????????????????????????????
        cm.setPrimaryClip(mClipData)
    }

    fun getDrawableByName(name: String?): Int? {
        val appInfo: ApplicationInfo? =
            ImBaseBridge.instance?.application?.getApplicationInfo()
        return ImBaseBridge.instance?.application?.getResources()?.getIdentifier(name, "drawable", appInfo?.packageName)
    }

    fun checkAudioPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
//                ToastUtils.showText(context, "????????????????????????????????????")
                return false
            }
        }
        return true
    }

    fun generateAtMsg(name: String?, content: String?): SpannableString? {
        if (TextUtils.isEmpty(content)) {
            return null
        }
        val atPre = "[??????@???]"
        val contentShow = SpannableString(String.format("%s%s:%s", atPre, name, content))
        contentShow.setSpan(
            ForegroundColorSpan(Color.RED),
            0,
            atPre.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return contentShow
    }

    fun getAppVersionCode(context: Context): Int {
        try {
            val pm = context.packageManager
            val pi = pm.getPackageInfo(context.packageName, 0)
            return pi.versionCode
        } catch (e: Exception) {
        }
        return 0
    }

    fun getAppVersionName(context: Context): String {
        try {
            val pm = context.packageManager
            val pi = pm.getPackageInfo(context.packageName, 0)
            return pi.versionName
        } catch (e: Exception) {
        }
        return ""
    }
}
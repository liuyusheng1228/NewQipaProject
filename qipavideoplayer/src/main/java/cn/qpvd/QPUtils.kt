package cn.qpvd

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.net.ConnectivityManager
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewConfiguration
import android.view.Window
import android.view.WindowManager
import java.lang.StringBuilder
import java.util.*

object QPUtils {
    const val TAG = "QPVD"
    var SYSTEM_UI = 0
    fun stringForTime(timeMs: Long): String {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00"
        }
        val totalSeconds = timeMs / 1000
        val seconds = (totalSeconds % 60).toInt()
        val minutes = (totalSeconds / 60 % 60).toInt()
        val hours = (totalSeconds / 3600).toInt()
        val stringBuilder = StringBuilder()
        val mFormatter = Formatter(stringBuilder, Locale.getDefault())
        return if (hours > 0) {
            mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            mFormatter.format("%02d:%02d", minutes, seconds).toString()
        }
    }

    /**
     * This method requires the caller to hold the permission ACCESS_NETWORK_STATE.
     *
     * @param context context
     * @return if wifi is connected,return true
     */
    fun isWifiConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI
    }

    /**
     * Get activity from context object
     *
     * @param context context
     * @return object of Activity or null if it is not Activity
     */
    fun scanForActivity(context: Context?): Activity? {
        if (context == null) return null
        if (context is Activity) {
            return context
        } else if (context is ContextWrapper) {
            return scanForActivity(context.baseContext)
        }
        return null
    }

    fun setRequestedOrientation(context: Context?, orientation: Int) {
        if (scanForActivity(context) != null) {
            scanForActivity(context)!!.requestedOrientation = orientation
        } else {
            scanForActivity(context)!!.requestedOrientation = orientation
        }
    }

    fun getWindow(context: Context?): Window {
        return if (scanForActivity(context) != null) {
            scanForActivity(context)!!.window
        } else {
            scanForActivity(context)!!.window
        }
    }

    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun saveProgress(context: Context, url: Any, progress: Long) {
        var progress = progress
        if (!QPvd.SAVE_PROGRESS) return
        Log.i(TAG, "saveProgress: $progress")
        if (progress < 5000) {
            progress = 0
        }
        val spn = context.getSharedPreferences(
            "JZVD_PROGRESS",
            Context.MODE_PRIVATE
        )
        val editor = spn.edit()
        editor.putLong("newVersion:$url", progress).apply()
    }

    fun getSavedProgress(context: Context, url: Any): Long {
        if (!QPvd.SAVE_PROGRESS) return 0
        val spn = context.getSharedPreferences(
            "JZVD_PROGRESS",
            Context.MODE_PRIVATE
        )
        return spn.getLong("newVersion:$url", 0)
    }

    /**
     * if url == null, clear all progress
     *
     * @param context context
     * @param url     if url!=null clear this url progress
     */
    fun clearSavedProgress(context: Context, url: Any?) {
        if (url == null) {
            val spn = context.getSharedPreferences(
                "JZVD_PROGRESS",
                Context.MODE_PRIVATE
            )
            spn.edit().clear().apply()
        } else {
            val spn = context.getSharedPreferences(
                "JZVD_PROGRESS",
                Context.MODE_PRIVATE
            )
            spn.edit().putLong("newVersion:$url", 0).apply()
        }
    }

    @SuppressLint("RestrictedApi")
    fun showStatusBar(context: Context?) {
        if (QPvd.TOOL_BAR_EXIST) {
            getWindow(context).clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    //如果是沉浸式的，全屏前就没有状态栏
    @SuppressLint("RestrictedApi")
    fun hideStatusBar(context: Context?) {
        if (QPvd.TOOL_BAR_EXIST) {
            getWindow(context).setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    @SuppressLint("NewApi")
    fun hideSystemUI(context: Context?) {
        var uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            uiOptions = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
        SYSTEM_UI = getWindow(context).decorView.systemUiVisibility
        getWindow(context).decorView.systemUiVisibility = uiOptions
    }

    @SuppressLint("NewApi")
    fun showSystemUI(context: Context?) {
        val uiOptions = View.SYSTEM_UI_FLAG_VISIBLE
        getWindow(context).decorView.systemUiVisibility = SYSTEM_UI
    }

    //获取状态栏的高度
    fun getStatusBarHeight(context: Context): Int {
        val resources: Resources = context.resources
        val resourceId: Int =
            resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    //获取NavigationBar的高度
    fun getNavigationBarHeight(context: Context): Int {
        val var1: Boolean = ViewConfiguration.get(context).hasPermanentMenuKey()
        var var2: Int
        return if (context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
                .also {
                    var2 = it
                } > 0 && !var1
        ) context.resources.getDimensionPixelSize(var2) else 0
    }

    //获取屏幕的宽度
    fun getScreenWidth(context: Context): Int {
        val dm: DisplayMetrics = context.resources.displayMetrics
        return dm.widthPixels
    }

    //获取屏幕的高度
    fun getScreenHeight(context: Context): Int {
        val dm: DisplayMetrics = context.resources.displayMetrics
        return dm.heightPixels
    }
}

package com.qipa.newboxproject.app.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import com.qipa.newboxproject.app.App
import com.qipa.qipaimbase.R2
import java.util.*

class AppMetaDataHelper private constructor() {
    private var metaBundle: Bundle? = null
    private fun getMetaBundle() {
        val context: Context = App.instance.applicationContext
        try {
            val info: ApplicationInfo = context.getPackageManager()
                .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
            if (info != null) {
                metaBundle = info.metaData
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    fun switchLocale(locale: LangUtils.RCLocale, context: Context) {
        val resources = context.resources
        val config = resources.configuration
        config.locale = locale.toLocale()
        if (Build.VERSION.SDK_INT < 24) {
            context.resources.updateConfiguration(config, resources.displayMetrics)
        }
        LangUtils.saveLocale(context, locale)
    }
    fun getAppLocale(context: Context?): LangUtils.RCLocale? {
        return LangUtils.getAppLocale(context!!)
    }
    fun getConfigurationContext(newBase: Context?): Context? {
        val context = LangUtils.getConfigurationContext(newBase!!)
        val configuration = context.resources.configuration
        return object : ContextThemeWrapper(context, R2.style.Theme_AppCompat_Empty) {
            override fun applyOverrideConfiguration(overrideConfiguration: Configuration) {
                if (overrideConfiguration != null) {
                    overrideConfiguration.setTo(configuration)
                }
                super.applyOverrideConfiguration(overrideConfiguration)
            }
        }
    }

    fun getSystemLocale(): Locale? {
        return LangUtils.getSystemLocale()
    }



    /**
     * 从manifestPlaceholders中获取定义的值
     * @param key
     * @return
     */
    fun getPlaceholderValue(key: String?): String {
        return if (metaBundle != null) {
            metaBundle!!.getString(key, "")
        } else ""
    }

    companion object {
        var instance: AppMetaDataHelper? = null
            get() {
                if (field == null) {
                    synchronized(AppMetaDataHelper::class.java) { field = AppMetaDataHelper() }
                }
                return field
            }
            private set
    }

    init {
        getMetaBundle()
    }
}


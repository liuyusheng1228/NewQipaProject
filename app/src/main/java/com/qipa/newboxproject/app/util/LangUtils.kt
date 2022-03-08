package com.qipa.newboxproject.app.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.*

object LangUtils {
    private const val LOCALE_CONF_FILE_NAME = "locale.config"
    private const val APP_LOCALE = "app_locale"
    private const val APP_PUSH_LANGUAGE = "app_push_language"
    private var systemLocale = Locale.getDefault()

    fun getConfigurationContext(context: Context): Context {
        val resources = context.resources
        val config = Configuration(resources.configuration)
        var configurationContext = context
        if (Build.VERSION.SDK_INT >= 24) {
            val localeList = LocaleList(*arrayOf(getAppLocale(context).toLocale()))
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
            configurationContext = context.createConfigurationContext(config)
        }
        return configurationContext
    }

    fun getAppLocale(context: Context): RCLocale {
        val sp = context.getSharedPreferences("locale.config", 0)
        val locale = sp.getString("app_locale", "auto")
        return RCLocale.valueOf(locale)
    }

    fun saveLocale(context: Context, locale: RCLocale) {
        val sp = context.getSharedPreferences("locale.config", 0)
        sp.edit().putString("app_locale", locale.value()).commit()
    }

//    fun getPushLanguage(context: Context): PushLanguage? {
//        val sp = context.getSharedPreferences("locale.config", 0)
//        val language = sp.getString("app_push_language", "")
//        return if (PushLanguage.ZH_CN.getMsg().equals(language)) {
//            PushLanguage.ZH_CN
//        } else {
//            if (PushLanguage.EN_US.getMsg().equals(language)) PushLanguage.EN_US else null
//        }
//    }
//
//    fun setPushLanguage(context: Context, pushLanguage: PushLanguage) {
//        val sp = context.getSharedPreferences("locale.config", 0)
//        sp.edit().putString("app_push_language", pushLanguage.getMsg()).commit()
//    }

    fun getCurrentLanguage(context: Context): RCLocale {
        val sp = context.getSharedPreferences("locale.config", 0)
        val locale = sp.getString("app_locale", "auto")
        return if ("auto" == locale) {
            if (systemLocale.toString() == "zh_CN") RCLocale.LOCALE_CHINA else RCLocale.LOCALE_US
        } else {
            RCLocale.valueOf(locale)
        }
    }
    fun getSystemLocale(): Locale? {
        return systemLocale
    }

    fun setSystemLocale(locale: Locale?) {
        systemLocale = locale
    }
    class RCLocale private constructor(private val rcLocale: String) {
        fun value(): String {
            return rcLocale
        }

        fun toLocale(): Locale {
            val locale: Locale
            locale = if (rcLocale == LOCALE_CHINA.value()) {
                Locale.CHINESE
            } else if (rcLocale == LOCALE_US.value()) {
                Locale.ENGLISH
            } else if (rcLocale == LOCALE_CHINA_TW.value()) {
                Locale.TAIWAN
            } else if (rcLocale == LOCALE_ARAB.value()) {
                Locale("ar")
            } else {
                systemLocale
            }
            return locale
        }

        companion object {
            val LOCALE_CHINA = RCLocale("zh")
            val LOCALE_CHINA_TW = RCLocale("rTW")
            val LOCALE_US = RCLocale("en")
            val LOCALE_ARAB = RCLocale("ar")
            val LOCALE_AUTO = RCLocale("auto")
            fun valueOf(rcLocale: String?): RCLocale {
                val locale: RCLocale
                locale = if (LOCALE_CHINA.value() == rcLocale) {
                    LOCALE_CHINA
                } else if (LOCALE_US.value() == rcLocale) {
                    LOCALE_US
                } else if (LOCALE_ARAB.value() == rcLocale) {
                    LOCALE_ARAB
                } else if (LOCALE_CHINA_TW.value() == rcLocale) {
                    LOCALE_CHINA_TW
                } else {
                    LOCALE_AUTO
                }
                return locale
            }
        }
    }
}
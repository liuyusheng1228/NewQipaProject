package com.qipa.newboxproject.app.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.*

class AppTask(context: Context) {
//    private val appsService: AppService
    private val context: Context

    /**
     * 获取当前的最新版本
     *
     * @return
     */
//    val newVersion: LiveData<Any>
//        get() = object : NetworkOnlyResource<VersionInfo?, VersionInfo?>() {
//            protected fun createCall(): LiveData<VersionInfo> {
//                return appsService.getNewVersion()
//            }
//
//            protected fun transformRequestType(response: VersionInfo): VersionInfo {
//                return response
//            }
//        }.asLiveData()

    /**
     * SDK 版本号
     *
     * @return
     */
//    val sDKVersion: String
//        get() = BuildVar.SDK_VERSION

    /**
     * 获取聊天室
     *
     * @return
     */
//    val discoveryChatRoom: LiveData<Any>
//        get() = object :
//            NetworkOnlyResource<List<ChatRoomResult?>?, Result<List<ChatRoomResult?>?>?>() {
//            protected fun createCall(): LiveData<Result<List<ChatRoomResult>>> {
//                return appsService.getDiscoveryChatRoom()
//            }
//        }.asLiveData()// RongConfigurationManager.getInstance().getAppLocale(context);

    /**
     * 获取当前app 的语音设置
     *
     * @return
     */
    open fun getLanguageLocal(): LangUtils.RCLocale? {
        var appLocale: LangUtils.RCLocale? = AppMetaDataHelper.instance?.getAppLocale(context) // RongConfigurationManager.getInstance().getAppLocale(context);
        if (appLocale === LangUtils.RCLocale.LOCALE_AUTO) {
            val systemLocale: Locale? = AppMetaDataHelper.instance?.getSystemLocale()
            appLocale = if (systemLocale?.language == Locale.CHINESE.language) {
                LangUtils.RCLocale.LOCALE_CHINA
            } else if (systemLocale?.language == Locale.ENGLISH.language) {
                LangUtils.RCLocale.LOCALE_US
            } else if (systemLocale?.language == Locale.TAIWAN.language) {
                LangUtils.RCLocale.LOCALE_CHINA_TW
            } else if (systemLocale?.language == Locale("ar").language) {
                LangUtils.RCLocale.LOCALE_ARAB
            } else {
                LangUtils.RCLocale.LOCALE_CHINA
            }
        }
        return appLocale
    }
    /**
     * 设置当前应用的 语音
     *
     * @param selectedLocale
     */
    fun changeLanguage(selectedLocale: LangUtils.RCLocale): Boolean {
        //todo
//        LangUtils.RCLocale appLocale = RongConfigurationManager.getInstance().getAppLocale(context);
//        if (selectedLocale == appLocale) {
//            return false;
//        }
        if (selectedLocale === LangUtils.RCLocale.LOCALE_CHINA) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val resources = context.resources
                val dm = resources.displayMetrics
                val config = resources.configuration
                val localeList = LocaleList(selectedLocale.toLocale())
                LocaleList.setDefault(localeList)
                config.setLocales(localeList)
                context.getApplicationContext().createConfigurationContext(config)
                Locale.setDefault(selectedLocale.toLocale())
                resources.updateConfiguration(config, dm)
                // 保存语言状态
                LangUtils.saveLocale(context, selectedLocale)

//                Resources resources = getResources();
//                Configuration config = resources.getConfiguration();
//                DisplayMetrics dm = resources.getDisplayMetrics();
//                if (language.equals("en")) {
//                    config.locale = Locale.ENGLISH;
//                } else {
//                    config.locale = Locale.SIMPLIFIED_CHINESE;
//                }
//                resources.updateConfiguration(config, dm);
            } else {
                AppMetaDataHelper.instance?.switchLocale(LangUtils.RCLocale.LOCALE_CHINA, context)
            }
//            setPushLanguage(RongIMClient.PushLanguage.ZH_CN)
        } else if (selectedLocale === LangUtils.RCLocale.LOCALE_US) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val resources = context.resources
                val dm = resources.displayMetrics
                val config = resources.configuration
                val localeList = LocaleList(selectedLocale.toLocale())
                LocaleList.setDefault(localeList)
                config.setLocales(localeList)
                context.getApplicationContext().createConfigurationContext(config)
                Locale.setDefault(selectedLocale.toLocale())
                resources.updateConfiguration(config, dm)
                LangUtils.saveLocale(context, selectedLocale)
                //                updateResources(context,"en");
            } else {
                AppMetaDataHelper.instance?.switchLocale(LangUtils.RCLocale.LOCALE_US, context)
            }
//            setPushLanguage(RongIMClient.PushLanguage.EN_US)
        } else if (selectedLocale === LangUtils.RCLocale.LOCALE_CHINA_TW) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val resources = context.resources
                val dm = resources.displayMetrics
                val config = resources.configuration
                val localeList = LocaleList(selectedLocale.toLocale())
                LocaleList.setDefault(localeList)
                config.setLocales(localeList)
                context.getApplicationContext().createConfigurationContext(config)
                Locale.setDefault(selectedLocale.toLocale())
                resources.updateConfiguration(config, dm)
                LangUtils.saveLocale(context, selectedLocale)
                //                updateResources(context,"en");
            } else {
                AppMetaDataHelper.instance?.switchLocale(LangUtils.RCLocale.LOCALE_CHINA_TW, context)
            }
//            setPushLanguage(RongIMClient.PushLanguage.EN_US)
        } else if (selectedLocale === LangUtils.RCLocale.LOCALE_ARAB) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val resources = context.resources
                val dm = resources.displayMetrics
                val config = resources.configuration
                val localeList = LocaleList(selectedLocale.toLocale())
                LocaleList.setDefault(localeList)
                config.setLocales(localeList)
                context.getApplicationContext().createConfigurationContext(config)
                Locale.setDefault(selectedLocale.toLocale())
                resources.updateConfiguration(config, dm)
                LangUtils.saveLocale(context, selectedLocale)
            } else {
                AppMetaDataHelper.instance?.switchLocale(LangUtils.RCLocale.LOCALE_ARAB, context)
            }
            //            setPushLanguage(RongIMClient.PushLanguage.LOCALE_ARAB);
        }
        return true
    }

//    /**
//     * 设置 push 的语言
//     *
//     * @param language
//     */
//    fun setPushLanguage(language: RongIMClient.PushLanguage) {
//        RongIMClient.getInstance()
//            .setPushLanguageCode(language.getMsg(), object : OperationCallback() {
//                fun onSuccess() {
//                    //设置成功也存起来
//                    //todo
//                    // RongConfigurationManager.getInstance().setPushLanguage(context, language);
//                }
//
//                fun onError(errorCode: RongIMClient.ErrorCode?) {}
//            })
//    }//TODO 获取是否是 Debug 模式

    /**
     * 是否是 Debug 模式
     *
     * @return
     */
    val isDebugMode: Boolean
        get() =//TODO 获取是否是 Debug 模式
            context.getSharedPreferences("config", Context.MODE_PRIVATE)
                .getBoolean("isDebug", false)

    companion object {
         fun updateResources(context: Context, language: String): Context {
            var context = context
            val locale = Locale(language)
            Locale.setDefault(locale)
            val res = context.resources
            val config = Configuration(res.configuration)
            if (Build.VERSION.SDK_INT >= 17) {
                config.setLocale(locale)
                context = context.createConfigurationContext(config)
            } else {
                config.locale = locale
                res.updateConfiguration(config, res.displayMetrics)
            }
            return context
        }
    }

    init {
        this.context = context.applicationContext
    }
}

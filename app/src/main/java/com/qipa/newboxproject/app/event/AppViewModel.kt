package com.qipa.newboxproject.app.event

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.qipa.jetpackmvvm.base.appContext
import com.qipa.jetpackmvvm.base.viewmodel.BaseViewModel
import com.qipa.jetpackmvvm.callback.livedata.event.EventLiveData
import com.qipa.newboxproject.app.util.AppTask
import com.qipa.newboxproject.app.util.CacheUtil
import com.qipa.newboxproject.app.util.LangUtils
import com.qipa.newboxproject.app.util.SettingUtil
import com.qipa.newboxproject.data.model.WxUserInfoResponse
import com.qipa.newboxproject.data.model.bean.UserInfo

/**
 * 描述　:APP全局的ViewModel，可以存放公共数据，当他数据改变时，所有监听他的地方都会收到回调,也可以做发送消息
 * 比如 全局可使用的 地理位置信息，账户信息,App的基本配置等等，
 */
class AppViewModel : BaseViewModel() {
    private var appTask: AppTask? = null
    //App的账户信息
    var userInfo = UnPeekLiveData.Builder<WxUserInfoResponse>().setAllowNullValue(true).create()

    //App主题颜色 中大型项目不推荐以这种方式改变主题颜色，比较繁琐耦合，且容易有遗漏某些控件没有设置主题色
    var appColor = EventLiveData<Int>()

    //App 列表动画
    var appAnimation = EventLiveData<Int>()

    //语言切换
    private var languageLocal: MutableLiveData<LangUtils.RCLocale>? = MutableLiveData<LangUtils.RCLocale>()
    init {
        //默认值保存的账户信息，没有登陆过则为null
        userInfo.value = CacheUtil.getUser()
        //默认值颜色
        appColor.value = SettingUtil.getColor(appContext)
        //初始化列表动画
        appAnimation.value = SettingUtil.getListMode()
        appTask = AppTask(appContext)
        // 语言
        languageLocal?.value = appTask?.getLanguageLocal()
    }

    /**
     * 当前本地语音
     *
     * @return
     */
    fun getLanguageLocal(): LiveData<LangUtils.RCLocale?>? {
        return languageLocal
    }
    /**
     * 当前本地语音
     *
     * @return
     */
    fun updateLanguageLocal() : Context {
       return AppTask.updateResources(appContext,languageLocal.toString())
    }

    /**
     * 切换语音
     *
     * @param selectedLocale
     */
    fun changeLanguage(selectedLocale: LangUtils.RCLocale?) {
        if (selectedLocale?.let { appTask?.changeLanguage(it) }!!) {
            languageLocal?.postValue(appTask?.getLanguageLocal())
        }
    }

}
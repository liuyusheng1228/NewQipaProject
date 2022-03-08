package com.qipa.newboxproject.app.base

import android.content.Context
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.qipa.jetpackmvvm.base.activity.BaseVmDbActivity
import com.qipa.jetpackmvvm.base.viewmodel.BaseViewModel
import com.qipa.newboxproject.app.ext.dismissLoadingExt
import com.qipa.newboxproject.app.ext.showLoadingExt
import com.qipa.newboxproject.app.util.AppMetaDataHelper

/**
 * 描述　: 你项目中的Activity基类，在这里实现显示弹窗，吐司，还有加入自己的需求操作 ，如果不想用Databind，请继承
 * BaseVmActivity例如
 * abstract class BaseActivity<VM : BaseViewModel> : BaseVmActivity<VM>() {
 */
abstract class BaseActivity<VM : BaseViewModel, DB : ViewDataBinding> : BaseVmDbActivity<VM, DB>() {

    abstract override fun layoutId(): Int

    override fun attachBaseContext(newBase: Context?) {
        val context: Context? =
            AppMetaDataHelper.instance?.getConfigurationContext(newBase)
        super.attachBaseContext(context)
    }
    abstract override fun initView(savedInstanceState: Bundle?)

    /**
     * 创建liveData观察者
     */
    override fun createObserver() {}

    /**
     * 打开等待框
     */
    override fun showLoading(message: String) {
        showLoadingExt(message)
    }

    /**
     * 关闭等待框
     */
    override fun dismissLoading() {
        dismissLoadingExt()
    }

   /* *//**
     * 在任何情况下本来适配正常的布局突然出现适配失效，适配异常等问题，只要重写 Activity 的 getResources() 方法
     *//*
    override fun getResources(): Resources {
        AutoSizeCompat.autoConvertDensityOfGlobal(super.getResources())
        return super.getResources()
    }*/
}
package com.qipa.jetpackmvvm.base.activity

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.SkinAppCompatDelegateImpl
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.arialyy.aria.core.Aria
import com.gyf.immersionbar.ktx.immersionBar
import com.qipa.jetpackmvvm.R
import com.qipa.jetpackmvvm.base.viewmodel.BaseViewModel
import com.qipa.jetpackmvvm.ext.getVmClazz
import com.qipa.jetpackmvvm.network.manager.NetState
import com.qipa.jetpackmvvm.network.manager.NetworkStateManager
import com.qipa.jetpackmvvm.util.AndroidBugFixUtils
import com.qipa.jetpackmvvm.util.ScreenAdaptUtil
import com.qipa.jetpackmvvm.widget.LoadingDialog

/**
 * 描述　: ViewModelActivity基类，把ViewModel注入进来了
 */
abstract class BaseVmActivity<VM : BaseViewModel> : AppCompatActivity() , View.OnClickListener{

    /**
     * 是否需要使用DataBinding 供子类BaseVmDbActivity修改，用户请慎动
     */
    private var isUserDb = false

    lateinit var mViewModel: VM

    abstract fun layoutId(): Int

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun showLoading(message: String = "请求网络中...")

    abstract fun dismissLoading()

    private lateinit var mLoadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        ARouter.getInstance().inject(this)
        Aria.download(this).register()
        if (!isUserDb) {
            setContentView(layoutId())
        } else {
            initDataBind()
        }

        mLoadingDialog = LoadingDialog(this, false)
        initBaseBar()
        init(savedInstanceState)
//        setNavigationBarColor(0xffff0000.toInt())
//        setStatusBarNoFillAndTransParent()
    }

    private fun init(savedInstanceState: Bundle?) {
        mViewModel = createViewModel()
        registerUiChange()
        initView(savedInstanceState)
        createObserver()
        NetworkStateManager.instance.mNetworkStateCallback.observeInActivity(this, Observer {
            onNetworkStateChanged(it)
        })
    }

    private fun initBaseBar(){
        immersionBar {
            statusBarColor(R.color.colorPrimary)
            navigationBarColor(R.color.colorPrimary)
        }
    }

//    override fun getDelegate(): AppCompatDelegate {
//        return SkinAppCompatDelegateImpl.get(this, this)
//    }

    /**
     * 网络变化监听 子类重写
     */
    open fun onNetworkStateChanged(netState: NetState) {}

    /**
     * 创建viewModel
     */
    private fun createViewModel(): VM {
        return ViewModelProvider(this).get(getVmClazz(this))
    }

    /**
     * 创建LiveData数据观察者
     */
    abstract fun createObserver()

    /**
     * 注册UI 事件
     */
    private fun registerUiChange() {
        //显示弹窗
        mViewModel.loadingChange.showDialog.observeInActivity(this, Observer {
            showLoading(it)
        })
        //关闭弹窗
        mViewModel.loadingChange.dismissDialog.observeInActivity(this, Observer {
            dismissLoading()
        })
    }

    /**
     * 将非该Activity绑定的ViewModel添加 loading回调 防止出现请求时不显示 loading 弹窗bug
     * @param viewModels Array<out BaseViewModel>
     */
    protected fun addLoadingObserve(vararg viewModels: BaseViewModel){
        viewModels.forEach {viewModel ->
            //显示弹窗
            viewModel.loadingChange.showDialog.observeInActivity(this, Observer {
                showLoading(it)
            })
            //关闭弹窗
            viewModel.loadingChange.dismissDialog.observeInActivity(this, Observer {
                dismissLoading()
            })
        }
    }



    fun userDataBinding(isUserDb: Boolean) {
        this.isUserDb = isUserDb
    }

    /**
     * 供子类BaseVmDbActivity 初始化Databinding操作
     */
    open fun initDataBind() {}

    override fun getResources(): Resources {
        //假设 设计图宽度为360
        return ScreenAdaptUtil.adaptWidth(super.getResources(), 360)
    }

    override fun onDestroy() {
        super.onDestroy()
        Aria.download(this).unRegister()
        // 解决某些特定机型会触发的Android本身的Bug
        AndroidBugFixUtils().fixSoftInputLeaks(this)
    }


    /**
     * show 加载中
     */
    fun showVLoading() {
        mLoadingDialog.showDialog(this, false)
    }

    /**
     * dismiss loading dialog
     */
    fun dismissVLoading() {
        mLoadingDialog.dismissDialog()
    }
}
package com.qipa.newboxproject.ui.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import cn.qpvd.QPvd
import com.blankj.utilcode.util.ToastUtils
import com.qipa.jetpackmvvm.network.manager.NetState
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.appViewModel
import com.qipa.newboxproject.app.base.BaseActivity
import com.qipa.newboxproject.app.upload.bean.CheckVersionResult
import com.qipa.newboxproject.app.weight.dialog.UpdateDialog
import com.qipa.newboxproject.databinding.ActivityMainBinding
import com.qipa.newboxproject.viewmodel.state.LoginFragmentViewModel
import com.qipa.newboxproject.viewmodel.state.MainViewModel
import java.security.MessageDigest


class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>()  {

    private val mTheme = 0
    private var mDialog: UpdateDialog? = null
    var exitTime = 0L
    private var checkVersionResult: CheckVersionResult? = null
    override fun layoutId() = R.layout.activity_main
    private val isTokenFlag //是否是token登录
            = false
    private var mFragmentViewModel: LoginFragmentViewModel? = null
    override fun initView(savedInstanceState: Bundle?) {

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val nav = Navigation.findNavController(this@MainActivity, R.id.host_fragment)
                if (nav.currentDestination != null && nav.currentDestination!!.id != R.id.mainfragment) {
                    //如果当前界面不是主页，那么直接调用返回即可
                    nav.navigateUp()
                } else {
                    //是主页
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        ToastUtils.showShort(getString(R.string.press_again_to_exit_the_program))
                        exitTime = System.currentTimeMillis()
                    } else {
                        finish()
                    }
                }
            }
        })
        appViewModel.appColor.value?.let {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
//            supportActionBar?.setBackgroundDrawable(ColorDrawable(it))
//            StatusBarUtil.setTranslucentDiff(this)
        }
        val temp = ImageView(this)

//        org.alee.component.skin.page.WindowManager.getInstance().getWindowProxy(this)
//            .addEnabledThemeSkinView(temp, SkinElement("src", R.mipmap.ic_launcher))
        mDialog = UpdateDialog(this,null)
//        ThemeSkinService.getInstance().subscribeSwitchThemeSkin(this)
      
        getFaceBookKey()
//        onThemeSkinSwitch()
//        ThemeSkinService.getInstance().switchThemeSkin(mTheme)
//        mDialog?.show()
//        StatusBarUtil.setColor(this, it, 0)
    }

    private fun getFaceBookKey(){
        try {
            var i = 0
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                i++
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val KeyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                //KeyHash 就是你要的，不用改任何代码  复制粘贴 ;
                Log.e("获取应用KeyHash", "KeyHash: $KeyHash")
            }
        } catch (e: Exception) {
        }
    }

    override fun onBackPressed() {

        if (QPvd.backPress()) {
            return
        }
        super.onBackPressed()
    }

    override fun createObserver() {
//        appViewModel.appColor.observeInActivity(this, Observer {
//            supportActionBar?.setBackgroundDrawable(ColorDrawable(it))
//            StatusBarUtil.setTranslucentDiff(this)
//        })

        loginHuanXin()
    }

    private fun loginHuanXin(){
        mFragmentViewModel = ViewModelProvider(this).get(
            LoginFragmentViewModel::class.java
        )
        mFragmentViewModel?.login("liuyusheng", "123456", false)
        mFragmentViewModel?.getLoginObservable()?.observe(this,{
           Log.i("Api","登陆"+it.errorCode+it.data+it.status)
        })

    }

    /**
     * 示例，在Activity/Fragment中如果想监听网络变化，可重写onNetworkStateChanged该方法
     */
    override fun onNetworkStateChanged(netState: NetState) {
        super.onNetworkStateChanged(netState)
        if (netState.isSuccess) {
            Toast.makeText(applicationContext, "我特么终于有网了啊!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "我特么怎么断网了!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }


}
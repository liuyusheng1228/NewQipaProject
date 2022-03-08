package com.qipa.newboxproject.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2
import com.mob.MobSDK
import com.zhpan.bannerview.BannerViewPager
import kotlinx.android.synthetic.main.activity_welcome.*
import com.qipa.jetpackmvvm.base.viewmodel.BaseViewModel
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseActivity
import com.qipa.newboxproject.app.util.CacheUtil
import com.qipa.newboxproject.app.util.SettingUtil
import com.qipa.newboxproject.app.weight.banner.WelcomeBannerAdapter
import com.qipa.newboxproject.app.weight.banner.WelcomeBannerViewHolder
import com.qipa.newboxproject.databinding.ActivityWelcomeBinding
import com.qipa.jetpackmvvm.ext.view.gone
import com.qipa.jetpackmvvm.ext.view.visible
import com.qipa.newboxproject.app.appViewModel
import com.qipa.newboxproject.app.util.LangUtils


/**
 * 描述　:
 */
@Suppress("DEPRECATED_IDENTITY_EQUALS")
class WelcomeActivity : BaseActivity<BaseViewModel, ActivityWelcomeBinding>() {

    private var resList = arrayOf("唱", "跳", "r a p")

    private lateinit var mViewPager: BannerViewPager<String, WelcomeBannerViewHolder>

    override fun layoutId() = R.layout.activity_welcome

    override fun initView(savedInstanceState: Bundle?) {
        //防止出现按Home键回到桌面时，再次点击重新进入该界面bug
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT !== 0) {
            finish()
            return
        }
        MobSDK.submitPolicyGrantResult(true,null)
        mDatabind.click = ProxyClick()
        welcome_baseview.setBackgroundColor(SettingUtil.getColor(this))
        mViewPager = findViewById(R.id.banner_view)
        if (CacheUtil.isFirst()) {
            //是第一次打开App 显示引导页
            welcome_image.gone()
            mViewPager.apply {
                adapter = WelcomeBannerAdapter()
                setLifecycleRegistry(lifecycle)
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        if (position == resList.size - 1) {
                            welcomeJoin.visible()
                        } else {
                            welcomeJoin.gone()
                        }
                    }
                })
                create(resList.toList())
            }
        } else {
            //不是第一次打开App 0.3秒后自动跳转到主页
            welcome_image.visible()
            mViewPager.postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                //带点渐变动画
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }, 300)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            initPermission()
        }
//        changeLanguage(LangUtils.RCLocale.LOCALE_CHINA_TW)
    }

    private fun changeLanguage(locale: LangUtils.RCLocale) {
            appViewModel.changeLanguage(locale)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initPermission(){
        // push
//        PhotonPushManager.getInstance().logPushClick(getIntent());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
                ), 101
            )
        }
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }

    inner class ProxyClick {
        fun toMain() {
            CacheUtil.setFirst(false)
            startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
            finish()
            //带点渐变动画
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

}
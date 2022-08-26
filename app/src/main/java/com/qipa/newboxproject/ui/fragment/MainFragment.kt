package com.qipa.newboxproject.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.forEach
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.qipa.jetpackmvvm.lottile.LottieAnimation
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.appViewModel
import com.qipa.newboxproject.databinding.FragmentMainBinding
import kotlinx.android.synthetic.main.fragment_main.*
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.app.ext.initMain
import com.qipa.newboxproject.app.ext.interceptLongClick
import com.qipa.newboxproject.app.ext.setUiTheme
import com.qipa.newboxproject.viewmodel.state.MainViewModel

/**
 * 描述　:项目主页Fragment
 */
class MainFragment : BaseFragment<MainViewModel, FragmentMainBinding>() {

    override fun layoutId() = R.layout.fragment_main

    private val navigationAnimationList = arrayListOf(
        LottieAnimation.LIVE,
        LottieAnimation.GAME,
        LottieAnimation.MESSAGE,
        LottieAnimation.PALY,
        LottieAnimation.MINE
    )
    private val navigationTitleList = arrayListOf(
        "首页",
        "直播",
        "消息",
        "福利",
        "我的"
    )

    private var mPreClickPosition = 0

    override fun initView(savedInstanceState: Bundle?) {
        //初始化viewpager2
        mainViewpager.initMain(this)
        //初始化 bottomBar
//        mainBottoms.init{
//
//            when (it) {
//                R.id.menu_main ->{
//                    mPreClickPosition =0
//                    mainViewpager.setCurrentItem(0, false)
//                }
//
//                R.id.menu_project ->{
//                    mPreClickPosition =1
//                    mainViewpager.setCurrentItem(1, false)
//                }
//
//                R.id.menu_system -> {
//                    mPreClickPosition =2
//                    mainViewpager.setCurrentItem(2, false)
//                }
//                R.id.menu_public -> {
//                    mPreClickPosition =3
//                    mainViewpager.setCurrentItem(3, false)
//                }
//                R.id.menu_me -> {
//                    mPreClickPosition =4
//                    mainViewpager.setCurrentItem(4, false)
//                }
//            }
//        }
//        mainBottoms.setTextSize(10f)
//        mainBottoms.interceptLongClick(R.id.menu_main,R.id.menu_project,R.id.menu_system,R.id.menu_public,R.id.menu_me)

    }

    override fun createObserver() {
//        appViewModel.appColor.observeInFragment(this, Observer {
//            setUiTheme(it, mainBottom)
//        })
        initBottomNavigationView()

    }

    /**
     * 初始化底部BottomNavigationView
     */
    private fun initBottomNavigationView() {
        mainBottoms.menu.apply {

            for (i in 0 until navigationTitleList.size) {
                add(Menu.NONE, i, Menu.NONE, navigationTitleList[i])
            }
            setLottieDrawable(getLottieAnimationList())
        }

        Log.i("Api","ssss")
        initListeners()
    }

    /**
     * 初始化监听事件
     */
    private fun initListeners() {
        mainBottoms.setOnNavigationItemSelectedListener {
            handleNavigationItem(it)
            // 联动 ViewPager2
            mainViewpager.setCurrentItem(it.itemId, true)
            return@setOnNavigationItemSelectedListener true
        }
        mainBottoms.setOnNavigationItemReselectedListener {
            handleNavigationItem(it)
        }
        // 默认选中第一个
        mainBottoms.selectedItemId = 0
        // 处理长按 MenuItem 提示 TooltipText
        mainBottoms.menu.forEach {
            val menuItemView = mainBottoms.findViewById(it.itemId) as BottomNavigationItemView
            menuItemView.setOnLongClickListener {
                true
            }
        }
        showMsgDot(6)

    }

    private fun Menu.setLottieDrawable(lottieAnimationList: ArrayList<LottieAnimation>) {
        for (i in 0 until 5) {
            val item = findItem(i)
            Log.i("Api","animation:"+getLottieDrawable(lottieAnimationList[i], mainBottoms))
            item.icon = getLottieDrawable(lottieAnimationList[i], mainBottoms)
        }
    }

    private fun handleNavigationItem(item: MenuItem) {
        handlePlayLottieAnimation(item)
        mPreClickPosition = item.itemId
    }

    private fun handlePlayLottieAnimation(item: MenuItem) {
        val currentIcon = item.icon as? LottieDrawable
        currentIcon?.apply {
            playAnimation()
        }
        // 处理 tab 切换，icon 对应调整
        if (item.itemId != mPreClickPosition) {
            mainBottoms.menu.findItem(mPreClickPosition).icon =
                getLottieDrawable(getLottieAnimationList()[mPreClickPosition], mainBottoms)
        }
    }

    /**
     * 获取 Lottie Drawable
     */
    private fun getLottieDrawable(
        animation: LottieAnimation,
        bottomNavigationView: BottomNavigationView
    ): LottieDrawable {
        return LottieDrawable().apply {
            val result = LottieCompositionFactory.fromAssetSync(
                bottomNavigationView.context.applicationContext, animation.value
            )
            Log.i("Api","animation"+animation.value+""+result.value)
            callback = bottomNavigationView
            composition = result.value
        }
    }

    /**
     * 获取不同模式下 Lottie json 文件
     */
    private fun getLottieAnimationList(): ArrayList<LottieAnimation> {
        return navigationAnimationList
    }

    /**
     * 展示底部消息Item角标
     */
    private fun showMsgDot(count: Int) {
        val badge = mainBottoms.getOrCreateBadge(2)
        badge?.badgeGravity = BadgeDrawable.TOP_END
        badge?.backgroundColor = Color.RED
        badge?.verticalOffset = 22
        badge?.horizontalOffset = 23
        badge?.maxCharacterCount = 3
        badge?.number = count
        badge?.isVisible = count > 0
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}
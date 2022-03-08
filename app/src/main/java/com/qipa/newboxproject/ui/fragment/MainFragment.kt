package com.qipa.newboxproject.ui.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
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

    override fun initView(savedInstanceState: Bundle?) {
        //初始化viewpager2
        mainViewpager.initMain(this)
        //初始化 bottomBar
        mainBottoms.init{
            when (it) {
                R.id.menu_main -> mainViewpager.setCurrentItem(0, false)
                R.id.menu_project -> mainViewpager.setCurrentItem(1, false)
                R.id.menu_system -> mainViewpager.setCurrentItem(2, false)
                R.id.menu_public -> mainViewpager.setCurrentItem(3, false)
                R.id.menu_me -> mainViewpager.setCurrentItem(4, false)
            }
        }
//        mainBottoms.setTextSize(10f)
//        mainBottoms.interceptLongClick(R.id.menu_main,R.id.menu_project,R.id.menu_system,R.id.menu_public,R.id.menu_me)
    }

    override fun createObserver() {
//        appViewModel.appColor.observeInFragment(this, Observer {
//            setUiTheme(it, mainBottom)
//        })
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}
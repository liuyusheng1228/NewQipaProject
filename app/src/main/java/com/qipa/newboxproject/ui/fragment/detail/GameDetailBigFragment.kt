package com.qipa.newboxproject.ui.fragment.detail

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import cn.qpvd.QPvd
import cn.sharesdk.framework.Platform
import cn.sharesdk.onekeyshare.OnekeyShare
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.bindViewPager2
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.databinding.FragmentGameDetailBinding
import com.qipa.newboxproject.viewmodel.state.GameDetailModel
import kotlinx.android.synthetic.main.fragment_game_detail.*
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator

import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import com.blankj.utilcode.util.SizeUtils
import com.mob.MobSDK
import cn.sharesdk.framework.PlatformActionListener

import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback
import com.blankj.utilcode.util.AppUtils
import com.google.android.material.appbar.AppBarLayout
import com.qipa.jetpackmvvm.ext.download.DownloadResultState
import com.qipa.jetpackmvvm.ext.download.FileTool.getBasePath
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.navigateAction
import com.qipa.jetpackmvvm.ext.util.logd
import com.qipa.newboxproject.app.ext.showMessage
import com.qipa.newboxproject.app.weight.textview.DownloadProgressButton
import com.qipa.newboxproject.app.weight.textview.DownloadProgressButton.Companion.STATE_NORMAL
import kotlinx.android.synthetic.main.detail_back.*
import kotlinx.android.synthetic.main.include_game_detail_name.*
import androidx.core.graphics.ColorUtils
import com.qipa.newboxproject.databinding.FragmentGameBigDetailBinding
import kotlinx.android.synthetic.main.layout_uc_content.*

class GameDetailBigFragment : BaseFragment<GameDetailModel,FragmentGameBigDetailBinding>() {
    override fun layoutId() = R.layout.fragment_game_big_detail

    override fun initView(savedInstanceState: Bundle?) {

        initDatas()
    }

    fun initDatas(){

    }



    override fun onPause() {
        super.onPause()
    }

    inner class ProxyClick{


    }

    // 3.下载成功，开始安装,兼容8.0安装位置来源的权限
    private fun installApkO(context: Context, downloadApkPath: String) {

    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onBackPressed(): Boolean {
        return false
    }


}
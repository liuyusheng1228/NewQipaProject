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
import com.qipa.jetpackmvvm.ext.download.DownloadResultState
import com.qipa.jetpackmvvm.ext.download.FileTool.getBasePath
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.navigateAction
import com.qipa.jetpackmvvm.ext.util.logd
import com.qipa.newboxproject.app.ext.showMessage
import com.qipa.newboxproject.app.weight.textview.DownloadProgressButton
import com.qipa.newboxproject.app.weight.textview.DownloadProgressButton.Companion.STATE_NORMAL


class GameDetailFragment : BaseFragment<GameDetailModel,FragmentGameDetailBinding>() {
    override fun layoutId() = R.layout.fragment_game_detail
    private val mDataDetailList : MutableList<String> = arrayListOf()
    private var fragments : ArrayList<Fragment> = arrayListOf()
    private var apkPath : String =""
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()
        mViewModel.downStatusValue.set(getString(R.string.download))
        fragments.add(DetailsFragment.newInstance(true))
        fragments.add(EvaluateFragment.newInstance(true))
        detail_view_pager.init(this,fragments)

        mDataDetailList.add(getString(R.string.details))
        mDataDetailList.add(getString(R.string.evaluate))
        detail_magic_indicator.bindViewPager2(detail_view_pager,mDataDetailList,false)
        val commonNavigator = CommonNavigator(mActivity)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return if (mDataDetailList == null) 0 else mDataDetailList.size
            }

            override fun getTitleView(context: Context?, index: Int): IPagerTitleView? {
                val colorTransitionPagerTitleView = ColorTransitionPagerTitleView(context)
                colorTransitionPagerTitleView.normalColor = Color.GRAY
                colorTransitionPagerTitleView.selectedColor = Color.BLACK
                colorTransitionPagerTitleView.setText(mDataDetailList.get(index))
                colorTransitionPagerTitleView.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View?) {
                        detail_view_pager.setCurrentItem(index)
                    }
                })
                return colorTransitionPagerTitleView
            }

            override fun getIndicator(context: Context?): IPagerIndicator? {
                val indicator = LinePagerIndicator(context)
                //????????????
                indicator.lineWidth = SizeUtils.dp2px(30f).toFloat()
                //????????????
                indicator.lineHeight = SizeUtils.dp2px(5f).toFloat()
                //????????????
                indicator.setColors(resources.getColor(R.color.teal_200))
                //????????????
                indicator.roundRadius = 5f
                //????????????
                indicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                return indicator
            }
        }
        detail_magic_indicator.navigator = commonNavigator
        detail_magic_indicator.navigator.notifyDataSetChanged()
        detail_view_pager.adapter?.notifyDataSetChanged()
        detail_view_pager.offscreenPageLimit = fragments.size
        blurView.setBlurRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70f, getResources().getDisplayMetrics()))
        initDatas()
    }

    fun initDatas(){
        if(AppUtils.isAppInstalled("me.hgj.jetpackmvvm.demo")){
            downloadProgressButton.state = DownloadProgressButton.STATE_OPEEN
            downloadProgressButton.setCurrentText(getString(R.string.open))
        }else{
            downloadProgressButton.state = STATE_NORMAL
            downloadProgressButton.setCurrentText(getString(R.string.download))
        }

        mViewModel.downloadData.observe(viewLifecycleOwner, Observer {
            when(it){
                is DownloadResultState.Pending ->{
                    //
                    "????????????".logd()
                }
                is DownloadResultState.Progress ->{
                    downloadProgressButton.state = DownloadProgressButton.STATE_DOWNLOADING
                    downloadProgressButton.setProgressText(getString(R.string.Downloading), it.progress.toFloat());
                    //?????????
//                    downloadProgressBar.progress = it.progress
//                    "????????? ${it.soFarBytes}/${it.totalBytes}".logd()
//                    downloadProgress.text = "${it.progress}%"
//                    downloadSize.text ="${FileTool.bytes2kb(it.soFarBytes)}/${FileTool.bytes2kb(it.totalBytes)}"
                }
                is DownloadResultState.Success ->{
                    downloadProgressButton.state = DownloadProgressButton.STATE_FINISH
                    downloadProgressButton.setCurrentText(getString(R.string.installation_in_progress))
                    installApkO(mActivity,it.filePath)
                    apkPath = it.filePath
                    "????????????".logd()
                }
                is DownloadResultState.Pause -> {
                    if (downloadProgressButton.state == DownloadProgressButton.STATE_DOWNLOADING) {
                        downloadProgressButton.state = DownloadProgressButton.STATE_PAUSE
                        downloadProgressButton.setCurrentText(getString(R.string.suspend))
                    }

                    "????????????".logd()
                }
                is DownloadResultState.Error ->{
                    "????????????".logd()
                }

            }
        })
    }

    fun shareMethod(){
        val oks = OnekeyShare()
        // ?????????????????????????????????????????????
        // ??????????????????????????????
        //??????sso??????
        oks.disableSSOWhenAuthorize();

        oks.text = "????????????"
        oks.setImageUrl("https://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339485237265.jpg")
        //??????OneKeyShareCallback????????????????????????????????????
        oks.shareContentCustomizeCallback = ShareContentCustomizeDemo()
        oks.callback = callbacks
        oks.show(MobSDK.getContext())
    }
    var callbacks: PlatformActionListener = object : PlatformActionListener {
        override fun onComplete(platform: Platform?, i: Int, hashMap: HashMap<String?, Any?>?) {
            // TODO ????????????????????????????????????
        }

        override fun onError(platform: Platform, i: Int, throwable: Throwable) {
            Log.i("Api",""+throwable.message)
        }

        override fun onCancel(platform: Platform, i: Int) {
            // TODO ??????????????????
        }
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????
     * ??????????????????????????????Twitter?????????????????????????????????????????????
     */
    class ShareContentCustomizeDemo : ShareContentCustomizeCallback {
        override fun onShare(platform: Platform, paramsToShare: Platform.ShareParams) {
            // ??????twitter??????????????????text???????????????????????????
            // ??????twitter?????????????????????????????????????????????????????????
//            if (Twitter.NAME == platform.name) {
//                paramsToShare.setText("????????????????????????")
//            }
        }
    }

    override fun onPause() {
        super.onPause()
        QPvd.releaseAllVideos()
    }

    inner class ProxyClick{

        fun share(){
            shareMethod()
        }

        fun commit(){
            nav().navigateAction(R.id.action_gameDetailFragment_to_userEvaluateFragment)
        }

        fun download(){
            if(downloadProgressButton.state == DownloadProgressButton.STATE_NORMAL|| downloadProgressButton.state == DownloadProgressButton.STATE_PAUSE){
                mViewModel.downloadApk(getBasePath(),
                    "https://down.qq.com/qqweb/QQlite/Android_apk/qqlite_4.0.1.1060_537064364.apk",
                    "qq")
            }else if(downloadProgressButton.state == DownloadProgressButton.STATE_DOWNLOADING){
                mViewModel.downloadPause("qq")
            }else if(downloadProgressButton.state == DownloadProgressButton.STATE_OPEEN){
                AppUtils.launchApp("me.hgj.jetpackmvvm.demo")
            }

        }

    }

    // 3.???????????????????????????,??????8.0???????????????????????????
    private fun installApkO(context: Context, downloadApkPath: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //????????????????????????????????????
            val haveInstallPermission: Boolean = mActivity.getPackageManager().canRequestPackageInstalls()
            if (haveInstallPermission) {
                "8.0?????????????????????????????????????????????????????????????????????".logd()
                AppUtils.installApp(downloadApkPath)
            } else {
                showMessage( "????????????????????????????????????????????????????????????????????????????????????","????????????","??????",{
                    val packageUri = Uri.parse("package:" + AppUtils.getAppPackageName())
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageUri)
                    startActivityForResult(intent, 10086)
                })


            }
        } else {
            AppUtils.installApp(downloadApkPath)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12) {
            downloadProgressButton.state = DownloadProgressButton.STATE_OPEEN
            downloadProgressButton.setCurrentText(getString(R.string.open))
        }else if (requestCode == 10086) {
            installApkO(mActivity,apkPath)
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }


}
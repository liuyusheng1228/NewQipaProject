package com.qipa.newboxproject.ui.fragment.me

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.qipa.jetpackmvvm.callback.databind.StringObservableField
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.appViewModel
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.app.ext.jumpByLogin
import com.qipa.newboxproject.app.ext.setUiTheme
import com.qipa.newboxproject.data.model.bean.BannerResponse
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.navigateAction
import com.qipa.jetpackmvvm.ext.util.notNull
import com.qipa.newboxproject.app.util.CacheUtil
import com.qipa.newboxproject.app.util.ColorUtil
import com.qipa.newboxproject.app.util.PlatformMananger
import com.qipa.newboxproject.app.weight.dialog.ThirdLoginDialog
import com.qipa.newboxproject.data.model.bean.ShareListItemInEntity
import com.qipa.newboxproject.databinding.FragmentMeBinding
import com.qipa.newboxproject.ui.activity.DownAppActivity
import com.qipa.newboxproject.viewmodel.request.RequestMeViewModel
import com.qipa.newboxproject.viewmodel.state.MeViewModel
import kotlinx.android.synthetic.main.fragment_me.*

/**
 * 描述　: 我的
 */

class MeFragment : BaseFragment<MeViewModel, FragmentMeBinding>() {

//    private var rank: IntegralResponse? = null

    private val requestMeViewModel: RequestMeViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_me
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        mDatabind.click = ProxyClick()
        appViewModel.appColor.value?.let { setUiTheme(it, me_linear, me_integral) }
        if(appViewModel.userInfo != null){
            appViewModel.userInfo.value?.let { mViewModel.name.set(if (it.nickname.isEmpty()) it.nickname else it.nickname) }
        }

        me_swipe.init {
            requestMeViewModel.getIntegral()
        }
    }

    override fun initData() {
        super.initData()
//        if(CacheUtil.isLogin()){
//            mViewModel.imageUrl.set( appViewModel.userInfo.value?.headPic)
//        }else{
//            mViewModel.imageUrl.set("")
//        }

    }

    override fun lazyLoadData() {
        appViewModel.userInfo.value?.run {
            mViewModel.imageUrl?.set(ColorUtil.userImage())
//            me_swipe.isRefreshing = true
//            requestMeViewModel.getIntegral()
        }
    }

    override fun createObserver() {

//
//        requestMeViewModel.meData.observe(viewLifecycleOwner, Observer { resultState ->
//            me_swipe.isRefreshing = false
//            parseState(resultState, {
//                rank = it
//                mViewModel.info.set("id：${it.userId}　排名：${it.rank}")
//                mViewModel.integral.set(it.coinCount)
//            }, {
//                ToastUtils.showShort(it.errorMsg)
//            })
//        })

        appViewModel.run {
            appColor.observeInFragment(this@MeFragment, Observer {
                setUiTheme(it, me_linear, me_swipe, me_integral)
            })
            userInfo.observeInFragment(this@MeFragment, Observer {
                it.notNull({
//                    me_swipe.isRefreshing = true
                    mViewModel.imageUrl?.set(it.headPic)
                    mViewModel.name.set(if (it.nickname.isEmpty()) it.nickname else it.nickname)
//                    requestMeViewModel.getIntegral()
                }, {
                    mViewModel.name.set("请先登录~")
                    mViewModel.info.set("id：--　排名：--")
                    mViewModel.integral.set(0)
                })
            })
        }
    }

    inner class ProxyClick {

        /** 登录 */
        fun login() {
            nav().jumpByLogin {}
        }

        fun mygamelist(){
            nav().navigateAction(R.id.action_mainfragment_to_myGameListFragment)

        }

        /** 收藏 */
        fun collect() {
            nav().jumpByLogin {
                it.navigateAction(R.id.action_mainfragment_to_collectFragment)
            }
        }

        /** 积分 */
        fun integral() {
//            nav().jumpByLogin {
//                it.navigateAction(R.id.action_mainfragment_to_integralFragment,
//                    Bundle().apply {
////                        putParcelable("rank", rank)
//                    }
//                )
//            }
        }

        /** 文章 */
        fun ariticle() {
            nav().navigateAction(R.id.action_mainfragment_to_myCustomerServiceFragment)
        }

        fun todo() {
            nav().navigateAction(R.id.action_mainfragment_to_downLoadLibraryFragment)
//            nav().jumpByLogin {
////                it.navigateAction(R.id.action_mainfragment_to_todoListFragment)
//            }
        }

        /** 玩Android开源网站 */
        fun about() {
//            nav().navigateAction(R.id.action_to_gameDetailbigFragment)
            nav().navigateAction(R.id.gameDetailFragment, Bundle().apply {
                putParcelable(
                    "bannerdata",
                    BannerResponse(
                        title = "玩Android网站",
                        url = "https://www.wanandroid.com/"
                    )
                )
            })
        }

        /** 加入我们 */
        fun join() {

            startActivity(Intent(mActivity,DownAppActivity::class.java))
//            nav().navigateAction(R.id.action_mainfragment_action_to_rechargeRebateFragment)

//            joinQQGroup("9n4i5sHt4189d4DvbotKiCHy-5jZtD4D")
        }

        /** 设置*/
        fun setting() {
            nav().navigateAction(R.id.action_mainfragment_to_settingFragment)
        }

        /**demo*/
        fun demo() {
            nav().navigateAction(R.id.action_mainfragment_to_missionHallFragment)
        }

        fun onUserInfor(){
            nav().jumpByLogin {
                it.navigateAction(R.id.action_mainfragment_to_personalInformationFragment)
            }

        }

    }

    override fun onBackPressed(): Boolean {
        return false
    }
}
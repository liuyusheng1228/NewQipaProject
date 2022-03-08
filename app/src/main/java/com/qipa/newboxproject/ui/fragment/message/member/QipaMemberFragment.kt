package com.qipa.newboxproject.ui.fragment.message.member

import android.os.Bundle
import android.view.View.GONE
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.navigateAction
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.weight.banner.MemberBannerAdapter
import com.qipa.newboxproject.app.weight.banner.MemberBannerViewHolder
import com.qipa.newboxproject.data.model.bean.BannerResponse
import com.qipa.newboxproject.databinding.FragmentQipaMemberBinding
import com.qipa.newboxproject.viewmodel.state.QiPaMemberModel
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.constants.PageStyle.MULTI_PAGE
import kotlinx.android.synthetic.main.fragment_qipa_member.*
import kotlinx.android.synthetic.main.include_back.*

class QipaMemberFragment : BaseFragment<QiPaMemberModel,FragmentQipaMemberBinding>() {
    override fun layoutId() = R.layout.fragment_qipa_member
    private var mDataBananer : MutableList<BannerResponse> = arrayListOf()

    override fun initView(savedInstanceState: Bundle?) {
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }
        toolbar_titletv.text ="奇葩会员"
        initDatas()
    }

     fun initDatas(){
         for(index in 1..6){
             mDataBananer.add(BannerResponse("",index,"https://static.runoob.com/images/demo/demo2.jpg"))
         }
         member_banner.apply {
             findViewById<BannerViewPager<BannerResponse, MemberBannerViewHolder>>(R.id.banner_view).apply {
                 adapter = MemberBannerAdapter()
                 setAutoPlay(false)
                 setCanLoop(false)
                 setPageStyle(MULTI_PAGE)
                 setRevealWidth(40)
                 setPageMargin(15)
                 setIndicatorVisibility(GONE)
                 setLifecycleRegistry(lifecycle)
                 setOnPageClickListener {
                     nav().navigateAction(
                         R.id.action_to_gameDetailFragment,
                         Bundle().apply { putParcelable("bannerdata", mDataBananer[it]) })
                 }
                 create(mDataBananer)
             }
         }
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}
package com.qipa.newboxproject.ui.fragment.me

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.SizeUtils
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.bindViewPager2
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.databinding.FragmentRechargeRebateBinding
import com.qipa.newboxproject.viewmodel.state.RechargeRebateModel
import kotlinx.android.synthetic.main.include_back.*
import kotlinx.android.synthetic.main.include_viewpager.*
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

class RechargeRebateFragment : BaseFragment<RechargeRebateModel,FragmentRechargeRebateBinding>() {
    override fun layoutId() = R.layout.fragment_recharge_rebate
    //fragment集合
    var fragments: ArrayList<Fragment> = arrayListOf()

    //标题集合
    var mDataList: ArrayList<String> = arrayListOf()

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }
        toolbar_titletv.text = "充值返利"
        view_pager.init(this,fragments)
//        StatusNavigationUtils.setStatusBarColor(mActivity,R.color.black)
        magic_indicator.bindViewPager2(view_pager,mDataList,false)
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }
        loadData()
        viewpager_linear.setBackgroundColor(resources.getColor(R.color.white))

    }

    fun loadData(){
        mDataList.add("全部")
        mDataList.add("待平台审核")
        mDataList.add("待游戏商审核")
        mDataList.add("已发放")
        mDataList.add("追加发放")
        mDataList.add("信息错误")
        fragments.add(RechargeRebateChildFragment.newInstance(1))
        fragments.add(RechargeRebateChildFragment.newInstance(2))
        fragments.add(RechargeRebateChildFragment.newInstance(3))
        fragments.add(RechargeRebateChildFragment.newInstance(4))
        fragments.add(RechargeRebateChildFragment.newInstance(5))
        fragments.add(RechargeRebateChildFragment.newInstance(6))
        initIndicator()
    }

    fun initIndicator(){
        val commonNavigator = CommonNavigator(mActivity)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return if (mDataList == null) 0 else mDataList.size
            }

            override fun getTitleView(context: Context?, index: Int): IPagerTitleView? {
                val colorTransitionPagerTitleView = ColorTransitionPagerTitleView(context)
                colorTransitionPagerTitleView.normalColor = Color.GRAY
                colorTransitionPagerTitleView.selectedColor = Color.BLACK
                colorTransitionPagerTitleView.textSize = SizeUtils.px2dp(25f).toFloat()
                colorTransitionPagerTitleView.width = SizeUtils.dp2px(150f)
                colorTransitionPagerTitleView.setText(mDataList.get(index))
                colorTransitionPagerTitleView.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View?) {
                        view_pager.setCurrentItem(index)
                    }
                })
                return colorTransitionPagerTitleView
            }

            override fun getIndicator(context: Context?): IPagerIndicator? {
                val indicator = LinePagerIndicator(context)
                //设置宽度
                indicator.lineWidth = SizeUtils.dp2px(30f).toFloat()
                //设置高度
                indicator.lineHeight = SizeUtils.dp2px(5f).toFloat()
                //设置颜色
                indicator.setColors(resources.getColor(R.color.mainColor))
                //设置圆角
                indicator.roundRadius = 5f
                //设置模式
                indicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                return indicator
            }
        }
        magic_indicator.navigator = commonNavigator
        magic_indicator.navigator.notifyDataSetChanged()
        view_pager.adapter?.notifyDataSetChanged()
        view_pager.offscreenPageLimit = fragments.size
    }



    override fun onBackPressed(): Boolean {
        return false
    }
}
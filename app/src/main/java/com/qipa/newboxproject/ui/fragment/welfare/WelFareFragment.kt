package com.qipa.newboxproject.ui.fragment.welfare

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.gyf.immersionbar.ImmersionBar
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.navigateAction
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.app.weight.recyclerview.SpaceItemDecoration
import com.qipa.newboxproject.data.model.bean.WelFareListBean
import com.qipa.newboxproject.databinding.FragmentWelFareBinding
import com.qipa.newboxproject.ui.adapter.WelFareListAdapter
import com.qipa.newboxproject.viewmodel.request.RequestWelfareViewModel
import com.qipa.newboxproject.viewmodel.state.WelfareModel
import kotlinx.android.synthetic.main.fragment_wel_fare.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 */
class WelFareFragment : BaseFragment<WelfareModel, FragmentWelFareBinding>() {
    private val welFareListAdapter : WelFareListAdapter by lazy { WelFareListAdapter(arrayListOf()) }

    private val requestWelfareViewModel : RequestWelfareViewModel by viewModels()

    private val mDataListWelfare : MutableList<WelFareListBean> = arrayListOf()


    override fun layoutId() = R.layout.fragment_wel_fare


    override fun initView(savedInstanceState: Bundle?) {
        addLoadingObserve(requestWelfareViewModel)

        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()
        out_side_recyclerview.init(LinearLayoutManager(mActivity),welFareListAdapter).let {
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f), false))
        }

        loadData()

        mDatabind.click = ProxyClick()
    }

    fun loadData(){

        val listString : MutableList<String> = arrayListOf()
        for (index in 1..10){
            listString.add("ces"+index)
        }


        mDataListWelfare.add(WelFareListBean("测试1",listString))
        mDataListWelfare.add(WelFareListBean("测试2",listString))
        mDataListWelfare.add(WelFareListBean("测试3",listString))
        mDataListWelfare.add(WelFareListBean("测试1",listString))
        mDataListWelfare.add(WelFareListBean("测试2",listString))
        mDataListWelfare.add(WelFareListBean("测试3",listString))
        mDataListWelfare.add(WelFareListBean("测试1",listString))
        mDataListWelfare.add(WelFareListBean("测试2",listString))
        mDataListWelfare.add(WelFareListBean("测试3",listString))
        mDataListWelfare.add(WelFareListBean("测试1",listString))
        mDataListWelfare.add(WelFareListBean("测试2",listString))
        mDataListWelfare.add(WelFareListBean("测试3",listString))
        welFareListAdapter.setList(mDataListWelfare)
        welFareListAdapter.notifyDataSetChanged()
    }
    inner class ProxyClick {

        fun linmission_hall(){
             nav().navigateAction(R.id.action_mainfragment_to_missionHallFragment)
        }
        fun linpoints_mall(){
             nav().navigateAction(R.id.action_to_pointsMallFragment)
        }

        fun level_top(){
            nav().navigateAction(R.id.action_mainfragment_to_qipaMemberFragment)
        }
    }

    override fun setVisibleToUser() {
        super.setVisibleToUser()
        ImmersionBar.with(this)
            .statusBarColor(R.color.colorBlack333)
            .init()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

}
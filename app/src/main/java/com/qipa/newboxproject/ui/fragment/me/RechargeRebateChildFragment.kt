package com.qipa.newboxproject.ui.fragment.me

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.kingja.loadsir.core.LoadService
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.*
import com.qipa.newboxproject.data.model.bean.WelFareListBean
import com.qipa.newboxproject.databinding.FragmentRechargeRebateChildBinding
import com.qipa.newboxproject.ui.adapter.RechargeRebateChildListAdapter
import com.qipa.newboxproject.viewmodel.state.RechargeRebateChildModel
import kotlinx.android.synthetic.main.include_recyclerview.*

class RechargeRebateChildFragment : BaseFragment<RechargeRebateChildModel,FragmentRechargeRebateChildBinding> (){
    override fun layoutId() = R.layout.fragment_recharge_rebate_child
    private val rechargeRebateChildListAdapter : RechargeRebateChildListAdapter by lazy { RechargeRebateChildListAdapter(mDataListChild) }
    private var mDataListChild :ArrayList<WelFareListBean> = arrayListOf()
    private lateinit var loadsir : LoadService<Any>
    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            cid = it.getInt("cid")
        }
        swipeRefresh.init {
            loadsir.showEmpty()
        }
        recyclerView.init(LinearLayoutManager(mActivity),rechargeRebateChildListAdapter)
        loadsir = loadServiceInit(swipeRefresh){
        }

    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        loadsir.showEmptyMsg("暂时没有数据哟")
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    //改项目对应的id
    private var cid = 0
    companion object {
        fun newInstance( cid: Int): RechargeRebateChildFragment {
            val args = Bundle()
            args.putInt("cid", cid)
            val fragment = RechargeRebateChildFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
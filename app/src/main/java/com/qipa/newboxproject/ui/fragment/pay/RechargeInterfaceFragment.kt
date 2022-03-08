package com.qipa.newboxproject.ui.fragment.pay

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.app.weight.recyclerview.SpaceItemDecoration
import com.qipa.newboxproject.data.model.bean.RechargeInterfaceBean
import com.qipa.newboxproject.databinding.FragmentRechargeInterfaceBinding
import com.qipa.newboxproject.ui.adapter.RechargeInterfaceListAdapter
import com.qipa.newboxproject.viewmodel.state.RechargeInterfaceModel
import kotlinx.android.synthetic.main.fragment_recharge_interface.*

class RechargeInterfaceFragment : BaseFragment<RechargeInterfaceModel,FragmentRechargeInterfaceBinding>() {
    private val rechargeInterfaceListAdapter : RechargeInterfaceListAdapter by lazy { RechargeInterfaceListAdapter(mDataItemRechargeInterFace) }
    override fun layoutId() = R.layout.fragment_recharge_interface
    private var mDataItemRechargeInterFace : MutableList<RechargeInterfaceBean> = arrayListOf()

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        recycler_view_pay_item.init(LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false),rechargeInterfaceListAdapter).let {
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(10f), false))
        }
        for (index in 1..8){
            mDataItemRechargeInterFace.add(RechargeInterfaceBean(""+index))
        }
        rechargeInterfaceListAdapter.setList(mDataItemRechargeInterFace)
        rechargeInterfaceListAdapter.notifyDataSetChanged()
        rechargeInterfaceListAdapter.setOnItemClickListener { adapter, view, position ->
            rechargeInterfaceListAdapter.setSelection(position)
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}
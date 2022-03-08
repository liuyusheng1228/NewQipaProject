package com.qipa.newboxproject.ui.fragment.message

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.data.model.bean.IntegralDetailsBean
import com.qipa.newboxproject.databinding.FragmentIntegralDetailsBinding
import com.qipa.newboxproject.ui.adapter.IntegralDetailsListAdapter
import com.qipa.newboxproject.viewmodel.state.IntegralDetailsModel
import kotlinx.android.synthetic.main.fragment_integral_details.*
import kotlinx.android.synthetic.main.include_back.*

class IntegralDetailsFragment : BaseFragment<IntegralDetailsModel,FragmentIntegralDetailsBinding>(){
    override fun layoutId() = R.layout.fragment_integral_details
    private val integralDetailsListAdapter : IntegralDetailsListAdapter by lazy { IntegralDetailsListAdapter(mDataDetailsList) }
    private var mDataDetailsList : MutableList<IntegralDetailsBean> = arrayListOf()

    override fun initView(savedInstanceState: Bundle?) {
        toolbar_titletv.text = "积分明细"
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }
        for (index in 1..10){
            mDataDetailsList.add(IntegralDetailsBean("","",""))
        }
        recycler_integral_detail.init(LinearLayoutManager(mActivity),integralDetailsListAdapter)
        integralDetailsListAdapter.setList(mDataDetailsList)
        integralDetailsListAdapter.notifyDataSetChanged()
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}
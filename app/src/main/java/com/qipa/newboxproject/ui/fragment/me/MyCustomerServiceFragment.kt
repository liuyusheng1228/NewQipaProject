package com.qipa.newboxproject.ui.fragment.me

import android.os.Bundle
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.navigateAction
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.databinding.FragmentMyCustomerServiceBinding
import com.qipa.newboxproject.viewmodel.state.MyCustomerServiceModel
import kotlinx.android.synthetic.main.include_back.*

class MyCustomerServiceFragment : BaseFragment<MyCustomerServiceModel,FragmentMyCustomerServiceBinding>() {
    override fun layoutId() = R.layout.fragment_my_customer_service

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        mDatabind.click = ProClick()
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }
        toolbar_titletv.text = "我的客服"
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    inner class ProClick{

        fun clickAccount_appeal(){
            nav().navigateAction(R.id.myCustomerServiceFragment_action_to_complaintsAndSuggestionsFragment)
        }


    }
}
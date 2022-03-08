package com.qipa.newboxproject.ui.fragment.me.set

import android.os.Bundle
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.navigateAction
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.databinding.FragmentAccountandsecurityBinding
import com.qipa.newboxproject.viewmodel.state.AccountAndsecurityModel
import kotlinx.android.synthetic.main.include_back.*

class AccountAndsecurityFragment : BaseFragment<AccountAndsecurityModel,FragmentAccountandsecurityBinding>(){
    override fun layoutId() = R.layout.fragment_accountandsecurity

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }
        mDatabind.click = ProClick()
        toolbar_titletv.text = getString(R.string.account_number_and_security)

    }

    override fun onBackPressed(): Boolean {
        return false
    }

    inner class ProClick{
        fun cancel_account(){

        }

        fun real_name_authentication(){
            nav().navigateAction(R.id.action_personalInformationFragment_to_realNameAuthenticationFragment)
        }

        fun password_management(){
            nav().navigateAction(R.id.action_personinfor_to_unbandPhoneFragment,
                Bundle().apply {
                    putString("type","" )
                })
        }

        fun mobile_phone(){
            nav().navigateAction(R.id.action_personinfor_to_unbandPhoneFragment,
                Bundle().apply {
                    putString("type","" )
                })
        }

    }
}
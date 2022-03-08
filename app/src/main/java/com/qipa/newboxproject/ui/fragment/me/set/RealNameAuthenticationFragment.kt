package com.qipa.newboxproject.ui.fragment.me.set

import android.os.Bundle
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.util.CacheUtil
import com.qipa.newboxproject.databinding.FragmentRealNameAuthenticationBinding
import com.qipa.newboxproject.viewmodel.state.RealNameAuthenticationModel
import kotlinx.android.synthetic.main.include_back.*

class RealNameAuthenticationFragment : BaseFragment<RealNameAuthenticationModel , FragmentRealNameAuthenticationBinding>() {
    override fun layoutId() = R.layout.fragment_real_name_authentication

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }

        toolbar_titletv.text = getString(R.string.real_name_authentication)
        mViewModel.identityCard.set(CacheUtil.getUser()?.identityCard)
        mViewModel.realName.set(CacheUtil.getUser()?.realName)

    }

    override fun onBackPressed(): Boolean {
        return false
    }
}
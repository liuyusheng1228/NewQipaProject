package com.qipa.newboxproject.ui.fragment.pay

import android.os.Bundle
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.databinding.FragmentPlatformCurrencyDetailsBinding
import com.qipa.newboxproject.viewmodel.state.PlatformCurrencyDetailsModel

class PlatformCurrencyDetailsFragment : BaseFragment<PlatformCurrencyDetailsModel,FragmentPlatformCurrencyDetailsBinding> (){
    override fun layoutId() = R.layout.fragment_platform_currency_details

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun onBackPressed(): Boolean {
        return false
    }
}
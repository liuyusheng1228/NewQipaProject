package com.qipa.newboxproject.ui.fragment.me.set

import android.os.Bundle
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.navigateAction
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.databinding.FragmentSettingBinding
import com.qipa.newboxproject.viewmodel.state.SettingModel
import kotlinx.android.synthetic.main.include_back.*

class SettingFragment : BaseFragment<SettingModel,FragmentSettingBinding> (){
    override fun layoutId() = R.layout.fragment_setting

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        mDatabind.click = ProxyClick()
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }
        toolbar_titletv.text = resources.getString(R.string.em_about_me_set)
    }

    inner class ProxyClick {

        fun accountAndsecurity(){
            nav().navigateAction(R.id.settingFragment_action_to_accountAndsecurityFragment)
        }

        fun system_permission_setting(){

        }

        fun more_language(){
            nav().navigateAction(R.id.settingFragment_action_to_changeLanguageFragment)
        }

    }

    override fun onBackPressed(): Boolean {
        return false
    }
}
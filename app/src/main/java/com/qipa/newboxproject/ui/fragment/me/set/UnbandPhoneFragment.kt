package com.qipa.newboxproject.ui.fragment.me.set

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.viewModels
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.navigateAction
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.util.CacheUtil
import com.qipa.newboxproject.app.util.CountDownTimerUtils
import com.qipa.newboxproject.data.model.bean.BindPhoneBean
import com.qipa.newboxproject.databinding.FragmentUnbandphoneBinding
import com.qipa.newboxproject.viewmodel.request.RequestBindPhoneViewModel
import com.qipa.newboxproject.viewmodel.request.RequestLoginRegisterViewModel
import com.qipa.newboxproject.viewmodel.state.UnbandPhoneModel
import kotlinx.android.synthetic.main.fragment_mobile_login.*
import kotlinx.android.synthetic.main.fragment_unbandphone.*
import kotlinx.android.synthetic.main.include_back.*

class UnbandPhoneFragment : BaseFragment<UnbandPhoneModel,FragmentUnbandphoneBinding>() {
    override fun layoutId() = R.layout.fragment_unbandphone

    private val requestLoginRegisterViewModel : RequestLoginRegisterViewModel by viewModels()

    private val requestBindPhoneViewModel : RequestBindPhoneViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        mDatabind.click = ProxyClick()
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }
        toolbar_titletv.text = resources.getString(R.string.replace_bound_mobile_phone)
        tv_setNotGetCode.setText(Html.fromHtml(resources.getString(R.string.txt_not_get_code)))
        mViewModel.userphone.set(CacheUtil.getUser()?.userMobile?.substring(0,3)+"****"+CacheUtil.getUser()?.userMobile?.substring(7,
            CacheUtil.getUser()?.userMobile?.length!!
        ))
        requestBindPhoneViewModel.bindResult.observe(viewLifecycleOwner,{ data ->
            if(data.equals("1")){
                nav().navigateAction(R.id.action_personinfor_to_BandPhoneFragment)
            }
        })
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    inner class ProxyClick{

        fun getCode(){
            var modelPhone = CacheUtil.getUser()?.userMobile
            startCountDown()
            if (modelPhone != null) {
                requestLoginRegisterViewModel.getCode(modelPhone)
            }
        }

        fun btnNextStep(){
            var userId = CacheUtil.getUser()?.userId
            var modelPhone = CacheUtil.getUser()?.userMobile
            requestBindPhoneViewModel.unBindPhone(BindPhoneBean(userId,modelPhone,mViewModel.usercode.get()))

        }

    }

    private fun startCountDown() {
        CountDownTimerUtils(unbind_get_code, 60000, 1000)
    }
}
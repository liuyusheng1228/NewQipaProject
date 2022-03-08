package com.qipa.newboxproject.ui.fragment.me

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.navigateAction
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.showMessage
import com.qipa.newboxproject.app.util.CacheUtil
import com.qipa.newboxproject.app.util.CountDownTimerUtils
import com.qipa.newboxproject.data.model.bean.BindPhoneBean
import com.qipa.newboxproject.databinding.FragmentBindPhoneBinding
import com.qipa.newboxproject.viewmodel.request.RequestBindPhoneViewModel
import com.qipa.newboxproject.viewmodel.request.RequestLoginRegisterViewModel
import com.qipa.newboxproject.viewmodel.state.BindPhoneModel
import kotlinx.android.synthetic.main.fragment_mobile_login.*
import kotlinx.android.synthetic.main.include_back.*

class BindPhoneFragment : BaseFragment<BindPhoneModel,FragmentBindPhoneBinding>() {
    override fun layoutId() = R.layout.fragment_bind_phone
    private val requestLoginRegisterViewModel : RequestLoginRegisterViewModel by viewModels()
    private val requestBindPhoneViewModel : RequestBindPhoneViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        mDatabind.click = ProxyClick()

        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }



        toolbar_titletv.text = resources.getString(R.string.bind_phone_num)
    }

    override fun createObserver() {
        super.createObserver()
        requestBindPhoneViewModel.bindResult.observe(viewLifecycleOwner,{ data ->
            if(data.equals("1")){
                nav().navigateUp()
            }
        })
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    inner class ProxyClick{

        fun clear(){
            mViewModel.mobilephone.set("")
        }

        fun getCode(){
            if(mViewModel.mobilephone.get().length == 11){
                startCountDown()
                requestLoginRegisterViewModel.getCode(mViewModel.mobilephone.get())
            }else{
                showMessage("手机号输入有误")
            }
        }

        fun btBindPhone(){

            if(mViewModel.mobilephone.get().length == 11&& CacheUtil.isLogin()){
                var userId = CacheUtil.getUser()?.userId
                requestBindPhoneViewModel.bindPhone(BindPhoneBean(userId,mViewModel.mobilephone.get(),mViewModel.mobilecode.get()))
            }

        }

    }

    private fun startCountDown() {
        CountDownTimerUtils(txt_get_code, 60000, 1000)
    }
}
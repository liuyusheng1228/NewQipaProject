package com.qipa.newboxproject.ui.fragment.login

import android.os.Bundle
import android.widget.CompoundButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.qipa.newboxproject.R
import com.qipa.newboxproject.databinding.FragmentLoginBinding
import com.qipa.newboxproject.viewmodel.request.RequestLoginRegisterViewModel
import com.qipa.newboxproject.viewmodel.state.LoginRegisterViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.include_toolbar.*
import com.qipa.newboxproject.app.appViewModel
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.hideSoftKeyboard
import com.qipa.newboxproject.app.ext.initClose
import com.qipa.newboxproject.app.ext.showMessage
import com.qipa.newboxproject.app.util.CacheUtil
import com.qipa.newboxproject.app.util.SettingUtil
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.navigateAction
import com.qipa.jetpackmvvm.ext.parseState

/**
 * 描述　: 登录页面
 */
class LoginFragment : BaseFragment<LoginRegisterViewModel, FragmentLoginBinding>() {

    private val requestLoginRegisterViewModel: RequestLoginRegisterViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_login

    override fun initView(savedInstanceState: Bundle?) {
        addLoadingObserve(requestLoginRegisterViewModel)
        mDatabind.viewmodel = mViewModel

        mDatabind.click = ProxyClick()

        toolbar.initClose("登录") {
            nav().navigateUp()
        }
        //设置颜色跟主题颜色一致
        appViewModel.appColor.value?.let {
            SettingUtil.setShapColor(loginSub, it)
            loginGoregister.setTextColor(it)
            toolbar.setBackgroundColor(it)
        }
    }

    override fun createObserver() {
        requestLoginRegisterViewModel.loginResult.observe(viewLifecycleOwner,Observer { resultState ->
                parseState(resultState, {
                    //登录成功 通知账户数据发生改变鸟
                    CacheUtil.setUser(it)
                    CacheUtil.setIsLogin(true)
                    appViewModel.userInfo.value = it
                    nav().navigateUp()
                }, {
                    //登录失败
                    showMessage(it.errorMsg)
                })
            })
    }

    inner class ProxyClick {

        fun clear() {
            mViewModel.username.set("")
        }

        fun login() {
            when {
                mViewModel.username.get().isEmpty() -> showMessage("请填写账号")
                mViewModel.password.get().isEmpty() -> showMessage("请填写密码")
                else -> requestLoginRegisterViewModel.loginReq(
                    mViewModel.username.get(),
                    mViewModel.password.get()
                )
            }
        }

        fun goRegister() {
            hideSoftKeyboard(activity)
            nav().navigateAction(R.id.action_loginFragment_to_registerFrgment)
        }

        var onCheckedChangeListener =
            CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                mViewModel.isShowPwd.set(isChecked)
            }
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}
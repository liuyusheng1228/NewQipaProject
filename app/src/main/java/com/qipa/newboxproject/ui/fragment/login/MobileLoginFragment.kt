package com.qipa.newboxproject.ui.fragment.login

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.login.ThirdLoginManager
import com.qipa.jetpackmvvm.login.UploadMessageBean
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.appViewModel
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.showMessage
import com.qipa.newboxproject.app.util.CacheUtil
import com.qipa.newboxproject.app.util.CountDownTimerUtils
import com.qipa.newboxproject.app.weight.dialog.ThirdLoginDialog
import com.qipa.newboxproject.databinding.FragmentMobileLoginBinding
import com.qipa.newboxproject.viewmodel.request.RequestLoginRegisterViewModel
import com.qipa.newboxproject.viewmodel.state.MobileLoginModel
import kotlinx.android.synthetic.main.fragment_mobile_login.*
import kotlinx.android.synthetic.main.include_back.*


class MobileLoginFragment : BaseFragment<MobileLoginModel,FragmentMobileLoginBinding>(){
    override fun layoutId() = R.layout.fragment_mobile_login
    private val requestLoginRegisterViewModel : RequestLoginRegisterViewModel by viewModels()
//    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var isloginType = 5
    private var thirdLoginDialog : ThirdLoginDialog? = null
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        mDatabind.click = ProxyClick()
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }

        toolbar_titletv.text = getString(R.string.login_title)
        var style = SpannableStringBuilder()
        style.append(getString(R.string.service_agreement_and_privacy_policy))
        style = setSpannableText(style,2,10,11,17)
        tv_txt_agreet.movementMethod = LinkMovementMethod.getInstance()
        tv_txt_agreet.text = style
        tv_txt_agreet.highlightColor =  ContextCompat.getColor(mActivity,R.color.tranaction)

//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.server_client_id))
//            .requestEmail()
//            .build()
//
//        mGoogleSignInClient = GoogleSignIn.getClient(mActivity, gso)
        thirdLoginDialog = ThirdLoginDialog(mActivity)
    }

    override fun createObserver() {
        super.createObserver()
        requestLoginRegisterViewModel.wxUserInfoBean.observe(viewLifecycleOwner,{resultState ->
            showMessage("????????????")
            CacheUtil.setUser(resultState)
            appViewModel.userInfo.value = resultState
            dismissLoading()
            nav().navigateUp()
        })
        set_verificationcode.setListener { iscode ->
           if(iscode&&mViewModel.mobilephone.get().length == 11){
               var  logUploadMessageBean = UploadMessageBean()
               logUploadMessageBean.type = 5
               logUploadMessageBean.phone = mViewModel.mobilephone.get()
               logUploadMessageBean.code = set_verificationcode.getText()
               requestLoginRegisterViewModel.thirdlogin(logUploadMessageBean)
           }
        }
        thirdLoginDialog?.onClickLoginPos?.observe(viewLifecycleOwner,{
            loginThirdMethod(it)
        })
    }
    inner class ProxyClick {

        fun clear() {
            mViewModel.mobilephone.set("")
        }

        fun one_click_login(){
            thirdLoginDialog?.show()

        }

        fun getCode(){
            if(mViewModel.mobilephone.get().length == 11){
                startCountDown()
                requestLoginRegisterViewModel.getCode(mViewModel.mobilephone.get())
            }else{
                showMessage("?????????????????????")
            }
        }

        fun password_login(){
//            nav().navigateAction(R.id.action_to_loginFragment)

        }
    }

    private fun loginThirdMethod(platName : String){
//        ThirdLoginManager.INSTANCE.loginThirdMethod(platName,mActivity)
//        ThirdLoginManager.INSTANCE.setThirdLoginCallBackImpl(object : ThirdLoginManager.ThirdLoginCallBackImpl{
//            override fun onComplete(uploadMessageBean: UploadMessageBean) {
//                requestLoginRegisterViewModel.thirdlogin(uploadMessageBean)
//            }
//
//            override fun onError(throwable: Throwable) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onCancel(arg1: Int) {
//                TODO("Not yet implemented")
//            }
//
//        })
        when(platName){
            "Facebook" -> isloginType = 4
            "Google" -> isloginType = 3
            "KakaoTalk" -> isloginType = 6
            "QQ" -> isloginType = 2
            "????????????" -> {
                isloginType = 1
            }
        }
        val plat: Platform = ShareSDK.getPlatform(platName)
        if (plat.isAuthValid()) {
            plat.removeAccount(true)
            return
        }
        ShareSDK.setActivity(mActivity) //????????????????????????9.0
        //???????????????????????????????????????????????????????????????????????????????????????????????????UI????????????????????????handler????????????
        plat.setPlatformActionListener(object : PlatformActionListener {
            override fun onError(arg0: Platform?, arg1: Int, arg2: Throwable) {
                // TODO Auto-generated method stub
                arg2.printStackTrace()
                Log.i("Api",""+ arg2.printStackTrace())
            }

            override fun onComplete(arg0: Platform, arg1: Int, arg2: HashMap<String?, Any?>?) {
                // TODO Auto-generated method stub
                //????????????????????????
                // 3. ???onActivityResult???????????????
                Log.i("Api",""+arg0.getDb().exportData()+":"+arg0.getDb().token+":"+arg0.getDb().userId+":"+arg0.getDb().tokenSecret)
                var  logUploadMessageBean = UploadMessageBean()
                logUploadMessageBean = Gson().fromJson(""+ arg0.getDb().exportData(),
                    object : TypeToken<UploadMessageBean>() {}.getType())
                logUploadMessageBean.type = isloginType
                logUploadMessageBean.access_token = arg0.getDb().token
                requestLoginRegisterViewModel.thirdlogin(logUploadMessageBean)
                Log.i("Api",":"+logUploadMessageBean.nickname+":"+arg0.getDb().userId+":"+arg0.getDb().tokenSecret+arg2.toString())
            }

            override fun onCancel(arg0: Platform?, arg1: Int) {
                // TODO Auto-generated method stub
                Log.i("Api",""+ arg1)
            }
        })
        //?????????????????????????????????????????????????????????
        plat.authorize()
    }

    private fun startCountDown() {
        CountDownTimerUtils(txt_get_voice_code, 60000, 1000)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 3. ???onActivityResult???????????????
//        try {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            val account = task.getResult(ApiException::class.java)
////            requestLoginRegisterViewModel.googlelogin(""+account?.idToken,""+account?.id,""+account?.id)
//        }catch (e : ApiException){
//
//        }

    }


    override fun onBackPressed(): Boolean {
        return false
    }


    private fun setSpannableText(
        style : SpannableStringBuilder
        ,start:Int,end:Int
        ,start2:Int,end2:Int): SpannableStringBuilder{
        //??????????????????????????????
        val serviceAgreement: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                //?????????????????????
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(mActivity,R.color.teal_200)
            }
        }
        //??????????????????????????????
        val privacyPolicy: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                //?????????????????????
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(mActivity,R.color.teal_200)
            }
        }
        style.setSpan(serviceAgreement, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        style.setSpan(privacyPolicy, start2, end2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return style
    }


}
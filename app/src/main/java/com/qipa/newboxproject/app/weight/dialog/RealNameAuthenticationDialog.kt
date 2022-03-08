package com.qipa.newboxproject.app.weight.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.blankj.utilcode.util.ToastUtils
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.util.CacheUtil
import com.qipa.newboxproject.app.util.CountDownTimerUtils
import com.qipa.newboxproject.app.util.RegexUtil
import com.qipa.newboxproject.viewmodel.request.RequestLoginRegisterViewModel
import kotlinx.android.synthetic.main.fragment_unbandphone.*

class RealNameAuthenticationDialog(context: Context?) : Dialog(context!!, R.style.CustomDialog) {

    private var input_full_name : EditText? = null
    private var input_id_card_number : EditText? = null
    private var btn_authentication : Button? = null
    private var realnamePhone : TextView? = null
    private var realname_get_code : TextView? = null
    private var edit_get_code : EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.real_name_authentication)
        initView()
        initData()
    }

    fun initView(){
        input_full_name = findViewById(R.id.input_full_name)
        input_id_card_number = findViewById(R.id.input_id_card_number)
        btn_authentication = findViewById(R.id.btn_authentication)
        realnamePhone = findViewById(R.id.bind_phone)
        realname_get_code = findViewById(R.id.realname_get_code)
        edit_get_code = findViewById(R.id.edit_get_code)

    }

    fun initData(){
        realnamePhone?.text = ("已绑定手机号 "+
            CacheUtil.getUser()?.userMobile?.substring(0,3)+"****"+ CacheUtil.getUser()?.userMobile?.substring(7,
                CacheUtil.getUser()?.userMobile?.length!!
            ))
        btn_authentication?.setOnClickListener {
            input_full_name?.text.let {
                if (it == null) {
                   ToastUtils.showShort("请输入您的姓名")
                    return@let
                }
            }
            input_id_card_number?.text.let {
                if (it == null) {
                    ToastUtils.showShort("请输入您身份证号码")
                    return@let
                }
            }
            edit_get_code?.text.toString().let {
                if(it == null){
                    ToastUtils.showShort("请输入手机验证码")
                    return@let
                }
            }
            if(RegexUtil.isRealIDCard(input_id_card_number?.text.toString())){
                onClickAuthentication?.onClickAuthentication(input_full_name?.text.toString(),input_id_card_number?.text.toString(),edit_get_code?.text.toString())
            }else{
                ToastUtils.showShort("您输入的身份证号码有误")
            }


        }
        realname_get_code?.setOnClickListener {
            onClickAuthentication?.onClickCode()
            startCountDown()
        }


    }

    var onClickAuthentication : OnClickAuthentication? = null

    fun setOnClickAuthentication(mOnClickAuthentication : OnClickAuthentication) : RealNameAuthenticationDialog{
        onClickAuthentication = mOnClickAuthentication
        return this
    }

    interface OnClickAuthentication{
        fun onClickAuthentication(real_name : String ,id_card_name : String,code : String)
        fun onClickCode()
    }

    private fun startCountDown() {
        realname_get_code?.let { CountDownTimerUtils(it, 60000, 1000) }
    }

}
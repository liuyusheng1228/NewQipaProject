package com.qipa.newboxproject.ui.fragment.message

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.hyphenate.chat.EMGroup
import com.hyphenate.easeui.model.EaseEvent
import com.kingja.loadsir.core.LoadService
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.chat.enums.Status
import com.qipa.newboxproject.app.chat.net.Resource
import com.qipa.newboxproject.app.interfaces.OnResourceParseCallback
import com.qipa.newboxproject.app.util.Constants
import com.qipa.newboxproject.databinding.FragmentMessageBinding
import com.qipa.newboxproject.viewmodel.request.RequestMessageViewModel
import com.qipa.newboxproject.viewmodel.state.MessageModel
import com.qipa.newboxproject.data.model.bean.AuthResult

import com.qipa.newboxproject.data.model.bean.PayResult
import com.qipa.newboxproject.app.util.OrderInfoUtil2_0.buildOrderParam
import com.qipa.newboxproject.app.util.OrderInfoUtil2_0.buildOrderParamMap
import com.qipa.newboxproject.app.util.OrderInfoUtil2_0.getSign
import com.qipa.newboxproject.data.db.LiveDataBus
import com.qipa.newboxproject.data.db.delegates.DemoConstant
import com.qipa.newboxproject.data.model.UserDelete
import com.qipa.newboxproject.viewmodel.state.NewGroupViewModel
import com.qipa.qipaimbase.ImBaseBridge
import com.qipa.qipaimbase.chat.ChatBaseActivity
import com.qipa.qipaimbase.session.PhotonIMMessage


/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class MessageFragment : BaseFragment<MessageModel, FragmentMessageBinding>() {
    private val requestMessageViewModel : RequestMessageViewModel by viewModels()
    override fun layoutId() = R.layout.fragment_message
    private lateinit var loadsir : LoadService<Any>
    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Constants.SDK_PAY_FLAG -> {
                    val payResult = PayResult(msg.obj as Map<String?, String?>)

                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    val resultInfo = payResult.result // 同步返回需要验证的信息
                    val resultStatus = payResult.resultStatus
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                    }
                }
                Constants.SDK_AUTH_FLAG -> {
                    val authResult = AuthResult(msg.obj as Map<String?, String?>, true)
                    val resultStatus = authResult.getResultStatus()

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(
                            resultStatus,
                            "9000"
                        ) && TextUtils.equals(authResult.getResultCode(), "200")
                    ) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                    } else {
                        // 其他状态值则为授权失败
                    }
                }
                else -> {
                }
            }
        }
    }
    override fun initView(savedInstanceState: Bundle?) {
        addLoadingObserve(requestMessageViewModel)
        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()


    }

    override fun createObserver() {
        super.createObserver()
        viewModel = ViewModelProvider(this)[NewGroupViewModel::class.java]
        requestMessageViewModel.run {
            userListResponse.observe(viewLifecycleOwner,{resultState ->
                if(resultState.isSuccess){
                    Log.i("Api",""+resultState.listData.get(0).nickname)
                }

            })

        }
        viewModel?.groupObservable()!!.observe(this, { response ->
            parseResource(response, object : OnResourceParseCallback<EMGroup>() {
                override fun onSuccess(data: EMGroup?) {

                    LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(
                        EaseEvent.create(
                            DemoConstant.GROUP_CHANGE,
                            EaseEvent.TYPE.GROUP
                        )
                    )
                    ChatBaseActivity.startActivity(
                        mActivity as Activity, PhotonIMMessage.GROUP, data?.groupId,
                        ImBaseBridge.instance?.myIcon, data?.groupName, null, false
                    )
//                    nav().navigateAction(R.id.action_to_chatFragment,
//                        Bundle().apply {
//                            putString(EaseConstant.EXTRA_CONVERSATION_ID, data?.groupId)
//                            putInt(EaseConstant.EXTRA_CHAT_TYPE, DemoConstant.CHATTYPE_GROUP)
//                            putString(DemoConstant.HISTORY_MSG_ID, "")
//                            ChatHelper.instance?.model?.isMsgRoaming()?.let { it1 ->
//                                putBoolean(
//                                    EaseConstant.EXTRA_IS_ROAM,
//                                    it1
//                                )
//                            }
//                        }
//                    )

                }

                override fun onLoading(data: EMGroup?) {
                    super.onLoading(data)
                    showLoading(getString(R.string.request))
                }

                override fun hideLoading() {
                    super.hideLoading()
                    dismissLoading()
                }
            })
        })
    }

    /**
     * 解析Resource<T>
     * @param response
     * @param callback
     * @param <T>
    </T></T> */
    fun <T> parseResource(response: Resource<T>?, callback: OnResourceParseCallback<T>) {
        if (response == null) {
            return
        }
        if (response.status === Status.SUCCESS) {
            callback.hideLoading()
            callback.onSuccess(response.data)
        } else if (response.status === Status.ERROR) {
            callback.hideLoading()
            if (!callback.hideErrorMsg) {
//                showToast(response.getMessage())
            }
            callback.onError(response.errorCode, response.getMessage())
        } else if (response.status === Status.LOADING) {
            callback.onLoading(response.data)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.i("Apiii","显示")
        ImmersionBar.with(this)
            .statusBarColor(R.color.transparent)
            .init()
    }


    fun payapay(){

        if (TextUtils.isEmpty(Constants.APAYAPPID) || TextUtils.isEmpty(Constants.RSA2_PRIVATE) && TextUtils.isEmpty(Constants.RSA_PRIVATE)
        ) {
            return
        }

        /*
		 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
		 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
		 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
		 *
		 * orderInfo 的获取必须来自服务端；
		 */
        val rsa2: Boolean = Constants.RSA2_PRIVATE.length > 0
        val params: Map<String, String?> = buildOrderParamMap(Constants.APAYAPPID, rsa2)
        val orderParam = buildOrderParam(params)

        val privateKey: String = if (rsa2) Constants.RSA2_PRIVATE else Constants.RSA_PRIVATE
        val sign = getSign(params, privateKey, rsa2)
        val orderInfo = "$orderParam&$sign"

//        val payRunnable = Runnable {
//            val alipay = PayTask(mActivity)
//            val result: Map<String, String> = alipay.payV2(orderInfo, true)
//
//            val msg = Message()
//            msg.what = Constants.SDK_PAY_FLAG
//            msg.obj = result
//            mHandler.sendMessage(msg)
//        }
        // 必须异步调用
//        val payThread = Thread(payRunnable)
//        payThread.start()
    }


    override fun setVisibleToUser() {

        super.setVisibleToUser()

    }

    private var viewModel: NewGroupViewModel? = null
    private var newmembers: Array<String?>? = null


    inner class ProxyClick{

        fun test(){
//            val desc: String = "测试"
//            val option = EMGroupOptions()
//            option.maxUsers = 200
//            option.inviteNeedConfirm = true
//            val reason = getString(
//                R.string.em_group_new_invite_join_group,
//                ChatHelper.instance?.currentUser,
//                "测试"
//            )
//            if (true) {
//                option.style = if (true
//                ) EMGroupStyle.EMGroupStylePublicJoinNeedApproval else EMGroupStyle.EMGroupStylePublicOpenJoin
//            } else {
//                option.style = if (true
//                ) EMGroupStyle.EMGroupStylePrivateMemberCanInvite else EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite
//            }
//
//            option.style = EMGroupStyle.EMGroupStylePublicOpenJoin
//            if (newmembers == null) {
//                newmembers = arrayOf()
//            }
//            viewModel?.createGroup("测试", desc, newmembers, reason, option)
//            ChatBaseActivity.startActivity(
//                mActivity as Activity, PhotonIMMessage.GROUP,"123456",
//                ImBaseBridge.instance?.myIcon, "测试", null, false
//            )

            var intent = Intent()
            var  componentName= ComponentName("com.hongkongredlantern.google.gmstore", "com.hongkongredlantern.google.gmstore.ui.main.MainActivity")
            intent.setComponent(componentName)
            intent.putExtra("outermobilebinding",true)
            startActivity(intent);
//            //跳转到群组聊天页面
//            ChatActivity.actionStart(mContext, data.getGroupId(), DemoConstant.CHATTYPE_GROUP)


        }

         fun member(){
            var id : String = "1"
             Log.i("Api","json:"+Gson().toJson(UserDelete(id)))

//             Log.i("Api","json:"+ RSAUtils.decrypt(msg))
             requestMessageViewModel.getList()
//            nav().navigateAction(R.id.action_mainfragment_to_qipaMemberFragment)
        }


    }

    override fun onBackPressed(): Boolean {
        return false
    }

}
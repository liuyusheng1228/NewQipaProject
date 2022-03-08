package com.qipa.jetpackmvvm.pay.alipay

import android.app.Activity
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import com.alipay.sdk.app.PayTask
import com.qipa.jetpackmvvm.pay.alipay.Alipay.Config.appId
import com.qipa.jetpackmvvm.pay.alipay.Alipay.Config.partner
import com.qipa.jetpackmvvm.pay.alipay.Alipay.Config.rsa2_private
import com.qipa.jetpackmvvm.pay.alipay.Alipay.Config.rsa_private
import com.qipa.jetpackmvvm.pay.alipay.Alipay.Config.rsa_public
import com.qipa.jetpackmvvm.pay.alipay.Alipay.Config.seller
import com.qipa.jetpackmvvm.pay.wxpay.Wxpay


class Alipay @JvmOverloads constructor(
    private val context: Activity,
    private val version: Int = VERSION_2
) {
    private var mPayListener: PayListener? = null
    private var mAuthListener: AuthListener? = null

    interface PayListener {
        /**
         * 支付成功
         *
         * @param payResult
         */
        fun onPaySuccess(payResult: PayResult?)

        /**
         * 支付等待中...
         *
         * @param payResult
         */
        fun onPayWaiting(payResult: PayResult?)

        /**
         * 支付取消
         *
         * @param payResult
         */
        fun onPayCancel(payResult: PayResult?)

        /**
         * 支付失败
         *
         * @param payResult
         */
        fun onPayFailure(payResult: PayResult?)
    }

    interface AuthListener {
        fun onAuthSuccess(result: AuthResult?)
        fun onAuthFailure(result: AuthResult?)
    }

    /**
     * add pay call back listener
     *
     * @param listener
     */
    fun setPayListener(listener: PayListener?) {
        mPayListener = listener
    }

    fun setAuthListener(authListener: AuthListener?) {
        mAuthListener = authListener
    }

    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Config.SDK_PAY_FLAG -> {
                    val payResult: PayResult
                    if (msg.arg1 == VERSION_1) {
                        payResult = PayResult(msg.obj as String)
                    } else {
                        payResult = PayResult(msg.obj as Map<String?, String?>)
                    }
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    // 可以拿支付宝公钥验签
                    val resultInfo: String? = payResult.result // 同步返回需要验证的信息
                    val resultStatus: String? = payResult.resultStatus
                    if (DEBUG) {
                        log("支付结果:$payResult")
                    }
                    if (mPayListener != null) {
                        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                        if (TextUtils.equals(resultStatus, "9000")) {
                            mPayListener!!.onPaySuccess(payResult)
                        } else {
                            // 判断resultStatus 为非“9000”则代表可能支付失败
                            // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            if (TextUtils.equals(resultStatus, "8000")) {
                                //支付结果确认中
                                mPayListener!!.onPayWaiting(payResult)
                            } else if (TextUtils.equals(resultStatus, "6001")) {
                                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                                mPayListener!!.onPayCancel(payResult)
                            } else {
                                mPayListener!!.onPayFailure(payResult)
                            }
                        }
                    }
                }
                Config.SDK_AUTH_FLAG -> {
                    val authResult = AuthResult(msg.obj as Map<String?, String?>, true)
                    val resultStatus: String? = authResult.resultStatus
                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(
                            resultStatus,
                            "9000"
                        ) && TextUtils.equals(authResult.resultStatus, "200")
                    ) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        if (DEBUG) {
                            log(
                                """
                                    授权成功
                                    ${String.format("authCode:%s", authResult.authCode)}
                                    """.trimIndent()
                            )
                        }
                    } else {
                        // 其他状态值则为授权失败
                        log(
                            """
                            授权失败
                            ${String.format("authCode:%s", authResult.authCode)}
                            """.trimIndent()
                        )
                    }
                }
                else -> {
                }
            }
        }
    }

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    fun payV1(payInfo: String?) {
        val payRunnable = Runnable { // 构造PayTask 对象
            val alipay = PayTask(context)
            // 调用支付接口，获取支付结果
            val result: String = alipay.pay(payInfo, true) // true 在调起支付页面之前显示进度条
            val msg = Message()
            msg.what = Config.SDK_PAY_FLAG
            msg.arg1 = VERSION_1
            msg.obj = result
            mHandler.sendMessage(msg)
        }

        // 必须异步调用
        val payThread = Thread(payRunnable)
        payThread.start()
    }

    fun payV2(orderInfo: String?) {
        val payRunnable = Runnable {
            val alipay = PayTask(context)
            val result: Map<String, String> = alipay.payV2(orderInfo, true)
            val msg = Message()
            msg.what = Config.SDK_PAY_FLAG
            msg.arg1 = VERSION_2
            msg.obj = result
            mHandler.sendMessage(msg)
        }
        val payThread = Thread(payRunnable)
        payThread.start()
    }

    fun check(): Boolean {
        return if (version == VERSION_2) {
            checkV2()
        } else {
            checkV1()
        }
    }

    object Config {
        const val SDK_PAY_FLAG = 1
        const val SDK_AUTH_FLAG = 2

        /**
         * 商户PID
         */
        var partner = "2088202379311396"

        /**
         * 2.0 新增，需在支付宝开放平台注册应用
         */
        var appId = ""

        /**
         * 商户收款账号
         */
        var seller = ""
        /** 商户私钥，pkcs8格式  */
        /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个  */
        /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE  */
        /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE  */
        /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成，  */
        /** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1  */
        /**
         * 商户私钥，pkcs8格式
         */
        var rsa_private = ""

        /**
         * 2.0新增，推荐使用RSA2 2048位加密
         */
        var rsa2_private = ""

        /**
         * 支付宝公钥
         */
        var rsa_public = ""

        /**
         * 服务器异步通知页面路径
         */
        var notify_url: String? = null
    }

    companion object {
        const val VERSION_1 = 1
        const val VERSION_2 = 2
        val TAG: String = Wxpay.TAG
        var DEBUG = false
        fun log(msg: String?) {
            Log.v(TAG, msg!!)
        }

        /**
         * check whether the device has authentication alipay account.
         * 查询终端设备是否存在支付宝认证账户
         */
        //    public void check() {
        //        Runnable checkRunnable = new Runnable() {
        //
        //            @Override
        //            public void run() {
        //                // 构造PayTask 对象
        //                PayTask payTask = new PayTask(context);
        //                // 调用查询接口，获取查询结果
        //                boolean isExist = payTask.checkAccountIfExist();
        //
        //                Message msg = new Message();
        //                msg.what = AlipayConfig.SDK_CHECK_FLAG;
        //                msg.obj = isExist;
        //                mHandler.sendMessage(msg);
        //            }
        //        };
        //
        //        Thread checkThread = new Thread(checkRunnable);
        //        checkThread.start();
        //    }
        val isRSA2: Boolean
            get() {
                var rsa: String? = rsa2_private
                var rsa2 = false
                if (!TextUtils.isEmpty(rsa2_private)) {
                    rsa = rsa2_private
                    rsa2 = true
                }
                return rsa2
            }

        private fun checkV1(): Boolean {
            return !TextUtils.isEmpty(seller) && !TextUtils.isEmpty(rsa_public) && !TextUtils.isEmpty(
                partner
            ) && !(TextUtils.isEmpty(rsa2_private) && TextUtils.isEmpty(rsa2_private))
        }

        private fun checkV2(): Boolean {
            return !TextUtils.isEmpty(appId) && !TextUtils.isEmpty(rsa_public) && !TextUtils.isEmpty(
                partner
            ) && !(TextUtils.isEmpty(rsa2_private) && TextUtils.isEmpty(rsa2_private))
        }

        fun sign(content: String?): String {
            val rsa2 = isRSA2
            val privateKey: String = if (rsa2) rsa2_private else rsa_private
            return content?.let { SignUtils.sign(it, privateKey, rsa2).toString() }!!
        }
    }
}

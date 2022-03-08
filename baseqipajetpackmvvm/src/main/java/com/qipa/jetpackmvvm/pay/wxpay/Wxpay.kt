package com.qipa.jetpackmvvm.pay.wxpay

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.text.TextUtils
import android.widget.Toast
import com.qipa.jetpackmvvm.pay.wxpay.HttpsUtils.post
import com.qipa.jetpackmvvm.pay.wxpay.OrderInfoUtil.genSign
import com.qipa.jetpackmvvm.pay.wxpay.OrderInfoUtil.getPayReq
import com.tencent.mm.opensdk.constants.Build
import com.tencent.mm.opensdk.modelpay.PayReq

import com.tencent.mm.opensdk.modelbase.BaseResp

import com.tencent.mm.opensdk.modelpay.PayResp

import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler

import com.tencent.mm.opensdk.openapi.WXAPIFactory

import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.utils.Log
import java.lang.Exception


class Wxpay private constructor(context: Context, appId: String?, checkSignature: Boolean) {
    private var context: Context
    private var mWXApi: IWXAPI? = null
    fun handleIntent(intent: Intent?, eventHandler: IWXAPIEventHandler?): Boolean {
        return mWXApi!!.handleIntent(intent, eventHandler)
    }

    val isSupportPay: Boolean
        get() = mWXApi!!.isWXAppInstalled && mWXApi!!.wxAppSupportAPI >= Build.PAY_SUPPORTED_SDK_INT

    fun pay(req: PayReq?) {
        if (isSupportPay) {
            mWXApi!!.sendReq(req)
        } else {
            if (DEBUG) {
                log("您的微信版本太低或不支持支付")
            }
            if (payListener != null) {
                val resp = PayResp()
                resp.errCode = BaseResp.ErrCode.ERR_UNSUPPORT
                payListener!!.onPayFailure(resp)
            }
        }
    }

    fun pay(xml: String?) {
        if (!TextUtils.isEmpty(xml)) {
            val req = getPayReq(xml)
            pay(req)
        }
    }

    fun onResp(baseResp: BaseResp) {
        val code = baseResp.errCode
        if (DEBUG) {
            log(String.format("支付返回errCode=%d,errStr=%s", code, baseResp.errStr))
            if (baseResp is PayResp) {
                val resp = baseResp
                log(
                    String.format(
                        "returnKey=%s,prepayId=%s,extDate=%s,transaction=%s,openId=%s",
                        resp.returnKey,
                        resp.prepayId,
                        resp.extData,
                        resp.transaction,
                        resp.openId
                    )
                )
            }
        }
        if (payListener != null) {
            if (code == BaseResp.ErrCode.ERR_OK) {
                payListener!!.onPaySuccess(baseResp)
            } else if (code == BaseResp.ErrCode.ERR_USER_CANCEL) {
                payListener!!.onPayCanceled(baseResp)
            } else {
                payListener!!.onPayFailure(baseResp)
            }
        }
    }

    private var payListener: PayListener? = null
    fun setPayListener(payListener: PayListener?) {
        this.payListener = payListener
    }

    interface PayListener {
        fun onPaySuccess(resp: BaseResp?)
        fun onPayCanceled(resp: BaseResp?)
        fun onPayFailure(resp: BaseResp?)
    }

    object Config {
        /**
         * 微信appid，建议在 [android.app.Application.onCreate]中设置
         */
        var app_id: String? = null

        /**
         * 是否检查签名，新版本的微信默认将检查签名改为了true
         *
         * @since 0.0.3
         */
        var checkSignature = false

        /**
         * 商户号
         */
        var mch_id: String? = null

        /**
         * API密钥，在商户平台设置
         */
        var api_key: String? = null

        /**
         * 服务器异步通知页面路径
         */
        var notify_url: String? = null
    }

    class DefaultOrderTask(private val wxpay: Wxpay) : AbsUnifiedOrderTask() {
        private var dialog: ProgressDialog? = null
        protected override fun onPreExecute() {
            try {
                dialog = ProgressDialog.show(wxpay.context, "提示", "正在下单，请稍候...")
            } catch (e: Exception) {
                log("弹出下单提示框失败\n$e")
            }
        }

        protected override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if (dialog != null) {
                dialog?.dismiss()
            }
            if (payReq != null) {
                wxpay.pay(payReq)
            } else {
                Toast.makeText(wxpay.context, "下单失败！", Toast.LENGTH_LONG).show()
            }
        }
    }

    abstract class AbsUnifiedOrderTask :
        AsyncTask<Void?, Void?, Void?>() {
        var payReq: PayReq? = null
            private set
        private var params: MutableMap<String, String?>? = null
        fun setParams(params: MutableMap<String, String?>?) {
            this.params = params
        }

        protected override fun doInBackground(vararg params: Void?): Void? {
            // see https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_1
            val url = UNIFIED_ORDER_URL // 统一下单
            val body = getRequest(this.params, false)
            if (DEBUG) {
                log("下单请求xml为：\n$body")
            }
            val content = getResponse(url, body)
            if (DEBUG) {
                log("下单响应xml为：\n$content")
            }
            payReq = getPayReq(content)
            return null
        }

        protected fun getRequest(params: MutableMap<String, String?>?, signed: Boolean): String {
            if (!signed) {
                params!!["sign"] = genSign(params)
            }
            return map2xmlStr(params)
        }

        protected fun map2xmlStr(params: Map<String, String?>?): String {
            val sb = StringBuffer()
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
            sb.append("<xml>")
            val es: Set<*> = params!!.entries
            val it = es.iterator()
            while (it.hasNext()) {
                val entry = it.next() as Map.Entry<*, *>
                val key = entry.key as String
                val value = entry.value as String
                if ("attach".equals(key, ignoreCase = true) || "body".equals(
                        key,
                        ignoreCase = true
                    ) || "detail".equals(key, ignoreCase = true)
                ) {
                    sb.append("<$key><![CDATA[$value]]></$key>")
                } else {
                    sb.append("<$key>$value</$key>")
                }
            }
            sb.append("</xml>")
            var xml = ""
            try {
                // 使用HttpClient，需设置ISO-8859-1编码
                // xml = new String(sb.toString().getBytes(), "UTF-8");
                xml = sb.toString()
            } catch (e: Exception) {
                if (DEBUG) {
                    log(e.message)
                }
            }
            return xml
        }

        //-----------> Response
        protected fun getResponse(url: String?, body: String?): String {
            val buf = post(url, body!!) ?: return ""
            return String(buf)
        }
    }

    companion object {
        /**
         * 统一下单URL
         */
        const val UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder"

        /**
         * 测试环境下服务端下单，返回的是json
         */
        const val TEST_ORDER_URL = "https://wxpay.wxutil.com/pub_v2/app/app_pay.php"
        var DEBUG = false
        const val TAG = "pay_sdk"
        private var instance: Wxpay? = null
        fun log(msg: String?) {
            Log.v(TAG, msg)
        }

        /**
         * Suggestion called in [android.app.Application.onCreate]
         *
         * @param context        Context
         * @param appId          APP_ID register
         * @param checkSignature checkSignature
         *
         * @see com.tencent.mm.opensdk.openapi.WXAPIFactory.createWXAPI
         * @since 0.0.3
         */
        fun init(context: Context, appId: String?, checkSignature: Boolean) {
            if (instance == null) {
                instance = Wxpay(context, appId, checkSignature)
            }
        }

        fun getInstance(context: Context): Wxpay? {
            if (instance == null) {
                instance = Wxpay(context, Config.app_id, Config.checkSignature)
            }
            instance!!.context = context
            return instance
        }
    }

    init {
        this.context = context
        if (!TextUtils.isEmpty(appId)) {
            Config.app_id = appId
        }
        if (Config.checkSignature != checkSignature) {
            Config.checkSignature = checkSignature
        }
        if (mWXApi == null) {
            mWXApi = WXAPIFactory.createWXAPI(context, Config.app_id, Config.checkSignature)
            mWXApi?.registerApp(Config.app_id)
        }
    }
}

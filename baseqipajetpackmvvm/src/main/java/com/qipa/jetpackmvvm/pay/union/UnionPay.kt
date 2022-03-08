package com.qipa.jetpackmvvm.pay.union

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import com.qipa.jetpackmvvm.pay.wxpay.HttpsUtils
import com.qipa.jetpackmvvm.pay.wxpay.HttpsUtils.post
import com.tencent.mm.opensdk.utils.Log
import com.unionpay.UPPayAssistEx
import java.lang.Exception
import java.lang.IllegalStateException


class UnionPay private constructor(context: Context) {
    private val context: Context
    private var spId: String? = null
    private var provider: String? = null
    private var serverMode: String? = null
    private var tn: String? = null
    fun pay(tn: String?, serverMode: String) {
        pay(null, null, tn, serverMode)
    }

    fun pay(spId: String?, sysProvider: String?, tn: String?, serverMode: String) {
        try {
            Class.forName("com.unionpay.UPPayAssistEx")
        } catch (e: ClassNotFoundException) {
            throw IllegalStateException("未集成银联支付SDK，请参考https://github.com/Jamling/af-pay 说明文档")
        }
        val check = "00" == serverMode || "01" == serverMode
        require(check) { "serverMode参数错误" }
        this.spId = spId
        provider = sysProvider
        this.tn = tn
        this.serverMode = serverMode
        context.startActivity(Intent(context, UnionPayActivity::class.java))
    }

    fun handleIntent(intent: Intent?, unionPayActivity: UnionPayActivity?) {
        UPPayAssistEx.startPay(unionPayActivity, spId, provider, tn, serverMode)
    }

    fun onResp(data: Intent?) {
        val result = PayResult(data)
        if (DEBUG) {
            log("支付结果：$result")
        }
        if (payListener != null) {
            if ("success" == result.status) {
                payListener!!.onPaySuccess(result)
            } else if ("cancel" == result.status) {
                payListener!!.onPayCancel(result)
            } else {
                payListener!!.onPayFailure(result)
            }
        }
    }

    private var payListener: PayListener? = null
    fun setPayListener(payListener: PayListener?) {
        this.payListener = payListener
    }

    interface PayListener {
        /**
         * 支付成功
         *
         * @param payResult
         */
        fun onPaySuccess(payResult: PayResult?)

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

    /**
     * 仅供测试环境下使用，获取测试的TN
     */
    class DefaultTnTask(private val unionPay: UnionPay) :
        AsyncTask<String?, Void?, String?>() {
        private var dialog: ProgressDialog? = null
        protected override fun onPreExecute() {
            try {
                dialog = ProgressDialog.show(unionPay.context, "提示", "正在下单，请稍候...")
            } catch (e: Exception) {
                log("弹出下单提示框失败\n$e")
            }
        }

        protected override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (dialog != null) {
                dialog?.dismiss()
            }
        }

        protected override fun doInBackground(vararg params: String?): String {
            if (DEBUG) {
                log("获取TN url: " + TEST_TN_URL)
            }
            val buf = post(TEST_TN_URL, "") ?: return ""
            val tn = String(buf)
            if (DEBUG) {
                log("返回TN: $tn")
            }
            return tn
        }
    }

    companion object {
        const val TEST_TN_URL = "http://101.231.204.84:8091/sim/getacptn"
        var DEBUG = false
        const val TAG = "pay_sdk"
        private var instance: UnionPay? = null
        fun log(msg: String?) {
            Log.v(TAG, msg)
        }

        fun getInstance(context: Context): UnionPay? {
            if (instance == null) {
                instance = UnionPay(context)
            }
            return instance
        }
    }

    init {
        this.context = context
    }
}

package com.qipa.jetpackmvvm.pay.union

import android.content.Intent
import org.json.JSONException
import org.json.JSONObject

class PayResult internal constructor(data: Intent?) {
    /**
     * 返回支付结果状态
     *
     * @return 支付结果状态
     */
    var status = "Unknown"

    /**
     * 签名后做Base64的数据
     * 当支付成功时，result不为空，result为一段json
     *
     * @return 支付成功结果
     */
    var result: String? = null

    /**
     * 签名后做Base64的数据
     * 当支付成功时，sign不为空
     *
     * @return 支付成功时的签名
     */
    var sign: String? = null

    /**
     * 获取签名的原始数据
     * 当支付成功时，data不为空
     *
     * @return 支付成功时的签名数据
     */
    var data: String? = null

    override fun toString(): String {
        return "status={$status};result={$result}"
    }

    init {
        if (data != null) {
            status = data.getExtras()?.getString("pay_result")?.toLowerCase().toString()
            if ("success" == status) {

                // 如果想对结果数据验签，可使用下面这段代码，但建议不验签，直接去商户后台查询交易结果
                // result_data结构见c）result_data参数说明
                if (data.hasExtra("result_data")) {
                    result = data.getExtras()?.getString("result_data")
                    try {
                        val resultJson = JSONObject(result)
                        sign = resultJson.getString("sign")
                        this.data = resultJson.getString("data")
                    } catch (e: JSONException) {
                    }
                }
                // 结果result_data为成功时，去商户后台查询一下再展示成功
            }
        }

    }
}

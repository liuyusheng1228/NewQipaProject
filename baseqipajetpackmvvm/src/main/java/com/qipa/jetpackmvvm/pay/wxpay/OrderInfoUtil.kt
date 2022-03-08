package com.qipa.jetpackmvvm.pay.wxpay

import android.text.TextUtils
import android.util.Xml
import com.qipa.jetpackmvvm.pay.wxpay.MD5.getMessageDigest
import com.tencent.mm.opensdk.modelpay.PayReq
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap


object OrderInfoUtil {
    /**
     * <xml>
     * <return_code><![CDATA[SUCCESS]]></return_code>
     * <return_msg><![CDATA[OK]]></return_msg>
     * <appid><![CDATA[wx2421b1c4370ec43b]]></appid>
     * <mch_id><![CDATA[10000100]]></mch_id>
     * <nonce_str><![CDATA[IITRi8Iabbblz1Jc]]></nonce_str>
     * <sign><![CDATA[7921E432F65EB8ED0CE9755F0E86D72F]]></sign>
     * <result_code><![CDATA[SUCCESS]]></result_code>
     * <prepay_id><![CDATA[wx201411101639507cbf6ffd8b0779950874]]></prepay_id>
     * <trade_type><![CDATA[APP]]></trade_type>
    </xml> *
     * 解析统一下单返回的xml报文
     *
     * @param content 统一下单返回的xml字符串
     *
     * @return 解析完的map
     */
    fun parseXmlResponse(content: String?): Map<String, String>? {
        try {
            val xml: MutableMap<String, String> = HashMap()
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setInput(StringReader(content))
            var event: Int = parser.getEventType()
            while (event != XmlPullParser.END_DOCUMENT) {
                val nodeName: String = parser.getName()
                when (event) {
                    XmlPullParser.START_DOCUMENT -> {
                    }
                    XmlPullParser.START_TAG -> if ("xml" == nodeName == false) {
                        // 实例化对象
                        xml[nodeName] = parser.nextText()
                    }
                    XmlPullParser.END_TAG -> {
                    }
                }
                event = parser.next()
            }
            return xml
        } catch (e: Exception) {
            if (Wxpay.DEBUG) {
                Wxpay.log("无法从xml中解析统一下单信息：$e")
            }
        }
        return null
    }

    fun parseJsonResponse(content: String?): Map<String, String>? {
        try {
            val `object` = JSONObject(content)
            val map: MutableMap<String, String> = HashMap()
            val keys: Iterator<String> = `object`.keys()
            if (keys != null) {
                while (keys.hasNext()) {
                    val key = keys.next()
                    map[key] = `object`.getString(key)
                }
            }
            return map
        } catch (e: Exception) {
            if (Wxpay.DEBUG) {
                Wxpay.log("无法从json中解析统一下单信息：$e")
            }
        }
        return null
    }

    /**
     * Get [com.tencent.mm.opensdk.modelpay.PayReq] from order map result.
     *
     * @param result order result map
     *
     * @return PayReq
     */
    fun getPayReq(result: Map<String, String>?): PayReq? {
        var req: PayReq? = null
        if (result != null && "SUCCESS" == result["result_code"] && "SUCCESS" ==
            result["return_code"]
        ) {
            req = PayReq()
            req.appId = result["appid"]
            req.nonceStr = result["nonce_str"]
            req.partnerId = result["mch_id"]
            req.packageValue = "Sign=WXPay"
            req.prepayId = result["prepay_id"]
            req.timeStamp = (System.currentTimeMillis() / 1000).toString()
            req.sign = result["sign"]
            signPayReq(req)
        }
        return req
    }

    /**
     * 重新签名，当客户端下单的时候，统一订单接口并未返回timestamp，加上timestamp后，需要重新签名。
     * 如果是服务端下单，如果服务端已经重新生成了签名，那么无需客户端重新签名，直接拿PayReq对象去支付就可以了。
     *
     * @param req 将要调用微信客户端
     */
    fun signPayReq(req: PayReq) {
        val sortedMap: MutableMap<String, String?> = TreeMap()
        sortedMap["appid"] = req.appId
        sortedMap["noncestr"] = req.nonceStr
        sortedMap["partnerid"] = req.partnerId
        sortedMap["prepayid"] = req.prepayId
        sortedMap["timestamp"] = req.timeStamp
        sortedMap["package"] = req.packageValue
        val sign = genSign(sortedMap)
        if (Wxpay.DEBUG) {
            Wxpay.log("客户端支付重签名：$sign")
        }
        req.sign = sign
    }

    /**
     * Get [com.tencent.mm.opensdk.modelpay.PayReq] from order xml result.
     *
     * @param xmlResultContent order result xml content
     *
     * @return PayReq
     */
    fun getPayReq(xmlResultContent: String?): PayReq? {
        val result = parseXmlResponse(xmlResultContent)
        return getPayReq(result)
    }

    /**
     * Sample :
     * <xml>
     * <appid>wx2421b1c4370ec43b</appid>
     * <attach>支付测试</attach>
     * <body>APP支付测试</body>
     * <mch_id>10000100</mch_id>
     * <nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str>
     * <notify_url>http://wxpay.wxutil.com/pub_v2/pay/notify.v2.php</notify_url>
     * <out_trade_no>1415659990</out_trade_no>
     * <spbill_create_ip>14.23.150.211</spbill_create_ip>
     * <total_fee>1</total_fee>
     * <trade_type>APP</trade_type>
     * <sign>0CB01533B8C1EF103065174F50BCA001</sign>
    </xml> *
     *
     * @return request xml body
     */
    fun buildOrderParamMap(
        out_trade_no: String?, body: String, detail: String?, fee: String?,
        notify_url: String?, nonce_str: String?, ip: String?
    ): Map<String, String?> {
        val map: MutableMap<String, String?> = LinkedHashMap()
        map["appid"] = Wxpay.Config.app_id
        if (body.length > 128) {
            map["body"] = body.substring(0, 128)
        } else {
            map["body"] = body
        }
        if (!TextUtils.isEmpty(detail)) {
            map["detail"] = detail
        }
        map["mch_id"] = Wxpay.Config.mch_id
        map["nonce_str"] = if (TextUtils.isEmpty(nonce_str)) genNonceStr() else nonce_str
        map["notify_url"] =
            if (TextUtils.isEmpty(notify_url)) Wxpay.Config.notify_url else notify_url
        map["out_trade_no"] = out_trade_no
        map["spbill_create_ip"] = if (TextUtils.isEmpty(ip)) "127.0.0.1" else ip
        map["total_fee"] = fee
        map["trade_type"] = "APP"
        return map
    }

    fun genNonceStr(): String? {
        val str = Random().nextDouble().toString()
        return getMessageDigest(str.toByteArray())
    }

    fun genSign(parameters: MutableMap<String, String?>?): String? {
        // see https://pay.weixin.qq.com/wiki/tools/signverify/
        val sortedMap: MutableMap<String, String?> = TreeMap()
        for (key in parameters?.keys!!) {
            sortedMap[key] = parameters[key]
        }
        val sb = StringBuffer()
        val es: Set<String> = sortedMap.keys
        for (k in es) {
            val v = sortedMap[k]
            if (!TextUtils.isEmpty(v) && "sign" != k && "key" != k) {
                sb.append("$k=$v&")
            }
        }
        if (Wxpay.DEBUG) {
            Wxpay.log("生成字符串：$sb")
        }
        sb.append("key=" + Wxpay.Config.api_key)
        if (Wxpay.DEBUG) {
            Wxpay.log("连接商户key：$sb")
        }
        val sign = getMessageDigest(sb.toString().toByteArray())
        if (Wxpay.DEBUG) {
            Wxpay.log("MD5并转成大写：$sign")
        }
        return sign
    }
}

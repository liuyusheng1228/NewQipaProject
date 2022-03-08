package com.qipa.jetpackmvvm.pay.wxpay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tencent.mm.opensdk.openapi.WXAPIFactory

import com.tencent.mm.opensdk.openapi.IWXAPI


class AppRegister : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (Wxpay.DEBUG) {
            Wxpay.log("onReceive: $intent")
        }
        val api = WXAPIFactory.createWXAPI(context, null)
        api.registerApp(Wxpay.Config.app_id)
    }
}

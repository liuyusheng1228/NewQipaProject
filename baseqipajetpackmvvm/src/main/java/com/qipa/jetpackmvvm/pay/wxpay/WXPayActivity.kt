package com.qipa.jetpackmvvm.pay.wxpay

import android.app.Activity
import android.content.Intent
import com.tencent.mm.opensdk.constants.ConstantsAPI

import com.tencent.mm.opensdk.modelbase.BaseResp

import com.tencent.mm.opensdk.modelbase.BaseReq

import android.content.Intent.getIntent
import android.os.Bundle

import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler


class WXPayActivity : Activity(), IWXAPIEventHandler {
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Wxpay.getInstance(this) != null) {
            Wxpay.getInstance(this)!!.handleIntent(getIntent(), this)
        } else {
            finish()
        }
    }

    protected override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (Wxpay.getInstance(this) != null) {
            Wxpay.getInstance(this)!!.handleIntent(intent, this)
        }
    }

    override fun onReq(baseReq: BaseReq) {}
    override fun onResp(baseResp: BaseResp) {
        if (baseResp.type == ConstantsAPI.COMMAND_PAY_BY_WX) {
            Wxpay.getInstance(this)!!.onResp(baseResp)
            finish()
        }
    }
}
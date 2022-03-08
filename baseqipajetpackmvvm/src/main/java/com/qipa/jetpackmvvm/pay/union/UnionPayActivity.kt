package com.qipa.jetpackmvvm.pay.union

import android.app.Activity
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle


class UnionPayActivity : Activity() {
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (UnionPay.getInstance(this) != null) {
            UnionPay.getInstance(this)!!.handleIntent(getIntent(), this)
        } else {
            finish()
        }
    }

    protected override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (UnionPay.getInstance(this) != null) {
            UnionPay.getInstance(this)!!.handleIntent(intent, this)
        }
    }

    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /*************************************************
         * 步骤3：处理银联手机支付控件返回的支付结果
         */
        UnionPay.getInstance(this)!!.onResp(data)
        finish()
    }
}

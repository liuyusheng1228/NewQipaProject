package com.qipa.newboxproject.ui.fragment.pay

import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.qipa.jetpackmvvm.pay.google.GoogleBillingUtil

import com.android.billingclient.api.Purchase

import com.android.billingclient.api.PurchaseHistoryRecord

import com.android.billingclient.api.SkuDetails
import com.qipa.jetpackmvvm.pay.google.GoogleBillingUtil.GoogleBillingListenerTag

import com.qipa.jetpackmvvm.pay.google.OnGoogleBillingListener
import com.qipa.newboxproject.BuildConfig
import com.qipa.newboxproject.R


class PurchaseActivity : AppCompatActivity() {
    private var mGoogleBillingUtil: GoogleBillingUtil? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_purchase)
        GoogleBillingUtil.isDebug(BuildConfig.DEBUG)
        GoogleBillingUtil.setSkus(arrayOf("shopSku"), null) // 这里需要自行修改sku
        GoogleBillingUtil.setIsAutoAcknowledgePurchase(true)
        mGoogleBillingUtil = GoogleBillingUtil.instance
            .addOnGoogleBillingListener(this, OnMyGoogleBillingListener()).build(this)
//        findViewById(R.id.button1).setOnClickListener { v ->
//            mGoogleBillingUtil!!.purchaseInApp(
//                this,
//                mGoogleBillingUtil!!.getInAppSkuByPosition(0)!!
//            )
//        }
//        findViewById(R.id.button2).setOnClickListener { v ->
//            mGoogleBillingUtil!!.queryPurchaseHistoryAsyncInApp(
//                this
//            )
//        }
//        findViewById(R.id.button3).setOnClickListener { v ->
//            mGoogleBillingUtil!!.purchaseSubs(
//                this,
//                mGoogleBillingUtil!!.getSubsSkuByPosition(0)!!
//            )
//        }
//        findViewById(R.id.button4).setOnClickListener { v ->
//            mGoogleBillingUtil!!.queryPurchaseHistoryAsyncSubs(
//                this
//            )
//        }
    }

    class OnMyGoogleBillingListener : OnGoogleBillingListener() {
        override fun onSetupSuccess(isSelf: Boolean) {
            super.onSetupSuccess(isSelf)
            log("内购服务初始化完成")
        }

        override fun onQuerySuccess(skuType: String, list: List<SkuDetails?>, isSelf: Boolean) {
            super.onQuerySuccess(skuType!!, list, isSelf)
            if (!isSelf) {
                return
            }
            for (skuDetails in list) {
                log(skuDetails?.getSku() + "---" + skuDetails?.getDescription() + "---" + skuDetails?.getPrice())
            }
        }

        override fun onRecheck(
            @NonNull skuType: String,
            @NonNull purchase: Purchase,
            isSelf: Boolean
        ): Boolean {
            log(
                "检测到订单" + skuType + ":" + purchase.getSkus().toString() + "--" + getPurchaseState(
                    purchase.getPurchaseState()
                )
            )
            return true
        }

        override fun onPurchaseSuccess(@NonNull purchase: Purchase, isSelf: Boolean): Boolean {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                log("支付成功")
            }
            return true
        }

        override fun onConsumeSuccess(@NonNull purchaseToken: String, isSelf: Boolean) {
            log("消耗商品成功:$purchaseToken")
        }

        override fun onAcknowledgePurchaseSuccess(isSelf: Boolean) {
            log("确认购买商品成功")
        }

        override fun onFail(
            @NonNull tag: GoogleBillingListenerTag,
            responseCode: Int, isSelf: Boolean
        ) {
            log("操作失败:tag=" + tag.name + ",responseCode=" + responseCode)
        }

        override fun onError(
            @NonNull tag: GoogleBillingListenerTag,
            isSelf: Boolean
        ) {
            log("发生错误:tag= " + tag.name)
        }

        override fun onQueryHistory(@NonNull purchaseList: List<PurchaseHistoryRecord?>) {
            super.onQueryHistory(purchaseList)
            log("返回所有内购和订阅的历史订单: " + purchaseList.size)
        }
    }

    protected override fun onDestroy() {
        super.onDestroy()
        if (mGoogleBillingUtil != null) {
            mGoogleBillingUtil!!.onDestroy(this)
        }
        GoogleBillingUtil.endConnection()
    }

    companion object {
        fun log(msg: String?) {
            Log.e("googlebillinglib", ""+msg)
        }

        fun getPurchaseState(purchaseState: Int): String {
            return when (purchaseState) {
                Purchase.PurchaseState.PURCHASED -> "PURCHASED"
                Purchase.PurchaseState.PENDING -> "PENDING"
                Purchase.PurchaseState.UNSPECIFIED_STATE -> "UNSPECIFIED_STATE"
                else -> "未知状态"
            }
        }
    }
}
package com.qipa.jetpackmvvm.pay.google

import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord
import com.android.billingclient.api.SkuDetails


open class OnGoogleBillingListener {
    var tag: String? = null

    /**
     * 查询成功
     * @param skuType 内购或者订阅
     * @param list 商品列表
     * @param isSelf 是否是当前页面的结果
     */
    open fun onQuerySuccess(skuType: String, list: List<SkuDetails?>, isSelf: Boolean) {}
    open fun onPurchaseSuccess(purchase: Purchase, isSelf: Boolean): Boolean {
        return true
    }

    /**
     * 初始化成功
     * @param isSelf 是否是当前页面的结果
     */
    open fun onSetupSuccess(isSelf: Boolean) {}
    open  fun onRecheck(skuType: String, purchase: Purchase, isSelf: Boolean): Boolean {
        return false
    }

    /**
     * 链接断开
     */
    open fun onBillingServiceDisconnected() {}

    /**
     * 消耗成功
     * @param purchaseToken token
     * @param isSelf 是否是当前页面的结果
     */
    open fun onConsumeSuccess(purchaseToken: String, isSelf: Boolean) {}

    /**
     * 确认购买成功
     * @param isSelf 是否是当前页面的结果
     */
    open fun onAcknowledgePurchaseSuccess(isSelf: Boolean) {}

    /**
     * 失败回调
     * @param tag [GoogleBillingUtil.GoogleBillingListenerTag]
     * @param responseCode 返回码{https://developer.android.com/google/play/billing/billing_reference}
     * @param isSelf 是否是当前页面的结果
     */
    open fun onFail(tag: GoogleBillingUtil.GoogleBillingListenerTag, responseCode: Int, isSelf: Boolean) {}

    /**
     * google组件初始化失败等等。
     * @param tag [GoogleBillingUtil.GoogleBillingListenerTag]
     * @param isSelf 是否是当前页面的结果
     */
    open fun onError(tag: GoogleBillingUtil.GoogleBillingListenerTag, isSelf: Boolean) {}

    /**
     * 获取内购和订阅所有历史订单-无论是否还有效
     * @param purchaseList 商品历史列表
     */
    open fun onQueryHistory(purchaseList: List<PurchaseHistoryRecord?>) {}
}
package com.qipa.jetpackmvvm.pay.google

import android.app.Activity
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.android.billingclient.api.BillingClient

import com.android.billingclient.api.PurchaseHistoryRecord

import com.android.billingclient.api.BillingResult

import com.android.billingclient.api.PurchaseHistoryResponseListener

import com.android.billingclient.api.AcknowledgePurchaseResponseListener

import com.android.billingclient.api.ConsumeResponseListener

import com.android.billingclient.api.SkuDetails

import com.android.billingclient.api.SkuDetailsResponseListener

import com.android.billingclient.api.Purchase

import com.android.billingclient.api.PurchasesUpdatedListener

import com.android.billingclient.api.AcknowledgePurchaseParams

import com.android.billingclient.api.ConsumeParams

import com.android.billingclient.api.BillingFlowParams

import com.android.billingclient.api.SkuDetailsParams

import com.android.billingclient.api.BillingClientStateListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class GoogleBillingUtil private constructor() {
    private val purchasesUpdatedListener = MyPurchasesUpdatedListener()

    /**
     * 开始建立内购连接
     *
     * @param activity activity
     */
    fun build(activity: Activity?): GoogleBillingUtil {
        purchasesUpdatedListener.tag = getTag(activity)
        if (mBillingClient == null) {
            synchronized(instance) {
                if (mBillingClient == null) {
                    builder = activity?.let { BillingClient.newBuilder(it) }
                    mBillingClient =
                        builder!!.setListener(purchasesUpdatedListener)
                            .enablePendingPurchases()
                            .build()
                } else {
                    builder!!.setListener(purchasesUpdatedListener)
                }
            }
        } else {
            builder!!.setListener(purchasesUpdatedListener)
        }
        synchronized(instance) {
            if (instance.startConnection(activity)) {
                instance.queryInventoryInApp(getTag(activity))
                instance.queryInventorySubs(getTag(activity))
                instance.queryPurchasesInApp(getTag(activity))
            }
        }
        return instance
    }

    fun startConnection(activity: Activity?): Boolean {
        return startConnection(getTag(activity))
    }

    private fun startConnection(tag: String): Boolean {
        if (mBillingClient == null) {
            log("初始化失败:mBillingClient==null")
            return false
        }
        return if (!mBillingClient!!.isReady) {
            mBillingClient!!.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        for (listener in onGoogleBillingListenerList) {
                            listener.onSetupSuccess(listener.tag == tag)
                        }
                        queryInventoryInApp(tag)
                        queryInventorySubs(tag)
                        queryPurchasesInApp(tag)
                    } else {
                        log("初始化失败:onSetupFail:code=" + billingResult.responseCode)
                        for (listener in onGoogleBillingListenerList) {
                            listener.onFail(
                                GoogleBillingListenerTag.SETUP, billingResult.responseCode,
                                listener.tag == tag
                            )
                        }
                    }
                }

                override fun onBillingServiceDisconnected() {
                    for (listener in onGoogleBillingListenerList) {
                        listener.onBillingServiceDisconnected()
                    }
                    log("初始化失败:onBillingServiceDisconnected")
                }
            })
            false
        } else {
            true
        }
    }
    //endregion
    //region===================================查询商品=================================
    /**
     * 查询内购商品信息
     */
    fun queryInventoryInApp(activity: Activity?) {
        queryInventoryInApp(getTag(activity))
    }

    private fun queryInventoryInApp(tag: String) {
        queryInventory(tag, BillingClient.SkuType.INAPP)
    }

    /**
     * 查询订阅商品信息
     */
    fun queryInventorySubs(activity: Activity?) {
        queryInventory(getTag(activity), BillingClient.SkuType.SUBS)
    }

    fun queryInventorySubs(tag: String) {
        queryInventory(tag, BillingClient.SkuType.SUBS)
    }

    private fun queryInventory(tag: String, skuType: String) {
        val runnable = label@ Runnable {
            if (mBillingClient == null) {
                for (listener in onGoogleBillingListenerList) {
                    listener.onError(GoogleBillingListenerTag.QUERY, listener.tag == tag)
                }
                return@Runnable
            }
            val skuList: ArrayList<String> = ArrayList()
            if (skuType == BillingClient.SkuType.INAPP) {
                Collections.addAll(skuList, inAppSKUS.toString())
            } else if (skuType == BillingClient.SkuType.SUBS) {
                Collections.addAll(skuList, subsSKUS.toString())
            }
            if (!skuList.isEmpty()) {
                val params = SkuDetailsParams.newBuilder()
                params.setSkusList(skuList).setType(skuType)
                mBillingClient!!.querySkuDetailsAsync(
                    params.build(),
                    MySkuDetailsResponseListener(skuType, tag)
                )
            }
        }
        executeServiceRequest(tag, runnable)
    }
    //endregion
    //region===================================购买商品=================================
    /**
     * 发起内购
     *
     * @param skuId 内购商品id
     */
    fun purchaseInApp(activity: Activity, skuId: String) {
        purchase(activity, skuId, BillingClient.SkuType.INAPP)
    }

    /**
     * 发起订阅
     *
     * @param skuId 订阅商品id
     */
    fun purchaseSubs(activity: Activity, skuId: String) {
        purchase(activity, skuId, BillingClient.SkuType.SUBS)
    }

    private fun purchase(activity: Activity, skuId: String, skuType: String) {
        val tag = getTag(activity)
        if (mBillingClient == null) {
            for (listener in onGoogleBillingListenerList) {
                listener.onError(GoogleBillingListenerTag.PURCHASE, listener.tag == tag)
            }
            return
        }
        if (startConnection(tag)) {
            purchasesUpdatedListener.tag = tag
            builder!!.setListener(purchasesUpdatedListener)
            val skuList: MutableList<String> = ArrayList()
            skuList.add(skuId)
            val skuDetailsParams = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(skuType)
                .build()
            mBillingClient!!.querySkuDetailsAsync(
                skuDetailsParams
            ) { responseCode: BillingResult?, skuDetailsList: List<SkuDetails?>? ->
                if (skuDetailsList != null && !skuDetailsList.isEmpty()) {
                    val flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetailsList[0]!!)
                        .build()
                    mBillingClient!!.launchBillingFlow(
                        activity,
                        flowParams
                    )
                }
            }
        } else {
            for (listener in onGoogleBillingListenerList) {
                listener.onError(GoogleBillingListenerTag.PURCHASE, listener.tag == tag)
            }
        }
    }
    //endregion
    //region===================================消耗商品=================================
    /**
     * 消耗商品
     */
    fun consumeAsync(activity: Activity?, purchaseToken: String?) {
        purchaseToken?.let { consumeAsync(getTag(activity), it, null) }
    }

    fun consumeAsync(
        activity: Activity?,
        purchaseToken: String?,
        @Nullable developerPayload: String?
    ) {
        if (purchaseToken != null) {
            consumeAsync(getTag(activity), purchaseToken, developerPayload)
        }
    }

    /**
     * 消耗商品
     */
    private fun consumeAsync(
        tag: String,
        purchaseToken: String,
        @Nullable developerPayload: String? = null
    ) {
        if (mBillingClient == null) {
            return
        }
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        mBillingClient!!.consumeAsync(consumeParams, MyConsumeResponseListener(tag))
    }

    /**
     * 消耗内购商品-通过sku数组
     *
     * @param sku sku
     */
    fun consumeAsyncInApp(activity: Activity?,  sku: String) {
        if (mBillingClient == null) {
            return
        }
        val skuList: List<String> = listOf(sku)
        consumeAsyncInApp(activity, skuList, null)
    }

    /**
     * 消耗内购商品-通过sku数组
     *
     * @param skuList sku数组
     */
    fun consumeAsyncInApp(
        activity: Activity?,
        @NonNull skuList: List<String>,
        @Nullable developerPayloadList: List<String?>?
    ) {
        if (mBillingClient == null) {
            return
        }
        val purchaseList: List<Purchase>? = queryPurchasesInApp(activity)
        if (purchaseList != null) {
            for (purchase in purchaseList) {
                for (sku in purchase.skus) {
                    val index = skuList.indexOf(sku)
                    if (index != -1) {
                        if (developerPayloadList != null && index < developerPayloadList.size) {
                            consumeAsync(
                                activity, purchase.purchaseToken,
                                developerPayloadList[index]
                            )
                        } else {
                            consumeAsync(activity, purchase.purchaseToken, null)
                        }
                    }
                }
            }
        }
    }
    //endregion
    //region===================================确认购买=================================
    /**
     * 确认购买
     *
     * @param activity      activity
     * @param purchaseToken token
     */
    fun acknowledgePurchase(activity: Activity?, purchaseToken: String?) {
        acknowledgePurchase(activity, purchaseToken, null)
    }

    fun acknowledgePurchase(
        activity: Activity?,
        purchaseToken: String?,
        @Nullable developerPayload: String?
    ) {
        purchaseToken?.let { acknowledgePurchase(getTag(activity), it, developerPayload) }
    }

    private fun acknowledgePurchase(
        tag: String,
        purchaseToken: String,
        @Nullable developerPayload: String? = null
    ) {
        if (mBillingClient == null) {
            return
        }
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        mBillingClient!!.acknowledgePurchase(params, MyAcknowledgePurchaseResponseListener(tag))
    }
    //endregion
    //region===================================本地订单查询=================================
    /**
     * 获取已经内购的商品
     *
     * @return 商品列表
     */
    fun queryPurchasesInApp(activity: Activity?): List<Purchase>? {
        return queryPurchases(getTag(activity), BillingClient.SkuType.INAPP)
    }

    private fun queryPurchasesInApp(tag: String): List<Purchase>? {
        return queryPurchases(tag, BillingClient.SkuType.INAPP)
    }

    /**
     * 获取已经订阅的商品
     *
     * @return 商品列表
     */
    fun queryPurchasesSubs(activity: Activity?): List<Purchase>? {
        return queryPurchases(getTag(activity), BillingClient.SkuType.SUBS)
    }

    private fun queryPurchases(tag: String, skuType: String): List<Purchase>? {
        if (mBillingClient == null) {
            return null
        }
        if (!mBillingClient!!.isReady) {
            startConnection(tag)
        } else {
            val purchasesResult = mBillingClient!!.queryPurchases(skuType)
            if (purchasesResult != null) {
                if (purchasesResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val purchaseList = purchasesResult.purchasesList
                    if (purchaseList != null && !purchaseList.isEmpty()) {
                        for (purchase in purchaseList) {
                            for (listener in onGoogleBillingListenerList) {
                                val isSelf = listener.tag == tag //是否是当前页面
                                val isSuccess =
                                    listener.onRecheck(skuType, purchase, isSelf) //是否消耗或者确认
                                if (isSelf) {
                                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                        if (skuType == BillingClient.SkuType.INAPP) {
                                            if (isSuccess) {
                                                consumeAsync(tag, purchase.purchaseToken)
                                            } else if (isAutoAcknowledgePurchase) {
                                                if (!purchase.isAcknowledged) {
                                                    acknowledgePurchase(tag, purchase.purchaseToken)
                                                }
                                            }
                                        } else if (skuType == BillingClient.SkuType.SUBS) {
                                            if (isAutoAcknowledgePurchase) {
                                                if (!purchase.isAcknowledged) {
                                                    acknowledgePurchase(tag, purchase.purchaseToken)
                                                }
                                            }
                                        }
                                    } else {
                                        log("未支付的订单:" + purchase.skus.toString())
                                    }
                                }
                            }
                        }
                    }
                    return purchaseList
                }
            }
        }
        return null
    }
    //endregion
    //region===================================在线订单查询=================================
    /**
     * 异步联网查询所有的内购历史-无论是过期的、取消、等等的订单
     *
     * @param activity activity
     */
    fun queryPurchaseHistoryAsyncInApp(activity: Activity?): Boolean {
        return queryPurchaseHistoryAsync(getTag(activity), BILLING_TYPE_INAPP)
    }

    /**
     * 异步联网查询所有的订阅历史-无论是过期的、取消、等等的订单
     *
     * @param activity activity
     */
    fun queryPurchaseHistoryAsyncSubs(activity: Activity?): Boolean {
        return queryPurchaseHistoryAsync(getTag(activity), BILLING_TYPE_SUBS)
    }

    private fun queryPurchaseHistoryAsync(tag: String, type: String): Boolean {
        if (isReady) {
            mBillingClient!!.queryPurchaseHistoryAsync(type, MyPurchaseHistoryResponseListener(tag))
            return true
        }
        return false
    }
    //endregion
    //region===================================工具集合=================================
    /**
     * 获取有效订阅的数量
     *
     * @return -1查询失败，0没有有效订阅，大于0具有有效的订阅
     */
    fun getPurchasesSizeSubs(activity: Activity?): Int {
        val list = queryPurchasesSubs(activity)
        return list?.size ?: -1
    }

    /**
     * 通过sku获取订阅商品序号
     *
     * @param sku sku
     * @return 序号
     */
    fun getSubsPositionBySku(sku: String): Int {
        return getPositionBySku(sku, BillingClient.SkuType.SUBS)
    }

    /**
     * 通过sku获取内购商品序号
     *
     * @param sku sku
     * @return 成功返回需要 失败返回-1
     */
    fun getInAppPositionBySku(sku: String): Int {
        return getPositionBySku(sku, BillingClient.SkuType.INAPP)
    }

    private fun getPositionBySku(sku: String, skuType: String): Int {
        if (skuType == BillingClient.SkuType.INAPP) {
            var i = 0
            for (s in inAppSKUS) {
                if (s == sku) {
                    return i
                }
                i++
            }
        } else if (skuType == BillingClient.SkuType.SUBS) {
            var i = 0
            for (s in subsSKUS) {
                if (s == sku) {
                    return i
                }
                i++
            }
        }
        return -1
    }

    /**
     * 通过序号获取订阅sku
     *
     * @param position 序号
     * @return sku
     */
    fun getSubsSkuByPosition(position: Int): String? {
        return if (position >= 0 && position < subsSKUS.size) {
            subsSKUS[position]
        } else {
            null
        }
    }

    /**
     * 通过序号获取内购sku
     *
     * @param position 序号
     * @return sku
     */
    fun getInAppSkuByPosition(position: Int): String? {
        return if (position >= 0 && position < inAppSKUS.size) {
            inAppSKUS[position]
        } else {
            null
        }
    }

    /**
     * 通过sku获取商品类型(订阅获取内购)
     *
     * @param sku sku
     * @return inapp内购，subs订阅
     */
    fun getSkuType(sku: String?): String? {
        if (inAppSKUS.contains(sku)) {
            return BillingClient.SkuType.INAPP
        } else if (subsSKUS.contains(sku)) {
            return BillingClient.SkuType.SUBS
        }
        return null
    }


    private fun getTag(activity: Activity?): String {
        return activity?.getLocalClassName().toString()
    }

    private fun executeServiceRequest(tag: String, runnable: Runnable) {
        if (startConnection(tag)) {
            runnable.run()
        }
    }

    //endregion
    fun addOnGoogleBillingListener(
        activity: Activity?,
        onGoogleBillingListener: OnGoogleBillingListener
    ): GoogleBillingUtil {
        val tag = getTag(activity)
        onGoogleBillingListener.tag = tag
        onGoogleBillingListenerMap[getTag(activity)] =
            onGoogleBillingListener
        for (i in onGoogleBillingListenerList.indices.reversed()) {
            val listener = onGoogleBillingListenerList[i]
            if (listener.tag == tag) {
                onGoogleBillingListenerList.remove(listener)
            }
        }
        onGoogleBillingListenerList.add(onGoogleBillingListener)
        return this
    }

    fun removeOnGoogleBillingListener(onGoogleBillingListener: OnGoogleBillingListener) {
        onGoogleBillingListenerList.remove(onGoogleBillingListener)
    }

    fun removeOnGoogleBillingListener(activity: Activity?) {
        val tag = getTag(activity)
        for (i in onGoogleBillingListenerList.indices.reversed()) {
            val listener = onGoogleBillingListenerList[i]
            if (listener.tag == tag) {
                removeOnGoogleBillingListener(listener)
                onGoogleBillingListenerMap.remove(tag)
            }
        }
    }

    /**
     * 清除内购监听器，防止内存泄漏-在Activity-onDestroy里面调用。
     * 需要确保onDestroy和build方法在同一个线程。
     */
    fun onDestroy(activity: Activity?) {
        if (builder != null) {
            builder!!.setListener { billingResult, mutableList ->
                billingResult == null
                mutableList?.clear()
            }

        }

        removeOnGoogleBillingListener(activity)
    }

    /**
     * Google购买商品回调接口(订阅和内购都走这个接口)
     */
    private inner class MyPurchasesUpdatedListener : PurchasesUpdatedListener {
        var tag: String? = null
        override fun onPurchasesUpdated(
            billingResult: BillingResult,
            @Nullable list: List<Purchase>?
        ) {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
                for (purchase in list) {
                    for (listener in onGoogleBillingListenerList) {
                        val isSelf = listener.tag == tag //是否是当前页面
                        val isSuccess = listener.onPurchaseSuccess(purchase, isSelf) //是否自动消耗
                        for (sku in purchase.skus) {
                            if (isSelf && purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                //是当前页面，并且商品状态为支付成功，才会进行消耗与确认的操作
                                val skuType = getSkuType(sku)
                                if (BillingClient.SkuType.INAPP == skuType) {
                                    if (isSuccess) {
                                        //进行消耗
                                        tag?.let { consumeAsync(it, purchase.purchaseToken) }
                                    } else if (isAutoAcknowledgePurchase) {
                                        //进行确认购买
                                        if (!purchase.isAcknowledged) {
                                            tag?.let { acknowledgePurchase(it, purchase.purchaseToken) }
                                        }
                                    }
                                } else if (BillingClient.SkuType.SUBS == skuType) {
                                    //进行确认购买
                                    if (isAutoAcknowledgePurchase) {
                                        if (!purchase.isAcknowledged) {
                                            tag?.let { acknowledgePurchase(it, purchase.purchaseToken) }
                                        }
                                    }
                                }
                            } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                                log("待处理的订单:$sku")
                            }
                        }
                    }
                }
            } else {
                if (IS_DEBUG) {
                    log("购买失败,responseCode:" + billingResult.responseCode + ",msg:" + billingResult.debugMessage)
                }
                for (listener in onGoogleBillingListenerList) {
                    listener.onFail(
                        GoogleBillingListenerTag.PURCHASE, billingResult.responseCode,
                        listener.tag == tag
                    )
                }
            }
        }
    }

    /**
     * Google查询商品信息回调接口
     */
    private class MySkuDetailsResponseListener(
        private val skuType: String,
        private val tag: String
    ) :
        SkuDetailsResponseListener {
        override fun onSkuDetailsResponse(billingResult: BillingResult, list: List<SkuDetails>?) {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
                for (listener in onGoogleBillingListenerList) {
                    listener.onQuerySuccess(skuType, list, listener.tag == tag)
                }
            } else {
                for (listener in onGoogleBillingListenerList) {
                    listener.onFail(
                        GoogleBillingListenerTag.QUERY, billingResult.responseCode,
                        listener.tag == tag
                    )
                }
                if (IS_DEBUG) {
                    log("查询失败,responseCode:" + billingResult.responseCode + ",msg:" + billingResult.debugMessage)
                }
            }
        }
    }

    /**
     * Googlg消耗商品回调
     */
    private class MyConsumeResponseListener(private val tag: String) :
        ConsumeResponseListener {
        override fun onConsumeResponse(billingResult: BillingResult, purchaseToken: String) {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (listener in onGoogleBillingListenerList) {
                    listener.onConsumeSuccess(purchaseToken, listener.tag == tag)
                }
            } else {
                for (listener in onGoogleBillingListenerList) {
                    listener.onFail(
                        GoogleBillingListenerTag.COMSUME, billingResult.responseCode,
                        listener.tag == tag
                    )
                }
                if (IS_DEBUG) {
                    log("消耗失败,responseCode:" + billingResult.responseCode + ",msg:" + billingResult.debugMessage)
                }
            }
        }
    }

    /**
     * Googlg消耗商品回调
     */
    private class MyAcknowledgePurchaseResponseListener(private val tag: String) :
        AcknowledgePurchaseResponseListener {
        override fun onAcknowledgePurchaseResponse(billingResult: BillingResult) {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (listener in onGoogleBillingListenerList) {
                    listener.onAcknowledgePurchaseSuccess(listener.tag == tag)
                }
            } else {
                for (listener in onGoogleBillingListenerList) {
                    listener.onFail(
                        GoogleBillingListenerTag.AcKnowledgePurchase, billingResult.responseCode,
                        listener.tag == tag
                    )
                }
                if (IS_DEBUG) {
                    log("确认购买失败,responseCode:" + billingResult.responseCode + ",msg:" + billingResult.debugMessage)
                }
            }
        }
    }

    private class MyPurchaseHistoryResponseListener(private val tag: String) :
        PurchaseHistoryResponseListener {
        override fun onPurchaseHistoryResponse(
            billingResult: BillingResult,
            list: List<PurchaseHistoryRecord>?
        ) {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
                for (listener in onGoogleBillingListenerList) {
                    listener.onQueryHistory(list)
                }
            } else {
                for (listener in onGoogleBillingListenerList) {
                    listener.onFail(
                        GoogleBillingListenerTag.HISTORY, billingResult.responseCode,
                        listener.tag == tag
                    )
                }
            }
        }
    }

    enum class GoogleBillingListenerTag(var tag: String) {
        QUERY("query"), PURCHASE("purchase"), SETUP("setup"), COMSUME("comsume"), AcKnowledgePurchase(
            "AcKnowledgePurchase"
        ),
        HISTORY("history");
    }

    companion object {
        private const val TAG = "GoogleBillingUtil"
        private var IS_DEBUG = false
        private var inAppSKUS = arrayOf<String>() //内购ID,必填，注意！如果用不着的请去掉多余的
        private var subsSKUS = arrayOf<String>() //订阅ID,必填，注意！如果用不着的请去掉多余的
        const val BILLING_TYPE_INAPP = "inapp" //内购
        const val BILLING_TYPE_SUBS = "subs" //订阅
        private var mBillingClient: BillingClient? = null
        private var builder: BillingClient.Builder? = null
        private val onGoogleBillingListenerList: MutableList<OnGoogleBillingListener> = ArrayList()
        private val onGoogleBillingListenerMap: MutableMap<String, OnGoogleBillingListener> =
            HashMap()
        private var isAutoAcknowledgePurchase = true
        val instance = GoogleBillingUtil()

        //初始化google应用内购买服务
        /**
         * 设置skus
         *
         * @param inAppSKUS 内购id
         * @param subsSKUS  订阅id
         */
        fun setSkus(@Nullable inAppSKUS: Array<String?>?, @Nullable subsSKUS: Array<String?>?) {
            if (inAppSKUS != null) {
                Companion.inAppSKUS = Arrays.copyOf(inAppSKUS, inAppSKUS.size)
            }
            if (subsSKUS != null) {
                Companion.subsSKUS = Arrays.copyOf(subsSKUS, subsSKUS.size)
            }
        }

        private fun <T> copyToArray(base: Array<T>, target: Array<T>) {
            System.arraycopy(base, 0, target, 0, base.size)
        }

        /**
         * google内购服务是否已经准备好
         *
         * @return boolean
         */
        val isReady: Boolean
            get() = mBillingClient != null && mBillingClient!!.isReady

        /**
         * 设置是否自动确认购买
         *
         * @param isAutoAcknowledgePurchase boolean
         */
        fun setIsAutoAcknowledgePurchase(isAutoAcknowledgePurchase: Boolean) {
            Companion.isAutoAcknowledgePurchase = isAutoAcknowledgePurchase
        }

        /**
         * 断开连接google服务
         * 注意！！！一般情况不建议调用该方法，让google保留连接是最好的选择。
         */
        fun endConnection() {
            //注意！！！一般情况不建议调用该方法，让google保留连接是最好的选择。
            if (mBillingClient != null) {
                if (mBillingClient!!.isReady) {
                    mBillingClient!!.endConnection()
                    mBillingClient = null
                }
            }
        }

        private fun log(msg: String) {
            if (IS_DEBUG) {
                Log.e(TAG, msg)
            }
        }

        fun isDebug(isDebug: Boolean) {
            IS_DEBUG = isDebug
        }
    }
}
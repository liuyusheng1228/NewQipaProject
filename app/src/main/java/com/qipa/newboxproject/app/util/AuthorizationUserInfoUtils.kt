package com.qipa.newboxproject.app.util

object AuthorizationUserInfoUtils {
    fun canGetUserInfo(platform: String): Boolean {
        return !("WechatMoments" == platform || "WechatFavorite" == platform || "ShortMessage" == platform || "Email" == platform || "Pinterest" == platform || "Yixin" == platform || "YixinMoments" == platform || "Bluetooth" == platform || "WhatsApp" == platform || "Pocket" == platform || "BaiduTieba" == platform || "Laiwang" == platform || "LaiwangMoments" == platform || "Alipay" == platform || "AlipayMoments".endsWith(
            platform
        )
                || "FacebookMessenger" == platform || "Youtube" == platform || "Meipai" == platform || "Tiktok" == platform || "Telegram" == platform)
    }

    fun canAuthorize(platform: String): Boolean {
        return !("WechatMoments" == platform || "WechatFavorite" == platform || "Pinterest" == platform || "Yixin" == platform || "YixinMoments" == platform || "Email" == platform || "Bluetooth" == platform || "WhatsApp" == platform || "BaiduTieba" == platform || "Laiwang" == platform || "LaiwangMoments" == platform || "Alipay" == platform || "AlipayMoments" == platform || "FacebookMessenger" == platform || "Youtube" == platform || "Meipai" == platform || "Tiktok" == platform || "Telegram" == platform)
    }
}
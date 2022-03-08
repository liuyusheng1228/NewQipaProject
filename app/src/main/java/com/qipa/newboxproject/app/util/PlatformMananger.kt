package com.qipa.newboxproject.app.util

import android.content.Context
import android.os.Message
import cn.sharesdk.framework.CustomPlatform
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.ShareSDK
import com.mob.tools.utils.ResHelper
import com.qipa.newboxproject.app.App
import com.qipa.newboxproject.data.model.bean.PlatformEntity
import com.qipa.newboxproject.data.model.bean.ShareListItemInEntity
import java.util.*

class PlatformMananger private constructor(){
    private var lists: MutableList<ShareListItemInEntity?> = ArrayList<ShareListItemInEntity?>()
    private var chinaList: MutableList<ShareListItemInEntity?> = ArrayList<ShareListItemInEntity?>()
    private var systemList: MutableList<ShareListItemInEntity?> =
        ArrayList<ShareListItemInEntity?>()
    private var systemListNormal: MutableList<PlatformEntity?> = ArrayList<PlatformEntity?>()

    private var chinaListNormal: MutableList<PlatformEntity?> = ArrayList<PlatformEntity?>()
    private var normalList: MutableList<PlatformEntity?> = ArrayList<PlatformEntity?>()

    private var chinaListNormalUserInfo: MutableList<PlatformEntity?> = ArrayList<PlatformEntity?>()
    private var normalListUserInfo: MutableList<PlatformEntity?> = ArrayList<PlatformEntity?>()

    private var instance: PlatformMananger? = null
    private var context: Context? = null
    val SDK_SINAWEIBO_UID = "3189087725"
    val SDK_TENCENTWEIBO_UID = "shareSDK"
    var china = arrayOf(
        "SinaWeibo",
        "TencentWeibo",
        "QZone",
        "Wechat",
        "WechatMoments",
        "WechatFavorite",
        "QQ",
        "Renren",
        "KaiXin",
        "Douban",
        "YouDao",
        "Yixin",
        "YixinMoments",
        "Mingdao",
        "Alipay",
        "AlipayMoments",
        "Dingding",
        "Meipai",
        "Cmcc",
        "Telecom",
        "Douyin",
        "Wework",
        "HWAccount",
        "Oasis",
        "XMAccount",
        "Kuaishou",
        "Littleredbook",
        "Watermelonvideo"
    )
    var system = arrayOf("Email", "ShortMessage", "Bluetooth")

   private fun PlatformMananger() {
        this.context = App.instance.applicationContext
        val list = ShareSDK.getPlatformList()
        if (list != null) {
            val msg = Message()
            msg.obj = list
            afterPlatformsGot(msg.obj as Array<Platform>)

//			UIHandler.sendMessage(msg, new Handler.Callback() {
//				public boolean handleMessage(Message msg) {
//					afterPlatformsGot((Platform[]) msg.obj);
//					return false;
//				}
//			});
        }
    }

    init {
        PlatformMananger()
    }

    private fun afterPlatformsGot(platforms: Array<Platform>) {
        var entity: ShareListItemInEntity? = null
        var normalEntity: PlatformEntity? = null
        for (platform in platforms) {
            val name = platform.name
            //客户端分享的情况
//			if (DemoUtils.isUseClientToShare(name)) {
//				continue;
//			}
            if (platform is CustomPlatform) {
                continue
            }
            //#if def{lang} == cn
            // 处理左边按钮和右边按钮
            //#elif def{lang} == en
            // initiate buttons
            //#endif
            entity = ShareListItemInEntity()
            entity.platform = platform
            normalEntity = PlatformEntity()
            normalEntity.setmPlatform(platform)
            entity.type = 3
            val resName = "ssdk_oks_classic_$name"
            val platNameRes = ResHelper.getStringRes(context, "ssdk_" + name.toLowerCase())
            val resId: Int = ResourcesUtils.getBitmapRes(context, resName.toLowerCase())
            if (resId > 0) {
                entity.icon = resId
                normalEntity.setmIcon(resId)
            }
            if (platNameRes > 0) {
                val platName = context!!.getString(platNameRes)
                entity.name = platName
                normalEntity.setName(platName)
            }
            if (Arrays.asList(*china).contains(name)) {
                if (name != "Cmcc" && name != "Telecom") {
                    chinaList.add(entity)
                }
                if (AuthorizationUserInfoUtils.canAuthorize(name)) {
                    chinaListNormal.add(normalEntity)
                }
                if (AuthorizationUserInfoUtils.canGetUserInfo(name)) {
                    chinaListNormalUserInfo.add(normalEntity)
                }
            } else {
                if (Arrays.asList(*system).contains(name)) {
                    systemList.add(entity)
                    if (AuthorizationUserInfoUtils.canAuthorize(name)) {
                        systemListNormal.add(normalEntity)
                    }
                    if (AuthorizationUserInfoUtils.canGetUserInfo(name)) {
                        systemListNormal.add(normalEntity)
                    }
                } else {
                    if (name != "Accountkit") {
                        lists.add(entity)
                    }
                    if (AuthorizationUserInfoUtils.canAuthorize(name)) {
                        normalList.add(normalEntity)
                    }
                    if (AuthorizationUserInfoUtils.canGetUserInfo(name)) {
                        normalListUserInfo.add(normalEntity)
                    }
                }
            }
        }
    }
    companion object {
        val instance: PlatformMananger by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PlatformMananger() }
    }


    fun getList(): List<ShareListItemInEntity?>? {
        return lists
    }

    fun setList(list: MutableList<ShareListItemInEntity?>) {
        lists = list
    }

    fun getNormalList(): List<PlatformEntity?>? {
        return normalList
    }

    fun setNormalList(normalList: MutableList<PlatformEntity?>) {
        this.normalList = normalList
    }

    fun getChinaList(): List<ShareListItemInEntity?>? {
        return chinaList
    }

    fun setChinaList(chinaList: MutableList<ShareListItemInEntity?>) {
        this.chinaList = chinaList
    }

    fun getSystemList(): List<ShareListItemInEntity?>? {
        return systemList
    }

    fun setSystemList(systemList: MutableList<ShareListItemInEntity?>) {
        this.systemList = systemList
    }

    fun getChinaListNormal(): List<PlatformEntity?>? {
        return chinaListNormal
    }

    fun setChinaListNormal(chinaListNormal: MutableList<PlatformEntity?>) {
        this.chinaListNormal = chinaListNormal
    }

    fun getSystemListNormal(): List<PlatformEntity?>? {
        return systemListNormal
    }

    fun setSystemListNormal(systemListNormal: MutableList<PlatformEntity?>) {
        this.systemListNormal = systemListNormal
    }

    fun getChinaListNormalUserInfo(): List<PlatformEntity?>? {
        return chinaListNormalUserInfo
    }

    fun setChinaListNormalUserInfo(chinaListNormalUserInfo: MutableList<PlatformEntity?>) {
        this.chinaListNormalUserInfo = chinaListNormalUserInfo
    }

    fun getNormalListUserInfo(): List<PlatformEntity?>? {
        return normalListUserInfo
    }

    fun setNormalListUserInfo(normalListUserInfo: MutableList<PlatformEntity?>) {
        this.normalListUserInfo = normalListUserInfo
    }
}
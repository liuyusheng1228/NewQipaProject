package com.qipa.newboxproject.app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import cat.ereza.customactivityoncrash.activity.DefaultErrorActivity
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadSir
import com.mob.MobSDK
import com.tencent.mmkv.MMKV
import com.qipa.newboxproject.app.event.AppViewModel
import com.qipa.newboxproject.app.event.EventViewModel
import com.qipa.newboxproject.app.ext.getProcessName
import com.qipa.newboxproject.app.weight.loadCallBack.EmptyCallback
import com.qipa.newboxproject.app.weight.loadCallBack.ErrorCallback
import com.qipa.newboxproject.app.weight.loadCallBack.LoadingCallback
import com.qipa.newboxproject.ui.activity.ErrorActivity
import com.qipa.newboxproject.ui.activity.WelcomeActivity
import com.qipa.jetpackmvvm.ext.util.jetpackMvvmLog
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.external.ExternalAdaptInfo
import com.qipa.jetpackmvvm.base.BaseApp
import com.github.gzuliyujiang.dialog.DialogColor

import com.github.gzuliyujiang.dialog.DialogConfig

import com.github.gzuliyujiang.dialog.DialogStyle

import com.github.gzuliyujiang.dialog.DialogLog
import com.google.auto.service.AutoService
import com.qipa.newboxproject.app.util.LangUtils
import com.qipa.newboxproject.app.util.PreferenceManager
import com.qipa.newboxproject.ui.fragment.chat.groupinfo.GroupInfoActivity
import com.qipa.qipaimbase.BuildConfig
import com.qipa.qipaimbase.ImBaseBridge
import com.qipa.qipaimbase.chat.ChatData
import com.qipa.qipaimbase.utils.ToastUtils
import com.qipa.qipaimbase.utils.http.HttpUtils
import com.qipa.qipaimbase.utils.http.jsons.JsonContactRecent
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import skin.support.app.SkinCardViewInflater

import skin.support.constraint.app.SkinConstraintViewInflater

import skin.support.design.app.SkinMaterialViewInflater

import skin.support.app.SkinAppCompatViewInflater

import skin.support.SkinCompatManager
import com.gyf.immersionbar.OnBarListener

import com.gyf.immersionbar.OnKeyboardListener

import com.gyf.immersionbar.BarHide

import com.gyf.immersionbar.ImmersionBar
import com.qipa.jetpackmvvm.base.ApplicationLifecycle
import com.qipa.newboxproject.R


/**
 * 描述　:
 */

//Application全局的ViewModel，里面存放了一些账户信息，基本配置信息等
val appViewModel: AppViewModel by lazy { App.appViewModelInstance }

//Application全局的ViewModel，用于发送全局通知操作
val eventViewModel: EventViewModel by lazy { App.eventViewModelInstance }

class App : BaseApp() {

    companion object {
        lateinit var instance: App
        lateinit var eventViewModelInstance: EventViewModel
        lateinit var appViewModelInstance: AppViewModel
        private const val APP_ID = "280f8ef2cec41cde3bed705236ab9bc4"

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private lateinit var mContext: Context

        fun getContext() : Context{
            return mContext
        }


    }






    private fun getListener(): ImBaseBridge.BusinessListener? {
        return object : ImBaseBridge.BusinessListener {
//            private var iUserInfoModel: IUserInfoModel? = null
           override fun onRelayClick(activity: Activity?, chatData: ChatData?) {
//                ForwardActivity.startActivity(activity, chatData)
            }

            override fun getUserIcon(userId: String?, onGetUserIconListener: ImBaseBridge.OnGetUserIconListener?) {
//                if (iUserInfoModel == null) {
//                    iUserInfoModel = UserInfoModel()
//                }
//                iUserInfoModel.getUserInfo(userId) { jsonOtherInfo ->
//                    if (jsonOtherInfo != null && jsonOtherInfo.success() && onGetUserIconListener != null) {
//                        onGetUserIconListener.onGetUserIcon(
//                            jsonOtherInfo.getData().getProfile().getAvatar(),
//                            jsonOtherInfo.getData().getProfile().getNickname()
//                        )
//                    }
//                }
            }

            override fun onAtListener(activity: Activity?, gid: String?) {
//                GroupMemberSelectActivity.start(activity, gid)
            }

            override fun onKickUser(activity: Activity?) {
//                PhotonPushManager.getInstance().unRegister();
                ImBaseBridge.instance?.logout()
                ToastUtils.showText(activity, "服务器强制下线")
//                nav()
//                val intent = Intent(activity, Ph::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                activity.startActivity(intent)
//                activity.finish()
            }

            override fun onGroupInfoClick(activity: Activity?, gId: String?) {
                activity?.let { GroupInfoActivity.startActivity(it, gId) }
            }

            override fun getOthersInfo(ids: Array<String?>?): JsonResult<JsonRequestResult> {
                return HttpUtils.getInstance().getOthersInfo(
                    ids, "LoginInfo.getInstance().getSessionId()",
                    "LoginInfo.getInstance().getUserId()"
                ) as JsonResult<JsonRequestResult>
            }

            override fun getGroupProfile(groupId: String?): JsonResult<JsonRequestResult> {
                return HttpUtils.getInstance().getGroupProfile(
                    "LoginInfo.getInstance().getSessionId()",
                    "LoginInfo.getInstance().getUserId()", groupId
                )
            }

            override val recentUser: JsonContactRecent?
                get() = HttpUtils.getInstance().getRecentUser(
                    "LoginInfo.getInstance().getSessionId()",
                    "LoginInfo.getInstance().getUserId()"
                ).get() as JsonContactRecent

            override fun setIgnoreStatus(remoteId: String?, igoreAlert: Boolean): JsonResult<JsonRequestResult> {
                return HttpUtils.getInstance().setIgnoreStatus(
                    remoteId, igoreAlert,
                   " LoginInfo.getInstance().getSessionId()", "LoginInfo.getInstance().getUserId()"
                )
            }

           override fun getIgnoreStatus(userId: String?): JsonResult<in JsonRequestResult> {
                return HttpUtils.getInstance().getIgnoreStatus(
                    "LoginInfo.getInstance().getSessionId()",
                    "LoginInfo.getInstance().getUserId()",
                    userId
                ) as JsonResult<in JsonRequestResult>
            }

           override fun sendVoiceFile(localFile: String?): JsonResult<JsonRequestResult> {
                return HttpUtils.getInstance().sendVoiceFile(
                    localFile,
                    "LoginInfo.getInstance().getSessionId()"," LoginInfo.getInstance().getUserId()"
                ) as JsonResult<JsonRequestResult>
            }

           override fun sendPic(localFile: String?): JsonResult<JsonRequestResult> {
                return HttpUtils.getInstance().sendPic(
                    localFile,
                   " LoginInfo.getInstance().getSessionId()", "LoginInfo.getInstance().getUserId()"
                ) as JsonResult<JsonRequestResult>
            }

            override val userId: String?
                get() = "LoginInfo.getInstance().getUserId()"
            override val tokenId: String?
                get() = "LoginInfo.getInstance().getToken()"
        }
    }

    private fun initHx() {
        // 初始化PreferenceManager
        PreferenceManager.init(mContext)
        // init hx sdk
        Log.i("DemoApplication", "application initHx"+ChatHelper.instance?.autoLogin!!)
        if (ChatHelper.instance?.autoLogin!!) {
            Log.i("DemoApplication", "application initHx")
            ChatHelper.instance?.init(mContext)
        }
    }

    private fun initWeheel(){
        DialogLog.enable()
        DialogConfig.setDialogStyle(DialogStyle.Default)
        DialogConfig.setDialogColor(
            DialogColor()
                .cancelTextColor(-0x666667)
                .okTextColor(-0xff6634)

        )
    }

    /**
     * 动态注册初始化推送
     */
    private fun initPush() {
//        XPush.debug(BuildConfig.DEBUG)
        //动态注册，根据平台名或者平台码动态注册推送客户端
//        XPush.init(this) { platformCode, platformName ->
//                platformCode == JPushClient.JPUSH_PLATFORM_CODE && platformName == JPushClient.JPUSH_PLATFORM_NAME
//        }
//        XPush.register()
    }

    fun initAutoSize(){
        AutoSize.initCompatMultiProcess(mContext)
        /**
         * 以下是 AndroidAutoSize 可以自定义的参数, {@link AutoSizeConfig} 的每个方法的注释都写的很详细
         * 使用前请一定记得跳进源码，查看方法的注释, 下面的注释只是简单描述!!!
         */
        AutoSizeConfig.getInstance()

            //是否让框架支持自定义 Fragment 的适配参数, 由于这个需求是比较少见的, 所以须要使用者手动开启
            //如果没有这个需求建议不开启，上面3个fragment自定义适配就需要打开这个参数
            .setCustomFragment(true)

        //是否屏蔽系统字体大小对 AndroidAutoSize 的影响, 如果为 true, App 内的字体的大小将不会跟随系统设置中字体大小的改变
        //如果为 false, 则会跟随系统设置中字体大小的改变, 默认为 false
//                .setExcludeFontScale(true)

        //是否打印 AutoSize 的内部日志, 默认为 true, 如果您不想 AutoSize 打印日志, 则请设置为 false
//                .setLog(false)

        //是否使用设备的实际尺寸做适配, 默认为 false, 如果设置为 false, 在以屏幕高度为基准进行适配时
        //AutoSize 会将屏幕总高度减去状态栏高度来做适配
        //设置为 true 则使用设备的实际屏幕高度, 不会减去状态栏高度
//                .setUseDeviceSize(true)

        //是否全局按照宽度进行等比例适配, 默认为 true, 如果设置为 false, AutoSize 会全局按照高度进行适配
//                .setBaseOnWidth(false)

        //设置屏幕适配逻辑策略类, 一般不用设置, 使用框架默认的就好
//                .setAutoAdaptStrategy(new AutoAdaptStrategy())
        ;
//        customAdaptForExternal();

    }

    fun customAdaptForExternal(){
        AutoSizeConfig.getInstance().getExternalAdaptManager()
            .addExternalAdaptInfoOfActivity(DefaultErrorActivity::class.java, ExternalAdaptInfo(true,400f))

            //加入的 Activity 将会放弃屏幕适配, 一般用于三方库的 Activity, 详情请看方法注释
            //如果不想放弃三方库页面的适配, 请用 addExternalAdaptInfoOfActivity 方法, 建议对三方库页面进行适配, 让自己的 App 更完美一点
//                .addCancelAdaptOfActivity(DefaultErrorActivity.class)

            //为指定的 Activity 提供自定义适配参数, AndroidAutoSize 将会按照提供的适配参数进行适配, 详情请看方法注释
            //一般用于三方库的 Activity, 因为三方库的设计图尺寸可能和项目自身的设计图尺寸不一致, 所以要想完美适配三方库的页面
            //就需要提供三方库的设计图尺寸, 以及适配的方向 (以宽为基准还是高为基准?)
            //三方库页面的设计图尺寸可能无法获知, 所以如果想让三方库的适配效果达到最好, 只有靠不断的尝试
            //由于 AndroidAutoSize 可以让布局在所有设备上都等比例缩放, 所以只要您在一个设备上测试出了一个最完美的设计图尺寸
            //那这个三方库页面在其他设备上也会呈现出同样的适配效果, 等比例缩放, 所以也就完成了三方库页面的屏幕适配
            //即使在不改三方库源码的情况下也可以完美适配三方库的页面, 这就是 AndroidAutoSize 的优势
            //但前提是三方库页面的布局使用的是 dp 和 sp, 如果布局全部使用的 px, 那 AndroidAutoSize 也将无能为力
            //经过测试 DefaultErrorActivity 的设计图宽度在 380dp - 400dp 显示效果都是比较舒服的
    }

    override fun attachBaseContext(context: Context) {
        instance = this
        mContext = context!!
        AppManager.doAttachBaseContext(context)
        val context = LangUtils.getConfigurationContext(context!!)
        super.attachBaseContext(context)
    }



    override fun onCreate() {
        super.onCreate()
        AppManager.doOnCreate(mContext)
        MMKV.initialize(mContext.filesDir.absolutePath + "/mmkv")
        instance = this
        initAutoSize()
        MultiDex.install(mContext)
        MobSDK.init(mContext)

        SkinCompatManager.withoutActivity(application)

            .addInflater(SkinAppCompatViewInflater()) // 基础控件换肤初始化
            .addInflater(SkinMaterialViewInflater()) // material design 控件换肤初始化[可选]
            .addInflater(SkinConstraintViewInflater()) // ConstraintLayout 控件换肤初始化[可选]
            .addInflater(SkinCardViewInflater()) // CardView v7 控件换肤初始化[可选]
            .setSkinStatusBarColorEnable(false) // 关闭状态栏换肤，默认打开[可选]
            .setSkinWindowBackgroundEnable(false) // 关闭windowBackground换肤，默认打开[可选]
            .loadSkin()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        //界面加载管理 初始化
        LoadSir.beginBuilder()
            .addCallback(LoadingCallback())//加载
            .addCallback(ErrorCallback())//错误
            .addCallback(EmptyCallback())//空
            .setDefaultCallback(SuccessCallback::class.java)//设置默认加载状态页
            .commit()
        //初始化Bugly
        val context = mContext
        // 获取当前包名
        val packageName = context.packageName
        // 获取当前进程名
        val processName = getProcessName(android.os.Process.myPid())
        // 设置是否为上报进程
//        val strategy = CrashReport.UserStrategy(context)
//        strategy.isUploadProcess = processName == null || processName == packageName
        // 初始化Bugly
//        Bugly.init(context, if (BuildConfig.DEBUG) "xxx" else "a52f2b5ebb", BuildConfig.DEBUG)
//        "".logd()
//        jetpackMvvmLog = BuildConfig.DEBUG

        GlobalScope.launch(Dispatchers.Main) {
            initHx()
            initPush()
            initWeheel()
        }

        eventViewModelInstance = BaseApp.application.getAppViewModelProvider().get(EventViewModel::class.java)
        appViewModelInstance = AppViewModel()

        //防止项目崩溃，崩溃后打开错误界面
        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
            .enabled(true)//是否启用CustomActivityOnCrash崩溃拦截机制 必须启用！不然集成这个库干啥？？？
            .showErrorDetails(false) //是否必须显示包含错误详细信息的按钮 default: true
            .showRestartButton(false) //是否必须显示“重新启动应用程序”按钮或“关闭应用程序”按钮default: true
            .logErrorOnRestart(false) //是否必须重新堆栈堆栈跟踪 default: true
            .trackActivities(true) //是否必须跟踪用户访问的活动及其生命周期调用 default: false
            .minTimeBetweenCrashesMs(2000) //应用程序崩溃之间必须经过的时间 default: 3000
            .restartActivity(WelcomeActivity::class.java) // 重启的activity
            .errorActivity(ErrorActivity::class.java) //发生错误跳转的activity
            .apply()


        val builder: ImBaseBridge.Builder = ImBaseBridge.Builder()
            .application(application)
            .appId(APP_ID)
            .addListener(getListener())

        ImBaseBridge.instance?.init(builder)
    }




}

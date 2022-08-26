
/**
 * 项目依赖版本统一管理
 *
 * @author zwb
 * @since 2/27/22
 */


/**
 * AndroidX相关依赖
 *
 * @author zwb
 * @since 2/27/22
 */
object AndroidX {
    const val AndroidJUnitRunner = "androidx.test.runner.AndroidJUnitRunner"
    const val AppCompat = "androidx.appcompat:appcompat:1.4.0"
    const val CoreKtx = "androidx.core:core-ktx:1.7.0"
    const val ConstraintLayout = "androidx.constraintlayout:constraintlayout:2.1.2"
    const val ActivityKtx = "androidx.activity:activity-ktx:1.1.0"
    const val FragmentKtx = "androidx.fragment:fragment-ktx:1.3.1"
    const val Room = "androidx.room:room-runtime:2.4.0"
    const val RoomKtx = "androidx.room:room-ktx:2.3.0"
    const val RoomCompiler = "androidx.room:room-compiler:2.4.0"
    const val MultiDex = "androidx.multidex:multidex:2.0.1"
    const val Viewpager2 = "androidx.viewpager2:viewpager2:1.0.0"
    const val TestExtJunit = "androidx.test.ext:junit:1.1.2"
    const val TestEspresso = "androidx.test.espresso:espresso-core:3.3.0"
    const val PreferenceKtx = "androidx.preference:preference-ktx:1.1.1"
}

/**
 * Android相关依赖
 *
 * @author zwb
 * @since 2/27/22
 */
object Android {
    const val Junit = "junit:junit:4.13"
    const val Material = "com.google.android.material:material:1.4.0"
}

/**
 * JetPack相关依赖
 *
 * @author zwb
 * @since 2/27/22
 */
object JetPack {
    const val ViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1"
    const val LiveData = "androidx.lifecycle:lifecycle-livedata-ktx:2.3.1"
    const val Lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:2.3.1"
    const val LifecycleCommon = "androidx.lifecycle:lifecycle-common-java8:2.2.0"
    const val LifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
    const val LiveDataUnpeek =  "com.kunminx.archi:unpeek-livedata:4.4.1-beta1"
    const val ViewModelSavedState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:2.3.1"
    const val LifecycleCompilerAPT = "androidx.lifecycle:lifecycle-compiler:2.3.1"
    const val NavigationFragment = "androidx.navigation:navigation-fragment-ktx:2.3.5"
    const val NavigationUI = "androidx.navigation:navigation-ui-ktx:2.3.5"
}

/**
 * Kotlin相关依赖
 *
 * @author zwb
 * @since 2/27/22
 */
object Kotlin {
    const val Kotlin = "org.jetbrains.kotlin:kotlin-stdlib:1.5.30"
    const val CoroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.6"
    const val CoroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.6"
    const val KotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:1.3.50"
}

/**
 * GitHub及其他相关依赖
 *
 * @author zwb
 * @since 2/27/22
 */
object GitHub {
    const val OkHttp = "com.squareup.okhttp3:okhttp:4.9.0"
    const val Okio = "com.squareup.okio:okio:2.4.0"
    const val OkHttpInterceptorLogging = "com.squareup.okhttp3:logging-interceptor:4.9.1"
    const val Retrofit = "com.squareup.retrofit2:retrofit:2.9.0"
    const val RetrofitConverterGson = "com.squareup.retrofit2:converter-gson:2.9.0"
    const val PersistentCookieJar = "com.github.franmontiel:PersistentCookieJar:v1.0.1"
    const val RetrofitUrlManager = "me.jessyan:retrofit-url-manager:1.4.0"
    // 基础依赖包，必须要依赖
    const val Immersionbar = "com.geyifeng.immersionbar:immersionbar:3.2.2"
    // kotlin扩展（可选）
    const val ImmersionbarKtx = "com.geyifeng.immersionbar:immersionbar-ktx:3.2.2"

    const val AndroidAutoSize = "com.github.JessYanCoding:AndroidAutoSize:v1.2.1"
    // 腾讯 MMKV 替代SP
    const val MMKV = "com.tencent:mmkv:1.0.22"

    const val ARoute = "com.alibaba:arouter-api:1.5.1"
    const val ARouteCompiler = "com.alibaba:arouter-compiler:1.5.1"
    const val RecyclerViewAdapter = "com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.4"
    const val EventBus = "org.greenrobot:eventbus:3.2.0"
    const val EventBusAPT = "org.greenrobot:eventbus-annotation-processor:3.2.0"
    const val PermissionX = "com.permissionx.guolindev:permissionx:1.4.0"
    const val LeakCanary = "com.squareup.leakcanary:leakcanary-android:2.7"

    // 自动生成SPI暴露服务文件
    const val AutoService = "com.google.auto.service:auto-service:1.0"
    const val AutoServiceAnnotations = "com.google.auto.service:auto-service-annotations:1.0"


    // Koin AndroidX Scope feature
    const val KoinAndroidxWorkManager = "io.insert-koin:koin-androidx-workmanager:3.2.0"
    // Koin AndroidX ViewModel feature
    const val KoinAndroid = "io.insert-koin:koin-android:3.2.0"
    // Koin AndroidX Fragment Factory (unstable version)
    const val KoinAndroidxNavigation = "io.insert-koin:koin-androidx-navigation:3.2.0"
    const val KoinAndroidxCompat = "io.insert-koin:koin-android-compat:3.2.0"

    const val Banner = "com.youth.banner:banner:2.1.0"
    const val SmartRefreshLayout = "com.scwang.smartrefresh:SmartRefreshLayout:1.1.0"
    const val MagicIndicator = "com.github.hackware1993:MagicIndicator:1.7.0"
    const val Glide = "com.github.bumptech.glide:glide:4.12.0"
    const val GlideCompiler = "com.github.bumptech.glide:compiler:4.12.0"
    const val GSYVideoPlayer = "com.shuyu:GSYVideoPlayer:7.1.5"
    const val RoundedImageView = "com.rishabhharit.roundedimageview:RoundedImageView:0.8.4"
    const val Loadsir = "com.kingja.loadsir:loadsir:1.3.8"
    const val SuperTextView = "com.github.lygttpod:SuperTextView:2.4.6"

    //微信支付
    const val WechatSdkAndroid = "com.tencent.mm.opensdk:wechat-sdk-android:+"

    //google支付
    const val BillingKtx = "com.android.billingclient:billing-ktx:4.0.0"
    const val UtilCodex  = "com.blankj:utilcodex:1.30.6"
    //加载动画
    const val Lottie =  "com.airbnb.android:lottie:5.0.3"

    const val Transformations = "jp.wasabeef:glide-transformations:4.3.0"

    const val Core = "me.laoyuyu.aria:core:3.8.16"
    const val VideoLayout = "com.github.AsynctaskCoffee:VideoLayout:1.3"//视频背景控件
    //轮播图
    const val BannerViewPager = "com.github.zhpanvip:BannerViewPager:3.1.5"

    const val Annotation = "androidx.annotation:annotation:1.2.0"

    const val DownloadInstaller = "io.github.anylifezlb:downloadInstaller:2.0.0"//app
    const val Numberprogressbar = "com.daimajia.numberprogressbar:library:1.4@aar"
    //RevealLayout
    const val GoweiRevealLayout = "com.github.goweii:RevealLayout:1.1.1"
    //第三方recyclerview
    const val Recyclerview = "com.yanzhenjie.recyclerview:x:1.3.2"
    //防崩溃
    const val CustomActivityOncrash = "cat.ereza:customactivityoncrash:2.3.0"
    const val Multidex = "androidx.multidex:multidex:2.0.1"
    //底部bottomBar
    const val BottomNavigationViewEx = "com.github.ittianyu:BottomNavigationViewEx:2.0.4"
    const val Cardview = "androidx.cardview:cardview:1.0.0"
    const val Swiperefreshlayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"


    //dialog
    const val Dialogs_Lifecycle = "com.afollestad.material-dialogs:lifecycle:3.3.0"
    const val DialogsCore = "com.afollestad.material-dialogs:core:3.3.0"
    const val DialogsColor = "com.afollestad.material-dialogs:color:3.3.0"
    const val DialogsDatetime = "com.afollestad.material-dialogs:datetime:3.3.0"
    const val DialogsBottomsheets = "com.afollestad.material-dialogs:bottomsheets:3.3.0"
    const val SplittiesCollections = "com.louiscad.splitties:splitties-collections:2.1.1"
    //官方的
    const val FlexBox = "com.google.android:flexbox:2.0.1"
    //吸顶效果
    const val GroupedRecyclerViewAdapter = "com.github.donkingliang:GroupedRecyclerViewAdapter:2.3.0"
    const val Realtimeblurview = "com.github.mmin18:realtimeblurview:1.2.1"
    //气泡弹窗
    const val PopMenu = "me.kareluo.ui:popmenu:1.1.0"
    const val Rvadapter = "com.zhy:base-rvadapter:3.0.3"
    const val DynamicPublishing = "com.github.Giftedcat:DynamicPublishing:1.0.0"
    //圆角圆形图片控件 主要是可以带边框
    const val Roundedimageview = "com.makeramen:roundedimageview:2.3.0"
    //推送核心库
    const val Jpush = "cn.jiguang.sdk:jpush:4.5.0" // 此处以JPush 4.5.0 版本为例。
    const val Jcore = "cn.jiguang.sdk:jcore:3.1.2"  // 此处以JCore 3.1.2 版本为例。
    //日期选择
    const val WheelPicker = "com.github.gzu-liyujiang.AndroidPicker:WheelPicker:4.1.6"
    //im聊天功能
    const val EaseImKit = "io.hyphenate:ease-im-kit:3.8.5"

    const val HyphenateChat = "io.hyphenate:hyphenate-chat:3.8.5"
    const val RoomRuntime = "androidx.room:room-runtime:2.2.5"
    const val ParseAndroid = "com.parse:parse-android:1.13.1"

    const val Gson = "com.google.code.gson:gson:2.8.6"

    const val Protobuf = "com.google.protobuf:protobuf-lite:3.0.1"
    //换肤功能
    const val SkinSupport =  "skin.support:skin-support:4.0.5"                   // skin-support
    const val SkinSupportAppcompat = "skin.support:skin-support-appcompat:4.0.5"         // skin-support 基础控件支持
    const val SkinSupportDesign = "skin.support:skin-support-design:4.0.5"          // skin-support-design material design 控件支持[可选]
    const val SkinSupportCardview = "skin.support:skin-support-cardview:4.0.5"          // skin-support-cardview CardView 控件支持[可选]
    const val SkinSupportConstraintLayout = "skin.support:skin-support-constraint-layout:4.0.5" // skin-support-constraint-layout ConstraintLayout 控件支持[可选]


}

/**
 * SDK相关依赖
 *
 * @author zwb
 * @since 2/27/22
 */
object SDK {
    // 腾讯Bugly 异常上报
    const val TencentBugly = "com.tencent.bugly:crashreport:3.3.9"

    // Bugly native异常上报
    const val TencentBuglyNative = "com.tencent.bugly:nativecrashreport:3.8.0"

    // 腾讯X5WebView
    const val TencentTBSX5 = "com.tencent.tbs.tbssdk:sdk:43939"
}

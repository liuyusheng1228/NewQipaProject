#----------------RxJava&RxAndroid----------------
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-keepattributes *Annotation*
-keep class **.*_SnakeProxy
-keep @com.youngfeng.snake.annotations.EnableDragToClose public class *

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}


#----------------okhttp3-------------------
-dontwarn javax.annotation.**
# 资源加载了相对路径，因此必须保留此类的包。
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# Animal Sniffer compileOnly依赖项，以确保API与旧版本的Java兼容
-dontwarn org.codehaus.mojo.animal_sniffer.*
# OkHttp平台仅在JVM上使用，并且当Conscrypt依赖性可用时。
-dontwarn okhttp3.internal.platform.ConscryptPlatform



# ---------------ButterKnife--------------
# 保留生成Unbinder的生成类
-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }
# 防止使用ButterKnife注释的类型的混淆，因为简单的名称用于反射地查找生成的ViewBinding
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
 -keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}



# -------------------Glide--------------------------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}



# ------------------retrofit2--------------------------------
# Retrofit会对方法和参数注释进行反射。
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
# 优化时保留服务方法参数。
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
# 忽略用于构建工具的注释。
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
# 忽略JSR 305注释以嵌入可为空性信息。
-dontwarn javax.annotation.**
# 由NoClassDefFoundError守护 try / catch，仅在类路径上使用。
-dontwarn kotlin.Unit
# 顶级功能，只能由Kotlin使用。
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions



# ------------------jetpack--------------------------------
# Lifecycle
# LifecycleObserver的空构造函数被proguard认为是未使用的
-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}
# 保留生命周期状态和事件枚举值
-keepclassmembers class android.arch.lifecycle.Lifecycle$State { *; }
-keepclassmembers class android.arch.lifecycle.Lifecycle$Event { *; }
-keepclassmembers class * {
    @android.arch.lifecycle.OnLifecycleEvent *;
}
-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}

-keep class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}

# ViewModel
# ViewModel的空构造函数被proguard视为未使用
-keepclassmembers class * extends android.arch.lifecycle.ViewModel {
    <init>(...);
}

-keepclassmembers class android.arch.** { *; }
-keep class android.arch.** { *; }
-dontwarn android.arch.**

# paging
-dontwarn android.arch.paging.DataSource

# Room 数据库
-dontwarn android.arch.persistence.room.paging.LimitOffsetDataSource

# databinding
-dontwarn android.databinding.**
-keep class android.databinding.** { *; }
-dontwarn com.android.databinding.**
-keep class com.android.databinding.** { *; }



# ---------------------Gson-------------------------
# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}


# --------------------tencent-bugly-------------------------
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}



# --------------------sharesdk-------------------------
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}

-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class com.mob.**{*;}
-keep class com.bytedance.**{*;}
-dontwarn cn.sharesdk.**
-dontwarn com.sina.**
-dontwarn com.mob.**

-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.sdk.app.H5PayCallback {
<fields>;
<methods>;
}
-keep class com.alipay.android.phone.mrpc.core.** { *; }
-keep class com.alipay.apmobilesecuritysdk.** { *; }
-keep class com.alipay.mobile.framework.service.annotation.** { *; }
-keep class com.alipay.mobilesecuritysdk.face.** { *; }
-keep class com.alipay.tscenter.biz.rpc.** { *; }
-keep class org.json.alipay.** { *; }
-keep class com.alipay.tscenter.** { *; }
-keep class com.ta.utdid2.** { *;}
-keep class com.ut.device.** { *;}

-dontoptimize
-dontpreverify

-keep class com.hyphenate.** {*;}
-dontwarn  com.hyphenate.**
#3.6.8版本之后移除apache，无需再添加
-keep class internal.org.apache.http.entity.** {*;}

# --------------------zxing-------------------------
-keep class com.google.zxing.** {*;}
-dontwarn com.google.zxing.**
-keep class com.mining.app.zxing.camera.** {*;}
-dontwarn com.mining.app.zxing.camera.**
-keep class com.staryea.** {*;}
-dontwarn com.staryea.**



# -------------------百度地图-------------------------
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-keep class mapsdkvi.com.** {*;}
-dontwarn com.baidu.**



# -------------------友盟-------------------------
-dontwarn com.umeng.**
-keepattributes SourceFile,LineNumberTable
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**
-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
-keep public class android.webkit.**
-keep class com.umeng.scrshot.**
-keep class com.umeng.socialize.sensor.**
-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class com.umeng.fb.ui.**
-keep public class * extends com.umeng.**


# -------------------腾讯全家桶-------------------------
-keep public interface com.tencent.**
-keep public class com.tencent.** {*;}
-dontwarn com.tencent.**


# -------------------eventbus 3.0-------------------------
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# 极光推送混淆
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }
-keep class * extends cn.jpush.android.service.JPushMessageReceiver{*;}



# -------------------picasso-------------------------
-keep class com.squareup.picasso.** {*; }
-dontwarn com.squareup.picasso.**


# -------------------volley-------------------------
-keep class com.android.volley.** {*;}
-keep class com.android.volley.toolbox.** {*;}
-keep class com.android.volley.Response$* { *; }
-keep class com.android.volley.Request$* { *; }
-keep class com.android.volley.RequestQueue$* { *; }
-keep class com.android.volley.toolbox.HurlStack$* { *; }
-keep class com.android.volley.toolbox.ImageLoader$* { *; }



# -------------------alibaba-------------------------
-dontwarn com.alibaba.**
-keep class com.alibaba.** { *; }


# -------------------avatarimageview-------------------------
-dontwarn cn.carbs.android.avatarimageview.library.**
-keep class cn.carbs.android.avatarimageview.library.** { *; }


# -------------------bingoogolapple-------------------------
-dontwarn cn.bingoogolapple.baseadapter.**
-keep class cn.bingoogolapple.baseadapter.** { *; }



# -------------------olive-------------------------
-dontwarn com.oliveapp.**
-keep class com.oliveapp.** { *; }


# -------------------lottie-------------------------
-keep class com.airbnb.lottie.** { *; }

# -------------------RxPermissions-------------------------
-keep class com.tbruyelle.rxpermissions2.** { *; }
-keep interface com.tbruyelle.rxpermissions2.** { *; }

# -------------------aria-------------------------
-dontwarn com.arialyy.aria.**
-keep class com.arialyy.aria.**{*;}
-keep class **$$DownloadListenerProxy{ *; }
-keep class **$$UploadListenerProxy{ *; }
-keep class **$$DownloadGroupListenerProxy{ *; }
-keep class **$$DGSubListenerProxy{ *; }
-keepclasseswithmembernames class * {
    @Download.* <methods>;
    @Upload.* <methods>;
    @DownloadGroup.* <methods>;
}


-dontwarn xcoding.commons.**
-keep class xcoding.commons.** { *; }


-dontwarn net.**
-keep class net.** { *; }

-keep class com.github.** {*;}
-dontwarn com.github.**


-dontwarn junit.**
-keep class junit.** { *; }


-dontwarn jp.wasabeef.glide.transformations.**
-keep class jp.wasabeef.glide.transformations.** { *; }


-dontwarn javax.inject.**
-keep class javax.inject.** { *; }


-dontwarn io.reactivex.**
-keep class io.reactivex.** { *; }


-dontwarn com.yanzhenjie.loading.**
-keep class com.yanzhenjie.loading.** { *; }


-dontwarn com.ximpleware.**
-keep class com.ximpleware.** { *; }
-dontwarn java_cup.**
-keep class java_cup.** { *; }


-dontwarn com.thoughtworks.xstream.**
-keep class com.thoughtworks.xstream.** { *; }


-dontwarn om.tbruyelle.rxpermissions.**
-keep class om.tbruyelle.rxpermissions.** { *; }


-dontwarn com.squareup.**
-keep class com.squareup.** { *; }


-keep class okio.** { *; }
-dontwarn okio.**


-dontwarn com.scwang.smartrefresh.layout.**
-keep class com.scwang.smartrefresh.layout.** { *; }


-dontwarn com.nineoldandroids.**
-keep class com.nineoldandroids.** { *; }



-dontwarn com.lcodecore.tkrefreshlayout.**
-keep class com.lcodecore.tkrefreshlayout.** { *; }


-dontwarn com.journeyapps.barcodescanner.**
-keep class com.journeyapps.barcodescanner.** { *; }



-dontwarn com.joooonho.**
-keep class com.joooonho.** { *; }


-dontwarn com.ibm.icu.**
-keep class com.ibm.icu.** { *; }


-dontwarn com.github.jaiimageio.**
-keep class com.github.jaiimageio.** { *; }



-dontwarn com.flyco.tablayout.**
-keep class com.flyco.tablayout.** { *; }


-dontwarn com.contrarywind.**
-keep class com.contrarywind.** { *; }


-dontwarn com.hp.hpl.sparta.**
-keep class com.hp.hpl.sparta.** { *; }
-dontwarn net.sourceforge.pinyin4j.**
-keep class net.sourceforge.pinyin4j.** { *; }


-dontwarn com.fasterxml.jackson.**
-keep class com.fasterxml.jackson.** { *; }



-dontwarn com.beust.jcommander.**
-keep class com.beust.jcommander.** { *; }



-dontwarn com.almworks.sqlite4java.**
-keep class com.almworks.sqlite4java.** { *; }


-dontwarn javolution.util.stripped.**
-keep class javolution.util.stripped.** { *; }



-dontwarn edu.emory.mathcs.backport.java.util.**
-keep class edu.emory.mathcs.backport.java.util.** { *; }

-dontwarn com.gzt.faceid5sdk.**
-keep class com.gzt.faceid5sdk.** { *; }


-keep class com.google.** {*;}
-dontwarn com.google.**
-keep class com.googlecode.** {*;}
-dontwarn com.googlecode.**

-keep class javazoom.jl.** {*;}
-dontwarn javazoom.jl.**

-keep class me.iwf.** {*;}
-dontwarn me.iwf.**

-keep class net.sf.** {*;}
-dontwarn net.sf.**


-keep class xcoding.commons.** {*;}
-dontwarn xcoding.commons.**

-keep class a.** {*;}
-dontwarn a.**

-keep class b.** {*;}
-dontwarn b.**


-keep class com.a.a** {*;}
-dontwarn com.a.a**


-keep class com.fasterxml.** {*;}
-dontwarn com.fasterxml.**

-keep class com.staryea.** {*;}
-dontwarn com.staryea.**

-keep class rx.internal.** {*;}
-dontwarn rx.internal.**

-keep class com.j256.** {*;}
-dontwarn com.j256.**


-dontwarn com.kingja.loadsir.**
-keep class com.kingja.loadsir.** {*;}

-keep class com.just.agentweb.** {
    *;
}

-dontwarn com.just.agentweb.**


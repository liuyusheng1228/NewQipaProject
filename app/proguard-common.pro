-optimizationpasses 5 # 指定代码的优化级别
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*,!code/allocation/variable  #混淆时所采用的算法
-dontshrink #默认压缩，可能有问题
-dontoptimize #关闭优化功能，这样一来，所谓的-optimizations和-optimizationpasses就没有意义

-ignorewarnings # 忽略警告
-dontpreverify # 关闭预校验功能
-dontusemixedcaseclassnames #是否使用大小写混合
-dontskipnonpubliclibraryclasses #不跳过jars中的非public classes

-verbose    # 混淆时是否记录日志，gradle build时在本项目根目录输出
-dump proguard/class_files.txt #输出apk包内所有class的内部结构
-printseeds proguard/seeds.txt #输出未混淆的类和成员
-printusage proguard/unused.txt #输出apk中删除的代码
-printmapping proguard/mapping.txt #输出混淆前后的映射


-keepattributes Exceptions # 解决AGPBI警告
-keepattributes Exceptions,InnerClasses,...
-keepattributes EnclosingMethod
-keepattributes SourceFile,LineNumberTable #运行抛出异常时保留代码行号

#保留泛型、内部类、封闭方法，为了反射正常运行
-keepattributes Signature,InnerClasses,EnclosingMethod

#保留注解
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation { *; }
-keep interface * extends java.lang.annotation.Annotation { *; }

#保留继承自activity,application,service,broadcastReceiver,contentprovider
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider


#保留android.support
-dontwarn android.support.**
-keep class android.support.** { *; }
-keep interface android.support.** { *; }

-keep class com.google.android.material.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**

#保留androidx包
-dontwarn androidx.**
-keep class androidx.** {*;}
-keep interface androidx.** {*;}
-keep public class * extends androidx.**

#保留Android的一些类，官网上的
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.support.multidex.MultiDexApplication
-keep public class * extends android.view.View
-keep class org.** {*;}
-keep interface org.** {*;}
-dontwarn org.**
-keep class com.android.**{*;}

#保留R文件下面的资源
-keep class **.R$* {*;}

#保留继承自 View 的 setXx() 和 getXx() 方法，因为属性动画会用到相应的 setter 和 getter
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

#保留自定义控件
-keep public class * extends android.view.View {
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保留Activity中参数是View的方法，因为在XML中配置android:onClick="buttonClick"属性时，点击该按钮时就会调用Activity中的buttonClick(View view)方法
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#保留带有回调函数的
-keepclassmembers class * {
    void **(**Event);
    void **(**Listener);
}


#保留R类里及其所有内部static类中的所有static变量字段，$是用来分割内嵌类与其母体的标志
-keep public class **.R$*{
   public static final int *;
}
-keepclassmembers class **.R$* {
	public static <fields>;
}

#与JS交互
-keepattributes *JavascriptInterface*
#WebView
-keep class **.Webview2JsInterface { *; }
-keepclassmembers class * extends android.webkit.WebViewClient {
     public void *(android.webkit.WebView,java.lang.String,android.graphics.Bitmap);
     public boolean *(android.webkit.WebView,java.lang.String);
     public void *(android.webkit.webView, jav.lang.String);
}
-keepclassmembers class * extends android.webkit.WebChromeClient {
     public void *(android.webkit.WebView,java.lang.String);
}


#保留枚举enum类的values和valueOf
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#保留native方法JNI）
-keepclasseswithmembernames class * {
    native <methods>;
}

#保留Parcelable
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#需要序列化和反序列化的类不能被混淆（注：Java反射用到的类也不能被混淆）
-keep public class * implements java.io.Serializable {
   public *;
}

#保留Serializable接口的子类中指定的某些成员变量和方法
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}


-keep class **.*bean*.** {*;}
-keep class **.*Bean*.** {*;}
-keep class **.*model*.** {*;}
-keep class **.*Model*.** {*;}


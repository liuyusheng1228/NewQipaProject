
object BuildConfig {
    const val compileSdkVersion = 31
    const val buildToolsVersion = "30.0.1"
    const val applicationId = "com.qipa.newboxproject"
    const val minSdkVersion = 21
    const val targetSdkVersion = 31
    const val versionCode = 1
    const val versionName = "1.0"

    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    const val isAppMode = false

    /**
     * 项目当前的版本状态
     * 该状态直接反映当前App是测试版 还是正式版 或者预览版
     * 正式版:RELEASE、预览版(α)-内部测试版:ALPHA、测试版(β)-公开测试版:BETA
     */
//    object Version {

    const val VERSION_RELEASE = "VERSION_STATUS_RELEASE"

    const val VERSION_ALPHA = "VERSION_STATUS_ALPHA"

    const val VERSION_BETA = "VERSION_STATUS_BETA"
//    }
}
package com.qipa.jetpackmvvm.login

import android.app.Activity
import android.util.Log

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class ThirdLoginManager {
    private var isloginType = 5
    private var mThirdLoginCallBackImpl : ThirdLoginCallBackImpl? = null
    companion object {
        val INSTANCE: ThirdLoginManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ThirdLoginManager()
        }
    }

     fun loginThirdMethod(platName : String,mActivity : Activity){
        when(platName){
            "Facebook" -> isloginType = 4
            "Google" -> isloginType = 3
            "KakaoTalk" -> isloginType = 6
            "QQ" -> isloginType = 2
            "微信好友" -> {
                isloginType = 1
            }
        }
//        val plat: Platform = ShareSDK.getPlatform(platName)
//        if (plat.isAuthValid()) {
//            plat.removeAccount(true)
//            return
//        }
//        ShareSDK.setActivity(mActivity) //抖音登录适配安卓9.0
//        //回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
//        plat.setPlatformActionListener(object : PlatformActionListener {
//            override fun onError(arg0: Platform?, arg1: Int, arg2: Throwable) {
//                // TODO Auto-generated method stub
//                arg2.printStackTrace()
//                Log.i("Api",""+ arg2.printStackTrace())
//                mThirdLoginCallBackImpl?.onError(arg2)
//            }
//
//            override fun onComplete(arg0: Platform, arg1: Int, arg2: HashMap<String?, Any?>?) {
//                // TODO Auto-generated method stub
//                //输出所有授权信息
//                // 3. 在onActivityResult中响应登录
//                var  logUploadMessageBean = UploadMessageBean()
//                logUploadMessageBean = Gson().fromJson(""+ arg0.getDb().exportData(),
//                    object : TypeToken<UploadMessageBean>() {}.getType())
//                logUploadMessageBean.type = isloginType
//                logUploadMessageBean.access_token = arg0.getDb().token
//                mThirdLoginCallBackImpl?.onComplete(logUploadMessageBean)
//            }
//
//            override fun onCancel(arg0: Platform?, arg1: Int) {
//                // TODO Auto-generated method stub
//                Log.i("Api",""+ arg1)
//                mThirdLoginCallBackImpl?.onCancel(arg1)
//            }
//        })
//        //执行登录，登录后在回调里面获取用户资料
//        plat.authorize()
    }

    fun setThirdLoginCallBackImpl(thirdLoginCallBackImpl : ThirdLoginCallBackImpl){
        mThirdLoginCallBackImpl = thirdLoginCallBackImpl
    }



    interface ThirdLoginCallBackImpl{
        fun onComplete(uploadMessageBean:  UploadMessageBean)
        fun onError(throwable: Throwable)
        fun onCancel(arg1: Int)
    }
}
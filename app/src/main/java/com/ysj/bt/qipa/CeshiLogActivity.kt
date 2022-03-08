package com.ysj.bt.qipa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.wechat.friends.Wechat
import com.qipa.jetpackmvvm.ext.parseState
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.util.Constants
import com.qipa.newboxproject.ui.activity.MainActivity
import com.qipa.newboxproject.viewmodel.request.RequestMessageViewModel
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory

class CeshiLogActivity : AppCompatActivity() {
    private lateinit var tv_loginb : Button
    private lateinit var up_load : Button
    private var req : SendAuth.Req? = null
    private var api: IWXAPI? = null
    private val requestMessageViewModel : RequestMessageViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ceshi_log)
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false)
        tv_loginb = findViewById(R.id.tv_loginb)
        up_load = findViewById(R.id.tv_upload)
        up_load.setOnClickListener {
//            2022-01-21 15:16:19.193 11979-25024/com.ysj.bt.qipa D/fan12: onResponse: {"access_token":"53_U8rQTbqH1I1cv1ExHWNT5dHa7iEPD0KWUHisFIl0TtZC7FREsqs0Xb0s3JXIv3YWiKhxJdgpdUTphqwtniJbdlRRHjLacxikTa7IauPG1Lk","expires_in":7200,"refresh_token":"53_Ih-q3fraxE8lL7pxtIS3HocbrHCQHnEod0AZzkjW4gMK86BSmXu2fkEl-PrqPq_I85pp7q9WkevfA6Ag6lMCx6IWxT3BQRvnBbwKOT3cH2c","openid":"oHOa205Rm159dbTgVs9mkwyZ21wM","scope":"snsapi_userinfo","unionid":"o7CSswY1O6ipMOvdkhop4bWKVbug"}
//
//            requestMessageViewModel.codeUser(
//                "53_U8rQTbqH1I1cv1ExHWNT5dHa7iEPD0KWUHisFIl0TtZC7FREsqs0Xb0s3JXIv3YWiKhxJdgpdUTphqwtniJbdlRRHjLacxikTa7IauPG1Lk", "oHOa205Rm159dbTgVs9mkwyZ21wM", "o7CSswY1O6ipMOvdkhop4bWKVbug"
//            )

        }

        requestMessageViewModel.run {
            wxUserInfoBean.observe(this@CeshiLogActivity,{resultState ->
//                parseState(resultState, { data ->
//                }
                if(resultState != null){
                    Log.i("Api","aa:"+resultState.nickname)
                }

            })

        }

        tv_loginb.setOnClickListener {
//            startActivity(Intent(this,MainActivity::class.java))
            Log.i("Api","点击")
            req = SendAuth.Req()
            req?.scope = "snsapi_userinfo";
            req?.state = "wechat_sdk_demo_test";
            api?.sendReq(req);
//            val plat: Platform = ShareSDK.getPlatform(Wechat.NAME)
//            ShareSDK.setActivity(this) //抖音登录适配安卓9.0
//            Log.i("Api","点击")
//            //回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
//            plat.setPlatformActionListener(object : PlatformActionListener {
//                override fun onError(arg0: Platform?, arg1: Int, arg2: Throwable) {
//                    // TODO Auto-generated method stub
//                    arg2.printStackTrace()
//                    Log.i("Api",""+ arg2.printStackTrace())
//                }
//
//                override fun onComplete(arg0: Platform, arg1: Int, arg2: HashMap<String?, Any?>?) {
//                    // TODO Auto-generated method stub
//                    //输出所有授权信息
//                    arg0.getDb().exportData()
//                    Log.i("Api",""+arg0.getDb().exportData())
//                }
//
//                override fun onCancel(arg0: Platform?, arg1: Int) {
//                    // TODO Auto-generated method stub
//                    Log.i("Api",""+ arg1)
//                }
//            })
//            //执行登录，登录后在回调里面获取用户资料
//            plat.authorize()
        }
    }
}
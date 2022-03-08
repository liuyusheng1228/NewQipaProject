package com.qipa.newboxproject.viewmodel.request

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qipa.jetpackmvvm.base.viewmodel.BaseViewModel
import com.qipa.newboxproject.app.network.apiService
import com.qipa.newboxproject.data.repository.request.HttpRequestCoroutine
import com.qipa.jetpackmvvm.ext.request
import com.qipa.jetpackmvvm.login.UploadMessageBean
import com.qipa.jetpackmvvm.network.utils.rsa.RSAUtils
import com.qipa.jetpackmvvm.state.ResultState
import com.qipa.newboxproject.app.upload.bean.UploadPicUserBean
import com.qipa.newboxproject.app.upload.bean.UserInfoBean
import com.qipa.newboxproject.app.upload.bean.UserInfoUploadBean
import com.qipa.newboxproject.data.model.WxUserInfoResponse
import com.qipa.newboxproject.data.model.bean.RealUserNameBean
import com.qipa.qipaimbase.utils.ToastUtils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * 描述　:登录注册的请求ViewModel
 */
class RequestLoginRegisterViewModel : BaseViewModel() {

    //方式1  自动脱壳过滤处理请求结果，判断结果是否成功
    var loginResult = MutableLiveData<ResultState<WxUserInfoResponse>>()

    //方式2  不用框架帮脱壳，判断结果是否成功
//    var loginResult2 = MutableLiveData<ResultState<ApiResponse<UserInfo>>>()

    var wxUserInfoBean : MutableLiveData<WxUserInfoResponse> = MutableLiveData()

    var bindResult : MutableLiveData<String> = MutableLiveData()
    var uploadPicResult : MutableLiveData<UploadPicUserBean> = MutableLiveData()
    var uploadUserResult : MutableLiveData<String> = MutableLiveData()
    var file : File? = null

    fun loginReq(username: String, password: String) {
        //1.这种是在 Activity/Fragment的监听回调中拿到已脱壳的数据（项目有基类的可以用）
        request(
            { apiService.login(username, password) }//请求体
            , loginResult,//请求的返回结果，请求成功与否都会改变该值，在Activity或fragment中监听回调结果，具体可看loginActivity中的回调
            true,//是否显示等待框，，默认false不显示 可以默认不传
            "正在登录中..."//等待框内容，可以默认不填请求网络中...
        )
        //2.这种是在Activity/Fragment中的监听拿到未脱壳的数据，你可以自己根据code做业务需求操作（项目没有基类的可以用）
        /*requestNoCheck({HttpRequestCoroutine.login(username,password)},loginResult2,true)*/

        //3. 这种是直接在当前ViewModel中就拿到了脱壳数据数据，做一层封装再给Activity/Fragment，如果 （项目有基类的可以用）
        /* request({HttpRequestCoroutine.login(username,password)},{
             //请求成功 已自动处理了 请求结果是否正常
         },{
             //请求失败 网络异常，或者请求结果码错误都会回调在这里
         })*/

        //4.这种是直接在当前ViewModel中就拿到了未脱壳数据数据，（项目没有基类的可以用）
        /*requestNoCheck({HttpRequestCoroutine.login(username,password)},{
            //请求成功 自己拿到数据做业务需求操作
            if(it.errorCode==0){
                //结果正确
            }else{
                //结果错误
            }
        },{
            //请求失败 网络异常回调在这里
        })*/
    }


    fun thirdlogin(logUploadMessageBean: UploadMessageBean) {
        var msg : String = RSAUtils.encrypt(Gson().toJson(logUploadMessageBean))
        request({ HttpRequestCoroutine.thirdlogin(msg) },{
            Log.i("Api",""+ Gson().fromJson(""+ RSAUtils.decrypt(it),
                object : TypeToken<WxUserInfoResponse>() {}.getType()))
            wxUserInfoBean.value = Gson().fromJson(""+ RSAUtils.decrypt(it),
                object : TypeToken<WxUserInfoResponse>() {}.getType())
        },{
          ToastUtils.showText("登陆失败")
        },true,
            "正在登陆中..." )
    }



    fun getUserInfo(userId: String) {
        var  logUploadMessageBean = UserInfoBean(userId)
        var msg : String = RSAUtils.encrypt(Gson().toJson(logUploadMessageBean))
        request({ HttpRequestCoroutine.getUserInfo(msg) },{
            Log.i("Api",""+ Gson().fromJson(""+ RSAUtils.decrypt(it),
                object : TypeToken<WxUserInfoResponse>() {}.getType()))
            wxUserInfoBean.value = Gson().fromJson(""+ RSAUtils.decrypt(it),
                object : TypeToken<WxUserInfoResponse>() {}.getType())
        },{
            ToastUtils.showText("获取失败")
        },true,
            "正在获取中..." )
    }


    fun userRealName(userId: String,phone: String,iDcard: String,realName: String,code: String) {
        var  realUserNameBean = RealUserNameBean(userId,phone,iDcard,realName,code)
        var msg : String = RSAUtils.encrypt(Gson().toJson(realUserNameBean))
        request({ HttpRequestCoroutine.userRealName(msg) },{
            Log.i("Api",""+ Gson().fromJson(""+ RSAUtils.decrypt(it),
                object : TypeToken<WxUserInfoResponse>() {}.getType()))
            uploadUserResult.value = it
        },{
            ToastUtils.showText("获取失败")
        },true,
            "正在获取中..." )
    }


    fun getCode(phoneCode: String) {
        var  logUploadMessageBean = UploadMessageBean()
        logUploadMessageBean.phone = phoneCode
        var msg : String = RSAUtils.encrypt(Gson().toJson(logUploadMessageBean))
        request({ HttpRequestCoroutine.getCode(msg) },{
            Log.i("Api",""+ Gson().fromJson(""+ RSAUtils.decrypt(it),
                object : TypeToken<String>() {}.getType()))
        } )
    }

    fun reUpLoadFile(pic : String) {
        file = File(pic)
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("image/png"), file)
        val mulbody: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file?.name, requestBody) //获取上传文件的请求体
        request({HttpRequestCoroutine.upload(mulbody)

        },{
            uploadPicResult.value = it
        })

    }


    fun updateUserInfo(userInfoUploadBean: UserInfoUploadBean) {

        var msg : String = RSAUtils.encrypt(Gson().toJson(userInfoUploadBean))
        request({HttpRequestCoroutine.updateUserInfo(msg)

        },{
            uploadUserResult.value = it
        })

    }

    fun registerAndlogin(username: String, password: String) {
        request(
            { HttpRequestCoroutine.register(username, password) }
            , loginResult,
            true,
            "正在注册中..."
        )
    }
}
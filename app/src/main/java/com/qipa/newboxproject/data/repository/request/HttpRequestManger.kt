package com.qipa.newboxproject.data.repository.request

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import com.qipa.newboxproject.app.network.apiService
import com.qipa.newboxproject.app.util.CacheUtil
import com.qipa.jetpackmvvm.network.AppException
import com.qipa.newboxproject.app.upload.bean.UploadPicUserBean
import com.qipa.newboxproject.data.model.WxUserInfoResponse
import com.qipa.newboxproject.data.model.bean.*
import okhttp3.MultipartBody

/**
 * 描述　: 处理协程的请求类
 */

val HttpRequestCoroutine: HttpRequestManger by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    HttpRequestManger()
}

class HttpRequestManger {
    /**
     * 获取首页文章数据
     */
    suspend fun getHomeData(pageNo: Int): ApiResponse<ApiPagerResponse<ArrayList<AriticleResponse>>> {
        //同时异步请求2个接口，请求完成后合并数据
        return withContext(Dispatchers.IO) {
            val listData = async { apiService.getAritrilList(pageNo) }
            //如果App配置打开了首页请求置顶文章，且是第一页
            if (CacheUtil.isNeedTop() && pageNo == 0) {
                val topData = async { apiService.getTopAritrilList() }
                listData.await().data.datas.addAll(0, topData.await().data)
                listData.await()
            } else {
                listData.await()
            }
        }
    }

    /**
     * 注册并登陆
     */
    suspend fun register(username: String, password: String): ApiResponse<WxUserInfoResponse> {
        val registerData = apiService.register(username, password, password)
        //判断注册结果 注册成功，调用登录接口
        if (registerData.isSucces()) {
            return apiService.login(username, password)
        } else {
            //抛出错误异常
            throw AppException(registerData.code, registerData.msg)
        }
    }

    /**
     * 注册并登陆
     */
    suspend fun deleteUser( params: String) : ApiResponse<String>{
        val deleteData = apiService.deleteUser(params)

        //判断注册结果 注册成功，调用登录接口
        if (deleteData.isSucces()) {
            return deleteData
        } else {
            //抛出错误异常
            throw AppException(deleteData.code, deleteData.msg)
        }
    }

    /**
     * 第三方登陆
     */
    suspend fun thirdlogin( code: String) : ApiResponse<String>{
        val codeData = apiService.thirdlogin(code)

        //判断注册结果 注册成功，调用登录接口
        if (codeData.isSucces()) {
            return codeData
        } else {
            //抛出错误异常
            throw AppException(codeData.code, codeData.msg)
        }
    }

    /**
     * 绑定手机
     */
    suspend fun bindPhone(code: String) : ApiResponse<String>{
        val codeData = apiService.BindPhone(code)

        //判断注册结果 注册成功，调用登录接口
        if (codeData.isSucces()) {
            return codeData
        } else {
            //抛出错误异常
            throw AppException(codeData.code, codeData.msg)
        }
    }

    /**
     * 解绑手机
     */
    suspend fun unBindPhone( code: String) : ApiResponse<String>{
        val codeData = apiService.unBindPhone(code)

        //判断注册结果 注册成功，调用登录接口
        if (codeData.isSucces()) {
            return codeData
        } else {
            //抛出错误异常
            throw AppException(codeData.code, codeData.msg)
        }
    }

    /**
     * 文件上传
     */
    suspend fun upload(code: MultipartBody.Part) : ApiResponse<UploadPicUserBean>{
        val codeData = apiService.upload(code)

        //判断注册结果 注册成功，调用登录接口
        if (codeData.isSucces()) {
            return codeData
        } else {
            //抛出错误异常
            throw AppException(codeData.code, codeData.msg)
        }
    }


    /**
     * 更新用户信息
     */
    suspend fun updateUserInfo(code: String) : ApiResponse<String>{
        val codeData = apiService.updateUserInfo(code)

        //判断注册结果 注册成功，调用登录接口
        if (codeData.isSucces()) {
            return codeData
        } else {
            //抛出错误异常
            throw AppException(codeData.code, codeData.msg)
        }
    }

    /**
     * 获取用户信息
     */
    suspend fun getUserInfo(code: String) : ApiResponse<String>{
        val codeData = apiService.getUserInfo(code)

        //判断注册结果 注册成功，调用登录接口
        if (codeData.isSucces()) {
            return codeData
        } else {
            //抛出错误异常
            throw AppException(codeData.code, codeData.msg)
        }
    }

    /**
     * 实名认证
     */
    suspend fun userRealName(code: String) : ApiResponse<String>{
        val codeData = apiService.userRealName(code)

        //判断注册结果 注册成功，调用登录接口
        if (codeData.isSucces()) {
            return codeData
        } else {
            //抛出错误异常
            throw AppException(codeData.code, codeData.msg)
        }
    }


    /**
     * 短信验证码
     */
    suspend fun getCode(code: String) : ApiResponse<String>{
        val codeData = apiService.getCode(code)

        //判断注册结果 注册成功，调用登录接口
        if (codeData.isSucces()) {
            return codeData
        } else {
            //抛出错误异常
            throw AppException(codeData.code, codeData.msg)
        }
    }


    /**
     * 注册并登陆
     */
    suspend fun getList() : ApiResponse<String>{
        val listeData = apiService.getList()

        //判断注册结果 注册成功，调用登录接口
        if (listeData.isSucces()) {
            return listeData
        } else {
            //抛出错误异常
            throw AppException(listeData.code, listeData.msg)
        }
    }


    /**
     * 获取项目标题数据
     */
    suspend fun getProjectData(
        pageNo: Int,
        cid: Int = 0,
        isNew: Boolean = false
    ): ApiResponse<ApiPagerResponse<ArrayList<AriticleResponse>>> {
        return if (isNew) {
            apiService.getProjecNewData(pageNo)
        } else {
            apiService.getProjecDataByType(pageNo, cid)
        }
    }
}
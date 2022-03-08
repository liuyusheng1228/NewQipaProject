package com.qipa.newboxproject.app.network

import com.qipa.newboxproject.app.util.CacheUtil
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 自定义头部参数拦截器，传入heads
 */
class MyHeadInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.addHeader("token", "eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX25hbWUiOiJhZG1pbiIsImxvZ2luX3VzZXJfa2V5IjoiMGE2YzljM2EtZTAxYy00OGVjLTk4ODAtZTZjMWY3M2QyMWIyIn0.u3LpaWrPZu7z8BLGCS7Nx_5Kihap1uTseuX7uWYm8_Iw8Tq70rpIxiHrEZ0IHd4ZzNcF-cMgRIPUgeN9BG4tIA").build()
        builder.addHeader("device", "Android").build()
//        builder.addHeader( "content-type","application/json;charset=UTF-8").build()
        builder.addHeader( "content-type","application/x-www-form-urlencoded").build()
        builder.addHeader( "Authorization","access_token").build()

//        builder.addHeader("isLogin", CacheUtil.isLogin().toString()).build()
        return chain.proceed(builder.build())
    }

}
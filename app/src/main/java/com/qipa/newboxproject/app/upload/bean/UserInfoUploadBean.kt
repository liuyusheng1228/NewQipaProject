package com.qipa.newboxproject.app.upload.bean

import okhttp3.MultipartBody
import java.util.*

data class UserInfoUploadBean(var userId : String?,var headPic: String?,var nickname: String?,var gender: Int?,var email: Int?,var location: String?,var birthdayTime: String?)
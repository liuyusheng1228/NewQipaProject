package com.qipa.newboxproject.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.databinding.BaseObservable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class WxUserInfoResponse(val userId: String ,val nickname: String ,val registerType: Int ,val gender: Int ,val headPic: String ,val userMobile: String ,val realName: String ,val identityCard: String ,val birthdayTime: String ,val registerTime: String ,
                              val registerIp: String ,val osType: String ,val userStatus: String ,val lastLoginIp: String,val lastLoginTime: String,val location: String ) :
    BaseObservable(),Parcelable
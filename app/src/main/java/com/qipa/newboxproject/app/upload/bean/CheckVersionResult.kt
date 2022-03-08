package com.qipa.newboxproject.app.upload.bean

import android.os.Parcel

import android.os.Parcelable.Creator

import android.os.Parcelable


class CheckVersionResult : Parcelable {
    var packageUrl: String?
    var versionCode: Int
    var versionName: String?
    var created: String?
    var description: String?
    var updateType //1表示强制更新
            : Int

    constructor(
        packageUrl: String?,
        versionCode: Int,
        versionName: String?,
        created: String?,
        description: String?,
        updateType: Int
    ) {
        this.packageUrl = packageUrl
        this.versionCode = versionCode
        this.versionName = versionName
        this.created = created
        this.description = description
        this.updateType = updateType
    }

    protected constructor(`in`: Parcel) {
        packageUrl = `in`.readString()
        versionCode = `in`.readInt()
        versionName = `in`.readString()
        created = `in`.readString()
        description = `in`.readString()
        updateType = `in`.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(packageUrl)
        dest.writeInt(versionCode)
        dest.writeString(versionName)
        dest.writeString(created)
        dest.writeString(description)
        dest.writeInt(updateType)
    }

    companion object CREATOR : Creator<CheckVersionResult> {
        override fun createFromParcel(parcel: Parcel): CheckVersionResult {
            return CheckVersionResult(parcel)
        }

        override fun newArray(size: Int): Array<CheckVersionResult?> {
            return arrayOfNulls(size)
        }
    }

}
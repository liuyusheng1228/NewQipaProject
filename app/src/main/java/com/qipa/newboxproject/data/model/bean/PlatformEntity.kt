package com.qipa.newboxproject.data.model.bean

import cn.sharesdk.framework.Platform

class PlatformEntity {
    private var type = 0
    private var name: String? = null
    private var icon = 0
    private var status: String? = null
    private var platform: Platform? = null

    fun getmType(): Int {
        return type
    }

    fun setmType(type: Int) {
        this.type = type
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getmIcon(): Int {
        return icon
    }

    fun setmIcon(mIcon: Int) {
        icon = mIcon
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getmPlatform(): Platform? {
        return platform
    }

    fun setmPlatform(mPlatform: Platform?) {
        platform = mPlatform
    }
}
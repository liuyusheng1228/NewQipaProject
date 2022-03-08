package com.qipa.newboxproject.data.model.bean

import cn.sharesdk.framework.Platform
import java.io.Serializable

class ShareListItemInEntity : Serializable {
    var name: String? = null
    var icon = 0
    var type = 0
    var platform: Platform? = null
        set(platform) {
            field = platform
            if (platform != null) {
                platName = platform.name
            }
        }
    var platName: String? = null
}

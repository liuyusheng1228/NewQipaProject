package com.qipa.newboxproject.viewmodel.state

import com.qipa.jetpackmvvm.base.viewmodel.BaseViewModel
import com.qipa.jetpackmvvm.callback.databind.StringObservableField
import com.qipa.newboxproject.app.util.CacheUtil
import com.qipa.newboxproject.app.util.ColorUtil

class PersonalInformationModel : BaseViewModel() {
    var imageUrl = ColorUtil.userImage()?.let { StringObservableField(it) }
}
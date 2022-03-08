package com.qipa.newboxproject.viewmodel.state

import com.qipa.jetpackmvvm.base.viewmodel.BaseViewModel
import com.qipa.jetpackmvvm.callback.databind.IntObservableField
import com.qipa.jetpackmvvm.callback.databind.StringObservableField
import com.qipa.newboxproject.app.util.CacheUtil
import com.qipa.newboxproject.app.util.ColorUtil

/**
 * 描述　: 专门存放 MeFragment 界面数据的ViewModel
 */
class MeViewModel : BaseViewModel() {

    var name = StringObservableField("请先登录~")

    var integral = IntObservableField(0)

    var info = StringObservableField("id：--　排名：-")

    var imageUrl = ColorUtil.userImage()?.let { StringObservableField(it) }

}
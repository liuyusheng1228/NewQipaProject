package com.qipa.newboxproject.viewmodel.state

import com.qipa.jetpackmvvm.base.viewmodel.BaseViewModel
import com.qipa.jetpackmvvm.callback.databind.StringObservableField

/**
 * 描述　:
 */
class AriticleViewModel : BaseViewModel() {

    //分享文章标题
    var shareTitle = StringObservableField()

    //分享文章网址
    var shareUrl = StringObservableField()

    //分享文章的人
    var shareName = StringObservableField()

}
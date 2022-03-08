package com.qipa.newboxproject.viewmodel.state

import com.qipa.jetpackmvvm.base.viewmodel.BaseViewModel
import com.qipa.jetpackmvvm.callback.databind.StringObservableField

class RealNameAuthenticationModel:BaseViewModel() {
    var realName = StringObservableField()
    var identityCard = StringObservableField()
}
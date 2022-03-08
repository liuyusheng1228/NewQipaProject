package com.qipa.newboxproject.viewmodel.state

import com.qipa.jetpackmvvm.base.viewmodel.BaseViewModel
import com.qipa.jetpackmvvm.callback.databind.StringObservableField


class UnbandPhoneModel : BaseViewModel() {
    var notGetCode = StringObservableField()
    var userphone = StringObservableField()
    var usercode = StringObservableField()
}
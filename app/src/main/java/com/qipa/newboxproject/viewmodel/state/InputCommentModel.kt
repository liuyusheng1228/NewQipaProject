package com.qipa.newboxproject.viewmodel.state

import com.qipa.jetpackmvvm.base.viewmodel.BaseViewModel
import com.qipa.jetpackmvvm.callback.databind.StringObservableField


class InputCommentModel : BaseViewModel() {
    var commentUserName = StringObservableField()
    var commentStarNum = StringObservableField()
    var commentMsgValue = StringObservableField()
    var commentRepeyNum = StringObservableField()
    var commentLikesNum = StringObservableField()
}
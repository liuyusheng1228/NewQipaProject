package com.qipa.qipaimbase.chat.chatset.ichatset

import android.os.Bundle
import androidx.annotation.Nullable
import com.qipa.qipaimbase.base.BaseActivity
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import com.qipa.qipaimbase.utils.mvpbase.IView

abstract class IChatSetView : BaseActivity(), IView {
    protected var presenter: IChatSetPresenter<*,*>? = null
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = getIPresenter() as IChatSetPresenter<*, *>
        checkNotNull(presenter) { "contactPresenter is null" }
    }

    abstract fun onTopChangeStatusResult(success: Boolean)

    abstract fun onIgnoreChangeStatusResult(success: Boolean)

    abstract fun onGetIgnoreStatus(result: JsonResult<JsonRequestResult>)
}
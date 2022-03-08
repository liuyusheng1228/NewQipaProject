package com.qipa.qipaimbase.session.isession

import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.qipa.qipaimbase.base.RvBaseFragment
import com.qipa.qipaimbase.session.SessionData
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import com.qipa.qipaimbase.utils.mvpbase.IView

abstract class ISessionView : RvBaseFragment(), IView {
    protected var iSessionPresenter: ISessionPresenter<*,*>? = null
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iSessionPresenter = getIPresenter() as ISessionPresenter?
    }

    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRv()
    }
    abstract fun onLoadHistory(sessionData: List<SessionData>?)
    abstract fun onGetOtherInfoResult(result: JsonResult<JsonRequestResult>, sessionData: SessionData?)
    abstract fun onDeleteSession(data: SessionData?)
    abstract fun onClearSession(data: SessionData?)
    abstract fun onNewSession(sessionData: SessionData?)
    abstract fun onGetAllUnReadCount(result: Int)
    abstract fun onLoadHistoryFromRemote(sessionData: List<SessionData>?)
}

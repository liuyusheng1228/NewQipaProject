package com.qipa.qipaimbase.utils.mvpbase

interface IView {
    fun getIPresenter(): IPresenter<in IView, in IModel>?
}

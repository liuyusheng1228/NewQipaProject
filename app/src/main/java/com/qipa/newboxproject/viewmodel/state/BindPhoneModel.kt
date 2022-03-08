package com.qipa.newboxproject.viewmodel.state

import android.view.View
import androidx.databinding.ObservableInt
import com.qipa.jetpackmvvm.base.viewmodel.BaseViewModel
import com.qipa.jetpackmvvm.callback.databind.StringObservableField


class BindPhoneModel : BaseViewModel() {
    var mobilephone = StringObservableField()
    var mobilecode = StringObservableField()
    var clearVisible = object : ObservableInt(mobilephone){
        override fun get(): Int {
            return if(mobilephone.get().isEmpty()){
                View.GONE
            }else{
                View.VISIBLE
            }
        }
    }
}
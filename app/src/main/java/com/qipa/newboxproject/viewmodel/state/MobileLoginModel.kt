package com.qipa.newboxproject.viewmodel.state

import android.view.View
import androidx.databinding.ObservableInt
import com.qipa.jetpackmvvm.base.viewmodel.BaseViewModel
import com.qipa.jetpackmvvm.callback.databind.StringObservableField


class MobileLoginModel : BaseViewModel() {
    var mobilephone = StringObservableField()


    //用户名清除按钮是否显示   不要在 xml 中写逻辑 所以逻辑判断放在这里
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
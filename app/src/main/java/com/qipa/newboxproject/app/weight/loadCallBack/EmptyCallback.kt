package com.qipa.newboxproject.app.weight.loadCallBack


import com.kingja.loadsir.callback.Callback
import com.qipa.newboxproject.R


class EmptyCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_empty
    }

}
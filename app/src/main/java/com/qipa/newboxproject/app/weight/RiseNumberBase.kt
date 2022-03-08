package com.qipa.newboxproject.app.weight

import com.qipa.newboxproject.app.weight.textview.RiseNumberTextView
import com.qipa.newboxproject.app.weight.textview.RiseNumberTextView.EndListener


interface RiseNumberBase {
    fun start()
    fun withNumber(number: Float): RiseNumberTextView?
    fun withNumber(number: Float, flag: Boolean): RiseNumberTextView?
    fun withNumber(number: Int): RiseNumberTextView?
    fun setDuration(duration: Long): RiseNumberTextView?
    fun setOnEnd(callback: EndListener?)
}
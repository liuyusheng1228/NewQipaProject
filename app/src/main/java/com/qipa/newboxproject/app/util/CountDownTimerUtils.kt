package com.qipa.newboxproject.app.util

import android.os.CountDownTimer
import android.widget.TextView
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.App

class CountDownTimerUtils(
    private val textView: TextView,
    millisInFuture: Long,
    countDownInterval: Long
) :
    CountDownTimer(millisInFuture, countDownInterval) {
    override fun onTick(millisUntilFinished: Long) {
        textView.isClickable = false //设置不可点击
        textView.setText(""+millisUntilFinished / 1000 + App.instance.applicationContext.getResources().getString(R.string.reacquire)
        ) //设置倒计时时间
        /* SpannableString spannableString=new SpannableString(bt_getcord.getText().toString());//获取按钮上的文字
        ForegroundColorSpan span=new ForegroundColorSpan(Color.RED);//设置文字颜色
        bt_getcord.setAllCaps(false);
        spannableString.setSpan(span,0,2,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);将倒计时的时间设置为红色
        bt_getcord.setText(spannableString);*/
    }

    override fun onFinish() {
        textView.isClickable = true //重新获得点击
        textView.setText(
            App.instance.applicationContext.getResources().getString(R.string.reacquire)
        )
    }

    init { //控件，定时总时间,间隔时间
        start()
    }
}

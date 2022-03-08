package com.qipa.newboxproject.app.weight.video

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import cn.qpvd.QPvdStd
import com.qipa.newboxproject.R


class QipaQPvdStd : QPvdStd {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    override fun init(context: Context?) {
        super.init(context)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        val i: Int = v.getId()
        if (i == R.id.fullscreen) {
            Log.i(TAG, "onClick: fullscreen button")
        } else if (i == R.id.start) {
            Log.i(TAG, "onClick: start button")
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        super.onTouch(v, event)
        val id: Int = v.getId()
        if (id == R.id.surface_container) {
            when (event.getAction()) {
                MotionEvent.ACTION_UP -> {
                    if (mChangePosition) {
                        Log.i(TAG, "Touch screen seek position")
                    }
                    if (mChangeVolume) {
                        Log.i(TAG, "Touch screen change volume")
                    }
                }
            }
        }
        return false
    }

//    override fun getLayoutId(): Int {
//        return R.layout.jz_layout_std
//    }



    override val layoutId: Int
        get() = R.layout.jz_layout_std


    override fun startVideo() {
        super.startVideo()
        Log.i(TAG, "startVideo")
    }


    override fun onStopTrackingTouch(seekBar: SeekBar) {
        super.onStopTrackingTouch(seekBar)
        Log.i(TAG, "Seek position ")
    }


    override fun gotoFullscreen() {
        super.gotoFullscreen()
        Log.i(TAG, "goto Fullscreen")
    }

    override fun gotoNormalScreen() {
        super.gotoNormalScreen()
        Log.i(TAG, "quit Fullscreen")
    }

    override fun autoFullscreen(x: Float) {
        super.autoFullscreen(x)
        Log.i(TAG, "auto Fullscreen")
    }

    override fun onClickUiToggle() {
        super.onClickUiToggle()
        Log.i(TAG, "click blank")
    }

    //onState 代表了播放器引擎的回调，播放视频各个过程的状态的回调
    override fun onStateNormal() {
        super.onStateNormal()
    }

    override fun onStatePreparing() {
        super.onStatePreparing()
    }

    override fun onStatePlaying() {
        super.onStatePlaying()
    }

    override fun onStatePause() {
        super.onStatePause()
    }

    override fun onStateError() {
        super.onStateError()
    }

    override fun onStateAutoComplete() {
        super.onStateAutoComplete()
        Log.i(TAG, "Auto complete")
    }

    //changeUiTo 真能能修改ui的方法
    override fun changeUiToNormal() {
        super.changeUiToNormal()
    }

    override fun changeUiToPreparing() {
        super.changeUiToPreparing()
    }

    override fun changeUiToPlayingShow() {
        super.changeUiToPlayingShow()
    }

    override fun changeUiToPlayingClear() {
        super.changeUiToPlayingClear()
    }

    override fun changeUiToPauseShow() {
        super.changeUiToPauseShow()
    }

    override fun changeUiToPauseClear() {
        super.changeUiToPauseClear()
    }

    override fun changeUiToComplete() {
        super.changeUiToComplete()
    }

    override fun changeUiToError() {
        super.changeUiToError()
    }

    override fun onInfo(what: Int, extra: Int) {
        super.onInfo(what, extra)
    }

    override fun onError(what: Int, extra: Int) {
        super.onError(what, extra)
    }
}
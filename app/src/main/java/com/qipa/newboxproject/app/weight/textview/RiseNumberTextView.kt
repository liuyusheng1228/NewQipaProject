package com.qipa.newboxproject.app.weight.textview

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.qipa.newboxproject.app.weight.RiseNumberBase
import java.math.RoundingMode
import java.text.DecimalFormat


/**
 * MyApplication --  cq.cake.custom
 * Created by Small Cake on  2017/5/20 11:18.
 */
class RiseNumberTextView : AppCompatTextView, RiseNumberBase {
    private var mPlayingState = STOPPED
    private var number = 0f
    private var fromNumber = 0f
    private var duration: Long = 1000

    /**
     * 1.int 2.float
     */
    private var numberType = 2
    private var flags = true
    private var mEndListener: EndListener? = null

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attr: AttributeSet?) : super(
        context!!, attr
    ) {
    }

    constructor(context: Context?, attr: AttributeSet?, defStyle: Int) : super(
        context!!, attr, defStyle
    ) {
    }

    interface EndListener {
        fun onEndFinish()
    }

    val isRunning: Boolean
        get() = mPlayingState == RUNNING

    private fun runFloat() {
        val valueAnimator = ValueAnimator.ofFloat(fromNumber, number)
        valueAnimator.duration = duration
        valueAnimator.addUpdateListener { valueAnimator ->
            if (flags) {
                text = format("##0.00")!!
                    .format(valueAnimator.animatedValue.toString().toDouble())
                if (valueAnimator.animatedValue.toString()
                        .equals(number.toString() + "", ignoreCase = true)
                ) {
                    text = format("##0.00")!!
                        .format((number.toString() + "").toDouble())
                }
            } else {
                text = format("##0.00")!!
                    .format(valueAnimator.animatedValue.toString().toDouble())
                if (valueAnimator.animatedValue.toString()
                        .equals(number.toString() + "", ignoreCase = true)
                ) {
                    text = format("##0.00")!!
                        .format((number.toString() + "").toDouble())
                }
            }
            if (valueAnimator.animatedFraction >= 1) {
                mPlayingState = STOPPED
                if (mEndListener != null) mEndListener!!.onEndFinish()
            }
        }
        valueAnimator.start()
    }

    private fun runInt() {
        val valueAnimator = ValueAnimator.ofInt(
            fromNumber.toInt(), number.toInt()
        )
        valueAnimator.duration = duration
        valueAnimator.addUpdateListener { valueAnimator ->
            text = valueAnimator.animatedValue.toString()
            if (valueAnimator.animatedFraction >= 1) {
                mPlayingState = STOPPED
                if (mEndListener != null) mEndListener!!.onEndFinish()
            }
        }
        valueAnimator.start()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun start() {
        if (!isRunning) {
            mPlayingState = RUNNING
            if (numberType == 1) runInt() else runFloat()
        }
    }

    override fun withNumber(number: Float, flag: Boolean): RiseNumberTextView {
        this.number = number
        flags = flag
        numberType = 2
        fromNumber = 0f
        return this
    }

    override fun withNumber(number: Float): RiseNumberTextView {
        println(number)
        this.number = number
        numberType = 2
        fromNumber = 0f
        return this
    }

    override fun withNumber(number: Int): RiseNumberTextView {
        this.number = number.toFloat()
        numberType = 1
        fromNumber = 0f
        return this
    }

    override fun setDuration(duration: Long): RiseNumberTextView {
        this.duration = duration
        return this
    }

    override fun setOnEnd(callback: EndListener?) {
        mEndListener = callback
    }

    companion object {
        private const val STOPPED = 0
        private const val RUNNING = 1
        val sizeTable =
            intArrayOf(9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Int.MAX_VALUE)

        fun sizeOfInt(x: Int): Int {
            var i = 0
            while (true) {
                if (x <= sizeTable[i]) return i + 1
                i++
            }
        }

        /**
         * 格式化
         */
        private var dfs: DecimalFormat? = null
        fun format(pattern: String?): DecimalFormat? {
            if (dfs == null) {
                dfs = DecimalFormat()
            }
            dfs!!.roundingMode = RoundingMode.FLOOR
            dfs!!.applyPattern(pattern)
            return dfs
        }

        /**
         * 去掉多余的0
         * @param s
         * @return
         */
        fun subZeroAndDot(s: String): String {
            var s = s
            if (s.indexOf(".") > 0) {
                s = s.replace("0+?$".toRegex(), "") //去掉多余的0
                s = s.replace("[.]$".toRegex(), "") //如最后一位是.则去掉
            }
            return s
        }
    }
}
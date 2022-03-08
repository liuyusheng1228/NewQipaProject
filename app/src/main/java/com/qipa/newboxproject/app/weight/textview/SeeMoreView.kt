package com.qipa.newboxproject.app.weight.textview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.qipa.newboxproject.R
import kotlinx.android.synthetic.main.view_seemore.view.*

class SeeMoreView: FrameLayout {
    //是否展开和收起的标记
    private var mIsShowAll:Boolean = false
    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        View.inflate(context, R.layout.view_seemore,this)
        initListener()
    }
    private fun initListener() {
        //查看更多的点击事件
        view_seemore_tv_seemore.setOnClickListener {
            if(mIsShowAll){
                //这是收起的关键代码，将最大行数设置为你想展示的最小行数即可
                view_seemore_tvcontent.maxLines = 6
                view_seemore_tv_seemore.text = "查看更多"
            }else{
                //这是查看更多的关键代码，将最大行数设置一个大数即可
                view_seemore_tvcontent.maxLines = 20
                view_seemore_tv_seemore.text = "收起"
            }
            mIsShowAll = !mIsShowAll
        }
        //attachedToWindow之后执行操作
        post {
            //这里必须这样写，这是在attachedToWindow之后执行操作，否则获取行数会出问题
            Log.e("测试","OnLayout${view_seemore_tvlinecount.lineCount}")
            if(view_seemore_tvlinecount.lineCount>6){
                view_seemore_tv_seemore.visibility = View.VISIBLE
            }else{
                view_seemore_tv_seemore.visibility = View.GONE
            }
        }

    }
    /**
     * 设置文字
     */
    fun setText(text:String){
        //每次设置文字后都要进行重置
        view_seemore_tvcontent.text = text
        view_seemore_tvlinecount.text = text
        view_seemore_tv_seemore.text = "查看更多"
        view_seemore_tvcontent.maxLines = 6
        mIsShowAll = false
        if(view_seemore_tvlinecount.lineCount>6){
            view_seemore_tv_seemore.visibility = View.VISIBLE
        }else{
            view_seemore_tv_seemore.visibility = View.GONE
        }
    }
}


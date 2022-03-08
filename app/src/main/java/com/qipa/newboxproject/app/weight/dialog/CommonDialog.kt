package com.qipa.newboxproject.app.weight.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.qipa.newboxproject.R

/**
 * description:自定义dialog
 */
class CommonDialog(context: Context?) : Dialog(context!!, R.style.CustomDialog) {
    /**
     * 显示的图片
     */
    private var imageIv: ImageView? = null

    /**
     * 显示的标题
     */
    private var titleTv: TextView? = null

    /**
     * 显示的消息
     */
    private var messageTv: TextView? = null

    /**
     * 确认和取消按钮
     */
    private var negtiveBn: Button? = null
    private var positiveBn: Button? = null

    private var input_nick_name: EditText? = null

    /**
     * 按钮之间的分割线
     */
    private var columnLineView: View? = null

    /**
     * 都是内容数据
     */
    var message: String? = null
        private set
    var title: String? = null
        private set
    var positive: String? = null
        private set
    var negtive: String? = null
        private set
    var imageResId = -1
        private set

    /**
     * 底部是否只有一个按钮
     */
    var isSingle = false
        private set

    var isShowInput = false
        private set

    var inputmessage: String? = null

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_sign_tips)
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false)
        //初始化界面控件
        initView()
        //初始化界面数据
        refreshView()
        //初始化界面控件的事件
        initEvent()
    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private fun initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        positiveBn?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (onClickBottomListener != null) {
                    onClickBottomListener!!.onPositiveClick(input_nick_name?.text.toString())
                }
            }
        })
        //设置取消按钮被点击后，向外界提供监听
        negtiveBn?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (onClickBottomListener != null) {
                    onClickBottomListener!!.onNegtiveClick()
                }
            }
        })
    }

    /**
     * 初始化界面控件的显示数据
     */
    private fun refreshView() {
        //如果用户自定了title和message
        if (!TextUtils.isEmpty(title)) {
            titleTv?.setText(title)
            titleTv?.setVisibility(View.VISIBLE)
        } else {
            titleTv?.setVisibility(View.GONE)
        }
        if(isShowInput){
            messageTv?.visibility = View.GONE
            input_nick_name?.visibility = View.VISIBLE
        }
        if (!TextUtils.isEmpty(message)) {
            messageTv?.setText(message)
        }
        //如果设置按钮的文字
        if (!TextUtils.isEmpty(positive)) {
            positiveBn?.setText(positive)
        } else {
            positiveBn?.setText(context.getString(R.string.confirm))
        }
        if (!TextUtils.isEmpty(negtive)) {
            negtiveBn?.setText(negtive)
        } else {
            negtiveBn?.setText(context.getString(R.string.cancel))
        }
        if (imageResId != -1) {
            imageIv?.setImageResource(imageResId)
            imageIv?.setVisibility(View.VISIBLE)
        } else {
            imageIv?.setVisibility(View.GONE)
        }
        /**
         * 只显示一个按钮的时候隐藏取消按钮，回掉只执行确定的事件
         */
        if (isSingle) {
            columnLineView?.setVisibility(View.GONE)
            negtiveBn?.setVisibility(View.GONE)
        } else {
            negtiveBn?.setVisibility(View.VISIBLE)
            columnLineView?.setVisibility(View.VISIBLE)
        }

        if(isShowInput){
            input_nick_name?.setText(inputmessage)
        }
    }

    override fun show() {
        super.show()
        refreshView()
    }

    /**
     * 初始化界面控件
     */
    private fun initView() {
        negtiveBn = findViewById(R.id.negtive) as Button?
        positiveBn = findViewById(R.id.positive) as Button?
        titleTv = findViewById(R.id.title) as TextView?
        messageTv = findViewById(R.id.message) as TextView?
        imageIv = findViewById(R.id.image) as ImageView?
        columnLineView = findViewById(R.id.column_line)
        input_nick_name = findViewById(R.id.input_nick_name)
    }

    /**
     * 设置确定取消按钮的回调
     */
    var onClickBottomListener: OnClickBottomListener? = null
    fun setOnClickBottomListener(onClickBottomListener: OnClickBottomListener?): CommonDialog {
        this.onClickBottomListener = onClickBottomListener
        return this
    }

    interface OnClickBottomListener {
        /**
         * 点击确定按钮事件
         */
        fun onPositiveClick(inputmsg : String)

        /**
         * 点击取消按钮事件
         */
        fun onNegtiveClick()
    }

    fun setMessage(message: String?): CommonDialog {
        this.message = message
        return this
    }

    fun setTitle(title: String?): CommonDialog {
        this.title = title
        return this
    }

    fun setPositive(positive: String?): CommonDialog {
        this.positive = positive
        return this
    }

    fun setNegtive(negtive: String?): CommonDialog {
        this.negtive = negtive
        return this
    }

    fun setSingle(single: Boolean): CommonDialog {
        isSingle = single
        return this
    }

    fun setShowInput(input: Boolean): CommonDialog {
        isShowInput = input
        return this
    }

    fun setInputMessage(inputMessage: String): CommonDialog {
        inputmessage = inputMessage
        return this
    }

    fun setImageResId(imageResId: Int): CommonDialog {
        this.imageResId = imageResId
        return this
    }
}
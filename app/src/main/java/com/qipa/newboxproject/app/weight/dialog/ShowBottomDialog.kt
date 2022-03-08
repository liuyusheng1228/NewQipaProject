package com.qipa.newboxproject.app.weight.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import com.qipa.newboxproject.R

class ShowBottomDialog {
    private lateinit var view: View
    fun BottomDialog(context: Context) {
        //1、使用Dialog、设置style
        val dialog = Dialog(context, R.style.DialogTheme)
        //2、设置布局
        view = View.inflate(context, R.layout.head_bottom_dialog, null)
        dialog.setContentView(view)
        val window: Window? = dialog.getWindow()
        //设置弹出位置
        window?.setGravity(Gravity.BOTTOM)
        //设置弹出动画
        window?.setWindowAnimations(R.style.BottomDialogAnimation)
        //设置对话框大小
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
        dialog.findViewById<TextView>(R.id.tv_take_photo).setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if(onClickBottomListener != null){
                    onClickBottomListener?.onTakePhotoClick()
                }
                dialog.dismiss()
            }
        })
        dialog.findViewById<TextView>(R.id.tv_take_pic).setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if(onClickBottomListener != null){
                    onClickBottomListener?.onTakePicClick()
                }
                dialog.dismiss()
            }
        })
        dialog.findViewById<TextView>(R.id.tv_view_big_picture).setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if(onClickBottomListener != null){
                    onClickBottomListener?.onViewBigPicture()
                }
                dialog.dismiss()
            }
        })
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                dialog.dismiss()
            }
        })
    }

    /**
     * 设置确定取消按钮的回调
     */
    var onClickBottomListener: OnClickBottomListener? = null
    fun setOnClickBottomListener(onClickBottomListener: OnClickBottomListener?): ShowBottomDialog {
        this.onClickBottomListener = onClickBottomListener
        return this
    }

    interface OnClickBottomListener {
        /**
         * 点击拍照
         */
        fun onTakePhotoClick()
        /**
         * 点击查看大图
         */
        fun onViewBigPicture()

        /**
         * 点击相册
         */
        fun onTakePicClick()
    }
}

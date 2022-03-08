package com.qipa.newboxproject.app.weight.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*

import androidx.appcompat.app.AppCompatDialog
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.App
import kotlinx.android.synthetic.main.dialog_input_text_msg.*


/**
 * 发送输入框
 */
class InputTextMsgDialog(context: Context, theme: Int) :
    AppCompatDialog(context, theme) {
    private val mContext: Context
    private var imm: InputMethodManager? = null
    private var messageTextView: EditText? = null
    private var rlDlg: RelativeLayout? = null
    private var mLastDiff = 0
    private var tvNumber: TextView? = null
    private var maxNumber = 100

    interface OnTextSendListener {
        fun onTextSend(msg: String?)
        fun dismiss()
    }

    private var mOnTextSendListener: OnTextSendListener? = null

    /**
     * 最大输入字数  默认100
     */
    @SuppressLint("SetTextI18n")
    fun setMaxNumber(maxNumber: Int) {
        this.maxNumber = maxNumber
        tvNumber?.setText("0/$maxNumber")
    }

    /**
     * 设置输入提示文字
     */
    fun setHint(text: String?) {
        messageTextView?.setHint(text)
    }

    private fun init() {
        setContentView(R.layout.dialog_input_text_msg)
        messageTextView = findViewById<View>(R.id.et_input_message) as EditText?
        tvNumber = findViewById<View>(R.id.tv_test) as TextView?
        val rldlgview: LinearLayout? = findViewById<View>(R.id.rl_inputdlg_view) as LinearLayout?
        messageTextView?.requestFocus()
        messageTextView?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                tvNumber?.setText(editable.length.toString() + "/" + maxNumber)
            }
        })
        imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val iv_confirm: TextView? = iv_confirm
        iv_confirm?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val msg: String = messageTextView?.getText().toString().trim()
                if (msg.length > maxNumber) {
                    Toast.makeText(mContext, "超过最大字数限制$maxNumber", Toast.LENGTH_LONG).show()
                    return
                }
                if (!TextUtils.isEmpty(msg)) {
                    mOnTextSendListener!!.onTextSend(msg)
                    imm?.showSoftInput(messageTextView, InputMethodManager.SHOW_FORCED)
                    imm?.hideSoftInputFromWindow(messageTextView?.getWindowToken(), 0)
                    messageTextView?.setText("")
                    dismiss()
                } else {
                    Toast.makeText(App.instance.applicationContext, "请输入文字", Toast.LENGTH_LONG)
                        .show()
                    //                Toast.makeText(mContext, "", Toast.LENGTH_LONG).show();
                }
                messageTextView?.setText(null)
            }
        })
        messageTextView?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                return when (actionId) {
                    KeyEvent.KEYCODE_ENDCALL, KeyEvent.KEYCODE_ENTER -> {
                        if (messageTextView?.text?.length!! > maxNumber) {
                            Toast.makeText(mContext, "超过最大字数限制", Toast.LENGTH_LONG).show()
                            return true
                        }
                        if (messageTextView?.text?.length!! > 0) {
                            imm?.hideSoftInputFromWindow(messageTextView?.windowToken, 0)
                            dismiss()
                        } else {
                            Toast.makeText(mContext, "请输入文字", Toast.LENGTH_LONG).show()
                        }
                        true
                    }
                    KeyEvent.KEYCODE_BACK -> {
                        dismiss()
                        false
                    }
                    else -> false
                }
            }
        })
        messageTextView?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(view: View?, i: Int, keyEvent: KeyEvent): Boolean {
                Log.d("My test", "onKey " + keyEvent.getCharacters())
                return false
            }
        })
        rlDlg = findViewById(R.id.rl_outside_view)
        rlDlg?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if (v.getId() !== R.id.rl_inputdlg_view) dismiss()
            }
        })
        rldlgview?.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                view: View?,
                i: Int,
                i1: Int,
                i2: Int,
                i3: Int,
                i4: Int,
                i5: Int,
                i6: Int,
                i7: Int
            ) {
                val r = Rect()
                //获取当前界面可视部分
                this@InputTextMsgDialog.window!!.decorView.getWindowVisibleDisplayFrame(r)
                //获取屏幕的高度
                val screenHeight = this@InputTextMsgDialog.window!!.decorView.rootView.height
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                val heightDifference: Int = screenHeight - r.bottom
                if (heightDifference <= 0 && mLastDiff > 0) {
                    dismiss()
                }
                mLastDiff = heightDifference
            }
        })
        rldlgview?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                imm?.hideSoftInputFromWindow(messageTextView?.windowToken, 0)
                dismiss()
            }
        })
        setOnKeyListener { dialogInterface, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.repeatCount === 0) dismiss()
            false
        }
    }

    private fun setLayout() {
        window!!.setGravity(Gravity.BOTTOM)
        val m: WindowManager = window!!.windowManager
        val d: Display = m.getDefaultDisplay()
        val p: WindowManager.LayoutParams = window!!.attributes
        p.width = WindowManager.LayoutParams.MATCH_PARENT
        p.height = WindowManager.LayoutParams.WRAP_CONTENT
        window!!.attributes = p
        setCancelable(true)
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    fun setmOnTextSendListener(onTextSendListener: OnTextSendListener?) {
        mOnTextSendListener = onTextSendListener
    }

    override fun dismiss() {
        super.dismiss()
        //dismiss之前重置mLastDiff值避免下次无法打开
        mLastDiff = 0
        if (mOnTextSendListener != null) mOnTextSendListener!!.dismiss()
    }

    override fun show() {
        super.show()
    }

    init {
        mContext = context
        this.window?.setWindowAnimations(R.style.Animation_Design_BottomSheetDialog)
        init()
        setLayout()
    }
}

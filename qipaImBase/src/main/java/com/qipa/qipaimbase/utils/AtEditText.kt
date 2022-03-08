package com.qipa.qipaimbase.utils

import android.content.Context
import android.text.*
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import com.qipa.qipaimbase.R
import java.util.ArrayList

class AtEditText : AppCompatEditText {
    var atLists: ArrayList<Entity>? = null
    private var contexts: Context? = null
    private var mOnAtInputListener: OnAtInputListener? = null

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        this.contexts = context
        atLists = ArrayList()
        setOnKeyListener(MyOnKeyListener(this))
        addTextChangedListener(MentionTextWatcher())
    }

    /**
     * 添加@的内容
     *
     * @param parm 最多传4个 ,分别对应 id,name,parm1,parm2
     */
    fun addAtContent(vararg parm: String?) {
        atLists!!.add(Entity(parm[0], parm[1]))
        // 光标位置之前的字符是否是 @ , 如果不是 加上一个@
        val selectionStart = selectionStart
        // 获取当前内容
        val sss = text.toString()
        // 获取光标前以为字符
        val s =
            if (selectionStart != 0) sss.toCharArray()[selectionStart - 1].toString() + "" else ""
        // 将内容插入 , 改变文字颜色
        setText(
            changeTextColor(
                sss.substring(
                    0,
                    selectionStart
                ) + (if (s != "@") "@" else "") + parm[1] + sss.substring(
                    selectionStart,
                    sss.length
                )
            )
        ) //字符串替换，删掉符合条件的字符串
        // 设置光标位置
        setSelection(
            (sss.substring(
                0,
                selectionStart
            ) + (if (s != "@") "@" else "") + parm[1]).length
        )
    }

    private fun changeTextColor(sText: String): SpannableString {
        var startIndex = 0
        val spanIndexes = getSpanIndexes(sText)
        val spanText = SpannableString(sText)
        if (spanIndexes != null && spanIndexes.size != 0) {
            for (i in spanIndexes.indices) {
                if (i % 2 == 0) {  // 开始位置
                    startIndex = spanIndexes[i]
                } else {  // 结束位置
                    spanText.setSpan(
                        ForegroundColorSpan(contexts!!.resources.getColor(R.color.black)),
                        startIndex,
                        spanIndexes[i],
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }
        return spanText
        //        return changeChatEmoji(sText, spanText);
    }

    fun getSpanIndexes(sText: String): List<Int> {
        var endIndex = 0
        var startIndex = 0
        val spanIndexes: MutableList<Int> = ArrayList()
        for (i in atLists!!.indices) {
            val tempname = "@" + atLists!![i].name
            if (sText.indexOf(tempname, endIndex).also { startIndex = it } != -1) {
                endIndex = startIndex + tempname.length
                spanIndexes.add(startIndex) //name 的开始索引，键值为偶数，从0开始
                spanIndexes.add(startIndex + tempname.length) //name 的结束索引，键值为奇数，从1开始
            }
        }
        return spanIndexes
    }

    fun clearAtStatus() {
        if (atLists != null) {
            atLists!!.clear()
        }
    }

    private inner class MyOnKeyListener(private val editText: EditText) :
        OnKeyListener {
        override fun onKey(view: View?, keyCode: Int, event: KeyEvent): Boolean {
            return if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) { //当为删除键并且是按下动作时执行
                myDelete()
            } else false
        }

        /**
         * 删除方法
         *
         * @return
         */
        private fun myDelete(): Boolean {
            val content = editText.text.toString()
            val selectionStart = editText.selectionStart
            var endIndex = 0
            var startIndex: Int
            var deleteNum = 0
            val spanIndexes: MutableList<Int> = ArrayList()
            for (i in atLists!!.indices) {
                val name = "@" + atLists!![i].name
                if (content.indexOf(name, endIndex).also { startIndex = it } != -1) {
                    if (startIndex > selectionStart) break // 如果开始索引值,大于光标位置,那么退出遍历
                    endIndex = startIndex + name.length
                    deleteNum = i
                    spanIndexes.add(startIndex) //name 的开始索引，键值为偶数，从0开始
                    spanIndexes.add(startIndex + name.length) //name 的结束索引，键值为奇数，从1开始
                    if (endIndex > selectionStart) break // 如果结束索引值,大于光标位置,那么退出遍历
                }
            }
            // spanIndexes 必须大于0 且 光标位置不能大于 结束索引位置
            return if (spanIndexes.size > 0 && spanIndexes[spanIndexes.size - 2] < selectionStart && spanIndexes[spanIndexes.size - 1] >= selectionStart) {
                editText.setText(
                    changeTextColor(
                        content.substring(
                            0,
                            spanIndexes[spanIndexes.size - 2]
                        ) + content.substring(spanIndexes[spanIndexes.size - 1])
                    )
                ) //字符串替换，删掉符合条件的字符串
                editText.setSelection(spanIndexes[spanIndexes.size - 2]) // 设置光标位置
                atLists!!.removeAt(deleteNum)
                true
            } else {
                false
            }
        }
    }

    private inner class MentionTextWatcher : TextWatcher {
        //若从整串string中间插入字符，需要将插入位置后面的range相应地挪位
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(charSequence: CharSequence, index: Int, i1: Int, count: Int) {
            if (mOnAtInputListener != null) {
                if (count == 1 && !TextUtils.isEmpty(charSequence)) {
                    val mentionChar = charSequence.toString()[index]
                    if ('@' == mentionChar) {
                        mOnAtInputListener!!.onAtCharacterInput()
                    }
                }
            }
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    //    /**
    //     * 刷新@列表
    //     */
    //    public void refreshAtList(String... parm) {
    //        atList.add(new Entity(parm[0], parm[1], parm[2]));
    //        setText(changeTextColor(getText().toString()));
    //    }
    fun getAtList(): ArrayList<Entity>? {
        val content = editableText.toString()
        var endIndex = 0
        var startIndex = 0
        var i = 0
        while (i < atLists!!.size) {
            val tempname = "@" + atLists!![i].name
            if (content.indexOf(tempname, endIndex).also { startIndex = it } != -1) {
                endIndex = startIndex + tempname.length
            } else {
                atLists!!.removeAt(i)
                i--
            }
            i++
        }
        return atLists
    }

    fun setOnAtInputListener(OnAtInputListener: OnAtInputListener?) {
        mOnAtInputListener = OnAtInputListener
    }

    interface OnAtInputListener {
        fun onAtCharacterInput()
    }

    /**
     * @*** 格式的实体类
     */
    class Entity(vararg parm: String?) {
        var id: String? = null
        var name: String? = null
        var parm1: String? = null
        var parm2: String? = null

        fun containIndex(index: Int) {}

        /**
         * @param parm 最多传4个 ,分别对应 id,name,parm1,parm2
         */
        init {
            if (parm.size >= 1) id = parm[0]
            if (parm.size >= 2) name = parm[1]
            if (parm.size >= 3) parm1 = parm[2]
            if (parm.size >= 4) parm2 = parm[3]
        }
    }
}


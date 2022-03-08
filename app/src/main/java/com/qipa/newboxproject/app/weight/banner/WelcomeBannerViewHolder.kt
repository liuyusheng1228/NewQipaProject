package com.qipa.newboxproject.app.weight.banner

/**
 * 描述　:
 */

import android.view.View
import android.widget.TextView
import com.zhpan.bannerview.BaseViewHolder
import com.qipa.newboxproject.R

class WelcomeBannerViewHolder(view: View) : BaseViewHolder<String>(view) {
    override fun bindData(data: String?, position: Int, pageSize: Int) {
        val textView = findView<TextView>(R.id.banner_text)
        textView.text = data
    }

}

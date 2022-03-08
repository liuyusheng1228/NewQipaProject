package com.qipa.newboxproject.data.model.bean

import android.content.Context
import com.bumptech.glide.load.engine.DiskCacheStrategy

import com.bumptech.glide.Glide

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.weight.transformerslayout.holder.Holder


class NavAdapterViewHolder internal constructor(itemView: View) :
    Holder<Nav?>(itemView) {
    private var icon: ImageView? = null
    private var text: TextView? = null

    override fun initView(itemView: View?) {
        icon = itemView?.findViewById(R.id.iv_menu_icon)
        text = itemView?.findViewById(R.id.tv_menu_text)
    }

    override fun onBind(context: Context?, list: List<Nav?>?, data: Nav?, position: Int) {
        if (data == null) return
        text!!.text = data.text
        //        icon.setImageResource(data.getIcon());
        icon?.let {
            context?.let { it1 ->
                Glide.with(it1)
                    .asBitmap()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.load_error)
                    .load(data.url)
                    .into(it)
            }
        }
    }
}

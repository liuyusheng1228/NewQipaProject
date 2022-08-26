package com.qipa.newboxproject.app.weight.banner

/**
 * 描述　:
 */

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.zhpan.bannerview.BaseViewHolder
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.App
import com.qipa.newboxproject.data.model.bean.BannerResponse

class MemberBannerViewHolder(view: View) : BaseViewHolder<BannerResponse>(view) {
    private val  roundedCorners: RoundedCorners = RoundedCorners(20)
    private var options = RequestOptions.bitmapTransform(roundedCorners)
    override fun bindData(data: BannerResponse?, position: Int, pageSize: Int) {
        val img = itemView.findViewById<ImageView>(R.id.bannerhome_img)

        data?.let {
            Glide.with(App.getContext())
                .load(it.imagePath)
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(img)
        }
    }

}

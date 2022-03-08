package io.github.sdwfqin.widget.pictureupload

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qipa.jetpackmvvm.R
import com.qipa.jetpackmvvm.util.GlideUtils

/**
 * 描述：
 *
 */
class PictureUploadAdapter<T : PictureUploadModel>(layoutResId: Int, data: MutableList<T?>?) :
    BaseQuickAdapter<T?, BaseViewHolder>(layoutResId, data) {

    init {
        addChildClickViewIds(R.id.ii_img, R.id.ii_del)
    }

    override fun convert(holder: BaseViewHolder, item: T?) {
        val iiImg = holder.getView<ImageView>(R.id.ii_img)
        item?.let {
            holder.setVisible(R.id.ii_del, true)
            GlideUtils.loadImage(context, item.pictureImage.toString(),iiImg)
        } ?: let {
            holder.setVisible(R.id.ii_del, false)
            iiImg.setImageResource(R.drawable.quick_add_img)
        }
    }
}
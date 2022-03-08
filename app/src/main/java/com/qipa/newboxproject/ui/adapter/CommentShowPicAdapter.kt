package com.qipa.newboxproject.ui.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.giftedcat.picture.lib.photoview.GlideImageLoader
import com.giftedcat.picture.lib.photoview.style.index.NumberIndexIndicator
import com.giftedcat.picture.lib.photoview.style.progress.ProgressBarIndicator
import com.giftedcat.picture.lib.photoview.transfer.TransferConfig
import com.giftedcat.picture.lib.photoview.transfer.Transferee
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.ext.setAdapterAnimation
import com.qipa.newboxproject.app.util.SettingUtil

class CommentShowPicAdapter  (var contexts: Context, datas : MutableList<String>, rvImages: RecyclerView?) :  BaseQuickAdapter<String, BaseViewHolder>(
    R.layout.item_img, datas) {

    protected var transferee: Transferee? = null
    protected var config: TransferConfig? = null
    init {

        initTransfer(rvImages)
        setAdapterAnimation(SettingUtil.getListMode())
    }

    /**
     * 初始化大图查看控件
     */
    private fun initTransfer(rvImages: RecyclerView?) {
        transferee = Transferee.getDefault(contexts)
        config = TransferConfig.build()
            .setSourceImageList(data)
            .setProgressIndicator(ProgressBarIndicator())
            .setIndexIndicator(NumberIndexIndicator())
            .setImageLoader(GlideImageLoader.with(contexts.applicationContext))
            .setJustLoadHitImage(true)
            .bindRecyclerView(rvImages, R.id.iv_thum)
    }

    override fun convert(holder: BaseViewHolder, item: String) {
        val iv_thum = holder.getView<ImageView>(R.id.iv_thum)
        iv_thum.visibility = View.VISIBLE
        Glide.with(context).load(item).into(iv_thum)

        iv_thum.setOnClickListener {
            data.forEach {
                if(it.length == 0){
                    data.remove(it)
                }
            }
            data.add("")
            config?.setNowThumbnailIndex(getItemPosition(item))
            config?.setSourceImageList(data)
            transferee?.apply(config)?.show()
        }
    }
}
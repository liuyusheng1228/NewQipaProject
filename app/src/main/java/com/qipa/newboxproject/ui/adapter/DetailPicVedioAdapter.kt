package com.qipa.newboxproject.ui.adapter

import android.view.View
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.ext.setAdapterAnimation
import com.qipa.newboxproject.app.util.SettingUtil
import com.qipa.newboxproject.app.weight.video.QipaQPvdStd
import com.qipa.newboxproject.data.model.bean.DetailPicVedioBean

class DetailPicVedioAdapter (data : MutableList<DetailPicVedioBean>) :  BaseQuickAdapter<DetailPicVedioBean, BaseViewHolder>(R.layout.item_game_detail_vedio_pic, data) {
    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }
    override fun convert(holder: BaseViewHolder, item: DetailPicVedioBean) {
        val cardVedio = holder.getView<CardView>(R.id.card_vedio)
        val cardVe = holder.getView<CardView>(R.id.card_vie)
        val cardVe2 = holder.getView<CardView>(R.id.card_vie2)
        when(item.picType){
            1 ->{
                cardVedio.visibility = View.VISIBLE
                cardVe.visibility = View.GONE
                cardVe2.visibility = View.GONE
                val jzvdStd: QipaQPvdStd = holder.getView(R.id.item_nice_video_player)
                jzvdStd.setUp(
                    item.urlPic,
                    "饺子闭眼睛"
                )
                jzvdStd.posterImageView?.let {
                    Glide.with(context).load("https://static.runoob.com/images/demo/demo2.jpg").into(
                        it
                    )
                }
            }
            2 ->{
                cardVe.visibility = View.VISIBLE
                cardVedio.visibility = View.GONE
                cardVe2.visibility = View.GONE
                Glide.with(context).load(item.urlPic).into(holder.getView(R.id.iv_show_grade))
            }
            3 ->{
                cardVe2.visibility = View.VISIBLE
                cardVe.visibility = View.GONE
                cardVedio.visibility = View.GONE
                Glide.with(context).load(item.urlPic).into(holder.getView(R.id.iv_show_grade_xu))
            }

        }

    }
}
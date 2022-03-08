package com.qipa.newboxproject.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.appcompat.view.menu.MenuBuilder
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.giftedcat.picture.lib.photoview.GlideImageLoader
import com.giftedcat.picture.lib.photoview.style.index.NumberIndexIndicator
import com.giftedcat.picture.lib.photoview.style.progress.ProgressBarIndicator
import com.giftedcat.picture.lib.photoview.transfer.TransferConfig
import com.giftedcat.picture.lib.photoview.transfer.Transferee

import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.interfaces.OnAddPicturesListener
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder
import me.kareluo.ui.PopupMenuView
import me.kareluo.ui.PopupView


class NineGridAdapter(
    var context: Context,
    selectPath: MutableList<String>?,
    rvImages: RecyclerView
) :
    CommonAdapter<String>(context, R.layout.item_img, selectPath) {
    /**
     * 删除小弹窗
     */
    var menuView: PopupMenuView? = null
    var listener: OnAddPicturesListener? = null
    private var deletePosition = 0
    protected var transferee: Transferee? = null
    protected var config: TransferConfig? = null

    /**
     * 设置最大图片数量
     */
    fun setMaxSize(maxNum: Int) {
        config?.setMax(maxNum)
    }

    /**
     * 设置点击添加按钮的监听
     */
    fun setOnAddPicturesListener(listener: OnAddPicturesListener?) {
        this.listener = listener
    }

    /**
     * 初始化大图查看控件
     */
    private fun initTransfer(rvImages: RecyclerView) {
        transferee = Transferee.getDefault(context)
        config = TransferConfig.build()
            .setSourceImageList(datas)
            .setProgressIndicator(ProgressBarIndicator())
            .setIndexIndicator(NumberIndexIndicator())
            .setImageLoader(GlideImageLoader.with(context.applicationContext))
            .setJustLoadHitImage(true)
            .bindRecyclerView(rvImages, R.id.iv_thum)
    }

    /**
     * 初始化图片删除小弹窗
     */
    @SuppressLint("RestrictedApi")
    private fun initDeleteMenu() {
        menuView = PopupMenuView(context, R.menu.menu_pop, MenuBuilder(context))
        menuView!!.setSites(PopupView.SITE_TOP)
        menuView!!.setOnMenuClickListener { position, menu ->
            datas.removeAt(deletePosition)
            if (datas[datas.size - 1] != "") {
                //列表最后一张不是添加按钮时，加入添加按钮
                datas.add("")
            }
            notifyDataSetChanged()
            true
        }
    }

    override fun convert(viewHolder: ViewHolder, item: String, position: Int) {
        val ivThum = viewHolder.getView<ImageView>(R.id.iv_thum)
        val ivAdd = viewHolder.getView<ImageView>(R.id.iv_add)
        if (item == "") {
            //item为添加按钮
            ivThum.visibility = View.GONE
            ivAdd.visibility = View.VISIBLE
        } else {
            //item为普通图片
            ivThum.visibility = View.VISIBLE
            ivAdd.visibility = View.GONE
        }
        Glide.with(mContext).load(item).into(ivThum)
        ivThum.setOnClickListener(PicturesClickListener(position))
        ivAdd.setOnClickListener(PicturesClickListener(position))
        ivThum.setOnLongClickListener { view ->
            deletePosition = position
            //最上面的三个删除按钮是往下的  其他的都是往上的
            if (position < 3) {
                menuView?.setSites(PopupView.SITE_BOTTOM)
            } else {
                menuView?.setSites(PopupView.SITE_TOP)
            }
            menuView?.show(view)
            false
        }
    }

    /**
     * 图片点击事件
     */
    private inner class PicturesClickListener(var position: Int) : View.OnClickListener {
        override fun onClick(view: View) {
            when (view.id) {
                R.id.iv_thum -> {
                    //点击图片
                    config?.setNowThumbnailIndex(position)
                    config?.setSourceImageList(datas)
                    transferee?.apply(config)?.show()
                }
                R.id.iv_add ->                     //点击添加按钮
                    if (listener != null) listener?.onAdd()
            }
        }
    }

    init {
        selectPath?.add("")
        initDeleteMenu()
        initTransfer(rvImages)
    }
}
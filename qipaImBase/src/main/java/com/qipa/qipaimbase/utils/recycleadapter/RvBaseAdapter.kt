package com.qipa.qipaimbase.utils.recycleadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.qipa.qipaimbase.R
import java.util.ArrayList



abstract class RvBaseAdapter<T : ItemData?>(private var baseDataList: List<T>?) :
    RecyclerView.Adapter<RvViewHolder?>() {
    private lateinit var rvListener: RvListenerImpl
    private var onClickListener: View.OnClickListener? = null
        private get() {
            if (field == null) {
                field = View.OnClickListener { v ->
                    val position = v.getTag(R.id.baseapdater_tag_item_position) as Int
                    val baseData = v.getTag(R.id.baseapdater_tag_item_data) as ItemData
                    if (rvListener != null) {
                        rvListener.onClick(v, baseData, position)
                    }
                }
            }
            return field
        }
    private var onLongClickListener: View.OnLongClickListener? = null
        private get() {
            if (field == null) {
                field = View.OnLongClickListener { v ->
                    val position = v.getTag(R.id.baseapdater_tag_item_position) as Int
                    val baseData = v.getTag(R.id.baseapdater_tag_item_data) as ItemData
                    if (rvListener != null) {
                        rvListener.onLongClick(v, baseData, position)
                    }
                    true
                }
            }
            return field
        }
    private var onClickViewArray: IntArray? = null
    private var onLongClickViewArray: IntArray? = null
    fun addItemType(itemType: ItemType<in ItemData>) {
        ItemManager.instance.addItems(itemType)
    }

    override fun getItemViewType(position: Int): Int {
        return ItemManager.instance.getType(baseDataList!![position], position)
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, viewType: Int): RvViewHolder {
        val layout: Int = ItemManager.instance.getLayout(viewType)
        val inflate = LayoutInflater.from(viewGroup.context).inflate(layout, viewGroup, false)
        return RvViewHolder(inflate)
    }

    override fun onBindViewHolder(@NonNull rvViewHolder: RvViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        val item: ItemType<in ItemData> = ItemManager.instance.getItemType(viewType)
        rvViewHolder.itemViews.setTag(R.id.baseapdater_tag_item_position, position)
        rvViewHolder.itemViews.setTag(R.id.baseapdater_tag_item_data, baseDataList!![position])
        if (item.openClick()) {
//            rvViewHolder.getItemView().setOnClickListener(getOnClickListener());
            onClickViewArray = item.onClickViews
            if (onClickViewArray != null) {
                for (i in onClickViewArray!!.indices) {
                    val view = rvViewHolder.getView(onClickViewArray!![i]) ?: continue
                    view.setTag(R.id.baseapdater_tag_item_position, position)
                    view.setTag(R.id.baseapdater_tag_item_data, baseDataList!![position])
                    view.setOnClickListener(onClickListener)
                }
            }
        } else {
            onClickViewArray = item.onClickViews
            if (onClickViewArray != null) {
                for (i in onClickViewArray!!.indices) {
                    rvViewHolder.getView(onClickViewArray!![i]).setOnClickListener(null)
                }
            }
            //            rvViewHolder.getItemView().setOnClickListener(null);
        }
        if (item.openLongClick()) {
            onLongClickViewArray = item.onLongClickViews
            if (onLongClickViewArray != null) {
                for (i in onLongClickViewArray!!.indices) {
                    val view = rvViewHolder.getView(onLongClickViewArray!![i])
                    view.setTag(R.id.baseapdater_tag_item_position, position)
                    view.setTag(R.id.baseapdater_tag_item_data, baseDataList!![position])
                    view.setOnLongClickListener(onLongClickListener)
                }
            }
        } else {
            onLongClickViewArray = item.onLongClickViews
            if (onLongClickViewArray != null) {
                for (i in onLongClickViewArray!!.indices) {
                    rvViewHolder.getView(onLongClickViewArray!![i]).setOnLongClickListener(null)
                }
            }
        }
        item.fillContent(rvViewHolder, position, baseDataList!![position])
    }

    override fun getItemCount(): Int {
        return if (baseDataList == null) 0 else baseDataList!!.size
    }

    open fun setRvListener(rvListener: RvListenerImpl) {
        this.rvListener = rvListener
    }



    init {
        if (baseDataList == null) {
            baseDataList = ArrayList()
        }
    }
}
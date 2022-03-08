package com.qipa.newboxproject.ui.adapter

import android.content.Context
import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter
import com.donkingliang.groupedadapter.holder.BaseViewHolder
import com.qipa.newboxproject.R
import com.qipa.newboxproject.data.model.bean.AriticleResponse
import com.qipa.newboxproject.data.model.bean.OpenTestTypeBean


/**
 * @describe 这是普通的分组Adapter 每一个组都有头部、尾部和子项。
 */
open class GroupedListAdapter(context: Context?, groups: ArrayList<OpenTestTypeBean>?) :
    GroupedRecyclerViewAdapter(context) {
    protected var mGroups: ArrayList<OpenTestTypeBean>?
    override fun getGroupCount(): Int {
        return if (mGroups == null) 0 else mGroups?.size!!
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        val children: ArrayList<AriticleResponse> = mGroups!![groupPosition].children
        return if (children == null) 0 else children.size
    }

    fun clear() {
        mGroups!!.clear()
        notifyDataChanged()
    }

    fun setGroups(groups: ArrayList<OpenTestTypeBean>?) {
        mGroups = groups
        notifyDataChanged()
    }

    override fun hasHeader(groupPosition: Int): Boolean {
        return true
    }

    override fun hasFooter(groupPosition: Int): Boolean {
        return true
    }

    override fun getHeaderLayout(viewType: Int): Int {
        return R.layout.adapter_open_test_header
    }

    override fun getFooterLayout(viewType: Int): Int {
        return R.layout.adapter_footer
    }

    override fun getChildLayout(viewType: Int): Int {
        return R.layout.item_game_list_title
    }

    override fun onBindHeaderViewHolder(holder: BaseViewHolder, groupPosition: Int) {
        val entity: OpenTestTypeBean = mGroups!![groupPosition]
        holder.setText(R.id.tv_header, entity.header)
    }

    override fun onBindFooterViewHolder(holder: BaseViewHolder, groupPosition: Int) {
        val entity: OpenTestTypeBean = mGroups!![groupPosition]
        holder.setText(R.id.tv_footer, entity.footer)
    }

    override fun onBindChildViewHolder(
        holder: BaseViewHolder,
        groupPosition: Int,
        childPosition: Int
    ) {
        val entity: AriticleResponse = mGroups!![groupPosition].children.get(childPosition)
        holder.setText(R.id.name, entity.author)
    }

    init {
        mGroups = groups
    }
}
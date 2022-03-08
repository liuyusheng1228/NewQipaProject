package com.qipa.newboxproject.ui.adapter

import android.text.TextUtils
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.app.ext.setAdapterAnimation
import com.qipa.newboxproject.app.util.SettingUtil
import com.qipa.newboxproject.app.weight.FlowLayoutManager
import com.qipa.newboxproject.data.model.bean.AriticleResponse
import com.qipa.newboxproject.data.model.bean.TagBean

class MyGameShowDetailAdapter (data: MutableList<AriticleResponse>): BaseDelegateMultiAdapter<AriticleResponse, BaseViewHolder>(data) {
    private val Playing = 1//正在玩
    private val Concerned = 2//已关注
    private val Subscribe = 3//已预约
    private val flowAdapter : FlowAdapter  by lazy { FlowAdapter(arrayListOf()) }
    private var mStrings: ArrayList<TagBean> = ArrayList()
    init {
        setAdapterAnimation(SettingUtil.getListMode())
//       第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<AriticleResponse>() {
            override fun getItemType(data: List<AriticleResponse>, position: Int): Int {
                //根据是否有图片 判断为文章还是项目，好像有点low的感觉。。。我看实体类好像没有相关的字段，就用了这个，也有可能是我没发现
                return if (TextUtils.isEmpty(data[position].envelopePic)) Playing else Concerned
            }
        })
        // 第二步，绑定 item 类型
        getMultiTypeDelegate()?.let {
            it.addItemType(Playing, R.layout.item_playing_game)
            it.addItemType(Concerned, R.layout.item_game_list_no_video)
            it.addItemType(Subscribe, R.layout.item_game_list_no_video)
        }
    }


    override fun convert(holder: BaseViewHolder, item: AriticleResponse) {
        when (holder.itemViewType) {
            Playing -> {

            }
            Concerned -> {
                holder.getView<RecyclerView>(R.id.labels).init(FlowLayoutManager(),flowAdapter)
                mStrings.clear()
                mStrings.add(TagBean("充值1:5000",1))
                mStrings.add(TagBean("正版IP",2))
                mStrings.add(TagBean("充值返利",3))
                flowAdapter.setList(mStrings)
                flowAdapter.notifyDataSetChanged()
            }
        }
    }

}
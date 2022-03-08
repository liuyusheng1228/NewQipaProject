package com.qipa.newboxproject.ui.adapter

import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.ext.setAdapterAnimation
import com.qipa.newboxproject.app.util.SettingUtil
import com.qipa.newboxproject.app.weight.customview.CollectView
import com.qipa.newboxproject.data.model.bean.AriticleResponse
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.app.weight.FlowLayoutManager
import com.qipa.newboxproject.data.model.bean.TagBean


class AriticleAdapter(data: MutableList<AriticleResponse>?) :
    BaseDelegateMultiAdapter<AriticleResponse, BaseViewHolder>(data) {
    private val Ariticle = 1//文章类型
    private val Project = 2//项目类型 本来打算不区分文章和项目布局用统一布局的，但是布局完以后发现差异化蛮大的，所以还是分开吧
    private var showTag = false//是否展示标签 tag 一般主页才用的到
    private val flowAdapter : FlowAdapter  by lazy { FlowAdapter(arrayListOf()) }
    private var mStrings: ArrayList<TagBean> = ArrayList()
    private var collectAction: (item: AriticleResponse, v: CollectView, position: Int) -> Unit =
        { _: AriticleResponse, _: CollectView, _: Int -> }

    constructor(data: MutableList<AriticleResponse>?, showTag: Boolean) : this(data) {
        this.showTag = showTag
    }

    init {
        setAdapterAnimation(SettingUtil.getListMode())
//       第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<AriticleResponse>() {
            override fun getItemType(data: List<AriticleResponse>, position: Int): Int {
                //根据是否有图片 判断为文章还是项目，好像有点low的感觉。。。我看实体类好像没有相关的字段，就用了这个，也有可能是我没发现
                return if (TextUtils.isEmpty(data[position].envelopePic)) Ariticle else Project
            }
        })
        // 第二步，绑定 item 类型
        getMultiTypeDelegate()?.let {
            it.addItemType(Ariticle, R.layout.item_game_list_title)
//            it.addItemType(Project, R.layout.item_project)
        }
    }

    override fun convert(helper: BaseViewHolder, item: AriticleResponse) {
        when (helper.itemViewType) {
            Ariticle -> {
                //文章布局的赋值
                item.run {
//                    helper.setText(
//                        R.id.name,
//                        if (author.isNotEmpty()) author else shareUser
//                    )
//                    helper.setText(R.id.score, title.toHtml())
//                    helper.setText(R.id.types, "$superChapterName·$chapterName".toHtml())
                    helper.getView<RecyclerView>(R.id.labels).init(FlowLayoutManager(),flowAdapter)
                    mStrings.clear()
                    mStrings.add(TagBean("充值1:5000",1))
                    mStrings.add(TagBean("正版IP",2))
                    mStrings.add(TagBean("充值返利",3))
                    flowAdapter.setList(mStrings)
                    flowAdapter.notifyDataSetChanged()
                    if(item.gameTitle != null){
                        if(item.gameTitle.length>0){
                            helper.getView<LinearLayout>(R.id.game_top_lin).visibility = View.VISIBLE
                            helper.setText(R.id.tv_show_game_title,""+item.gameTitle)
                            helper.setText(R.id.tv_show_game_des,""+item.gameTitledes)
                        }else{
                            helper.getView<LinearLayout>(R.id.game_top_lin).visibility = View.GONE
                        }
                    }


//                    helper.setText(R.id.item_home_date, niceDate)
//                    helper.getView<CollectView>(R.id.item_home_collect).isChecked = collect
//                    if (showTag) {
//                        //展示标签
//                        helper.setGone(R.id.item_home_new, !fresh)
//                        helper.setGone(R.id.item_home_top, type != 1)
//                        if (tags.isNotEmpty()) {
//                            helper.setGone(R.id.item_home_type1, false)
//                            helper.setText(R.id.item_home_type1, tags[0].name)
//                        } else {
//                            helper.setGone(R.id.item_home_type1, true)
//                        }
//                    } else {
//                        //隐藏所有标签
//                        helper.setGone(R.id.item_home_top, true)
//                        helper.setGone(R.id.item_home_type1, true)
//                        helper.setGone(R.id.item_home_new, true)
//                    }
//                }
//                helper.getView<CollectView>(R.id.item_home_collect)
//                    .setOnCollectViewClickListener(object : CollectView.OnCollectViewClickListener {
//                        override fun onClick(v: CollectView) {
//                            collectAction.invoke(item, v, helper.adapterPosition)
//                        }
//                    })
            }
//            Project -> {
//                //项目布局的赋值
//                item.run {
//                    helper.setText(
//                        R.id.item_project_author,
//                        if (author.isNotEmpty()) author else shareUser
//                    )
//                    helper.setText(R.id.item_project_title, title.toHtml())
//                    helper.setText(R.id.item_project_content, desc.toHtml())
//                    helper.setText(
//                        R.id.item_project_type,
//                        "$superChapterName·$chapterName".toHtml()
//                    )
//                    helper.setText(R.id.item_project_date, niceDate)
//                    if (showTag) {
//                        //展示标签
//                        helper.setGone(R.id.item_project_new, !fresh)
//                        helper.setGone(R.id.item_project_top, type != 1)
//                        if (tags.isNotEmpty()) {
//                            helper.setGone(R.id.item_project_type1, false)
//                            helper.setText(R.id.item_project_type1, tags[0].name)
//                        } else {
//                            helper.setGone(R.id.item_project_type1, true)
//                        }
//                    } else {
//                        //隐藏所有标签
//                        helper.setGone(R.id.item_project_top, true)
//                        helper.setGone(R.id.item_project_type1, true)
//                        helper.setGone(R.id.item_project_new, true)
//                    }
//                    helper.getView<CollectView>(R.id.item_project_collect).isChecked = collect
//                    Glide.with(context).load(envelopePic)
//                        .transition(DrawableTransitionOptions.withCrossFade(500))
//                        .into(helper.getView(R.id.item_project_imageview))
//                }
//                helper.getView<CollectView>(R.id.item_project_collect)
//                    .setOnCollectViewClickListener(object : CollectView.OnCollectViewClickListener {
//                        override fun onClick(v: CollectView) {
//                            collectAction.invoke(item, v, helper.adapterPosition)
//                        }
//                    })
            }
        }
    }

    fun setCollectClick(inputCollectAction: (item: AriticleResponse, v: CollectView, position: Int) -> Unit) {
        this.collectAction = inputCollectAction
    }

}



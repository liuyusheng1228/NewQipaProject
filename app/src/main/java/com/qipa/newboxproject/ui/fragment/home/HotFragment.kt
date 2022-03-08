package com.qipa.newboxproject.ui.fragment.home
import com.qipa.newboxproject.R
import android.os.Bundle

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.kingja.loadsir.core.LoadService
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.navigateAction
import com.qipa.jetpackmvvm.ext.parseState
import com.qipa.newboxproject.app.appViewModel
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.*
import com.qipa.newboxproject.app.weight.banner.HotBannerAdapter
import com.qipa.newboxproject.app.weight.banner.HotBannerViewHolder
import com.qipa.newboxproject.app.weight.recyclerview.DefineLoadMoreView
import com.qipa.newboxproject.app.weight.recyclerview.SpaceItemDecoration
import com.qipa.newboxproject.app.weight.transformerslayout.TransformersLayout
import com.qipa.newboxproject.app.weight.transformerslayout.holder.Holder
import com.qipa.newboxproject.app.weight.transformerslayout.holder.TransformersHolderCreator
import com.qipa.newboxproject.app.weight.transformerslayout.listener.OnTransformersItemClickListener
import com.qipa.newboxproject.data.model.bean.AriticleResponse
import com.qipa.newboxproject.data.model.bean.BannerResponse
import com.qipa.newboxproject.databinding.FragmentHotBinding
import com.qipa.newboxproject.ui.adapter.AriticleAdapter
import com.qipa.newboxproject.viewmodel.request.RequestHotViewModel
import com.qipa.newboxproject.viewmodel.state.HotModel
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import com.zhpan.bannerview.BannerViewPager
import kotlinx.android.synthetic.main.include_list.*
import kotlinx.android.synthetic.main.include_recyclerview.*
import com.qipa.newboxproject.data.model.bean.Nav
import com.qipa.newboxproject.data.model.bean.NavAdapterViewHolder
import kotlinx.android.synthetic.main.fragment_hot.*

class HotFragment : BaseFragment<HotModel, FragmentHotBinding>() {
    //适配器
    private val articleAdapter: AriticleAdapter by lazy { AriticleAdapter(arrayListOf(), true) }

    private val requestHotViewModel : RequestHotViewModel by viewModels()
    //界面状态管理者
    private lateinit var loadsir: LoadService<Any>

    private val mListAriticleResponse: ArrayList<AriticleResponse> = arrayListOf()

    private var ariticleResponse : AriticleResponse? = null

    //recyclerview的底部加载view 因为在首页要动态改变他的颜色，所以加了他这个字段
    private lateinit var footView: DefineLoadMoreView
    override fun layoutId() = R.layout.fragment_hot
    companion object {
        fun newInstance( isNew: Boolean): HotFragment {
            val args = Bundle()
            args.putBoolean("isNew", isNew)
            val fragment = HotFragment()
            fragment.arguments = args
            return fragment
        }
    }
    override fun initView(savedInstanceState: Bundle?) {
        loadsir = loadServiceInit(swipeRefreshs){
            loadsir.showLoading()
            requestHotViewModel.getBannerData()

        }

        val header: TransformersLayout<Nav> = tab_show_up as TransformersLayout<Nav>
        header.apply(null).addOnTransformersItemClickListener(object :
            OnTransformersItemClickListener {
            override fun onItemClick(position: Int) {
                TODO("Not yet implemented")
            }

        }).load(loadData().toMutableList(), object : TransformersHolderCreator<Nav> {

            override val layoutId = R.layout.item_nav_list


            override fun createHolder(itemView: View): Holder<Nav> {
                return NavAdapterViewHolder(itemView) as Holder<Nav>
            }


        })

        hot_recyclerView.init(LinearLayoutManager(context),articleAdapter).let {
            //因为首页要添加轮播图，所以我设置了firstNeedTop字段为false,即第一条数据不需要设置间距
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f), false))
            footView =it.initFooter(SwipeRecyclerView.LoadMoreListener {
//                requestHomeViewModel.getHomeData(false)//获取数据
            })
            it.initFloatBtn(hot_floatbtn)
        }
        loadData()
        swipeRefreshs.init {
            swipeRefreshs.isRefreshing = false
            //触发刷新监听时请求数据
//            requestHomeViewModel.getHomeData(true)
        }

        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()

    }
    fun loadData(): MutableList<Nav> {
        for (index in 1..10){
            if(index == 1){
                ariticleResponse = AriticleResponse("","明天试试"+index,0,"",
                    false,0,"","",false,0,"","","","","",
                    0,0,"","",null,"",0,0,0,0,"精品推荐","小编用心整理的超级福利")
            }else{
                ariticleResponse = AriticleResponse("","明天试试"+index,0,"",
                    false,0,"","",false,0,"","","","","",
                    0,0,"","",null,"",0,0,0,0,"","")
            }

            ariticleResponse?.let { mListAriticleResponse.add(it) }
        }
        for (index in 1..10){
            if(index == 1){
                ariticleResponse = AriticleResponse("","明天试试"+index,0,"",
                    false,0,"","",false,0,"","","","","",
                    0,0,"","",null,"",0,0,0,0,"本周热门","我们都在玩")
            }else{
                ariticleResponse = AriticleResponse("","明天试试"+index,0,"",
                    false,0,"","",false,0,"","","","","",
                    0,0,"","",null,"",0,0,0,0,"","")
            }

            ariticleResponse?.let { mListAriticleResponse.add(it) }
        }
        articleAdapter.setList(mListAriticleResponse)
        articleAdapter.notifyDataSetChanged()
        val navs: MutableList<Nav> = ArrayList()
        navs.add(
            Nav(
                R.mipmap.ic_launcher,
                "流量",
                "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172629_TK5jX.png"
            )
        )
        navs.add(
            Nav(
                R.mipmap.ic_launcher,
                "分账",
                "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172629_AhKVK.png"
            )
        )
        navs.add(
            Nav(
                R.mipmap.ic_launcher,
                "绑定",
                "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172629_kE4jY.png"
            )
        )
        navs.add(
            Nav(
                R.mipmap.ic_launcher,
                "会员",
                "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172705_2yXk8.png"
            )
        )
        navs.add(
            Nav(
                R.mipmap.ic_launcher,
                "消息",
                "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172705_irvud.png"
            )
        )
        navs.add(
            Nav(
                R.mipmap.ic_launcher,
                "物料",
                "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172629_PcmPU.png"
            )
        )
        navs.add(
            Nav(
                R.mipmap.ic_launcher,
                "营收",
                "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172629_NeFKy.png"
            )
        )
        navs.add(
            Nav(
                R.mipmap.ic_launcher,
                "收藏",
                "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172629_yUFai.png"
            )
        )
        navs.add(
            Nav(
                R.mipmap.ic_launcher,
                "商品",
                "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172629_KahtV.png"
            )
        )
        navs.add(
            Nav(
                R.mipmap.ic_launcher,
                "分组",
                "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172705_vuZsr.png"
            )
        )
        navs.add(
            Nav(
                R.mipmap.ic_launcher,
                "设备统计",
                "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172629_t4sRT.png"
            )
        )
        navs.add(
            Nav(
                R.mipmap.ic_launcher,
                "会员卡",
                "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172730_j2td3.png"
            )
        )
        navs.add(
            Nav(
                R.mipmap.ic_launcher,
                "积分",
                "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172730_rArtS.png"
            )
        )
        navs.add(
            Nav(
                R.mipmap.ic_launcher,
                "用户",
                "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172705_LsMFQ.png"
            )
        )
        navs.add(
            Nav(
                R.mipmap.ic_launcher,
                "设备管理",
                "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172629_UcuEH.png"
            )
        )
        navs.add(
            Nav(
                R.mipmap.ic_launcher,
                "装修",
                "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172705_fleMs.png"
            )
        )
        navs.add(
            Nav(
                R.mipmap.ic_launcher,
                "订单",
                "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172705_zzyP2.png"
            )
        )
        navs.add(
            Nav(
                R.mipmap.ic_launcher,
                "客服",
                "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172705_Wuun5.png"
            )
        )
        navs.add(
            Nav(
                R.mipmap.ic_launcher,
                "商城",
                "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172730_VnmYC.png"
            )
        )
        //        navs.add(new Nav(R.mipmap.ic_nav_stock, "库存", "https://c-ssl.duitang.com/uploads/item/201911/25/20191125172730_iSTcz.png"));
        return navs
    }
    override fun lazyLoadData() {
        super.lazyLoadData()
        //设置界面 加载中
        loadsir.showLoading()
        //请求轮播图数据
        requestHotViewModel.getBannerData()
        //请求列表数据
//        requestHomeViewModel.getHomeData(true)

    }

    override fun setVisibleToUser() {
        super.setVisibleToUser()
    }

    override fun createObserver() {
        super.createObserver()
        requestHotViewModel.run {
            bannerData.observe(viewLifecycleOwner, Observer { resultState ->
                parseState(resultState,{data ->
                    swipeRefreshs.isRefreshing = false
                    if(hot_recyclerView.headerCount == 0){
                        hot_banner.apply {
                            findViewById<BannerViewPager<BannerResponse,HotBannerViewHolder>>(R.id.banner_view).apply {
                                adapter = HotBannerAdapter()
                                setLifecycleRegistry(lifecycle)
                                setOnPageClickListener {
                                    nav().navigateAction(R.id.action_to_gameDetailFragment,Bundle().apply { putParcelable("bannerdata", data[it]) })
                                }
                                create(data)
                                loadsir.showSuccess()
                            }

                        }
//                        recyclerView.addHeaderView(headview)
//                        recyclerView.scrollToPosition(0)
                    }


                })

            })
        }

        appViewModel.run {
            appAnimation.observeInFragment(this@HotFragment) {
               articleAdapter.setAdapterAnimation(it)
            }
        }

    }

    inner class ProxyClick {

    }

    override fun onBackPressed(): Boolean {
        return false
    }


}

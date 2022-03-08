package com.qipa.newboxproject.ui.fragment.dis

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.kingja.loadsir.core.LoadService
import com.qipa.jetpackmvvm.ext.parseState
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.*
import com.qipa.newboxproject.app.weight.loadCallBack.ErrorCallback
import com.qipa.newboxproject.app.weight.recyclerview.SpaceItemDecoration
import com.qipa.newboxproject.databinding.FragmentConcentrationBinding
import com.qipa.newboxproject.ui.adapter.AriticleAdapter
import com.qipa.newboxproject.ui.adapter.MenuLeftAdapter
import com.qipa.newboxproject.viewmodel.request.RequestSearchViewModel
import com.qipa.newboxproject.viewmodel.state.ConcenTrationModel
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import kotlinx.android.synthetic.main.fragment_concentration.*
import kotlinx.android.synthetic.main.include_list.*
import kotlinx.android.synthetic.main.include_recyclerview.*

class ConcenTrationFragment : BaseFragment<ConcenTrationModel,FragmentConcentrationBinding>() {
    private val menuLeftAdapter : MenuLeftAdapter by lazy { MenuLeftAdapter(arrayListOf()) }
    private var mDataMenuString : ArrayList<String> = arrayListOf()


    //适配器
    private val articleAdapter: AriticleAdapter by lazy { AriticleAdapter(arrayListOf(), true) }

    private val requestSearchViewModel : RequestSearchViewModel by viewModels()

    private lateinit var loadsir : LoadService<Any>

    override fun layoutId() = R.layout.fragment_concentration

    override fun initView(savedInstanceState: Bundle?) {
        loadsir = loadServiceInit(swipeRefresh){
            loadsir.showLoading()
            requestSearchViewModel.getSearchResultData("面试",true)
        }

        recyclerView.init(LinearLayoutManager(context),articleAdapter).let {
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f)))
            it.initFooter(SwipeRecyclerView.LoadMoreListener {
                requestSearchViewModel.getSearchResultData("面试",true)
            })
            it.initFloatBtn(floatbtn)
        }

        swipeRefresh.init {
            requestSearchViewModel.getSearchResultData("面试",true)
        }

        menu_list_recyclerView.init(LinearLayoutManager(context),menuLeftAdapter)
        for(index in 1..10){
            mDataMenuString.add("新游"+index)
        }
        menuLeftAdapter.setList(mDataMenuString)
        menuLeftAdapter.notifyDataSetChanged()


        menuLeftAdapter.setOnItemClickListener { adapter, view, position ->

            menuLeftAdapter.setClickPosition(position)
            adapter.notifyDataSetChanged()

        }

    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        loadsir.showLoading()
        requestSearchViewModel.getSearchResultData("面试",true)
    }
    companion object {
        fun newInstance( isNew: Boolean): ConcenTrationFragment {
            val args = Bundle()
            args.putBoolean("isNew", isNew)
            val fragment = ConcenTrationFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun createObserver() {
        super.createObserver()
        requestSearchViewModel.seachResultData.observe(viewLifecycleOwner, Observer { resultState ->
            parseState(resultState,{
                swipeRefresh.isRefreshing = false
                requestSearchViewModel.pageNo ++
                if(it.isRefresh() && it.datas.size == 0){
                    loadsir.showEmpty()
                }else if(it.isRefresh()){
                    loadsir.showSuccess()
                    articleAdapter.setList(it.datas)
                }else{
                    loadsir.showSuccess()
                    articleAdapter.addData(it.datas)
                }
                recyclerView.loadMoreFinish(it.isEmpty(),it.hasMore())
            },{
                swipeRefresh.isRefreshing = true
                if(articleAdapter.data.size == 0){
                    loadsir.showError(it.errorMsg)
                    loadsir.showCallback(ErrorCallback ::class.java)
                }else{
                    recyclerView.loadMoreError(0,it.errorMsg)
                }
            })
        })
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}
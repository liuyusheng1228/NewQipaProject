package com.qipa.newboxproject.ui.fragment.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.navigateAction
import com.qipa.jetpackmvvm.ext.parseState
import com.qipa.jetpackmvvm.ext.util.toJson
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.app.util.CacheUtil
import com.qipa.newboxproject.app.util.SettingUtil
import com.qipa.newboxproject.databinding.FragmentSearchBinding
import com.qipa.newboxproject.ui.adapter.SearcHistoryAdapter
import com.qipa.newboxproject.ui.adapter.SearcHotAdapter
import com.qipa.newboxproject.viewmodel.request.RequestSearchViewModel
import com.qipa.newboxproject.viewmodel.state.SearchModel
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.include_recyclerview.*


class SearchFragment : BaseFragment<SearchModel, FragmentSearchBinding>() {
    private val historyAdapter: SearcHistoryAdapter by lazy { SearcHistoryAdapter(arrayListOf()) }

    private val hotAdapter: SearcHotAdapter by lazy { SearcHotAdapter(arrayListOf()) }

    private val requestSearchViewModel : RequestSearchViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_search

    override fun initView(savedInstanceState: Bundle?) {

        //初始化热门Recyclerview
        val layoutManager = FlexboxLayoutManager(context)
        //方向 主轴为水平方向，起点在左端
        layoutManager.flexDirection = FlexDirection.ROW
        //左对齐
        layoutManager.justifyContent = JustifyContent.FLEX_START
        search_hotRv.init(LinearLayoutManager(context), hotAdapter, false)
        search_historyRv.init(layoutManager,hotAdapter,false)
        hotAdapter.run {
            setOnItemClickListener { adapter, view, position ->
                val queryStr = hotAdapter.data.get(position).name
                updateKey(queryStr)
                nav().navigateAction(R.id.action_searchFragment_to_searchResultFragment,
                    Bundle().apply {
                        putString("searchKey", queryStr)
                    }
                )

            }
        }

        historyAdapter.run {
            setOnItemClickListener { adapter, view, position ->
                val queryStr = historyAdapter.data[position]
                updateKey(queryStr)
                nav().navigateAction(R.id.action_searchFragment_to_searchResultFragment,
                    Bundle().apply {
                        putString("searchKey",queryStr)
                    }
                )
            }
            addChildClickViewIds(R.id.item_history_del)
            setOnItemChildClickListener { adapter, view, position ->
                when(view.id){
                    R.id.item_history_del->{
                        requestSearchViewModel.historyData.value?.let {
                            it.removeAt(position)
                            requestSearchViewModel.historyData.value = it
                        }
                    }


                }

            }
        }

        search_clear.setOnClickListener {
            activity?.let {
                MaterialDialog(it)
                    .cancelable(false)
                    .lifecycleOwner(this)
                    .show {
                        title(text = "温馨提示")
                        message(text = "确定清空吗?")
                        negativeButton(text = "取消")
                        positiveButton(text = "清空") {
                            //清空
                            requestSearchViewModel.historyData.value = arrayListOf()
                        }
                        getActionButton(WhichButton.POSITIVE).updateTextColor(
                            SettingUtil.getColor(
                                it
                            )
                        )
                        getActionButton(WhichButton.NEGATIVE).updateTextColor(
                            SettingUtil.getColor(
                                it
                            )
                        )
                    }
            }
        }
        search_editText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length!! > 0){
                    val queryStr = p0.toString()
                    updateKey(queryStr)
                    nav().navigateAction(R.id.action_searchFragment_to_searchResultFragment,
                        Bundle().apply {
                            putString("searchKey", queryStr)
                        }
                    )
                }
            }
        })


        search_cancel.setOnClickListener {
            nav().navigateUp()
        }
    }

    override fun createObserver() {
        super.createObserver()

        requestSearchViewModel.run {
            hotData.observe(viewLifecycleOwner, Observer { resultState ->
                parseState(resultState,{
                      hotAdapter.setList(it)
                })

            })

            historyData.observe(viewLifecycleOwner, Observer {
                historyAdapter.data = it
                historyAdapter.notifyDataSetChanged()
                CacheUtil.setSearchHistoryData(it.toJson())
            })
        }
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        requestSearchViewModel.getHistoryData()
        requestSearchViewModel.getHotData()
    }

    /**
     * 更新搜索词
     */
    fun updateKey(keyStr: String) {
        requestSearchViewModel.historyData.value?.let {
            if (it.contains(keyStr)) {
                //当搜索历史中包含该数据时 删除
                it.remove(keyStr)
            } else if (it.size >= 10) {
                //如果集合的size 有10个以上了，删除最后一个
                it.removeAt(it.size - 1)
            }
            //添加新数据到第一条
            it.add(0, keyStr)
            requestSearchViewModel.historyData.value = it
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }


}
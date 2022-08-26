package com.qipa.newboxproject.ui.fragment.detail

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.app.weight.recyclerview.SpaceItemDecoration
import com.qipa.newboxproject.data.model.bean.DetailPicVedioBean
import com.qipa.newboxproject.databinding.FragmentDetailsBinding
import com.qipa.newboxproject.ui.adapter.DetailPicVedioAdapter
import com.qipa.newboxproject.viewmodel.request.RequestDetailViewModel
import com.qipa.newboxproject.viewmodel.state.DetailModel
import kotlinx.android.synthetic.main.fragment_details.*

class DetailsFragment : BaseFragment<DetailModel,FragmentDetailsBinding>() {

    private val detailPicVedioAdapter : DetailPicVedioAdapter  by lazy { DetailPicVedioAdapter(
        arrayListOf()) }

    private val requestDetailViewModel : RequestDetailViewModel by viewModels()

    private val mListPic : MutableList<DetailPicVedioBean> = arrayListOf()

    override fun layoutId() = R.layout.fragment_details
    companion object {
        fun newInstance( isNew: Boolean): DetailsFragment {
            val args = Bundle()
            args.putBoolean("isNew", isNew)
            val fragment = DetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }
    override fun initView(savedInstanceState: Bundle?) {
        addLoadingObserve(requestDetailViewModel)
        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        list_pic_vedios.init(linearLayoutManager,detailPicVedioAdapter).let {
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f), false))
        }
        list_pic_vedios.isNestedScrollingEnabled = false
        loadData()
    }

    fun loadData(){
        mListPic.add(DetailPicVedioBean("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",1))
        mListPic.add(DetailPicVedioBean("https://static.runoob.com/images/demo/demo2.jpg",2))
        mListPic.add(DetailPicVedioBean("https://static.runoob.com/images/demo/demo2.jpg",2))
        mListPic.add(DetailPicVedioBean("https://static.runoob.com/images/demo/demo2.jpg",2))
        mListPic.add(DetailPicVedioBean("https://static.runoob.com/images/demo/demo2.jpg",2))
        mListPic.add(DetailPicVedioBean("https://static.runoob.com/images/demo/demo2.jpg",3))
        mListPic.add(DetailPicVedioBean("https://static.runoob.com/images/demo/demo2.jpg",3))
        detailPicVedioAdapter.setList(mListPic)
        detailPicVedioAdapter.notifyDataSetChanged()
        tv_game_introduce_msg.setText("fdsf范德萨发顺丰公司规定是大法官会返回将后宫加速暗色" +
                "无群在操场撒大声地fdsf范德萨发顺丰公司规定是大法官会返回将后宫加速暗色无群在" +
                "操场撒大声地fdsf范德萨发顺丰公司规定是大法官会返回将后宫加速暗色无群在操场撒大声" +
                "地fdsf范德萨发顺丰公司规定是大法官会返回将后宫加速暗色无群在操场撒大声地fdsf范德萨发顺" +
                "丰公司规定是大法官会返回将后宫加速暗色无群在操场撒大声地fdsf范德萨发顺丰公司规定是大法官会返回将" +
                "后宫加速暗色无群在操场撒大声地fdsf范德萨发顺丰公司规定是大法官会返回将后宫加速暗色无群在操场撒大声地" +
                "fdsf范德萨发顺丰公司规定是大法官会返回将后宫加速暗色无群在操场撒大声地fdsf范德萨发顺丰公司规定是大法" +
                "官会返回将后宫加速暗色无群在操场撒大声地fdsf范德萨发顺丰公司规定是大法官会返回将后宫加速暗色无群在操场撒大声地")
    }

    inner class ProxyClick{}

    override fun onBackPressed(): Boolean {
        return false
    }

}
package com.qipa.newboxproject.ui.fragment.download

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.qipa.jetpackmvvm.ext.download.DownloadResultState
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.util.logd
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.app.ext.showMessage
import com.qipa.newboxproject.app.weight.recyclerview.SpaceItemDecoration
import com.qipa.newboxproject.databinding.FragmentDownloadBinding
import com.qipa.newboxproject.ui.adapter.DownloadListAdapter
import com.qipa.newboxproject.viewmodel.state.DownloadModel
import kotlinx.android.synthetic.main.include_back.*
import kotlinx.android.synthetic.main.include_recyclerview.*

class DownLoadFragment : BaseFragment<DownloadModel,FragmentDownloadBinding>() {
    override fun layoutId() = R.layout.fragment_download
    private val downloadListAdapter : DownloadListAdapter by lazy { DownloadListAdapter(mDataListDownload) }
    private var mDataListDownload : ArrayList<String> = arrayListOf()

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        mDatabind.click = ProClick()
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }
        toolbar_titletv.text = getString(R.string.download_list)

        recyclerView.init(LinearLayoutManager(mActivity),downloadListAdapter).let {
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f), false))
        }
        for (index in 1..3){
            mDataListDownload.add("index"+index)
        }
        downloadListAdapter.setList(mDataListDownload)
        downloadListAdapter.notifyDataSetChanged()

    }

    override fun createObserver() {
        downloadListAdapter.run {
            addChildClickViewIds(R.id.my_game_download_status)
            setOnItemChildClickListener { adapter, view, position ->
                when (view.id) {
                    R.id.my_game_download_status -> {
                       //???????????????
                    }
                }
            }
        }

        mViewModel.downloadData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DownloadResultState.Pending -> {
                    //????????????
                    "????????????".logd()
                }
                is DownloadResultState.Progress -> {
                    //?????????
//                    downloadProgressBar.progress = it.progress
                    "????????? ${it.soFarBytes}/${it.totalBytes}".logd()
//                    downloadProgress.text = "${it.progress}%"
//                    downloadSize.text ="${FileTool.bytes2kb(it.soFarBytes)}/${FileTool.bytes2kb(it.totalBytes)}"
                }
                is DownloadResultState.Success -> {
                    //????????????
//                    downloadProgressBar.progress = 100
//                    downloadProgress.text = "100%"
//                    downloadSize.text ="${FileTool.bytes2kb(it.totalBytes)}/${FileTool.bytes2kb(it.totalBytes)}"
                    showMessage("????????????--???????????????${it.filePath}")
                }
                is DownloadResultState.Pause -> {
                    showMessage("????????????")
                }
                is DownloadResultState.Error -> {
                    //????????????
                    showMessage("????????????:${it.errorMsg}")
                }
            }
        })
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    inner class ProClick{

    }
}
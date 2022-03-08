package com.qipa.newboxproject.ui.fragment.dis

import android.os.Bundle
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.databinding.FragmentOpenServiceBinding
import com.qipa.newboxproject.viewmodel.state.OpenServiceModel
import kotlinx.android.synthetic.main.fragment_open_service.*
import kotlinx.android.synthetic.main.fragment_ranking_list.*

class OpenServiceFragment : BaseFragment<OpenServiceModel,FragmentOpenServiceBinding>() {
    override fun layoutId() = R.layout.fragment_open_service

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = ProxyClick()
        selecrYesterday()

    }
    companion object {
        fun newInstance( isNew: Boolean): OpenServiceFragment {
            val args = Bundle()
            args.putBoolean("isNew", isNew)
            val fragment = OpenServiceFragment()
            fragment.arguments = args
            return fragment
        }
    }
    inner class ProxyClick{

        fun yesterday(){
            selecrYesterday()
        }

        fun today(){
            selecrToday()
        }

        fun tomorrow(){
            selecrTomorrow()
        }

    }
    fun selecrToday(){
        tv_yesterday.setBackgroundResource(R.drawable.bg_ranking_top_tab_unselected)
        tv_today.setBackgroundResource(R.drawable.bg_ranking_top_tab_selected)
        tv_tomorrow.setBackgroundResource(R.drawable.bg_ranking_top_tab_unselected)
        tv_today.setTextColor(resources.getColor(R.color.white))
        tv_yesterday.setTextColor(resources.getColor(R.color.black))
        tv_tomorrow.setTextColor(resources.getColor(R.color.black))
    }
    fun selecrYesterday(){
        tv_yesterday.setBackgroundResource(R.drawable.bg_ranking_top_tab_selected)
        tv_today.setBackgroundResource(R.drawable.bg_ranking_top_tab_unselected)
        tv_tomorrow.setBackgroundResource(R.drawable.bg_ranking_top_tab_unselected)
        tv_today.setTextColor(resources.getColor(R.color.black))
        tv_yesterday.setTextColor(resources.getColor(R.color.white))
        tv_tomorrow.setTextColor(resources.getColor(R.color.black))
    }

    fun selecrTomorrow(){
        tv_yesterday.setBackgroundResource(R.drawable.bg_ranking_top_tab_unselected)
        tv_today.setBackgroundResource(R.drawable.bg_ranking_top_tab_unselected)
        tv_tomorrow.setBackgroundResource(R.drawable.bg_ranking_top_tab_selected)
        tv_today.setTextColor(resources.getColor(R.color.black))
        tv_yesterday.setTextColor(resources.getColor(R.color.black))
        tv_tomorrow.setTextColor(resources.getColor(R.color.white))
    }

    override fun onBackPressed(): Boolean {
        return false
    }

}
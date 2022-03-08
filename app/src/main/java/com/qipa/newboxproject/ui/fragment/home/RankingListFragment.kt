package com.qipa.newboxproject.ui.fragment.home

import android.os.Bundle
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.databinding.FragmentRankingListBinding
import com.qipa.newboxproject.viewmodel.state.RankingListModel
import kotlinx.android.synthetic.main.fragment_ranking_list.*

class RankingListFragment : BaseFragment<RankingListModel,FragmentRankingListBinding>() {
    companion object {
        fun newInstance( isNew: Boolean): RankingListFragment {
            val args = Bundle()
            args.putBoolean("isNew", isNew)
            val fragment = RankingListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun layoutId() = R.layout.fragment_ranking_list

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = ProxyClick()
        selecrNormal()
    }

    inner class ProxyClick{

        fun play_list(){
            selecrNormal()
        }

        fun tour_weekly_list(){
            selecrUnNormal()
        }
    }

    fun selecrNormal(){
        tv_new_tour_weekly_list.setBackgroundResource(R.drawable.bg_ranking_top_tab_unselected)
        tv_must_play_list.setBackgroundResource(R.drawable.bg_ranking_top_tab_selected)
        tv_must_play_list.setTextColor(resources.getColor(R.color.white))
        tv_new_tour_weekly_list.setTextColor(resources.getColor(R.color.black))
    }
    fun selecrUnNormal(){
        tv_new_tour_weekly_list.setBackgroundResource(R.drawable.bg_ranking_top_tab_selected)
        tv_must_play_list.setBackgroundResource(R.drawable.bg_ranking_top_tab_unselected)
        tv_must_play_list.setTextColor(resources.getColor(R.color.black))
        tv_new_tour_weekly_list.setTextColor(resources.getColor(R.color.white))
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}
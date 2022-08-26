package com.qipa.newboxproject.ui.fragment.message

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ImmersionBar
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.navigateAction
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.data.model.bean.DailyTaskBean
import com.qipa.newboxproject.databinding.FragmentMissionHallBinding
import com.qipa.newboxproject.ui.adapter.DailyTaskListAdapter
import com.qipa.newboxproject.viewmodel.state.MissionHallModel
import kotlinx.android.synthetic.main.fragment_mission_hall.*
import kotlinx.android.synthetic.main.include_back.*

class MissionHallFragment : BaseFragment<MissionHallModel,FragmentMissionHallBinding>(){
    override fun layoutId() = R.layout.fragment_mission_hall
    private val dailyTaskListAdapter : DailyTaskListAdapter by lazy { DailyTaskListAdapter(mDataDailyTask) }
    private var mDataDailyTask : ArrayList<DailyTaskBean> = arrayListOf()

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarColor(R.color.white)
            .init()
        mDatabind.vm = mViewModel
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }
        toolbar_titletv.text = getString(R.string.mission_hall)
        mDatabind.click = ProxyClick()
        rel_detail_back.setOnClickListener {
            ImmersionBar.with(this)
                .statusBarColor(R.color.tranaction)
                .init()
            nav().navigateUp()
        }
        recycler_day_lsit.init(LinearLayoutManager(mActivity),dailyTaskListAdapter)
        for(index in 1..10){
            mDataDailyTask.add(DailyTaskBean("每日签到"+index,""+index,"签到"))
        }
        dailyTaskListAdapter.setList(mDataDailyTask)
        dailyTaskListAdapter.notifyDataSetChanged()
    }

    inner class  ProxyClick {

        fun BtnPointsMall(){
            nav().navigateAction(R.id.action_missionHallFragment_to_integralDetailsFragment)
        }
        fun BtnGotoIntegralDetail(){
            nav().navigateAction(R.id.action_missionHallFragment_to_integralDetailsFragment)
        }

    }

    override fun onBackPressed(): Boolean {
        ImmersionBar.with(this)
            .statusBarColor(R.color.tranaction)
            .init()
        return true
    }


}
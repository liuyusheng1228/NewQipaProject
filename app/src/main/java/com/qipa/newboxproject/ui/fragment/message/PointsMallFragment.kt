package com.qipa.newboxproject.ui.fragment.message

import android.os.Bundle
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.databinding.FragmentPointsMallBinding
import com.qipa.newboxproject.viewmodel.state.PointsMallModel
import kotlinx.android.synthetic.main.include_back.*

class PointsMallFragment : BaseFragment<PointsMallModel , FragmentPointsMallBinding>(){
    override fun layoutId() = R.layout.fragment_points_mall

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        mDatabind.click = ProxyClick()
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }
        toolbar_titletv.text = "积分商城"
    }

    inner class  ProxyClick {
        fun BtnGotoIntegralDetail(){

        }

        fun BtnEarnPoints(){

        }

    }

    override fun onBackPressed(): Boolean {
        return false
    }
}
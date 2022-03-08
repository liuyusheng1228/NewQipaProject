package com.qipa.newboxproject.ui.fragment.pay

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.navigateAction
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.app.weight.dialog.CommonDialog
import com.qipa.newboxproject.databinding.FragmentPlatformCurrencyRechargeBinding
import com.qipa.newboxproject.ui.adapter.PlatformCurrencyListAdapter
import com.qipa.newboxproject.viewmodel.state.PlatformCurrencyRechargeModel
import kotlinx.android.synthetic.main.fragment_platform_currency_recharge.*
import kotlinx.android.synthetic.main.include_back.*

class PlatformCurrencyRechargeFragment : BaseFragment<PlatformCurrencyRechargeModel,FragmentPlatformCurrencyRechargeBinding>() {
    override fun layoutId() = R.layout.fragment_platform_currency_recharge

    private val platformCurrencyListAdapter : PlatformCurrencyListAdapter by lazy { PlatformCurrencyListAdapter(mDataRechargeQuantityList) }

    private var mDataRechargeQuantityList : MutableList<String> = arrayListOf()

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        mDatabind.click = ProClick()
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }
        toolbar_titletv.text = "我的平台币"
        toolbar_user_txt.text = "平台币明细"
        toolbar_user_txt.setOnClickListener {
            nav().navigateAction(R.id.platformCurrencyRechargeFragment_action_to_platformCurrencyDetailsFragment)
        }
        toolbar_user_txt.setTextColor(resources.getColor(R.color.mainColor))
        recler_view_recharge_quantity.init(GridLayoutManager(mActivity, 3),platformCurrencyListAdapter)
        for(index in 1..6){
            mDataRechargeQuantityList.add(""+50*index)
        }
        platformCurrencyListAdapter.setList(mDataRechargeQuantityList)
        platformCurrencyListAdapter.notifyDataSetChanged()
        platformCurrencyListAdapter.setOnItemClickListener { adapter, view, position ->
            platformCurrencyListAdapter.setSelection(position)
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    inner class ProClick{
       fun payQues(){
           val dialog = CommonDialog(context)
           dialog.setMessage("这是一个自定义Dialog。")
               .setNegtive("知道了")
               //                .setTitle("系统提示")
               .setSingle(true).
               setOnClickBottomListener(object : CommonDialog.OnClickBottomListener {

                   override fun onPositiveClick(inputmsg: String) {
                       TODO("Not yet implemented")
                   }

                   override fun onNegtiveClick() {
                       dialog.dismiss()
                   }
               }).show()
       }
    }

}
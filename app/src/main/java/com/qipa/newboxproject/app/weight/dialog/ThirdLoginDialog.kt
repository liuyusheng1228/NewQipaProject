package com.qipa.newboxproject.app.weight.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.util.PlatformMananger
import com.qipa.newboxproject.data.model.bean.ShareListItemInEntity
import com.qipa.newboxproject.ui.adapter.DialogThirdLoginAdapter

class ThirdLoginDialog (context: Context?) : Dialog(context!!, R.style.CustomDialog) {
    private var lists: MutableList<ShareListItemInEntity?> = arrayListOf()
    private var third_login_recyclerview : RecyclerView? = null
    private lateinit var dialogThirdLoginAdapter : DialogThirdLoginAdapter
    var onClickLoginPos: MutableLiveData<String> = MutableLiveData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_third_login)
        initView()
        initData()
    }

    private fun initView(){
        third_login_recyclerview = findViewById(R.id.third_login_recyclerview)
    }

    private fun initData(){
        //纵向线性布局
        val layoutManagers = GridLayoutManager(context,3)
        lists.addAll(PlatformMananger.instance.getList()!!)
        lists.addAll(PlatformMananger.instance.getChinaList()!!)
        dialogThirdLoginAdapter = DialogThirdLoginAdapter(context,lists)
        third_login_recyclerview?.layoutManager = layoutManagers
        third_login_recyclerview?.adapter = dialogThirdLoginAdapter
        dialogThirdLoginAdapter.setonItemClickPosition(object : DialogThirdLoginAdapter.onItemClickPosition{
            override fun OnItemClick(pos: Int) {
                onClickLoginPos.postValue(lists.get(pos)?.name)
            }

        })
        dialogThirdLoginAdapter.notifyDataSetChanged()
    }

}
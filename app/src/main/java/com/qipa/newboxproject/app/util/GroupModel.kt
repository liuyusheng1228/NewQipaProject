package com.qipa.newboxproject.app.util

import com.qipa.newboxproject.data.model.bean.AriticleResponse
import com.qipa.newboxproject.data.model.bean.OpenTestTypeBean

object GroupModel {
    lateinit var ariticleResponse : AriticleResponse
    private val mAriticleResponseList : ArrayList<AriticleResponse> = arrayListOf()
    /**
     * 获取组列表数据
     *
     * @param groupCount    组数量
     * @param childrenCount 每个组里的子项数量
     * @return
     */
    fun getGroups(groupCount: Int, childrenCount: Int): ArrayList<OpenTestTypeBean>? {
        val groups: ArrayList<OpenTestTypeBean> = ArrayList()
        for (i in 0 until groupCount) {
            val children: ArrayList<AriticleResponse> = ArrayList()
            for (j in 0 until childrenCount) {
                ariticleResponse = AriticleResponse("","明天试试"+j,0,"",
                    false,0,"","",false,0,"","","","","",
                    0,0,"","",null,"",0,0,0,0,"","")
                children.add(ariticleResponse)
            }
            groups.add(
                OpenTestTypeBean(
                    "第" + (i + 1) + "组头部",
                    "第" + (i + 1) + "组尾部", children
                )
            )
        }
        return groups
    }
}
package com.qipa.newboxproject.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.network.stateCallback.ListDataUiState
import com.qipa.newboxproject.data.model.bean.AriticleResponse
import com.qipa.newboxproject.data.model.bean.ShareListItemInEntity

class DialogThirdLoginAdapter(context: Context?, allthridpic: List<ShareListItemInEntity?>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mallthridpic : List<ShareListItemInEntity?>
    var onClickLoginPos: MutableLiveData<Int> = MutableLiveData()
    private val mContext : Context?
    private var monItemClickPosition : onItemClickPosition? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View =
            LayoutInflater.from(mContext).inflate(R.layout.item_third_login, parent, false)
        return NormalHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val normalHolder :  NormalHolder = holder as NormalHolder
        normalHolder.iv_thum.visibility = View.VISIBLE
        normalHolder.text_login_name.text = mallthridpic.get(position)?.name
        mallthridpic?.get(position)?.icon?.let { normalHolder.iv_thum.setImageResource(it) }
        normalHolder.iv_thum.setOnClickListener {
            monItemClickPosition?.OnItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return mallthridpic?.size!!
    }

    fun setonItemClickPosition(onItemClickPosition :onItemClickPosition){
        monItemClickPosition = onItemClickPosition
    }

    interface onItemClickPosition{
        fun OnItemClick(pos : Int)
    }

    init {
        mContext = context
        mallthridpic = allthridpic
    }

    class NormalHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_thum: ImageView
        var text_login_name: TextView
        init {
            iv_thum = itemView.findViewById(R.id.iv_login)
            text_login_name = itemView.findViewById(R.id.text_login_name)
            iv_thum.setOnClickListener(View.OnClickListener {

            })
        }
    }
}
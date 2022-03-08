package com.qipa.qipaimbase.chat.emoji

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qipa.qipaimbase.R
import com.qipa.qipaimbase.base.RvBaseFragment
import com.qipa.qipaimbase.chat.emoji.EmojiContainerFragment.OnEmojiClickListener
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.RvBaseAdapter
import com.qipa.qipaimbase.utils.recycleadapter.RvListenerImpl

class EmojiFragment : RvBaseFragment() {
    private var recyclerView: RecyclerView? = null
    private var emojiRecyclerAdapter: EmojiRecyclerAdapter? = null
    private var emojiBeans: List<EmojiBean?>? = null
    private var onEmojiClickListener: OnEmojiClickListener? = null
    private var recycledViewPool: RecyclerView.RecycledViewPool? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_emoji_emoji, null)
        recyclerView = view.findViewById(R.id.recyclerView)
        initRv()
        recyclerView!!.setRecycledViewPool(recycledViewPool)
        return view
    }



    override fun getRecycleView(): RecyclerView? {
        return recyclerView
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager? {
        return GridLayoutManager(this.context, 8)
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration? {
        return null
    }

    override fun getAdapter(): RvBaseAdapter<ItemData> {
        if (emojiRecyclerAdapter == null) {
            emojiRecyclerAdapter = EmojiRecyclerAdapter(emojiBeans)
            emojiRecyclerAdapter?.setRvListener(object : RvListenerImpl() {
                override fun onClick(view: View?, data: ItemData, position: Int) {
                    val bean = data as EmojiBean
                    if (onEmojiClickListener != null) {
                        onEmojiClickListener?.onEmojiClick(bean.emojiContent)
                    }
                }
            })

        }
        return emojiRecyclerAdapter as EmojiRecyclerAdapter
    }


    fun setOnEmojiClickListener(onEmojiClickListener: OnEmojiClickListener?) {
        this.onEmojiClickListener = onEmojiClickListener
    }

    fun setEmojiBeans(emojiBeans: List<EmojiBean?>?) {
        this.emojiBeans = emojiBeans
    }

    fun setRecycledViewPool(recycledViewPool: RecyclerView.RecycledViewPool?) {
        this.recycledViewPool = recycledViewPool
    }
}

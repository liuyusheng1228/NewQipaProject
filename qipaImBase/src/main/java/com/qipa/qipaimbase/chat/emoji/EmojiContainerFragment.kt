package com.qipa.qipaimbase.chat.emoji

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.qipa.qipaimbase.R
import com.qipa.qipaimbase.base.BaseFragment
import com.qipa.qipaimbase.utils.Utils
import java.util.ArrayList

class EmojiContainerFragment : BaseFragment() {
    var viewPager: ViewPager? = null
    private var adapter: EmojiViewFragmentPagerAdapter? = null
    private var onSendListener: OnSendListener? = null
    private var onDelListener: OnDelListener? = null
    private var fragments: MutableList<Fragment>? = null
    private var ivDel : ImageView? = null

    //    private OnEmojiClickListener onEmojiClickListener;
    private var recycledViewPool: RecyclerView.RecycledViewPool? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_emoji, null)
        viewPager = view.findViewById(R.id.viewPager)
        ivDel = view.findViewById(R.id.ivDel)
        initView()
        return view
    }

    private fun initView() {
        recycledViewPool = RecyclerView.RecycledViewPool()
        ivDel?.setOnClickListener {
            onDelClick()
        }
        val allEmoji: List<EmojiJson.EmojiBean> = EmojiUtils.instance?.emojiBeans as List<EmojiJson.EmojiBean>
        fragments = ArrayList()
        val size = allEmoji.size
        val fragmentSize =
            if (size % EMOJI_PAGE_MAX == 0) size / EMOJI_PAGE_MAX else size / EMOJI_PAGE_MAX + 1
        var emojiBeans: MutableList<EmojiBean?>
        var beanTemp: EmojiBean
        var j = 0
        for (i in 0 until fragmentSize) {
            emojiBeans = ArrayList(EMOJI_PAGE_MAX)
            j = i * EMOJI_PAGE_MAX
            while (j < size) {
                beanTemp = EmojiBean()
                beanTemp.emojiContent = allEmoji[j].credentialName
                beanTemp.emojiId = Utils.getDrawableByName(allEmoji[j].resId)!!
                emojiBeans.add(beanTemp)
                if (emojiBeans.size == EMOJI_PAGE_MAX) {
                    break
                }
                j++
            }
            val tempFragment = EmojiFragment()
            tempFragment.setEmojiBeans(emojiBeans)
            //            tempFragment.setOnEmojiClickListener(onEmojiClickListener);
            tempFragment.setRecycledViewPool(recycledViewPool)
            (fragments as ArrayList<Fragment>).add(tempFragment)
        }
        adapter = EmojiViewFragmentPagerAdapter(fragmentManager, fragments)
        viewPager?.adapter = adapter
    }

    fun onDelClick() {
        if (onDelListener != null) {
            onDelListener!!.onDelClick()
        }
    }

//    @OnClick(R2.id.tvEmojiSend)
    fun onSendClick() {
        if (onSendListener != null) {
            onSendListener!!.onEmojiSend()
        }
    }

    interface OnSendListener {
        fun onEmojiSend()
    }

    interface OnDelListener {
        fun onDelClick()
    }

    interface OnEmojiClickListener {
        fun onEmojiClick(content: String?)
    }

    fun setOnSendListener(onSendListener: OnSendListener?) {
        this.onSendListener = onSendListener
    }

    fun setOnDelListener(onDelListener: OnDelListener?) {
        this.onDelListener = onDelListener
    }

    fun setOnEmojiClickListener(onEmojiClickListener: OnEmojiClickListener?) {
        if (fragments != null) {
            for (fragment in fragments!!) {
                (fragment as EmojiFragment).setOnEmojiClickListener(onEmojiClickListener)
            }
        }
        //        this.onEmojiClickListener = onEmojiClickListener;
    }

    companion object {
        private const val EMOJI_PAGE_MAX = 40
    }
}
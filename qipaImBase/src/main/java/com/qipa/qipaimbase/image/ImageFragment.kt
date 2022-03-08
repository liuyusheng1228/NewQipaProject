package com.qipa.qipaimbase.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qipa.qipaimbase.R
import com.qipa.qipaimbase.base.BaseFragment
import com.qipa.qipaimbase.utils.image.ImageLoaderUtils

class ImageFragment : BaseFragment() {
    private var imageUrl: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_chat_image, null)
        ImageLoaderUtils.getInstance().loadImage(
            getContext(),
            imageUrl,
            R.drawable.chat_placeholder,
            view.findViewById(R.id.ivImage)
        )
        return view
    }

    companion object {
        fun getInstance(imageUrl: String?): ImageFragment {
            val imageFragment = ImageFragment()
            imageFragment.imageUrl = imageUrl
            return imageFragment
        }
    }
}
package com.qipa.qipaimbase.image

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.qipa.qipaimbase.base.BaseFragmentPagerAdapter

class ImageCheckFragmentAdapter(fm: FragmentManager?, fragments: List<Fragment?>?) :
    BaseFragmentPagerAdapter(fm!!, fragments as List<Fragment>)

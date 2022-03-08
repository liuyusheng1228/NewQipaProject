package com.qipa.qipaimbase.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

open class BaseFragmentPagerAdapter(fm: FragmentManager, private val fragments: List<Fragment>) :
    FragmentPagerAdapter(fm) {
    override fun getItem(i: Int): Fragment {
        return fragments[i]
    }

    override fun getCount(): Int {
        return fragments?.size ?: 0
    }
}

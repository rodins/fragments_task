package com.sergeyrodin.fragmentstask

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragments = mutableListOf<PagerFragment>()

    fun addFragment(fragment: PagerFragment) {
        fragments.add(fragment)
        notifyItemInserted(fragments.lastIndex)
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun removeLastFragment() {
        val lastIndex = fragments.lastIndex
        fragments.removeAt(lastIndex)
        notifyItemRemoved(lastIndex)
    }

}

package com.sergeyrodin.fragmentstask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    private var fragmentNumber = 1
    private lateinit var pagerAdapter: ViewPagerAdapter
    private lateinit var pager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pager = findViewById(R.id.pager)

        pagerAdapter = ViewPagerAdapter(this)

        addFragment()

        pager.adapter = pagerAdapter
    }

    fun addFragment() {
        pagerAdapter.addFragment(PagerFragment.newInstance(fragmentNumber++))
        pager.currentItem = fragmentNumber - 1
    }

    fun removeLastFragment() {
        if (fragmentNumber > 1) {
            fragmentNumber--
            if (pager.currentItem == fragmentNumber) {
                pager.currentItem = fragmentNumber - 1
            }
            pagerAdapter.removeLastFragment()
        }
    }
}
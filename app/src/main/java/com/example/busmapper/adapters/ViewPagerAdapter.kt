package com.example.busmapper.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.busmapper.BusStopFragment
import com.example.busmapper.HomeFragment
import com.example.busmapper.MoreFragment


class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int {

        return 3
    }

    override fun createFragment(position: Int): Fragment {

        return when(position) {
            0 -> BusStopFragment()
            1 -> HomeFragment()
            2 -> MoreFragment()
            else -> HomeFragment()
        }
    }
}
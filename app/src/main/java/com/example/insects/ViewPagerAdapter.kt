package com.example.insects

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    var gameFragment: GameFragment? = null

    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                val fragment = GameFragment()
                gameFragment = fragment
                fragment
            }
            1 -> RegistrationFragment()
            2 -> RulesFragment()
            3 -> AuthorsFragment()
            4 -> SettingsFragment()
            5 -> RecordsFragment()
            else -> GameFragment()
        }
    }
}
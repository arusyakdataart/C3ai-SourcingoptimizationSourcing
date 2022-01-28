package com.c3ai.sourcingoptimization.presentation.item_details

import android.os.Bundle
import android.view.View
import com.c3ai.sourcingoptimization.databinding.FragmentItemDetailsViewPagerBinding

class ItemDetailsViewPagerFragment
    :
    BaseFragment<FragmentItemDetailsViewPagerBinding>(FragmentItemDetailsViewPagerBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabLayout = binding.tabs
        val viewPager = binding.viewPager
        viewPager.adapter = ItemsDetailPagerAdapter(childFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
    }
}
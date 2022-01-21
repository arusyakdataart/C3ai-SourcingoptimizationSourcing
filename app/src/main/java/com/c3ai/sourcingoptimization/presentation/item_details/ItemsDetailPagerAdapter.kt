package com.c3ai.sourcingoptimization.presentation.item_details

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.c3ai.sourcingoptimization.presentation.item_details.overview.ItemDetailsOverviewFragment

const val OVERVIEW_PAGE_INDEX = 0
const val PO_LINES_PAGE_INDEX = 1
const val SUPPLIERS_PAGE_INDEX = 2

class ItemsDetailPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    /**
     * Mapping of the ViewPager page indexes to their respective Fragments
     */
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        OVERVIEW_PAGE_INDEX to { ItemDetailsOverviewFragment() },
        PO_LINES_PAGE_INDEX to { ItemDetailsOverviewFragment() },
        SUPPLIERS_PAGE_INDEX to {ItemDetailsOverviewFragment()}
    )

    override fun getCount() = tabFragmentsCreators.size

    override fun getItem(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            OVERVIEW_PAGE_INDEX -> "Overview"
            PO_LINES_PAGE_INDEX -> "PO Lines"
            SUPPLIERS_PAGE_INDEX -> "Suppliers"
            else -> ""
        }
    }
}
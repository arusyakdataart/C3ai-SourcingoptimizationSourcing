package com.c3ai.sourcingoptimization.presentation.item_details

import android.os.Bundle
import com.c3ai.sourcingoptimization.databinding.ActivityItemDetailsBinding
import com.c3ai.sourcingoptimization.presentation.MainActivity
import com.c3ai.sourcingoptimization.presentation.search.SearchScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * An entry point for Item Details page. The activity[ItemDetailsActivity] setups view pager with 3 tabs
 * @see ItemDetailsOverviewFragment
 * */
@AndroidEntryPoint
class ItemDetailsActivity :
    BaseActivity<ActivityItemDetailsBinding>(ActivityItemDetailsBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getStringExtra("id") ?: "item1"
        supportActionBar?.title = "Item ID$id"
    }
}
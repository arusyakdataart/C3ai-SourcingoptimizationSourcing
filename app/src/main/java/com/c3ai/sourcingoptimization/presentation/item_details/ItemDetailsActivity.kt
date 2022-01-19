package com.c3ai.sourcingoptimization.presentation.item_details

import android.os.Bundle
import com.c3ai.sourcingoptimization.databinding.ActivityItemDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemDetailsActivity : BaseActivity<ActivityItemDetailsBinding>(ActivityItemDetailsBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Item ID453425"
        //supportActionBar?.setDisplayShowTitleEnabled(false)
    }
}
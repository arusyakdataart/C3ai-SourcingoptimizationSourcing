package com.c3ai.sourcingoptimization.feature_home.presentation

import android.os.Bundle
import com.c3ai.sourcingoptimization.common.BaseActivity
import com.c3ai.sourcingoptimization.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.text.text = "Helloooooo"
    }

    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)
}
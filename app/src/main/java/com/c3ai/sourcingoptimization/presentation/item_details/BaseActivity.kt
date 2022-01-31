package com.c3ai.sourcingoptimization.presentation.item_details

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.c3ai.sourcingoptimization.presentation.MainActivity
import com.c3ai.sourcingoptimization.presentation.search.SearchScreen

/**
 * A base activity for all activities not using jetpack compose.
 * The activity[BaseActivity] implements view binding functionality.
 * */
abstract class BaseActivity<Binding : ViewBinding>(private val factory: (LayoutInflater) -> Binding) :
    AppCompatActivity() {

    private var _binding: Binding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (_binding == null) {
            _binding = factory(layoutInflater)
        }

        setContentView(binding.root)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
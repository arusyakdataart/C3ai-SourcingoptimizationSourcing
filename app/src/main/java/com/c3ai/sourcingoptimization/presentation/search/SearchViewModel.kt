package com.c3ai.sourcingoptimization.presentation.search

import androidx.lifecycle.ViewModel
import com.c3ai.sourcingoptimization.domain.use_case.SearchUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCases: SearchUseCases
) : ViewModel() {

}
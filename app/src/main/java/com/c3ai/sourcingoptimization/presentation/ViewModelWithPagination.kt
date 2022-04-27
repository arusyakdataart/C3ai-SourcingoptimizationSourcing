package com.c3ai.sourcingoptimization.presentation

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT
import kotlinx.coroutines.launch

abstract class ViewModelWithPagination : ViewModel() {

    val page = mutableStateOf(0)
    var listScrollPosition = 0

    private fun incrementPage() {
        page.value = page.value + 1
    }

    fun onChangeListScrollPosition(position: Int) {
        listScrollPosition = position
    }

    fun nextPage() {
        viewModelScope.launch {
            if ((listScrollPosition + 1) >= (page.value * PAGINATED_RESPONSE_LIMIT) ) {
                incrementPage()
                Log.d("AlertsViewModel", "nextPage: triggered: ${page.value}")

                if (page.value > 0) {
                    refreshDetails(page = page.value)
                }
            }
        }
    }

    abstract fun refreshDetails(sortOrder: String = "", page: Int)

    private fun incrementPage(page: MutableState<Int>) {
        page.value = page.value + 1
    }

    fun onChangeListScrollPosition(listScrollPosition: MutableState<Int>, position: Int) {
        listScrollPosition.value = position
    }

    fun nextPage(page: MutableState<Int>, listScrollPosition: MutableState<Int>, tabPosition: Int) {
        viewModelScope.launch {
            if ((listScrollPosition.value + 1) >= (page.value * PAGINATED_RESPONSE_LIMIT) ) {
                incrementPage(page)
                Log.d("AlertsViewModel", "nextPage: triggered: ${page.value}")

                if (page.value > 0) {
                    refreshDetails(page = page.value, tabPosition = tabPosition)
                }
            }
        }
    }

    abstract fun refreshDetails(sortOrder: String = "", page: Int, tabPosition: Int)
}
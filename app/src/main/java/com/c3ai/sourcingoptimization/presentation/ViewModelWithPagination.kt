package com.c3ai.sourcingoptimization.presentation

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT
import kotlinx.coroutines.launch

abstract class ViewModelWithPagination : ViewModel() {

    /**
     * Variable to hold state of page value for screen each tab.
     */
    val pages = mutableListOf<MutableState<Int>>()

    /**
     * Variable to hold state of list scroll position for screen each tab.
     */
    private val listScrollPositions = mutableListOf<MutableState<Int>>()
    var size: Int = 1

    init {
        setSize()
        for (i in 0..size) {
            pages.add(mutableStateOf(0))
            listScrollPositions.add(mutableStateOf(0))
        }
    }

    private fun incrementPage(index: Int) {
        pages[index].value = pages[index].value + 1
    }

    fun onChangeListScrollPosition(position: Int, index: Int = 0) {
        listScrollPositions[index].value = position
    }

    fun nextPage(index: Int = 0) {
        viewModelScope.launch {
            if ((listScrollPositions[index].value + 1) >= (pages[index].value * PAGINATED_RESPONSE_LIMIT) ) {
                incrementPage(index)
                Log.d("AlertsViewModel", "nextPage: triggered: ${pages[index].value}")

                if (pages[index].value > 0) {
                    refreshDetails(page = pages[index].value, index = index)
                }
            }
        }
    }

    /**
     * Refresh data for screen all tabs
     */
    abstract fun refreshDetails(sortOrder: String = "", page: Int)

    /**
     * Refresh data for screen particular tab
     */
    abstract fun refreshDetails(sortOrder: String = "", page: Int, index: Int)

    /**
     * Set size of tabs for screen. In case there aren't any, set 1
     */
    abstract fun setSize()
}
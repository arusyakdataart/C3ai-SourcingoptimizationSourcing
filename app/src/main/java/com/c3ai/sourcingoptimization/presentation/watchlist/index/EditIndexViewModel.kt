package com.c3ai.sourcingoptimization.presentation.watchlist.index

import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.domain.model.MarketPriceIndex
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.use_case.EditIndexUseCases
import com.c3ai.sourcingoptimization.presentation.ViewModelState
import com.c3ai.sourcingoptimization.presentation.ViewModelWithPagination
import com.c3ai.sourcingoptimization.presentation.watchlist.suppliers.EditSuppliersEvent
import com.c3ai.sourcingoptimization.utilities.ErrorMessage
import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * UI state for the Home route.
 *
 * This is derived from [EditIndexUiState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface EditIndexUiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>
    val searchInput: String

    /**
     * There is no data to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoData(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ) : EditIndexUiState

    /**
     * There is data to render, as contained in model[indexes].
     *
     */
    data class HasData(
        val indexes: List<MarketPriceIndex>,
        val checkedItemId: String = "",
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String,
    ) : EditIndexUiState
}

/**
 * An internal representation of the EditIndex route state, in a raw form
 */
private data class EditIndexViewModelState(
    override val settings: C3AppSettingsProvider,
    var indexes: List<MarketPriceIndex>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String = "",
    val checkedItemId: String = ""
) : ViewModelState() {

    /**
     * Converts this [EditIndexViewModelState] into
     * a more strongly typed [EditIndexUiState] for driving the ui.
     */
    fun toUiState(): EditIndexUiState =
        if (indexes != null) {
            EditIndexUiState.HasData(
                indexes = indexes!!,
                checkedItemId = checkedItemId,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        } else {
            EditIndexUiState.NoData(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        }
}

@HiltViewModel
class EditIndexViewModel @Inject constructor(
    settings: C3AppSettingsProvider,
    private val useCases: EditIndexUseCases
) : ViewModelWithPagination() {

    private val viewModelState = MutableStateFlow(
        EditIndexViewModelState(
            settings = settings,
            isLoading = true
        )
    )

    // UI state exposed to the UI
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshDetails(page = 0)
    }

    /**
     * Update state by user event.
     */
    fun onEvent(event: EditSuppliersEvent) {

    }

    /**
     * Refresh index data and update the UI state accordingly
     */
    override fun refreshDetails(sortOrder: String, page: Int) {
        if (page == 0) {
            viewModelState.update { it.copy(isLoading = true) }
        }

        viewModelScope.launch {
            val result = useCases.getIndexes(page * PAGINATED_RESPONSE_LIMIT)
            viewModelState.update {
                when (result) {
                    is C3Result.Success -> it.copy(indexes = appendIndexes(result.data, page), isLoading = false)
                    is C3Result.Error -> {
                        val errorMessages = it.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        it.copy(errorMessages = errorMessages, isLoading = false)
                    }
                }
            }
        }
    }

    /**
     * Refresh index data and update the UI state accordingly
     */
    override fun refreshDetails(sortOrder: String, page: Int, index: Int) {
        refreshDetails(sortOrder, page)
    }

    override fun setSize() {
        size = 1
    }

    private fun appendIndexes(indexes: List<MarketPriceIndex>, page: Int): MutableList<MarketPriceIndex>? {
        if (viewModelState.value.indexes == null || page == 0) {
            viewModelState.value.indexes = mutableListOf()
        }
        val appendedList = viewModelState.value.indexes?.toMutableList()
        appendedList?.addAll(indexes)
        return appendedList
    }
}
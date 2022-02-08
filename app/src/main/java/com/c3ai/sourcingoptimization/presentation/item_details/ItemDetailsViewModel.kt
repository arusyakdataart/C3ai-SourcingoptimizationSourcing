package com.c3ai.sourcingoptimization.presentation.item_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.data.Result.Error
import com.c3ai.sourcingoptimization.data.Result.Success
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.domain.model.C3Item
import com.c3ai.sourcingoptimization.domain.model.OpenClosedPOLineQtyItem
import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT
import com.c3ai.sourcingoptimization.utilities.VISIBLE_THRESHOLD
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI state for the ItemDetails route.
 *
 * This is derived from [ItemDetailsViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface ItemDetailsUiState {

    val isLoading: Boolean
    val itemId: String

    /**
     * There are no item to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoItem(
        override val isLoading: Boolean,
        override val itemId: String
    ) : ItemDetailsUiState

    /**
     * There are item to render, as contained in [item].
     *
     */
    data class HasItems(
        val items: List<C3Item>,
        override val isLoading: Boolean,
        override val itemId: String
    ) : ItemDetailsUiState

    data class HasEvalMetrics(
        val evalMetrics: OpenClosedPOLineQtyItem,
        override val isLoading: Boolean,
        override val itemId: String
    ) : ItemDetailsUiState
}

/**
 * An internal representation of the Home route state, in a raw form
 */
private data class ItemDetailsViewModelState(
    val items: List<C3Item>? = null,
    val openClosedPOLineQty: OpenClosedPOLineQtyItem? = null,
    val savingOpportunity: OpenClosedPOLineQtyItem? = null,
    val isLoading: Boolean = false,
    val itemId: String = "",
) {

    /**
     * Converts this [ItemDetailsViewModelState] into a more strongly typed [ItemDetailsUiState]
     * for driving the ui.
     */
    fun toUiState(): ItemDetailsUiState {
        if (openClosedPOLineQty != null) {
            return  ItemDetailsUiState.HasEvalMetrics(
                evalMetrics = openClosedPOLineQty,
                isLoading = isLoading,
                itemId = itemId
            )
        }

        if (items != null) {
            return ItemDetailsUiState.HasItems(
                items = items,
                isLoading = isLoading,
                itemId = itemId
            )
        }

        return ItemDetailsUiState.NoItem(
            isLoading = isLoading,
            itemId = itemId
        )
    }
}

/**
 * ViewModel that handles the business logic of the Home screen
 */
class ItemDetailsViewModel @AssistedInject constructor(
    private val repository: C3Repository,
    @Assisted ("itemId") private val itemId: String,
    @Assisted ("expressions") private val expressions: List<String>,
    @Assisted ("startDate") private val startDate: String,
    @Assisted ("endDate") private val endDate: String,
    @Assisted ("interval") private val interval: String
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ItemDetailsViewModelState())
    private var offset = 0

    // UI state exposed to the UI
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    val accept: (UiAction) -> Unit

    init {
        refresh()

        accept = { action ->
            when (action) {
                is UiAction.Scroll -> if (action.shouldFetchMore(offset) && !viewModelState.value.isLoading) {
                    refresh()
                }
            }
        }
    }

    /**
     * Refresh posts and update the UI state accordingly
     */
    fun refresh() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val itemsResult = repository.getItemDetails(itemId)
            viewModelState.update {
                when (itemsResult) {
                    is Success -> {
                        offset += PAGINATED_RESPONSE_LIMIT
                        it.copy(items = itemsResult.data.objs, isLoading = false)
                    }
                    is Error -> {
                        it.copy(isLoading = false)
                    }
                }
            }

            val openClosedPOLineQtyResult = repository.getEvalMetrics(itemId, expressions, startDate, endDate, interval)
            viewModelState.update {
                when (openClosedPOLineQtyResult) {
                    is Success -> {
                        it.copy(openClosedPOLineQty = openClosedPOLineQtyResult.data, isLoading = false)
                    }
                    is Error -> {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }

    class Factory(
        private val assistedFactory: ItemDetailsViewModelAssistedFactory,
        private val itemId: String,
        private val expressions: List<String>,
        private val startDate: String,
        private val endDate: String,
        private val interval: String,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return assistedFactory.create(itemId, expressions, startDate, endDate, interval) as T
        }
    }
}

fun UiAction.Scroll.shouldFetchMore(offset: Int): Boolean {
    return offset == totalItemCount *  PAGINATED_RESPONSE_LIMIT && visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount
}

sealed class UiAction {
    data class Scroll(
        val visibleItemCount: Int,
        val lastVisibleItemPosition: Int,
        val totalItemCount: Int
    ) : UiAction()
}

@AssistedFactory
interface ItemDetailsViewModelAssistedFactory {

    fun create(@Assisted("itemId") itemId: String,
               @Assisted("expressions") expressions: List<String>,
               @Assisted("startDate") startDate: String,
               @Assisted("endDate") endDate: String,
               @Assisted("interval") interval: String): ItemDetailsViewModel
}

package com.c3ai.sourcingoptimization.presentation.item_details.po_lines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.domain.model.PurchaseOrder
import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT
import com.c3ai.sourcingoptimization.utilities.VISIBLE_THRESHOLD
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI state for the ItemPOLines route.
 *
 * This is derived from [ItemPOLinesViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface ItemPOLinesUiState {

    val isLoading: Boolean
    val itemId: String

    /**
     * There are no items to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoItem(
        override val isLoading: Boolean,
        override val itemId: String
    ) : ItemPOLinesUiState

    /**
     * There are items to render, as contained in [item].
     *
     */
    data class HasItems(
        val items: List<PurchaseOrder.Line>,
        override val isLoading: Boolean,
        override val itemId: String
    ) : ItemPOLinesUiState

//    data class HasPOLinesQtyMetrics(
//        val poLineQty: OpenClosedPOLineQtyItem,
//        override val isLoading: Boolean,
//        override val itemId: String
//    ) : ItemDetailsUiState
//
//    data class HasSavingsOpportunity(
//        val savingsOpportunity: SavingsOpportunityItem,
//        override val isLoading: Boolean,
//        override val itemId: String
//    ) : ItemDetailsUiState
//
//    data class HasSuppliers(
//        val suppliers: Vendors,
//        override val isLoading: Boolean,
//        override val itemId: String
//    ) : ItemDetailsUiState
}

/**
 * An internal representation of the Home route state, in a raw form
 */
private data class ItemPOLinesViewModelState(
    val items: List<PurchaseOrder.Line>? = null,
    val isLoading: Boolean = false,
    val itemId: String = "",
) {

    /**
     * Converts this [ItemDetailsViewModelState] into a more strongly typed [ItemDetailsUiState]
     * for driving the ui.
     */
    fun toUiState(): ItemPOLinesUiState {
        if (items != null) {
            return ItemPOLinesUiState.HasItems(
                items = items,
                isLoading = isLoading,
                itemId = itemId
            )
        }

        return ItemPOLinesUiState.NoItem(
            isLoading = isLoading,
            itemId = itemId
        )
    }
}

/**
 * ViewModel that handles the business logic of the Home screen
 */
class ItemPOLinesViewModel @AssistedInject constructor(
    private val repository: C3Repository,
    @Assisted("itemId") private val itemId: String
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ItemPOLinesViewModelState())
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
            val itemsResult = repository.getPOLines(itemId = itemId, orderId = null, order = "")
            viewModelState.update {
                when (itemsResult) {
                    is C3Result.Success -> {
                        offset += PAGINATED_RESPONSE_LIMIT
                        it.copy(
                            items = itemsResult.data,
                            isLoading = false
                        )
                    }
                    is C3Result.Error -> {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }

//    private fun fetchPOLines(poIds: List<String>) {
//        viewModelState.update { it.copy(isLoading = true) }
//        viewModelScope.launch {
//            val itemsResult = repository.getPOLines(poIds, "")
//            viewModelState.update {
//                when (itemsResult) {
//                    is C3Result.Success -> {
//                        offset += PAGINATED_RESPONSE_LIMIT
//                        it.copy(
//                        )
//                    }
//                    is C3Result.Error -> {
//                        it.copy(isLoading = false)
//                    }
//                }
//            }
//        }
//    }

    class Factory(
        private val assistedFactory: ItemPOLinesViewModelAssistedFactory,
        private val itemId: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return assistedFactory.create(itemId) as T
        }
    }
}

fun UiAction.Scroll.shouldFetchMore(offset: Int): Boolean {
    return offset == totalItemCount * PAGINATED_RESPONSE_LIMIT && visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount
}

sealed class UiAction {
    data class Scroll(
        val visibleItemCount: Int,
        val lastVisibleItemPosition: Int,
        val totalItemCount: Int
    ) : UiAction()
}

@AssistedFactory
interface ItemPOLinesViewModelAssistedFactory {

    fun create(@Assisted("itemId") itemId: String): ItemPOLinesViewModel
}

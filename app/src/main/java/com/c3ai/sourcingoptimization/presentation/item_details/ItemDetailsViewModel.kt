package com.c3ai.sourcingoptimization.presentation.item_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.data.Result.Error
import com.c3ai.sourcingoptimization.data.Result.Success
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.domain.model.C3Item
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
    data class HasItem(
        val item: List<C3Item>,
        override val isLoading: Boolean,
        override val itemId: String
    ) : ItemDetailsUiState
}

/**
 * An internal representation of the Home route state, in a raw form
 */
private data class ItemDetailsViewModelState(
    val item: List<C3Item>? = null,
    val isLoading: Boolean = false,
    val itemId: String = "",
) {

    /**
     * Converts this [ItemDetailsViewModelState] into a more strongly typed [ItemDetailsUiState]
     * for driving the ui.
     */
    fun toUiState(): ItemDetailsUiState =
        if (item == null) {
            ItemDetailsUiState.NoItem(
                isLoading = isLoading,
                itemId = itemId
            )
        } else {
            ItemDetailsUiState.HasItem(
                item = item,
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
    @Assisted private val itemId: String
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ItemDetailsViewModelState())

    // UI state exposed to the UI
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refresh()
    }

    /**
     * Refresh posts and update the UI state accordingly
     */
    fun refresh() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = repository.getItemDetails(itemId)
            viewModelState.update {
                when (result) {
                    is Success -> it.copy(item = result.data.objs, isLoading = false)
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
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return assistedFactory.create(itemId) as T
        }
    }
}

@AssistedFactory
interface ItemDetailsViewModelAssistedFactory {

    fun create(itemId: String): ItemDetailsViewModel
}

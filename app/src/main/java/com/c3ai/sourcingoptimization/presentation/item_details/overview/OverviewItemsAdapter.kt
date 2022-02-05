package com.c3ai.sourcingoptimization.presentation.item_details.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.c3ai.sourcingoptimization.databinding.OverviewItemViewBinding
import com.c3ai.sourcingoptimization.domain.model.C3Item

class OverviewItemsAdapter :
    ListAdapter<C3Item, OverviewItemsAdapter.OverviewViewHolder>(ItemsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OverviewViewHolder {
        val itemBinding =
            OverviewItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OverviewViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: OverviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OverviewViewHolder(private val itemBinding: OverviewItemViewBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: C3Item?) {

        }
    }
}

private class ItemsDiffCallback : DiffUtil.ItemCallback<C3Item>() {

    override fun areItemsTheSame(oldItem: C3Item, newItem: C3Item): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: C3Item, newItem: C3Item): Boolean =
        oldItem == newItem
}
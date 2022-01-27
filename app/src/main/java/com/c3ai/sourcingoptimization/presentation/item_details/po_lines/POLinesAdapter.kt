package com.c3ai.sourcingoptimization.presentation.item_details.po_lines

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.c3ai.sourcingoptimization.databinding.PoLineItemViewBinding
import com.c3ai.sourcingoptimization.domain.model.POLine

class POLinesAdapter() : PagedListAdapter<POLine, POLinesAdapter.POLineViewHolder>(POLinesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): POLineViewHolder {
        val itemBinding = PoLineItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return POLineViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: POLineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class POLineViewHolder(private val itemBinding: PoLineItemViewBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: POLine?) {

        }
    }
}

private class POLinesDiffCallback : DiffUtil.ItemCallback<POLine>() {

    override fun areItemsTheSame(oldItem: POLine, newItem: POLine): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: POLine, newItem: POLine): Boolean =
        oldItem == newItem
}

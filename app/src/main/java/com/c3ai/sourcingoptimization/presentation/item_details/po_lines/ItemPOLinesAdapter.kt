package com.c3ai.sourcingoptimization.presentation.item_details.po_lines

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.c3ai.sourcingoptimization.databinding.PoLineItemViewBinding
import com.c3ai.sourcingoptimization.domain.model.PurchaseOrder

class ItemPOLinesAdapter :
    ListAdapter<PurchaseOrder.Line, ItemPOLinesAdapter.POViewHolder>(ItemsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): POViewHolder {
        val itemBinding =
            PoLineItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return POViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: POViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class POViewHolder(private val itemBinding: PoLineItemViewBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: PurchaseOrder.Line?) {
            item ?: return
        }
    }
}

private class ItemsDiffCallback : DiffUtil.ItemCallback<PurchaseOrder.Line>() {

    override fun areItemsTheSame(
        oldItem: PurchaseOrder.Line,
        newItem: PurchaseOrder.Line
    ): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: PurchaseOrder.Line,
        newItem: PurchaseOrder.Line
    ): Boolean =
        oldItem == newItem
}
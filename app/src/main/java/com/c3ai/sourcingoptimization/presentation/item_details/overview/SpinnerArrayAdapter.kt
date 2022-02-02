package com.c3ai.sourcingoptimization.presentation.item_details.overview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.c3ai.sourcingoptimization.R

class SpinnerArrayAdapter(context: Context, data: List<String>)
    : ArrayAdapter<String>(context, 0, data) {

    var selectedPosition = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.spinner_dropdown_view, parent, false)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item_view, parent, false)
        view.findViewById<TextView>(R.id.title).text = item

        if (selectedPosition == position) {
            view.findViewById<View>(R.id.selectedView).visibility = View.VISIBLE
        }
        return view
    }
}
package com.c3ai.sourcingoptimization.common

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class InterTextView : AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        val stringBuilder = StringBuilder()
        stringBuilder.append("fonts/").append("Inter-Regular.ttf")
        val typeface = Typeface.createFromAsset(context.assets, stringBuilder.toString())
        this.typeface = typeface
    }
}
package com.cracky_axe.pxohlqo.keyi.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class SquareImageView(context: Context, attributeSet: AttributeSet) : ImageView(context, attributeSet) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(measuredWidth, measuredWidth)
    }
}
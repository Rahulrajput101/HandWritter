package com.elkdocs.handwritter.util

import android.content.Context
import android.util.TypedValue

object OtherUtility {

    fun provideBackgroundColorPrimary(context: Context): Int {
        val primaryColorAttr = android.R.attr.colorPrimary
        val primaryColorValue = TypedValue()
        context.theme.resolveAttribute(primaryColorAttr, primaryColorValue, true)
        return primaryColorValue.data
    }
}
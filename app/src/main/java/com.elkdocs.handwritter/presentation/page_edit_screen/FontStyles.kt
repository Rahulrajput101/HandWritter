package com.elkdocs.handwritter.presentation.page_edit_screen

import com.elkdocs.handwritter.R

sealed interface FontStyles {
    fun getResourceId() : Int
}
object EnglishFontStyle : FontStyles {
    override fun getResourceId(): Int = R.array.font_styles_array
}

object PhilippineFontStyle : FontStyles{
    override fun getResourceId(): Int = R.array.ph_styles_array
}

object ArabicFontStyle : FontStyles {
    override fun getResourceId(): Int = R.array.ar_styles_array
}
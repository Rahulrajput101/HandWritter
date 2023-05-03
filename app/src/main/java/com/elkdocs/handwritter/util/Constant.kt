package com.elkdocs.handwritter.util

import android.graphics.Color
import com.elkdocs.handwritter.R

object Constant {

    const val SHADOWS_INTO_LIGHT_REGULAR ="Shadows Into Light Regular"
    const val TILLANA_REGULAR ="Tillana Regular"
    const val INDIE_FLOWER_REGULAR ="Zeyada Regular"
    const val CAVEAT_VARIABLE_FONT ="Caveat Variable Font"
    const val DANCING_SCRIPT ="Dancing Script"
    const val PERMANENT_MARKER ="Permanent Marker"
    const val SEDGWICK_AVE_DISPLAY ="Sedgwick Ave Display"
    const val SACRAMENTO_REGULAR ="Sacramento Regular"

    val FONT_STYLES_MAP = mapOf(
        SHADOWS_INTO_LIGHT_REGULAR to R.font.shadows_into_light_regular,
        TILLANA_REGULAR to R.font.tillana_regular,
        INDIE_FLOWER_REGULAR to R.font.zeyada_regular,
        CAVEAT_VARIABLE_FONT to R.font.caveat_variablefont_wght,
        DANCING_SCRIPT to R.font.dancing_script_variablefont_wght,
        PERMANENT_MARKER to R.font.permanent_marker_regular,
        SEDGWICK_AVE_DISPLAY to R.font.sedgwick_ave_display_regular,
        SACRAMENTO_REGULAR to R.font.sacramento_regular
    )
   val REVERSE_FONT_STYLE_MAP = mapOf(
       R.font.shadows_into_light_regular to SHADOWS_INTO_LIGHT_REGULAR,
       R.font.tillana_regular to TILLANA_REGULAR,
       R.font.zeyada_regular to INDIE_FLOWER_REGULAR,
       R.font.caveat_variablefont_wght to CAVEAT_VARIABLE_FONT,
       R.font.dancing_script_variablefont_wght to DANCING_SCRIPT,
       R.font.permanent_marker_regular to PERMANENT_MARKER ,
       R.font.sedgwick_ave_display_regular to SEDGWICK_AVE_DISPLAY,
       R.font.sacramento_regular to SACRAMENTO_REGULAR
   )
    const val FONT_SIZE_12 = "12.0"
    const val FONT_SIZE_14 = "14.0"
    const val FONT_SIZE_16 = "16.0"
    const val FONT_SIZE_18 = "18.0"
    const val FONT_SIZE_20 = "20.0"
    const val FONT_SIZE_22 = "22.0"
    const val FONT_SIZE_24 = "24.0"
    const val FONT_SIZE_26 = "26.0"
    const val FONT_SIZE_28 = "28.0"
    const val FONT_SIZE_30 = "30.0"

    val FONT_SIZES_MAP = mapOf(
        FONT_SIZE_12 to 12F,
        FONT_SIZE_14 to 14f,
        FONT_SIZE_16 to 16f,
        FONT_SIZE_18 to 18f,
        FONT_SIZE_20 to 20f,
        FONT_SIZE_22 to 22f,
        FONT_SIZE_24 to 24f,
        FONT_SIZE_26 to 26f,
        FONT_SIZE_28 to 28f,
        FONT_SIZE_30 to 30f
    )


    const val ADD_LINE_ON = "on"
    const val ADD_LINE_OFF = "off"

    val ADD_LINE_MAP = mapOf(
        ADD_LINE_ON to true,
        ADD_LINE_OFF to false
    ).withDefault{true}

    const val LINE_COLOR_BLACK = "Black"
    const val LINE_COLOR_BLUE = "Blue"
    const val LINE_COLOR_RED = "Red"
    const val LINE_COLOR_GREEN = "Green"

    val LINE_COLOR_MAP = mapOf(
        LINE_COLOR_BLACK to Color.BLACK,
        LINE_COLOR_BLUE to Color.BLUE,
        LINE_COLOR_RED to Color.RED,
        LINE_COLOR_GREEN to Color.GREEN
    )


    //color lines
    const val PURPLE_LINE_COLOR = 0xFFB2B1D3.toInt()

    //font type
    const val NORMAL_FONT_TYPE = "Normal"
    const val BOLD_FONT_TYPE = "Bold"
    const val UNDERLINE_FONT_TYPE = "Underline"
}
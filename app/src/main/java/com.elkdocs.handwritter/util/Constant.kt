package com.elkdocs.handwritter.util

import android.graphics.Color
import com.elkdocs.handwritter.R

object Constant {

    const val SHADOWS_INTO_LIGHT_REGULAR = "Shadows Into Light Regular"
    const val TILLANA_REGULAR = "Tillana Regular"
    const val INDIE_FLOWER_REGULAR = "Zeyada Regular"
    const val CAVEAT_VARIABLE_FONT = "Caveat Variable Font"
    const val DANCING_SCRIPT = "Dancing Script"
    const val PERMANENT_MARKER = "Permanent Marker"
    const val SACRAMENTO_REGULAR = "Sacramento Regular"

    const val ENGLISH = "English"
    const val Hindi = "Hindi"

    val FONT_STYLES_MAP = mapOf(
        SHADOWS_INTO_LIGHT_REGULAR to R.font.shadows_into_light_regular,
        TILLANA_REGULAR to R.font.tillana_regular,
        INDIE_FLOWER_REGULAR to R.font.zeyada_regular,
        CAVEAT_VARIABLE_FONT to R.font.caveat_variablefont_wght,
        DANCING_SCRIPT to R.font.dancing_script_variablefont_wght,
        PERMANENT_MARKER to R.font.permanent_marker_regular,
        SACRAMENTO_REGULAR to R.font.sacramento_regular
    )
    val REVERSE_FONT_STYLE_MAP = mapOf(
        R.font.shadows_into_light_regular to SHADOWS_INTO_LIGHT_REGULAR,
        R.font.tillana_regular to TILLANA_REGULAR,
        R.font.zeyada_regular to INDIE_FLOWER_REGULAR,
        R.font.caveat_variablefont_wght to CAVEAT_VARIABLE_FONT,
        R.font.dancing_script_variablefont_wght to DANCING_SCRIPT,
        R.font.permanent_marker_regular to PERMANENT_MARKER,
        R.font.sacramento_regular to SACRAMENTO_REGULAR
    )

    val LANGUAGE_MAP = mapOf(
        ENGLISH to R.font.caveat_variablefont_wght,
        Hindi to R.font.hindi
    )
    val REVERSE_LANGUAGE_MAP = mapOf(
        R.font.caveat_variablefont_wght to ENGLISH,
        R.font.hindi to Hindi
    )

    const val FONT_SIZE_12 = 0
    const val FONT_SIZE_14 = 1
    const val FONT_SIZE_16 = 2
    const val FONT_SIZE_18 = 3
    const val FONT_SIZE_20 = 4
    const val FONT_SIZE_22 = 5
    const val FONT_SIZE_24 = 6
    const val FONT_SIZE_26 = 7
    const val FONT_SIZE_28 = 8
    const val FONT_SIZE_30 = 9

    val FONT_SIZES_MAP = mapOf(
        FONT_SIZE_12 to 12f,
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

    val REVERSE_FONT_SIZE_MAP = mapOf(
        12f to FONT_SIZE_12,
        14f to FONT_SIZE_14,
        16f to FONT_SIZE_16,
        18f to FONT_SIZE_18,
        20f to FONT_SIZE_20,
        22f to FONT_SIZE_22,
        24f to FONT_SIZE_24,
        26f to FONT_SIZE_26,
        28f to FONT_SIZE_28,
        30f to FONT_SIZE_30,

    )




    const val ADD_LINE_ON = "on"
    const val ADD_LINE_OFF = "off"

    val ADD_LINE_MAP = mapOf(
        ADD_LINE_ON to true,
        ADD_LINE_OFF to false
    ).withDefault { true }

    const val LINE_COLOR_BLUE = "Blue"
    const val LINE_COLOR_BLACK = "Black"
    const val LINE_COLOR_RED = "Red"
    const val LINE_COLOR_GREEN = "Green"
    const val NO_LINE = "No Line"

    val LINE_COLOR_MAP = mapOf(
        LINE_COLOR_BLUE to 0xFFB2B1D3.toInt(),
        LINE_COLOR_BLACK to 0xFFBFBFBD.toInt(),
        LINE_COLOR_RED to 0xFFCCAAC0.toInt(),
        LINE_COLOR_GREEN to 0xFF82A6A8.toInt(),
        NO_LINE to -1
    )
    val REVERSE_LINE_COLOR_MAP = mapOf(
        0xFFB2B1D3.toInt() to LINE_COLOR_BLUE,
        0xFFBFBFBD.toInt() to LINE_COLOR_BLACK,
        0xFFCCAAC0.toInt() to LINE_COLOR_RED,
        0xFF82A6A8.toInt() to LINE_COLOR_GREEN,
        -1 to NO_LINE

    )
//    const val INK_COLOR_BLACK = "Black"
//    const val INK_COLOR_BLUE = "Blue"
//    const val INK_COLOR_RED = "Red"
//    const val INK_COLOR_GREEN = "Green"
//    const val INK_COLOR_BLUE_GEL = "Blue gel"

    const val INK_COLOR_BLACK = 0xFF333333.toInt()
    const val INK_COLOR_BLUE = 0xFF191970.toInt()
    const val INK_COLOR_RED = 0xFF800000.toInt()
    const val INK_COLOR_GREEN = 0xFF006400.toInt()
    const val INK_COLOR_BLUE_GEL = 0xFF007BFF.toInt()

    val INK_COLOR_MAP = mapOf(
        INK_COLOR_BLACK to R.drawable.black_ink,
        INK_COLOR_BLUE to R.drawable.blue_ink,
        INK_COLOR_RED to R.drawable.red_ink,
        INK_COLOR_GREEN to R.drawable.green_ink,
        INK_COLOR_BLUE_GEL to R.drawable.blue_gel_ink
    )

    //color lines
    const val BLUE_LINE_COLOR = 0xFFB2B1D3.toInt()


    const val PAGE_COLOR_PALE_LAVENDER = 0xFFE3E3E5.toInt()
    const val PAGE_COLOR_LIGHT_GRAY = 0xFFEDEBDF.toInt()
    const val PAGE_COLOR_OFF_WHITE = 0xFFE1E2E4.toInt()
    const val PAGE_COLOR_PALE_BLUE = 0xFFEBDECD.toInt()
    const val PAGE_COLOR_LIGHT_BEIGE = 0xFFDADCF1.toInt()
    const val PAGE_COLOR_WHITE = Color.WHITE


    //sharedPref
    const val SHARED_PREFERENCE_NAME = "shared_pref"
    const val IS_LINEAR = " KEY_LOOK"

//    For Black:
//
//    Jet Black: 0xFF000000.toInt()
//    Midnight Black: 0xFF0C0C0C.toInt()
//    Charcoal Black: 0xFF333333.toInt()
//    For Blue:
//
//    Navy Blue: 0xFF000080.toInt()
//    Sapphire Blue: 0xFF082567.toInt()
//    Indigo Blue: 0xFF191970.toInt()
//    For Red:
//
//    Dark Red: 0xFF8B0000.toInt()
//    Maroon Red: 0xFF800000.toInt()
//    Burgundy Red: 0xFF800020.toInt()
//    For Green:
//
//    Dark Green: 0xFF006400.toInt()
//    Forest Green: 0xFF228B22.toInt()


//    const val BLUE_GEL_PEN_DARK = 0xFF0000A0.toInt()
//    const val BLUE_GEL_PEN_MEDIUM = 0xFF0047AB.toInt()
//    const val BLUE_GEL_PEN_LIGHT = 0xFF007BFF.toInt()
}
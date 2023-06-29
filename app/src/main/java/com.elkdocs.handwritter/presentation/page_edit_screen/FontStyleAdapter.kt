package com.elkdocs.handwritter.presentation.page_edit_screen

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.elkdocs.handwritter.R

class FontStyleAdapter(
    context: Context,
    resource: Int,
    objects: Array<String>,
    private val fontMap: Map<String, Int>
) : ArrayAdapter<String>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        applyFont(view, position)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        applyFont(view, position)
        return view
    }

    private fun applyFont(view: View, position: Int) {
        val fontResourceId = fontMap[getItem(position)]
        if (view is TextView) {
            try {
                val typeface = fontResourceId?.let { ResourcesCompat.getFont(context, it) }
                    ?: ResourcesCompat.getFont(context, R.font.caveat_variablefont_wght)
                view.typeface = typeface
            } catch (e: Resources.NotFoundException) {
                // Fallback to a default font
                view.typeface = Typeface.DEFAULT
            }
        }
    }
}
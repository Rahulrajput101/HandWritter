package com.elkdocs.notestudio.presentation.page_edit_screen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.elkdocs.notestudio.R

class FontSizeAdapter(
    context: Context,
    private val fontSizeList: List<String>
) : ArrayAdapter<String>(context, 0, fontSizeList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView: View

        if (convertView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.spinner_item_layout, parent, false)
        } else {
            itemView = convertView
        }

        val fontSizeTextView: TextView = itemView.findViewById(R.id.textView_spinner_item)
        fontSizeTextView.text = fontSizeList[position]

        return itemView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}
package com.elkdocs.handwritter.presentation.page_edit_screen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.elkdocs.handwritter.R

class IconAdapter(
    context: Context,
    private val iconIds: Array<Int>
) : ArrayAdapter<Int>(context, 0, iconIds) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView: View

        if (convertView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_align_drop_down, parent, false)
        } else {
            itemView = convertView
        }

        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        iconImageView.setImageResource(iconIds[position])

        return itemView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}
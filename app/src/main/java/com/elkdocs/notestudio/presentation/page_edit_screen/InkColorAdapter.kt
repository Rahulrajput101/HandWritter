package com.elkdocs.notestudio.presentation.page_edit_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elkdocs.notestudio.R
import com.elkdocs.notestudio.databinding.ItemInkBinding
import com.elkdocs.notestudio.util.Constant.INK_COLOR_BLACK
import com.elkdocs.notestudio.util.Constant.INK_COLOR_BLUE
import com.elkdocs.notestudio.util.Constant.INK_COLOR_BLUE_GEL
import com.elkdocs.notestudio.util.Constant.INK_COLOR_GREEN
import com.elkdocs.notestudio.util.Constant.INK_COLOR_RED


class InkColorAdapter(
     private val onInkColorClickListener : (inkColorItem : InkColorItem) -> Unit
    ) : RecyclerView.Adapter<InkColorAdapter.MyViewHolder>() {

    private val inkColorItems = listOf(
        InkColorItem(R.drawable.black_ink, INK_COLOR_BLACK),
        InkColorItem(R.drawable.blue_ink, INK_COLOR_BLUE),
        InkColorItem(R.drawable.red_ink, INK_COLOR_RED),
        InkColorItem(R.drawable.green_ink, INK_COLOR_GREEN),
        InkColorItem(R.drawable.blue_gel_ink, INK_COLOR_BLUE_GEL)
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InkColorAdapter.MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: InkColorAdapter.MyViewHolder, position: Int) {
        holder.bind(inkColorItems[position],onInkColorClickListener)
    }

    override fun getItemCount(): Int {
        return inkColorItems.size
    }

    class MyViewHolder(private val binding : ItemInkBinding) :  RecyclerView.ViewHolder(binding.root) {
        companion object{
            fun from(parent: ViewGroup) : InkColorAdapter.MyViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemInkBinding.inflate(inflater,parent,false)
                return MyViewHolder(binding)
            }
        }

        fun bind(inkColorItem : InkColorItem, onInkColorClickListener : (inkColorItemId: InkColorItem) -> Unit){
            binding.colorInkImageView.setImageResource(inkColorItem.imageId)
            binding.colorInkImageView.setOnClickListener {
                 onInkColorClickListener(inkColorItem)
            }
        }
    }

    data class InkColorItem(val imageId: Int, val color : Int)


}
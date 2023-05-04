package com.elkdocs.handwritter.presentation.page_edit_screen

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.elkdocs.handwritter.R
import com.elkdocs.handwritter.databinding.ItemPageColorBinding

import com.elkdocs.handwritter.presentation.page_edit_screen.PageEditState.Companion.pageColorList


class PageColorAdapter(
    private val onPageColorClick: (pageColor : Int,pagePosition : Int) -> Unit,
    ) : RecyclerView.Adapter<PageColorAdapter.MyViewHolder>() {

    private val pageColorsList : List<Int> = pageColorList
    private var selectedPosition = RecyclerView.NO_POSITION // Set initial value to an invalid position


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageColorAdapter.MyViewHolder {

        return MyViewHolder.from(parent,this)
    }

    override fun onBindViewHolder(holder: PageColorAdapter.MyViewHolder, position: Int) {
            holder.bind(pageColorsList[position],onPageColorClick,holder.adapterPosition, selectedPosition)

    }

    override fun getItemCount(): Int {
        return pageColorsList.size
    }

    class MyViewHolder(private val binding : ItemPageColorBinding, private val adapter: PageColorAdapter) :  RecyclerView.ViewHolder(binding.root) {
        companion object{
            fun from(parent: ViewGroup, adapter: PageColorAdapter) : PageColorAdapter.MyViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemPageColorBinding.inflate(inflater,parent,false)
                return MyViewHolder(binding,adapter)
            }
        }

        fun bind(color: Int, onPageColorClick : (pageColor : Int,pagePosition : Int) -> Unit,position: Int, selectedPosition: Int){
            binding.pageColorItemCardView.setCardBackgroundColor(color)

            if (position == selectedPosition) {
                binding.headerPageColorItemCardView.setCardBackgroundColor(Color.BLACK)
            } else {
                binding.headerPageColorItemCardView.setCardBackgroundColor(Color.WHITE)
            }

            binding.pageColorItemCardView.setOnClickListener {
                setSelectedPosition(adapterPosition)
                onPageColorClick(color,position)
            }


        }

        fun setSelectedPosition(position: Int) {
            adapter.selectedPosition = position
            adapter.notifyDataSetChanged()
        }
    }



}
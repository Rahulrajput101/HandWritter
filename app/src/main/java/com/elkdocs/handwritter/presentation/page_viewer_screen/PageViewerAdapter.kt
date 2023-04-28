package com.elkdocs.handwritter.presentation.page_viewer_screen

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elkdocs.handwritter.databinding.ItemPageViewerBinding
import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.domain.model.MyPageModel


class PageViewerAdapter() : RecyclerView.Adapter<PageViewerAdapter.MyViewHolder>() {

    private var pageList: List<MyPageModel> = emptyList()

    fun setAllPages(pages: List<MyPageModel>){
        pageList = pages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
       return pageList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       holder.bind(pageList[position])
    }

    class MyViewHolder(private val binding : ItemPageViewerBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object{
            fun from(parent: ViewGroup) : MyViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemPageViewerBinding.inflate(inflater,parent,false)
                return MyViewHolder(binding)
            }
        }

        fun bind(page : MyPageModel){
          binding.ItemPageViewerCardView.setCardBackgroundColor(Color.BLUE)
        }



    }


}
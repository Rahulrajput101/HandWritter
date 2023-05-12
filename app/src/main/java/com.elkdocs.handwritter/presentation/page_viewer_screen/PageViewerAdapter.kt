package com.elkdocs.handwritter.presentation.page_viewer_screen

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.elkdocs.handwritter.R
import com.elkdocs.handwritter.databinding.ItemPageViewerBinding
import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.domain.model.MyPageModel


class PageViewerAdapter(
    val onDeleteClick: (myPageModel :MyPageModel) -> Unit,
    val onPageLongClick: (myPageModel : MyPageModel) -> Unit,
    private val onPageClick : (MyPageModel) -> Unit
) : RecyclerView.Adapter<PageViewerAdapter.MyViewHolder>() {

    private var pageList: List<MyPageModel> = emptyList()

    var selectedItems = ArrayList<MyPageModel>()
    var isSelectModeEnabled = false

    fun setIsSelectedModeEnabled(enabled: Boolean) {
        isSelectModeEnabled = enabled
    }

    fun setAllPages(pages: List<MyPageModel>){
        pageList = pages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPageViewerBinding.inflate(inflater,parent,false)
        return MyViewHolder(binding,parent.context)
    }

    override fun getItemCount(): Int {
       return pageList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = pageList[position]
//        holder.bind(item)
//       holder.itemView.setOnClickListener {
//           onPageClick(item)
//       }

        holder.itemView.rootView.findViewById<CardView>(R.id.image_card_view).setOnClickListener {
            if (isSelectModeEnabled) {
                item.isSelected = !item.isSelected // toggle isSelected state
                holder.bind(item, onDeleteClick, isSelectModeEnabled) // re-bind the view to update the checkbox state
            } else {
                onPageClick(item)
            }
        }

        holder.itemView.rootView.findViewById<CardView>(R.id.image_card_view).setOnLongClickListener {
            onPageLongClick(item)
            true
        }
        holder.bind(item, onDeleteClick, isSelectModeEnabled)
    }

    fun clearSelectedItems() {
        selectedItems.clear()
    }

    fun toggleSelectAll(){
        if(selectedItems.size == pageList.size){
            pageList.forEach {
                it.isSelected = false
            }
            selectedItems.clear()
        }else{
            pageList.forEach {
                it.isSelected = true
            }
        }
    }

    inner class MyViewHolder(private val binding : ItemPageViewerBinding,val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(page : MyPageModel,onDeleteClick: (myPageModel: MyPageModel) -> Unit,isSelectModeEnabled: Boolean ){
               //binding.imagePagePreviewFrameLayout.background
            binding.imageCardView.background = BitmapDrawable(context.resources,page.bitmap)
                //binding.ivItemImage.setImageBitmap(page.bitmap)
            binding.checkBox.isChecked = page.isSelected

            if (isSelectModeEnabled) {
                binding.checkBox.visibility = View.VISIBLE
            } else {
                binding.checkBox.visibility = View.GONE
            }

            binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    selectedItems.add(page)
                } else {
                    selectedItems.remove(page)
                }
            }
        }
    }


}
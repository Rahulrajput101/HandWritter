package com.elkdocs.handwritter.presentation.folder_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.elkdocs.handwritter.databinding.ItemFolderGridViewBinding
import com.elkdocs.handwritter.databinding.ItemFolderListViewBinding
import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.presentation.page_viewer_screen.PageViewerAdapter

class FolderAdapter(
    val  onFolderClick : (folderId : Long) -> Unit,
   private val isLinear : Boolean,
) : RecyclerView.Adapter<FolderAdapter.MyViewHolder>() {
    
    private var folderListWithPages: List<MyFolderModel> = emptyList()

    
    fun setAllFolder(folders: List<MyFolderModel>){
        folderListWithPages = folders
        notifyDataSetChanged()
    }

    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderAdapter.MyViewHolder {

       return if(isLinear){

           val inflater = LayoutInflater.from(parent.context)
           val binding = ItemFolderListViewBinding.inflate(inflater, parent, false)
           MyViewHolder(binding)
       } else {
           val inflater = LayoutInflater.from(parent.context)
           val binding = ItemFolderGridViewBinding.inflate(inflater, parent, false)
           MyViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = folderListWithPages[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onFolderClick(item.folderId!!)
        }
    }

    override fun getItemCount(): Int {
        return folderListWithPages.size
    }


    class MyViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(folder: MyFolderModel) {
            when (binding) {
                is ItemFolderListViewBinding -> {
                    binding.tvFolderNameListView.text = folder.folderName
                    binding.tvFolderItemCountListView.text = folder.pageCount.toString()
                    binding.tvFolderLastUpdatedListView.text = folder.lastUpdated.toString()
                }
                is ItemFolderGridViewBinding -> {
                    binding.tvFolderNameGridView.text = folder.folderName
                    binding.tvFolderItemCountGridView.text = folder.pageCount.toString()
                    binding.tvFolderLastUpdatedGridView.text = folder.lastUpdated.toString()
                }
                else -> throw IllegalArgumentException("Invalid view binding")
            }
        }
    }



}
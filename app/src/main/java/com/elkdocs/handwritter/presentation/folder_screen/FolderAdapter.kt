package com.elkdocs.handwritter.presentation.folder_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elkdocs.handwritter.databinding.ItemFolderListViewBinding
import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.presentation.page_viewer_screen.PageViewerAdapter

class FolderAdapter(
   val  onFolderClick : (folderId : Long) -> Unit
) : RecyclerView.Adapter<FolderAdapter.MyViewHolder>() {
    
    private var folderListWithPages: List<MyFolderModel> = emptyList()

    
    fun setAllFolder(folders: List<MyFolderModel>){
        folderListWithPages = folders
        notifyDataSetChanged()
    }

    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderAdapter.MyViewHolder {
       return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FolderAdapter.MyViewHolder, position: Int) {
        val item = folderListWithPages[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onFolderClick(item.folderId!!)
        }
    }

    override fun getItemCount(): Int {
        return folderListWithPages.size
    }


    class MyViewHolder(private val binding : ItemFolderListViewBinding) : RecyclerView.ViewHolder(binding.root){

        companion object{
            fun from(parent: ViewGroup) : FolderAdapter.MyViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemFolderListViewBinding.inflate(inflater,parent,false)
                return MyViewHolder(binding)
            }
        }

        fun bind(folder: MyFolderModel){
            binding.tvFolderNameListView.text = folder.folderName
            binding.tvFolderItemCountListView.text = folder.pageCount.toString()
            binding.tvFolderLastUpdatedListView.text = folder.lastUpdated.toString()
        }


    }
}
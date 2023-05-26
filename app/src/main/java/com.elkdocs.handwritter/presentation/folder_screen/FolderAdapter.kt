package com.elkdocs.handwritter.presentation.folder_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.elkdocs.handwritter.R
import com.elkdocs.handwritter.databinding.ItemFolderGridViewBinding
import com.elkdocs.handwritter.databinding.ItemFolderListViewBinding
import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.presentation.page_viewer_screen.PageViewerAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FolderAdapter(
    val  onFolderClick : (folderId : Long) -> Unit,
    val onFolderLongClick : (folder: MyFolderModel) -> Unit,
    private val isLinear : Boolean,
) : RecyclerView.Adapter<FolderAdapter.MyViewHolder>() {

    private var folderListWithPages: List<MyFolderModel> = emptyList()

    var selectedItems = ArrayList<MyFolderModel>()
    var isSelectModeEnabled = false

    fun setIsSelectedModeEnabled(enabled: Boolean) {
        isSelectModeEnabled = enabled
    }


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
        holder.bind(item,isSelectModeEnabled,selectedItems)
//        holder.itemView.setOnClickListener {
//            onFolderClick(item.folderId!!)
//        }

        holder.itemView.setOnClickListener {
            if (isSelectModeEnabled) {
                item.isSelected = !item.isSelected // toggle isSelected state
                holder.bind(item, isSelectModeEnabled,selectedItems) // re-bind the view to update the checkbox state
            } else {
                onFolderClick(item.folderId!!)
            }
        }

        holder.itemView.rootView.setOnLongClickListener {
            onFolderLongClick(item)
            true
        }
    }

    override fun getItemCount(): Int {
        return folderListWithPages.size
    }

    fun clearSelectedItems() {
        selectedItems.clear()
    }

    fun toggleSelectAll(){
        if(selectedItems.size == folderListWithPages.size){
            folderListWithPages.forEach {
                it.isSelected = false
            }
            selectedItems.clear()
        }else{
            clearSelectedItems()
            folderListWithPages.forEach {
                it.isSelected = true
                selectedItems.add(it)
            }
        }
    }

    class MyViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(folder: MyFolderModel,isSelectModeEnabled: Boolean,selectedItems : ArrayList<MyFolderModel>) {
            when (binding) {

                is ItemFolderListViewBinding -> {

                    binding.tvFolderNameListView.text = folder.folderName
                    binding.tvFolderItemCountListView.text = folder.pageCount.toString()

                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = folder.lastUpdated
                    }
                    val date = SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(calendar.time)

                    binding.tvFolderLastUpdatedListView.text = date
                    binding.listMainCheckbox.isChecked = folder.isSelected
                    binding.ivMoreOptionsListView.isVisible = !isSelectModeEnabled

                    if (isSelectModeEnabled) {
                        binding.listMainCheckbox.visibility = View.VISIBLE

                    } else {
                        binding.listMainCheckbox.visibility = View.GONE

                    }

                    binding.listMainCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) {
                            if(!selectedItems.any { it.folderId == folder.folderId})
                                selectedItems.add(folder)
                        } else {
                            selectedItems.remove(folder)
                        }
                    }
                }

                is ItemFolderGridViewBinding -> {

                    binding.tvFolderNameGridView.text = folder.folderName
                    binding.tvFolderItemCountGridView.text = folder.pageCount.toString()

                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = folder.lastUpdated
                    }

                    val date = SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(calendar.time)
                    binding.tvFolderLastUpdatedGridView.text = date
                    binding.gridMainCheckbox.isChecked = folder.isSelected
                    binding.ivMoreOptions.isVisible =!isSelectModeEnabled

                    if (isSelectModeEnabled) {
                        binding.gridMainCheckbox.visibility = View.VISIBLE
                    } else {
                        binding.gridMainCheckbox.visibility = View.GONE
                    }

                    binding.gridMainCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) {
                            if(!selectedItems.any { it.folderId == folder.folderId})
                            selectedItems.add(folder)
                        } else {
                            selectedItems.remove(folder)
                        }
                    }

                }
                else -> throw IllegalArgumentException("Invalid view binding")
            }
        }
    }



}
package com.elkdocs.notestudio.presentation.folder_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.elkdocs.notestudio.databinding.ItemFolderGridViewBinding
import com.elkdocs.notestudio.databinding.ItemFolderListViewBinding
import com.elkdocs.notestudio.domain.model.MyFolderModel
import com.elkdocs.notestudio.domain.model.MyPageModel
import com.elkdocs.notestudio.util.OtherUtility
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FolderAdapter(
    val onFolderClick : (folderId : Long,folderName : String) -> Unit,
    val onFolderLongClick : (folder: MyFolderModel) -> Unit,
    val onMoreOptionClick: (folderId: Long,folderName : String, date : String,itemImageView : ImageView) -> Unit,
    private val isLinear : Boolean,
) : RecyclerView.Adapter<FolderAdapter.MyViewHolder>() {

    private var folderListWithPages: List<MyFolderModel> = emptyList()
    private var pageList: List<MyPageModel> = emptyList()



    var selectedItems = ArrayList<MyFolderModel>()
    var isSelectModeEnabled = false

    fun setIsSelectedModeEnabled(enabled: Boolean) {
        isSelectModeEnabled = enabled
    }


    fun setAllFolder(folders: List<MyFolderModel>){
        folderListWithPages = folders
        notifyDataSetChanged()
    }

    fun setPages(pageList: List<MyPageModel>){
        this.pageList = pageList
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
        val pages  = ArrayList<MyPageModel>()

       //filtering the pages with same folder id
        val matchingPages = pageList.filter { it.folderId == item.folderId }
        if (matchingPages.isNotEmpty()) {
            pages.addAll(matchingPages)
        }
        val totalPagesInFolder = pages.size

        holder.bind(item,isSelectModeEnabled,totalPagesInFolder,selectedItems,onMoreOptionClick)

        holder.itemView.setOnClickListener {
            if (isSelectModeEnabled) {
                item.isSelected = !item.isSelected // toggle isSelected state
                holder.bind(item, isSelectModeEnabled,totalPagesInFolder,selectedItems ,onMoreOptionClick) // re-bind the view to update the checkbox state
            } else {
                onFolderClick(item.folderId!!,item.folderName)
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
        folderListWithPages.forEach {
            it.isSelected = false
        }
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


//    fun toggleSelectAll() {
//        if (selectedItems.size == folderListWithPages.size) {
//            selectedItems.clear()
//        } else {
//            selectedItems.addAll(folderListWithPages.filter { !selectedItems.contains(it) })
//        }
//        folderListWithPages.forEach { it.isSelected = selectedItems.contains(it) }
//        notifyDataSetChanged()
//    }

    class MyViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            folder: MyFolderModel,
            isSelectModeEnabled: Boolean,
            totalPagesInFolder : Int,
            selectedItems : ArrayList<MyFolderModel>,
            onShareClick : (folderId : Long,folderName : String,date:String, imageView : ImageView) -> Unit) {
            when (binding) {

                is ItemFolderListViewBinding -> {

                    binding.tvFolderNameListView.text = folder.folderName
                    binding.ivFolderIconListView.setImageBitmap(folder.folderIcon)
                    binding.tvFolderItemCountListView.text =  totalPagesInFolder.toString()


                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = folder.lastUpdated
                    }
                 //   val date = SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(calendar.time)
                    val date = SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.getDefault()).format(calendar.time)
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

                    binding.ivMoreOptionsListView.setOnClickListener {
                        folder.folderId?.let { it1 -> onShareClick(it1,folder.folderName,date,binding.ivMoreOptionsListView) }
                    }
                }

                is ItemFolderGridViewBinding -> {

                    binding.tvFolderNameGridView.text = folder.folderName
                    binding.ivFolderIconGridView.setImageBitmap(folder.folderIcon)
                    binding.tvFolderItemCountGridView.text =  totalPagesInFolder.toString()

                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = folder.lastUpdated
                    }

                    val date = SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.getDefault()).format(calendar.time)
                    binding.tvFolderLastUpdatedGridView.text =date
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

                    binding.ivMoreOptions.setOnClickListener {

                        folder.folderId?.let { it1 -> onShareClick(it1,folder.folderName,date,binding.ivMoreOptions) }
                    }
                }
                else -> throw IllegalArgumentException("Invalid view binding")
            }
        }
    }

}
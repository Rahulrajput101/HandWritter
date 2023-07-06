package com.elkdocs.notestudio.presentation.folder_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elkdocs.notestudio.R
import com.elkdocs.notestudio.databinding.ItemInkBinding

class ThemesAdapter(
    private val onItemClickListener : (themeColors : ThemeColors) -> Unit
) : RecyclerView.Adapter<ThemesAdapter.MyViewHolder>() {

    private val themeColors = listOf(
      ThemeColors(R.drawable.blue_theme_icon ,R.style.AppTheme) ,
      ThemeColors(R.drawable.pink_theme_icon,R.style.AppTheme_pink),
      ThemeColors(R.drawable.teal_theme_icon,R.style.AppTheme_teal),
      ThemeColors(R.drawable.light_green_theme_icon,R.style.AppTheme_Green),
      ThemeColors(R.drawable.purple_theme_icon,R.style.AppTheme_purple)

    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(themeColors[position],onItemClickListener)
    }

    override fun getItemCount(): Int {
        return themeColors.size
    }

    class MyViewHolder(private val binding : ItemInkBinding) :  RecyclerView.ViewHolder(binding.root) {
        companion object{
            fun from(parent: ViewGroup) : MyViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemInkBinding.inflate(inflater,parent,false)
                return MyViewHolder(binding)
            }
        }

        fun bind(themeColors: ThemeColors , onItemClickListener : (themeColor : ThemeColors) -> Unit){
            binding.colorInkImageView.setImageResource(themeColors.imageId)
            binding.colorInkImageView.setOnClickListener {
                onItemClickListener(themeColors)
            }
        }
    }

    data class ThemeColors(val imageId: Int, val themeStyle: Int)


}
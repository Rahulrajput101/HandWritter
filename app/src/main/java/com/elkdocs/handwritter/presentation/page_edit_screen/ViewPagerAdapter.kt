//package com.elkdocs.handwritter.presentation.page_edit_screen
//
//import android.graphics.Color
//import android.text.Editable
//import android.text.TextWatcher
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import androidx.viewpager2.widget.ViewPager2
//import com.elkdocs.handwritter.databinding.ItemEditPageBinding
//import com.elkdocs.handwritter.databinding.ItemPageViewerBinding
//import com.elkdocs.handwritter.domain.model.MyPageModel
//import com.elkdocs.handwritter.presentation.page_viewer_screen.PageViewerAdapter
//import kotlinx.coroutines.delay
//import java.util.Timer
//import java.util.TimerTask
//
//class ViewPagerAdapter(
//    private val textUpdated : (String) -> Unit
//): RecyclerView.Adapter<ViewPagerAdapter.MyViewHolder>() {
//
//    private var pageList: List<MyPageModel> = emptyList()
//    private var pageEditState : PageEditState = PageEditState()
//
//    fun setUpdatedPageEditState(pageEditState: PageEditState){
//        this.pageEditState = pageEditState
//        notifyDataSetChanged()
//    }
//
//    fun setAllPages(pages: List<MyPageModel>){
//        pageList = pages
//        notifyDataSetChanged()
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        return MyViewHolder.from(parent)
//    }
//
//    override fun getItemCount(): Int {
//        return pageList.size
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        val item = pageList[position]
//        holder.bind(item,pageEditState,textUpdated)
//
//    }
//
//    class MyViewHolder(private val binding : ItemEditPageBinding) : RecyclerView.ViewHolder(binding.root) {
//
//        companion object{
//            fun from(parent: ViewGroup) : MyViewHolder {
//                val inflater = LayoutInflater.from(parent.context)
//                val binding = ItemEditPageBinding.inflate(inflater,parent,false)
//                return MyViewHolder(binding)
//            }
//        }
//
//        fun bind(page : MyPageModel,pageEditState: PageEditState,textUpdated : (String) -> Unit){
//
////            var timer: Timer? = null
////            var isTyping = false
////
////            // Add a TextWatcher to the EditText
////            binding.ivTextEditView.addTextChangedListener(object : TextWatcher {
////                override fun afterTextChanged(s: Editable?) {
////                    // If the user finishes typing, stop the timer and call the lambda function
////                    if (!isTyping) {
////                        timer?.cancel()
////                        timer = null
////                        textUpdated(s.toString())
////                    }
////                    isTyping = false
////                }
////
////                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
////
////                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
////                    // If the user starts typing, start the timer and set the flag
////                    if (!isTyping) {
////                        timer = Timer()
////                        isTyping = true
////                    }
////
////                    // Wait for 1 second after the user finishes typing before calling the lambda function
////                    timer?.schedule(object : TimerTask() {
////                        override fun run() {
////                            if (!isTyping) {
////                                textUpdated(s.toString())
////                            }
////                        }
////                    }, 1000)
////                }
////            })
//
////            binding.ivTextEditView.addTextChangedListener(object : TextWatcher {
////                override fun afterTextChanged(s: Editable?) {
////                    // Call the lambda function with the new text value
////
////                    textUpdated(s.toString())
////                }
////
////                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
////
////                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
////            })
//
//        }
//
//    }
//
//}
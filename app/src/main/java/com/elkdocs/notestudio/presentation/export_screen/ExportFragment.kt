package com.elkdocs.notestudio.presentation.export_screen

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.elkdocs.notestudio.R
import com.elkdocs.notestudio.databinding.FragmentExportDocumentBinding
import com.elkdocs.notestudio.presentation.folder_screen.FolderViewModel
import com.elkdocs.notestudio.util.PdfUtility.createFiles
import com.elkdocs.notestudio.util.PdfUtility.generatePdfs
import com.elkdocs.notestudio.util.PdfUtility.getSizeOfListOfBitmapsInKb
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@AndroidEntryPoint
class ExportFragment : Fragment() {
    private lateinit var binding: FragmentExportDocumentBinding
    private val viewModel: ExportViewModel by viewModels()
    private val navArgs: ExportFragmentArgs by navArgs()
    private lateinit var popupMenuFileSize: PopupMenu
    private var documentType = "PDF"
    private lateinit var mProgressDialog: Dialog


    private var actualSizeType = "Kb"
    private var largeSizeType = "Kb"
    private var mediumSizeType = "Kb"

    private var selectedQuality = 100

    private val folderPickerLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    if (uri != null) {
                        val documentFile = DocumentFile.fromTreeUri(requireContext(), uri)
                        if (documentType == "PDF") {
                            savePdfToDocumentFile(requireContext(), documentFile!!, selectedQuality)
                        } else {

                            saveBitmapToFile(documentFile!!, selectedQuality)
                        }
                    }
                }
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExportDocumentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showProgressDialog()
        handleRenameEvent()
        binding.etFileName.setText(navArgs.folderName)
        setUpPopUpMenuFileFormat()
        setUpPopUpMenuFileSize()
        lifecycleScope.launch {
            updateFileSizeMenuOptions()
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.etFileName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed in this case
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Called when the text is changing
            }

            override fun afterTextChanged(s: Editable?) {
                // Called after the text has changed
                val folderName = s?.toString() ?: ""
                viewModel.onEvent(ExportEvent.UpdateFolderName(folderName, navArgs.folderId))
            }
        })

        hideProgressDialog()

        binding.llSaveToStorage.setOnClickListener {
            val bitmaplist = viewModel.allPages.value.map {
                it.bitmap
            }

            if (documentType != "PDF" && bitmaplist.size > 1) {
                Toast.makeText(
                    requireContext(),
                    "Seems There are more than 1 Image so Can't be converted into JPEG",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if (!binding.etFileName.text.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    folderPickerLauncher.launch(intent)
                } else {
                    Toast.makeText(requireContext(), "Please enter the name", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }


    private fun setUpPopUpMenuFileFormat() {
        val popupMenuFormat = PopupMenu(requireContext(), binding.tvFileFormat)
        popupMenuFormat.inflate(R.menu.file_format_menu)
        popupMenuFormat.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_PDF_format -> {
                    binding.tvFileFormat.text = "PDF"
                    documentType = "PDF"
                }

                R.id.menu_JPEG_format -> {
                    binding.tvFileFormat.text = "JPEG"
                    documentType = "JPEG"
                }
            }
            binding.tvFileFormat.text = item.title
            documentType = item.title.toString()
            binding.tvFileFormat.clearFocus()
            true
        }

        popupMenuFormat.setOnDismissListener {
            binding.ivFormatDropUp.visibility = View.GONE
            binding.ivFormatDropDown.visibility = View.VISIBLE
        }

        binding.tvFileFormat.setOnFocusChangeListener { _, hasFocus ->
            binding.tvFileFormat.clearFocus()
            binding.ivFormatDropUp.visibility = View.VISIBLE
            binding.ivFormatDropDown.visibility = View.GONE
            if (hasFocus) {
                popupMenuFormat.show()
            } else {
                popupMenuFormat.dismiss()
                binding.ivFormatDropUp.visibility = View.GONE
                binding.ivFormatDropDown.visibility = View.VISIBLE
                binding.tvFileFormat.clearFocus()
            }
        }
    }

    private fun setUpPopUpMenuFileSize() {
        popupMenuFileSize = PopupMenu(requireContext(), binding.tvFileSize)
        popupMenuFileSize.inflate(R.menu.file_quality_menu)
         popupMenuFileSize.setOnMenuItemClickListener { item ->
             popupMenuFileSize.show()
            binding.tvFileSize.text = item.title
            selectedQuality = when (item.itemId) {
                R.id.menu_item_high_quality -> {
                    binding.tvFileSize.text = "High"
                    selectedQuality = 100
                    selectedQuality

                }

                R.id.menu_item_moderate_quality -> {
                    binding.tvFileSize.text = "Moderate"
                    selectedQuality = 75
                    selectedQuality
                }

                R.id.menu_item_low_quality -> {
                    binding.tvFileSize.text = "Low"
                    selectedQuality = 50
                    selectedQuality
                }

                else -> 100
            }
             binding.tvFileSize.clearFocus()
            true
        }

        popupMenuFileSize.setOnDismissListener {
            binding.ivFileSizeDropUp.visibility = View.GONE
            binding.ivFileSizeDropDown.visibility = View.VISIBLE

        }

        binding.tvFileSize.setOnFocusChangeListener { _, hasFocus ->
            binding.tvFileSize.clearFocus()
            binding.ivFileSizeDropUp.visibility = View.VISIBLE
            binding.ivFileSizeDropDown.visibility = View.GONE
            if (hasFocus) {
                popupMenuFileSize.show()
            } else {
                popupMenuFileSize.dismiss()
                binding.ivFileSizeDropUp.visibility = View.GONE
                binding.ivFileSizeDropDown.visibility = View.VISIBLE
                binding.tvFileSize.clearFocus()
            }
        }
    }


    private suspend fun getBitmap(folderId: Long, folderName: String): List<Bitmap> {
        return withContext(Dispatchers.IO) {
            val pages = viewModel.getAllPagesById(folderId)
            val pageModels = pages.firstOrNull() ?: emptyList()
            pageModels.map { it.bitmap }
        }
    }

    private suspend fun calculateBitmapSizes(bitmapList: List<Bitmap>): Triple<Int, Int, Int> {
        return withContext(Dispatchers.Default) {
            val actualSize = getSizeOfListOfBitmapsInKb(bitmapList, 100)
            val largeSize = getSizeOfListOfBitmapsInKb(bitmapList, 75)
            val mediumSize = getSizeOfListOfBitmapsInKb(bitmapList, 50)
            Triple(actualSize, largeSize, mediumSize)
        }
    }

    private suspend fun updateFileSizeMenuOptions() {
        val bitmapList = getBitmap(navArgs.folderId, navArgs.folderName)

        if (bitmapList.isNotEmpty()) {


            var (actualSize, largeSize, mediumSize) = calculateBitmapSizes(bitmapList)

            withContext(Dispatchers.Main) {
                var actualSizeMenu = popupMenuFileSize.menu.findItem(R.id.menu_item_high_quality)
                if (actualSize > 999) {
                    actualSize /= 1024
                    actualSizeMenu.title = "High"
                    actualSizeType = "Mb"
                } else {
                    actualSizeMenu.title = "High"
                }

                val largeSizeMenu = popupMenuFileSize.menu.findItem(R.id.menu_item_low_quality)
                if (largeSize > 999) {
                    largeSize /= 1024
                    largeSizeMenu.title = "Low"
                    largeSizeType = "Mb"
                } else {
                    largeSizeMenu.title = "Low"
                }

                val mediumSizeMenu =
                    popupMenuFileSize.menu.findItem(R.id.menu_item_moderate_quality)
                if (mediumSize > 999) {
                    mediumSize /= 1024
                    mediumSizeMenu.title = "Moderate"
                    mediumSizeType = "Mb"
                } else {
                    mediumSizeMenu.title = "Moderate"
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "There are no pages", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun savePdfToDocumentFile(context: Context, documentFile: DocumentFile, quality: Int) {
        lifecycleScope.launch {
            showProgressDialog()

            withContext(Dispatchers.IO) {
                val pdfFileName = "${binding.etFileName.text}"
                val pdfFile = createFiles(requireContext(), documentFile, fileName = pdfFileName)

                if (pdfFile != null) {
                    val bitmapList = viewModel.allPages.value.map {
                        it.bitmap
                    }
                    generatePdfs(requireContext(), pdfFile, bitmapList, quality)
                } else {
                    // Failed to create PDF file
                }
            }

            hideProgressDialog()

            Toast.makeText(
                requireContext(),
                "File has been saved in the selected folder",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun saveBitmapToFile(documentFile: DocumentFile, quality: Int) {
        val pdfFileName = "${binding.etFileName.text}.jpeg"
        val file = createFiles(
            requireContext(),
            documentFile,
            fileName = pdfFileName,
            mimeType = "image/jpeg"
        )
        val outputStream = file?.uri?.let { requireActivity().contentResolver.openOutputStream(it) }
        val bitmap = viewModel.allPages.value.map {
            it.bitmap
        }

        if (outputStream != null) {
            try {

                bitmap.first().compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Log.v("Tag", " output stream failed")
        }

        Toast.makeText(
            requireContext(),
            "Image has been saved in the selected folder",
            Toast.LENGTH_SHORT
        ).show()

    }

    private fun showProgressDialog() {
        mProgressDialog = Dialog(requireContext())
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.show()
    }

    private fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    private fun handleRenameEvent() {
        lifecycleScope.launch {
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    FolderViewModel.RenameFolderName.Success -> { }
                    is FolderViewModel.RenameFolderName.Error -> {
                        Toast.makeText(requireContext(), "Name Already existed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }


}
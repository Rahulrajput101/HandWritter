package com.elkdocs.notestudio.util

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object PdfUtility {

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    fun createPdf(context: Context, bitmaps: List<Bitmap>,fileName : String,quality : Int = 100, onPdfGenerated: (File) -> Unit) {
        executor.execute {
            val pdfFile = createPdfFile(context,fileName)
            generatePdf(pdfFile, bitmaps,quality)
            onPdfGenerated(pdfFile)

            // Open the PDF file using a PDF viewer activity
        }
    }

    private fun createPdfFile(context: Context,folderName: String?): File {
        val fileName = "${folderName}_pdf.pdf"
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(dir, fileName)
    }

    fun createFiles(context: Context, documentFile: DocumentFile,mimeType : String ="application/pdf", fileName: String): DocumentFile? {
        val newFile = documentFile.createFile(mimeType, fileName)
        return newFile
    }

     fun generatePdf(pdfFile: File, bitmaps: List<Bitmap>,quality: Int) {
        val document = Document()
        val writer = PdfWriter.getInstance(document, FileOutputStream(pdfFile))
        document.open()

        for (bitmap in bitmaps) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            val imageBytes = stream.toByteArray()
            val image = Image.getInstance(imageBytes)
            document.pageSize = image
            document.newPage()
            image.setAbsolutePosition(0f, 0f)
            document.add(image)
        }
        document.close()
        writer.close()
    }

    fun generatePdfs(context: Context,pdfFile: DocumentFile, bitmaps: List<Bitmap>, quality: Int) {
        val outputStream = context.contentResolver.openOutputStream(pdfFile.uri)
        outputStream.use { stream ->
            if (stream != null) {
                val document = Document()
                val writer = PdfWriter.getInstance(document, stream)
                document.open()
                for (bitmap in bitmaps) {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
                    val imageBytes = byteArrayOutputStream.toByteArray()
                    val image = Image.getInstance(imageBytes)
                    document.pageSize = image
                    document.newPage()
                    image.setAbsolutePosition(0f, 0f)
                    document.add(image)
                }
                document.close()
                writer.close()
            } else {
               Log.v("Tag","Failed to create pdf")
            }
        }
    }

     fun openPdfFile(context: Context, pdfFile: File) : Boolean {
         return try{
             val uri = FileProvider.getUriForFile(context, "com.elkdocs.notestudio.fileprovider", pdfFile)
             val intent = Intent(Intent.ACTION_VIEW)
             intent.setDataAndType(uri, "application/pdf")
             intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
             context.startActivity(intent)
             true
         }catch ( e : ActivityNotFoundException){
             e.printStackTrace()
             false
         }
    }


    fun sharePdf(context: Context, pdfFile: File) {
        val uri = FileProvider.getUriForFile(context, "com.elkdocs.notestudio.fileprovider", pdfFile)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/pdf"
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(shareIntent, "Share PDF"))
    }

    fun getSizeOfListOfBitmapsInKb(myBitmapList: List<Bitmap>, quality: Int): Int {
        var totalSize = 0

        // Loop through the bitmaps and calculate the size
        for (bitmap in myBitmapList) {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

            totalSize += outputStream.size()
        }

        return totalSize / 1024
    }



}
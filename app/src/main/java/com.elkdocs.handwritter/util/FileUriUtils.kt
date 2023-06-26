package com.elkdocs.handwritter.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object FileUriUtils {



    fun getDuplicateUriFromUri(context:Context,uri: Uri?, newFile: File): Uri? {
        val newFilePath = newFile.path
        var success = false
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            // Open input stream from the URI
            inputStream = context.contentResolver.openInputStream(uri!!)

            // Create output stream to the new file path
            outputStream = FileOutputStream(newFilePath)

            // Copy the data from the input stream to the output stream
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream!!.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            // Success
            success = true
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            // Close the streams
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return if(success){
            FileProvider.getUriForFile(context, "com.example.docscanner.fileprovider", newFile)
        }else{
            null
        }
    }

    fun replaceImageWithBitmap(context: Context, imageUri: Uri, bitmap: Bitmap) {
        val outputStream = context.contentResolver.openOutputStream(imageUri)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream?.flush()
        outputStream?.close()
    }

    fun getUriSize(uri: Uri, context: Context): Long {
    val inputStream = context.contentResolver.openInputStream(uri)
    val size = inputStream?.available()?.div(1024L) ?: 0L
    inputStream?.close()
    return size
    }

    fun compressImage(bitmap: Bitmap, maxSizeInBytes: Int): Bitmap {
        val outputStream = ByteArrayOutputStream()
        var quality = 100
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        while (outputStream.toByteArray().size > maxSizeInBytes && quality > 0) {
            quality -= 5
            outputStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        }
        val compressedBitmap = BitmapFactory.decodeStream(ByteArrayInputStream(outputStream.toByteArray()), null, null)
        return compressedBitmap!!
    }

    fun reduceBitmapQuality(bitmap: Bitmap): Bitmap {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        val compressedBitmap = BitmapFactory.decodeStream(ByteArrayInputStream(outputStream.toByteArray()))
        outputStream.close()
        return compressedBitmap
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


    fun printPdf(context: Context,pdfFile: File) {
        val pdfUri = FileProvider.getUriForFile(context, "com.example.docscanner.fileprovider", pdfFile)
        // Print intent
        val printIntent = Intent(Intent.ACTION_SEND)
        printIntent.type = "application/pdf"
        printIntent.putExtra(Intent.EXTRA_STREAM, pdfUri)
        printIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        // Create chooser with print option first
        val printChooserIntent = Intent.createChooser(
            printIntent,
            "Print PDF"
        )
        val printOptions = mutableListOf<Intent>()
        val packageManager = context.packageManager
        val activities = packageManager.queryIntentActivities(printIntent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in activities) {
            val packageName = resolveInfo.activityInfo.packageName
            val intent = Intent(printIntent)
            intent.setPackage(packageName)
            intent.component = ComponentName(packageName, resolveInfo.activityInfo.name)
            if (resolveInfo.activityInfo.exported) {
                if (packageName == "com.android.printspooler") {
                    printOptions.add(0, intent)
                } else {
                    printOptions.add(intent)
                }
            }
        }
        printChooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, printOptions.toTypedArray())
        printChooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // Show print options
        context.startActivity(printChooserIntent)

    }

//    fun convertImageToPdf(context: Context,myImageList: ArrayList<String>,pdfFile: File,quality: Int){
//
//                // Create a new document and a PDF writer
//                val document = Document()
//                val writer = PdfWriter.getInstance(document, FileOutputStream(pdfFile))
//
//                // Open the document
//                        document.open()
//
//
//
//                // Loop through the image URIs and add each image to the PDF document
//                for (uri in myImageList) {
//                    // Open an input stream for the image URI
//                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri.toUri())
//
//                    inputStream?.use {
//                        // Create a Bitmap from the input stream
//                        val bitmap = BitmapFactory.decodeStream(inputStream)
//
//                        // Compress the bitmap to the desired image quality
//                        val outputStream = ByteArrayOutputStream()
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
//
//                        // Create an Image object from the compressed bitmap bytes
//                        val image = Image.getInstance(outputStream.toByteArray())
//
//                        // Scale the image to fit the document size
//                        image.scaleToFit(document.pageSize.width, document.pageSize.height)
//
//                        // Add the image to the document
//                        document.add(image)
//
//                        // Add a new page for the next image
//                        document.newPage()
//                    }
//                }
//                // Close the document and the PDF writer
//                document.close()
//                writer.close()
//
//
//    }


}
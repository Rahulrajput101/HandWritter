package com.elkdocs.handwritter.domain.use_cases

import android.graphics.Bitmap
import android.os.Environment
import android.os.Handler
import android.os.Looper
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

//class CreatePdf {
//    operator fun invoke(bitmaps: List<Bitmap>){
//        val pdfFile = File(
//            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() +
//                    File.separator + "HandWriter_pdf" + System.currentTimeMillis() / 1000 + ".pdf"
//        )
//
//        val executor: ExecutorService = Executors.newSingleThreadExecutor()
//        val handler = Handler(Looper.getMainLooper())
//        // Create a new document and a PDF writer
//        val document = Document()
//        val writer = PdfWriter.getInstance(document, FileOutputStream(pdfFile))
//
//        executor.execute {
//            //Background work here
//
//            // Open the document
//            document.open()
//
//            // Loop through the bitmaps and add each image to the PDF document
//            for (bitmap in bitmaps) {
//                val stream = ByteArrayOutputStream()
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
//                val imageBytes: ByteArray = stream.toByteArray()
//                val image = com.itextpdf.text.Image.getInstance(imageBytes)
//
//                // Add the image to the document
//                document.pageSize = image
//                document.newPage()
//                image.setAbsolutePosition(0f, 0f)
//                document.add(image)
//            }
//
//            // Close the document and the PDF writer
//            document.close()
//            writer.close()
//    }
//}
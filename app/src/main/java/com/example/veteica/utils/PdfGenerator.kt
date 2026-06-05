package com.example.veteica.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.veteica.R
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerator {

    fun generateOwnerFicha(
        context: Context,
        ownerName: String,
        ownerPhone: String,
        ownerEmail: String,
        ownerAddress: String,
        petsList: List<Pair<String, String>>
    ): File? {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()

            var yPosition = 50f
            val margin = 50f

            // Título
            paint.color = Color.parseColor("#2E7D32")
            paint.textSize = 24f
            paint.isFakeBoldText = true
            canvas.drawText("VETEICA CLINIC", margin, yPosition, paint)
            yPosition += 30f

            paint.color = Color.BLACK
            paint.textSize = 18f
            paint.isFakeBoldText = true
            canvas.drawText("FICHA DEL DUEÑO", margin, yPosition, paint)
            yPosition += 40f

            paint.color = Color.GRAY
            paint.strokeWidth = 2f
            canvas.drawLine(margin, yPosition, 545f, yPosition, paint)
            yPosition += 30f

            paint.color = Color.BLACK
            paint.textSize = 14f
            paint.isFakeBoldText = false
            canvas.drawText("Nombre: $ownerName", margin, yPosition, paint)
            yPosition += 25f
            canvas.drawText("Teléfono: $ownerPhone", margin, yPosition, paint)
            yPosition += 25f
            canvas.drawText("Correo: $ownerEmail", margin, yPosition, paint)
            yPosition += 25f
            canvas.drawText("Dirección: $ownerAddress", margin, yPosition, paint)
            yPosition += 40f

            paint.color = Color.parseColor("#2E7D32")
            paint.textSize = 16f
            paint.isFakeBoldText = true
            canvas.drawText("MASCOTAS ASOCIADAS", margin, yPosition, paint)
            yPosition += 30f

            paint.color = Color.parseColor("#F0F0F0")
            paint.style = Paint.Style.FILL
            canvas.drawRect(margin, yPosition - 20, 545f, yPosition + 10, paint)
            paint.color = Color.BLACK
            paint.textSize = 12f
            paint.isFakeBoldText = true
            canvas.drawText("Nombre", margin + 10, yPosition, paint)
            canvas.drawText("Especie", margin + 200, yPosition, paint)
            yPosition += 30f

            paint.isFakeBoldText = false
            for (pet in petsList) {
                canvas.drawText(pet.first, margin + 10, yPosition, paint)
                canvas.drawText(pet.second, margin + 200, yPosition, paint)
                yPosition += 25f
            }

            yPosition = 820f
            paint.textSize = 10f
            paint.color = Color.GRAY
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            canvas.drawText("Documento generado: ${dateFormat.format(Date())}", margin, yPosition, paint)

            pdfDocument.finishPage(page)

            // Guardar en Download para que sea visible
            val fileName = "Ficha_${ownerName.replace(" ", "_")}.pdf"
            val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                    }
                }
                null
            } else {
                val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadDir, fileName)
                pdfDocument.writeTo(FileOutputStream(file))
                file
            }

            pdfDocument.close()

            Toast.makeText(context, "PDF guardado en: Downloads/$fileName", Toast.LENGTH_LONG).show()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    fun generateCarnetVeterinario(
        context: Context,
        ownerName: String,
        petName: String,
        petSpecies: String,
        petBreed: String,
        petColor: String
    ): File? {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(400, 600, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()

            var yPosition = 50f
            val margin = 50f

            paint.color = Color.parseColor("#0c607d")
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f
            canvas.drawRect(margin, yPosition - 20, 350f, 550f, paint)

            paint.style = Paint.Style.FILL
            paint.color = Color.parseColor("#2E7D32")
            paint.textSize = 20f
            paint.isFakeBoldText = true
            canvas.drawText("VETEICA CLINIC", margin + 50, yPosition, paint)
            yPosition += 30f

            paint.color = Color.BLACK
            paint.textSize = 14f
            paint.isFakeBoldText = true
            canvas.drawText("CARNET VETERINARIO", margin + 60, yPosition, paint)
            yPosition += 40f

            paint.color = Color.GRAY
            paint.strokeWidth = 1f
            canvas.drawLine(margin, yPosition, 350f, yPosition, paint)
            yPosition += 20f

            paint.color = Color.BLACK
            paint.textSize = 11f
            paint.isFakeBoldText = false
            canvas.drawText("Dueño:", margin + 10, yPosition, paint)
            paint.isFakeBoldText = true
            canvas.drawText(ownerName, margin + 100, yPosition, paint)
            yPosition += 25f

            paint.isFakeBoldText = false
            canvas.drawText("Mascota:", margin + 10, yPosition, paint)
            paint.isFakeBoldText = true
            canvas.drawText(petName, margin + 100, yPosition, paint)
            yPosition += 25f

            paint.isFakeBoldText = false
            canvas.drawText("Especie:", margin + 10, yPosition, paint)
            paint.isFakeBoldText = true
            canvas.drawText(petSpecies, margin + 100, yPosition, paint)
            yPosition += 25f

            paint.isFakeBoldText = false
            canvas.drawText("Raza:", margin + 10, yPosition, paint)
            paint.isFakeBoldText = true
            canvas.drawText(petBreed, margin + 100, yPosition, paint)
            yPosition += 25f

            paint.isFakeBoldText = false
            canvas.drawText("Color:", margin + 10, yPosition, paint)
            paint.isFakeBoldText = true
            canvas.drawText(petColor, margin + 100, yPosition, paint)
            yPosition += 40f

            paint.color = Color.parseColor("#FF9800")
            paint.textSize = 14f
            paint.isFakeBoldText = true
            canvas.drawText("VETEICA", margin + 120, yPosition, paint)
            yPosition += 20f
            canvas.drawText("CLINIC", margin + 125, yPosition, paint)

            pdfDocument.finishPage(page)

            val fileName = "Carnet_${ownerName.replace(" ", "_")}_${petName.replace(" ", "_")}.pdf"
            val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                    }
                }
                null
            } else {
                val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadDir, fileName)
                pdfDocument.writeTo(FileOutputStream(file))
                file
            }

            pdfDocument.close()

            Toast.makeText(context, "PDF guardado en: Downloads/$fileName", Toast.LENGTH_LONG).show()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }
}
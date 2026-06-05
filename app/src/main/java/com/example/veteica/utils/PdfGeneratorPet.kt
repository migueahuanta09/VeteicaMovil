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
import com.example.veteica.R
import com.example.veteica.models.Historial
import com.example.veteica.models.Vacuna
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfGeneratorPet {

    fun generatePetMedicalRecord(
        context: Context,
        petName: String,
        petSpecies: String,
        petBreed: String,
        petAge: Int,
        petGender: String,
        petWeight: Double,
        petColor: String,
        petOwnerName: String,
        petNotes: String,
        historialList: List<Historial>,
        vacunasList: List<Vacuna>
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
            canvas.drawText("FICHA MÉDICA DE MASCOTA", margin, yPosition, paint)
            yPosition += 40f

            // Línea separadora
            paint.color = Color.GRAY
            paint.strokeWidth = 2f
            canvas.drawLine(margin, yPosition, 545f, yPosition, paint)
            yPosition += 30f

            // Datos de la mascota
            paint.color = Color.BLACK
            paint.textSize = 14f
            paint.isFakeBoldText = false
            canvas.drawText("Nombre: $petName", margin, yPosition, paint)
            yPosition += 25f
            canvas.drawText("Especie: $petSpecies", margin, yPosition, paint)
            yPosition += 25f
            canvas.drawText("Raza: $petBreed", margin, yPosition, paint)
            yPosition += 25f
            canvas.drawText("Edad: $petAge años", margin, yPosition, paint)
            yPosition += 25f
            canvas.drawText("Sexo: $petGender", margin, yPosition, paint)
            yPosition += 25f
            canvas.drawText("Peso: ${petWeight} kg", margin, yPosition, paint)
            yPosition += 25f
            canvas.drawText("Color: $petColor", margin, yPosition, paint)
            yPosition += 25f
            canvas.drawText("Dueño: $petOwnerName", margin, yPosition, paint)
            yPosition += 40f

            // Notas
            if (petNotes.isNotEmpty()) {
                paint.color = Color.parseColor("#2E7D32")
                paint.textSize = 16f
                paint.isFakeBoldText = true
                canvas.drawText("NOTAS ADICIONALES", margin, yPosition, paint)
                yPosition += 25f
                paint.color = Color.BLACK
                paint.textSize = 12f
                paint.isFakeBoldText = false
                canvas.drawText(petNotes, margin, yPosition, paint)
                yPosition += 40f
            }

            // Historial clínico
            if (historialList.isNotEmpty()) {
                paint.color = Color.parseColor("#2E7D32")
                paint.textSize = 16f
                paint.isFakeBoldText = true
                canvas.drawText("HISTORIAL CLÍNICO", margin, yPosition, paint)
                yPosition += 25f

                paint.color = Color.parseColor("#F0F0F0")
                paint.style = Paint.Style.FILL
                canvas.drawRect(margin, yPosition - 20, 545f, yPosition + 10, paint)
                paint.color = Color.BLACK
                paint.textSize = 11f
                paint.isFakeBoldText = true
                canvas.drawText("Consulta", margin + 10, yPosition, paint)
                canvas.drawText("Fecha", margin + 130, yPosition, paint)
                canvas.drawText("Diagnóstico", margin + 230, yPosition, paint)
                canvas.drawText("Veterinario", margin + 400, yPosition, paint)
                yPosition += 25f

                paint.isFakeBoldText = false
                for (historial in historialList) {
                    if (yPosition > 800) break
                    canvas.drawText(historial.consulta.take(15), margin + 10, yPosition, paint)
                    canvas.drawText(historial.fecha, margin + 130, yPosition, paint)
                    canvas.drawText(historial.diagnostico.take(20), margin + 230, yPosition, paint)
                    canvas.drawText(historial.veterinario.take(12), margin + 400, yPosition, paint)
                    yPosition += 20f
                }
                yPosition += 20f
            }

            // Vacunas
            if (vacunasList.isNotEmpty()) {
                paint.color = Color.parseColor("#FF9800")
                paint.textSize = 16f
                paint.isFakeBoldText = true
                canvas.drawText("VACUNAS", margin, yPosition, paint)
                yPosition += 25f

                paint.color = Color.parseColor("#F0F0F0")
                paint.style = Paint.Style.FILL
                canvas.drawRect(margin, yPosition - 20, 545f, yPosition + 10, paint)
                paint.color = Color.BLACK
                paint.textSize = 11f
                paint.isFakeBoldText = true
                canvas.drawText("Vacuna", margin + 10, yPosition, paint)
                canvas.drawText("Cantidad", margin + 180, yPosition, paint)
                canvas.drawText("Fecha", margin + 300, yPosition, paint)
                yPosition += 25f

                paint.isFakeBoldText = false
                for (vacuna in vacunasList) {
                    if (yPosition > 800) break
                    canvas.drawText(vacuna.nombre, margin + 10, yPosition, paint)
                    canvas.drawText(vacuna.cantidad, margin + 180, yPosition, paint)
                    canvas.drawText(vacuna.fecha, margin + 300, yPosition, paint)
                    yPosition += 20f
                }
            }

            // Fecha de generación
            yPosition = 820f
            paint.textSize = 10f
            paint.color = Color.GRAY
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            canvas.drawText("Documento generado: ${dateFormat.format(Date())}", margin, yPosition, paint)

            pdfDocument.finishPage(page)

            val fileName = "Ficha_Medica_${petName.replace(" ", "_")}.pdf"
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
package com.example.pract_1.data.photoDataWork

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileNotFoundException
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExternalFilesManipulations(
    private val context: Context
) {
    suspend fun loadPhotos(): Result<List<File>> {
        return withContext(Dispatchers.IO) {
            try {
                val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    ?: throw FileNotFoundException()
                val files = dir.listFiles()
                    ?.filter { it.isFile && it.name.lowercase().endsWith(".jpg") }
                    ?.sortedByDescending { it.lastModified() }
                    ?: emptyList()
                Result.success(files)
            } catch (e: FileNotFoundException){
                Result.failure(e)
            } catch (e: Exception){
                Result.failure(e)
            }
        }
    }

    suspend fun savePhoto(bitmap: Bitmap): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    ?: throw FileNotFoundException()
                dir.mkdirs()

                val fileName =
                    "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
                val file = File(dir, fileName)

                file.outputStream().use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                Result.success(true)
            } catch (e: FileNotFoundException){
                Result.failure(e)
            } catch (e: Exception){
                Result.failure(e)
            }
        }
    }

    suspend fun deletePhoto(file: File): Result<Boolean>{
        return withContext(Dispatchers.IO){
            try {
                val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    ?: throw FileNotFoundException()

                if (!file.exists()) throw FileNotFoundException()

                file.delete()

                Result.success(true)
            } catch (e: FileNotFoundException){
                Result.failure(e)
            } catch (e: Exception){
                Result.failure(e)
            }
        }
    }
}
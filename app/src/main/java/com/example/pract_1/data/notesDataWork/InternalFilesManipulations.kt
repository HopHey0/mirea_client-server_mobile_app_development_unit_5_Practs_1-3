package com.example.pract_1.data.notesDataWork

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException

class InternalFilesManipulations(
    private val context: Context
) {
    private val filesDir = context.filesDir

    fun getFiles(): Result<List<File>> {
        try {
            val files = filesDir.listFiles()?.filter { it.isFile && it.extension == "txt" } ?: emptyList()
            return Result.success(files)
        } catch (e: Exception){
            return Result.failure(e)
        }
    }

    suspend fun writeFile(fileName: String, content: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                    output.write(content.toByteArray())
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun readFile(fileName: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.v("HERE2", fileName)
                context.openFileInput(fileName).use { input ->
                    val content = String(input.readBytes())
                    Result.success(content)
                }
            } catch (e: FileNotFoundException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteFile(fileName: String): Result<Boolean>{
        return withContext(Dispatchers.IO){
            try {
                context.deleteFile(fileName)
                Result.success(true)
            } catch (e: FileNotFoundException){
                Result.failure(e)
            } catch (e: Exception){
                Result.failure(e)
            }
        }
    }
}
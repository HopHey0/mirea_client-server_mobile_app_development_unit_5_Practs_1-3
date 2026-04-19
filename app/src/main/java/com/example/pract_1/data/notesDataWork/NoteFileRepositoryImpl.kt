package com.example.pract_1.data.notesDataWork

import com.example.pract_1.domain.noteDomain.NoteFileRepository
import com.example.pract_1.domain.noteDomain.model.NoteItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteFileRepositoryImpl(
    private val dataSource: InternalFilesManipulations
): NoteFileRepository {
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    override suspend fun getFiles(): Result<List<NoteItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val filesResult = dataSource.getFiles()
                if (filesResult.isFailure)
                    return@withContext Result.failure(filesResult.exceptionOrNull()!!)

                val noteItems = filesResult.getOrDefault(emptyList()).map { file ->
                    NoteItem(
                        header = file.name,
                        body = file.readText(),
                        date = dateFormat.format(Date(file.lastModified()))
                    )
                }
                Result.success(noteItems)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun readFile(fileName: String): Result<String> {
        return dataSource.readFile(fileName)
    }

    override suspend fun writeFile(fileName: String, content: String): Result<Unit> {
        return dataSource.writeFile(fileName, content)
    }

    override suspend fun deleteFile(fileName: String): Result<Boolean> {
        return dataSource.deleteFile(fileName)
    }
}
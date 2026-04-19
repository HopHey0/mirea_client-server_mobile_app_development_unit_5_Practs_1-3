package com.example.pract_1.domain.noteDomain

import com.example.pract_1.domain.noteDomain.model.NoteItem

interface NoteFileRepository {
    suspend fun getFiles(): Result<List<NoteItem>>
    suspend fun readFile(fileName: String): Result<String>
    suspend fun writeFile(fileName: String, content: String): Result<Unit>

    suspend fun deleteFile(fileName: String): Result<Boolean>
}
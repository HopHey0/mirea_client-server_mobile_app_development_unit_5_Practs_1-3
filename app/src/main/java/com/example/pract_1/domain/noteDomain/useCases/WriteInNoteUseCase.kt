package com.example.pract_1.domain.noteDomain.useCases

import com.example.pract_1.domain.noteDomain.NoteFileRepository

class WriteInNoteUseCase(
    private val repo: NoteFileRepository
) {
    suspend fun invoke(fileName: String, content: String): Result<Unit>{
        return repo.writeFile(fileName, content)
    }
}
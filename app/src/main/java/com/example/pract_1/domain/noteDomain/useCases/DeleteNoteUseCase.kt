package com.example.pract_1.domain.noteDomain.useCases

import com.example.pract_1.domain.noteDomain.NoteFileRepository

class DeleteNoteUseCase(private val repository: NoteFileRepository) {
    suspend operator fun invoke(fileName: String): Result<Boolean> {
        return repository.deleteFile(fileName)
    }
}
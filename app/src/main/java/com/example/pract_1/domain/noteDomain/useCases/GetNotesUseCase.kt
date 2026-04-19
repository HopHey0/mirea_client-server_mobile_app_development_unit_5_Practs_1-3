package com.example.pract_1.domain.noteDomain.useCases

import com.example.pract_1.domain.noteDomain.NoteFileRepository
import com.example.pract_1.domain.noteDomain.model.NoteItem

class GetNotesUseCase(private val repository: NoteFileRepository) {
    suspend operator fun invoke(): Result<List<NoteItem>> {
        return repository.getFiles()
    }
}
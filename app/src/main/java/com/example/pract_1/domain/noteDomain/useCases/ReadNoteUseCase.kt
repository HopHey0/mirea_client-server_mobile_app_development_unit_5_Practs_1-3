package com.example.pract_1.domain.noteDomain.useCases

import com.example.pract_1.domain.noteDomain.NoteFileRepository

class ReadNoteUseCase (
    private val repo: NoteFileRepository
){
    suspend fun invoke(fileName: String): Result<String>{
        return repo.readFile(fileName)
    }
}
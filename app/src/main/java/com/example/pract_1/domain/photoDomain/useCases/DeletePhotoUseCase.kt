package com.example.pract_1.domain.photoDomain.useCases

import com.example.pract_1.domain.photoDomain.PhotoFileRepository
import java.io.File

class DeletePhotoUseCase(
    private val repository: PhotoFileRepository
) {
    suspend fun invoke(file: File): Result<Boolean>{
        return repository.deletePhoto(file)
    }
}
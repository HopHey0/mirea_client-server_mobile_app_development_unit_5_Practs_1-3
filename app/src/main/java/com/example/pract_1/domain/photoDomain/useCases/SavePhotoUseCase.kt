package com.example.pract_1.domain.photoDomain.useCases

import android.graphics.Bitmap
import com.example.pract_1.domain.photoDomain.PhotoFileRepository

class SavePhotoUseCase(
    private val repository: PhotoFileRepository
) {
    suspend fun invoke(bitmap: Bitmap): Result<Boolean>{
        return repository.savePhoto(bitmap)
    }
}
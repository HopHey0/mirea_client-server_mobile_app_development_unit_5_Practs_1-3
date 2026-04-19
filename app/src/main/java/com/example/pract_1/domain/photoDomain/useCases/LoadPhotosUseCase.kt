package com.example.pract_1.domain.photoDomain.useCases

import com.example.pract_1.domain.photoDomain.PhotoFileRepository
import java.io.File

class LoadPhotosUseCase(
    private val repository: PhotoFileRepository
){
    suspend fun invoke(): Result<List<File>>{
        return repository.loadPhotos()
    }
}
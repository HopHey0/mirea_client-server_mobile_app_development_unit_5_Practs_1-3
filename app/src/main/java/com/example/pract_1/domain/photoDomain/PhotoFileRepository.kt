package com.example.pract_1.domain.photoDomain

import android.graphics.Bitmap
import java.io.File

interface PhotoFileRepository {
    suspend fun loadPhotos(): Result<List<File>>

    suspend fun savePhoto(bitmap: Bitmap): Result<Boolean>

    suspend fun deletePhoto(file: File): Result<Boolean>
}
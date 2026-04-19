package com.example.pract_1.data.photoDataWork

import android.graphics.Bitmap
import coil.decode.DataSource
import com.example.pract_1.domain.photoDomain.PhotoFileRepository
import java.io.File

class PhotoFileRepositoryImpl(
    private val dataSource: ExternalFilesManipulations
): PhotoFileRepository {
    override suspend fun loadPhotos(): Result<List<File>> {
        return dataSource.loadPhotos()
    }

    override suspend fun savePhoto(bitmap: Bitmap): Result<Boolean> {
        return dataSource.savePhoto(bitmap)
    }

    override suspend fun deletePhoto(file: File): Result<Boolean> {
        return dataSource.deletePhoto(file)
    }

}
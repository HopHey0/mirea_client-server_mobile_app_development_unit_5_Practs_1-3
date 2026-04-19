package com.example.pract_1.presentation.photosUi.viewModels

import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pract_1.domain.photoDomain.useCases.DeletePhotoUseCase
import com.example.pract_1.domain.photoDomain.useCases.LoadPhotosUseCase
import com.example.pract_1.domain.photoDomain.useCases.SavePhotoUseCase
import com.example.pract_1.presentation.photosUi.models.PhotoCardItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class PhotoListUiState(
    var photoList: List<PhotoCardItem> = emptyList(),
    var showCameraPermissionRequest: Boolean = false,
    var snackbarMessage: String? = null,
    var totalDrag: Float = 0f
)

class PhotoManipulationViewModel(
    private val loadPhotosUseCase: LoadPhotosUseCase,
    private val savePhotoUseCase: SavePhotoUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(PhotoListUiState())
    val uiState = _uiState.asStateFlow()

    var photoManipulationJob: Job? = null

    init {
        loadPhotos()
    }

    fun loadPhotos(){
        photoManipulationJob?.cancel()
        photoManipulationJob = viewModelScope.launch {
            loadPhotosUseCase.invoke().onSuccess {rawList ->
                _uiState.update { uiState ->
                    uiState.copy(
                        photoList = rawList.map {photo ->
                            PhotoCardItem(photo, photo.toURI(), false)
                        }
                    )
                }
            }
        }
    }

    fun savePhoto(bitmap: Bitmap){
        photoManipulationJob?.cancel()
        photoManipulationJob = viewModelScope.launch {
            savePhotoUseCase.invoke(bitmap)
                .onSuccess {
                _uiState.update {
                    it.copy(
                        snackbarMessage = "Фото успешно сохранено"
                    )
                }
            }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            snackbarMessage = "Ошибка сохранения"
                        )
                    }
                }
            loadPhotos()
        }
    }

    fun deletePhoto(file: File){
        photoManipulationJob?.cancel()
        photoManipulationJob = viewModelScope.launch {
            deletePhotoUseCase.invoke(file)
                .onSuccess {
                _uiState.update {
                    it.copy(
                        snackbarMessage = "Фото успешно удалено"
                    )
                }
            }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            snackbarMessage = "Ошибка удаления"
                        )
                    }
                }
            loadPhotos()
        }
    }

    fun exportToGallery(photo: PhotoCardItem, contentResolver: ContentResolver) {
        photoManipulationJob?.cancel()
        photoManipulationJob = viewModelScope.launch(Dispatchers.IO) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, photo.file.name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyGallery")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: return@launch

            contentResolver.openOutputStream(uri).use { out ->
                photo.file.inputStream().use { input -> input.copyTo(out!!) }
            }

            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(uri, contentValues, null, null)

            _uiState.update {
                it.copy(
                    snackbarMessage = "Фото успешно экспортировано"
                )
            }
        }
    }

    fun onLongTap(fileName: String){
        _uiState.update { uiState ->
            uiState.copy(
                photoList = uiState.photoList.map {
                    if (it.file.name == fileName)
                        it.copy(isExpanded = !it.isExpanded)
                    else
                        it
                }
            )
        }
    }

    fun onCreateClick() {
        _uiState.update { it.copy(showCameraPermissionRequest = true) }
    }

    fun onPermissionGranted() {
        _uiState.update { it.copy(showCameraPermissionRequest = false) }
    }

    fun onPermissionDenied() {
        _uiState.update {
            it.copy(
                showCameraPermissionRequest = false,
                snackbarMessage = "Нет доступа к камере"
            )
        }
    }

    fun onSnackbarShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }


    override fun onCleared() {
        super.onCleared()
        photoManipulationJob = null
    }
}
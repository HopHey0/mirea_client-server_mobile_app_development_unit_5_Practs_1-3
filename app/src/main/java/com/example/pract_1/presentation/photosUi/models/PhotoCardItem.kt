package com.example.pract_1.presentation.photosUi.models

import java.io.File
import java.net.URI

data class PhotoCardItem(
    val file: File,
    val uri: URI,
    val isExpanded: Boolean
)
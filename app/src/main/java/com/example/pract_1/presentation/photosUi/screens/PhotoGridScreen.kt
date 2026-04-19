package com.example.pract_1.presentation.photosUi.screens

import android.Manifest
import android.content.ContentResolver
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pract_1.R
import com.example.pract_1.presentation.photosUi.models.PhotoCardItem
import com.example.pract_1.presentation.photosUi.viewModels.PhotoListUiState
import java.io.File
import java.net.URI

@Composable
fun PhotoGridScreen(
    uiState: PhotoListUiState,
    onCreateClick: () -> Unit,
    onSavePhoto: (Bitmap) -> Unit,
    onLongTap: (String) -> Unit,
    onDeletePhoto: (File) -> Unit,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    onSnackbarShown: () -> Unit,
    onSwipeLeft: () -> Unit,
    onExport: (PhotoCardItem, ContentResolver) -> Unit
){
    val snackbarHostState = remember { SnackbarHostState() }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let { onSavePhoto(it) }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
            cameraLauncher.launch(null)
        }
        else
            onPermissionDenied()
    }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            onSnackbarShown()
        }
    }

    LaunchedEffect(uiState.showCameraPermissionRequest) {
        if (uiState.showCameraPermissionRequest) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (uiState.totalDrag > 300f) {
                            onSwipeLeft()
                        }
                        uiState.totalDrag = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        uiState.totalDrag += dragAmount
                    }
                )
            },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .padding(vertical = 10.dp),
                onClick = onCreateClick
            ) {
                Icon(Icons.Filled.Add, "Floating action button.")
            }
        }
    ) { innerPadding ->
        PhotoGrid(
            uiState,
            onLongTap,
            onDeletePhoto,
            onExport,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun PhotoGrid(
    uiState: PhotoListUiState,
    onLongTap: (String) -> Unit,
    onDeletePhoto: (File) -> Unit,
    onExport: (PhotoCardItem, ContentResolver) -> Unit,
    modifier: Modifier
){
    if (uiState.photoList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.empty_photo_grid_hint),
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = uiState.photoList,
                key = { photoCard -> photoCard.file.name }
            ) { item ->
                PhotoCard(
                    item,
                    onDeletePhoto,
                    onExport,
                    onLongTap
                )
            }
        }
    }
}

@Composable
fun PhotoCard(
    item: PhotoCardItem,
    onDeletePhoto: (File) -> Unit,
    onExport: (PhotoCardItem, ContentResolver) -> Unit,
    onLongTap: (String) -> Unit
){
    val context = LocalContext.current
    val resolver = context.contentResolver
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongTap(item.file.name) }
                )
            },
    ) {
        Box {
            DropdownMenu(
                expanded = item.isExpanded,
                onDismissRequest = { onLongTap(item.file.name) }
            ) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.share_item)) },
                    leadingIcon = { Icon(Icons.Filled.Share, contentDescription = null) },
                    onClick = { onExport(item, resolver) }
                )

                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.delete_item)) },
                    leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                    onClick = { onDeletePhoto(item.file) }
                )
            }
        }
        AsyncImage(
            model = item.file,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

    }
}

@Composable
@Preview(showSystemUi = true)
fun PhotoNotEmptyGridScreenPreview(){
    MaterialTheme{
        PhotoGridScreen(
            uiState = PhotoListUiState(
                listOf(
                    PhotoCardItem(File("dfsdx", "as"), URI("da"), false ),
                    PhotoCardItem(File("dfsdxx", "dwadfsd"), URI("dadfaffsfdds"), false ),
                    PhotoCardItem(File("dfsxxwd", "fg"), URI("dadfsfdgthgthds"), true )
                )
            ),
            onCreateClick = { },
            onSavePhoto = { },
            onLongTap = { },
            onDeletePhoto = { },
            onPermissionGranted = { },
            onPermissionDenied = { },
            onSnackbarShown = { },
            onSwipeLeft = { },
            onExport = { _, _ -> }
        )
    }
}

@Composable
@Preview(showSystemUi = true)
fun PhotoEmptyGridScreenPreview(){
    MaterialTheme{
        PhotoGridScreen(
            uiState = PhotoListUiState(),
            onCreateClick = { },
            onSavePhoto = { },
            onLongTap = { },
            onDeletePhoto = { },
            onPermissionGranted = { },
            onPermissionDenied = { },
            onSnackbarShown = { },
            onSwipeLeft = { },
            onExport = { _, _ -> }
        )
    }
}
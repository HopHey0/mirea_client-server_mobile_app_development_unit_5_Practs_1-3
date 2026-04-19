package com.example.pract_1.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pract_1.presentation.notesUi.screens.CreateNoteScreen
import com.example.pract_1.presentation.notesUi.screens.NoteListScreen
import com.example.pract_1.presentation.notesUi.viewModels.NoteManipulationViewModel
import com.example.pract_1.presentation.photosUi.screens.PhotoGridScreen
import com.example.pract_1.presentation.photosUi.viewModels.PhotoManipulationViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val notesViewModel: NoteManipulationViewModel = koinViewModel()
    val photosViewModel: PhotoManipulationViewModel = koinViewModel()

    NavHost(navController = navController, startDestination = Screens.NoteList.route) {

        composable(Screens.NoteList.route) {
            NoteListScreen(
                uiState = notesViewModel.listUiState.collectAsStateWithLifecycle().value,
                onNoteClick = { fileName ->
                    notesViewModel.readTheFile(fileName)
                    navController.navigate(Screens.NoteEdit.createRoute(fileName))
                },
                onCreateClick = {
                    navController.navigate(Screens.NoteEdit.createRoute("new"))
                    notesViewModel.clearFields()
                },
                onLongTap = { fileName ->
                    notesViewModel.onLongTap(fileName)
                },
                onDeleteNote = notesViewModel::deleteFile,
                onSwipeLeft = {
                    navController.navigate(Screens.PhotoList.route){
                        popUpTo(Screens.PhotoList.route) {inclusive = true}
                    }
                }
            )
        }

        composable(Screens.NoteEdit.route) { backStackEntry ->
            val fileName = backStackEntry.arguments?.getString("fileName")
            CreateNoteScreen(
                uiState = notesViewModel.createUiState.collectAsStateWithLifecycle().value,
                isNew = fileName == "new",
                onHeaderChange = notesViewModel::onHeaderChange,
                onBodyChange = notesViewModel::onBodyChange,
                onEditClick = {
                    notesViewModel.writeInFile()
                    notesViewModel.clearFields()
                    navController.popBackStack()
                },
                onBackClick = {
                    notesViewModel.clearFields()
                    navController.popBackStack()
                }
            )
        }

        composable(Screens.PhotoList.route) {
            PhotoGridScreen(
                uiState = photosViewModel.uiState.collectAsStateWithLifecycle().value,
                onCreateClick = photosViewModel::onCreateClick,
                onSavePhoto = photosViewModel::savePhoto,
                onLongTap = { fileName ->
                    photosViewModel.onLongTap(fileName)
                            },
                onDeletePhoto = {fileName ->
                    photosViewModel.deletePhoto(fileName)
                                },
                onPermissionGranted = photosViewModel::onPermissionGranted,
                onPermissionDenied = photosViewModel::onPermissionDenied,
                onSnackbarShown = photosViewModel::onSnackbarShown,
                onSwipeLeft = {
                    navController.navigate(Screens.NoteList.route){
                        popUpTo(Screens.NoteList.route) { inclusive = true }
                    }
                },
                onExport = photosViewModel::exportToGallery
            )
        }
    }
}
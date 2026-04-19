package com.example.pract_1.presentation.notesUi.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pract_1.R
import com.example.pract_1.presentation.notesUi.viewModels.CreateNoteUIState

@Composable
fun CreateNoteScreen(
    uiState: CreateNoteUIState,
    isNew: Boolean,
    onHeaderChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onEditClick: () -> Unit,
    onBackClick: () -> Unit
){
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = { TopAppBar(isNew, onEditClick, onBackClick) }
    ) { innerPadding ->
        CreateNote(
            uiState,
            onHeaderChange,
            onBodyChange,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    isNew: Boolean,
    onEditClick: () -> Unit,
    onBackClick: () -> Unit
){
    TopAppBar(
        modifier = Modifier.padding(horizontal = 8.dp),
        title = { Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = if (isNew) stringResource(R.string.create_note_top_app_bar_string) else stringResource(R.string.edit_note_top_app_bar_string))
                },
        navigationIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.clickable(onClick = onBackClick)) },
        actions = { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.clickable(onClick = onEditClick)) }
    )
}

@Composable
fun CreateNote(
    uiState: CreateNoteUIState,
    onHeaderChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.header,
            onValueChange = onHeaderChange,
            label = { Text(text = stringResource(R.string.write_header_text_field)) }
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            value = uiState.body,
            onValueChange = onBodyChange,
            label = { Text(text = stringResource(R.string.write_body_text_field)) }
        )
    }
}

@Composable
@Preview(showSystemUi = true, device = "id:pixel_5")
fun CreateNoteScreenPreview(){
    MaterialTheme{
        CreateNoteScreen(
            uiState = CreateNoteUIState(),
            isNew = true,
            onHeaderChange = {  },
            onBodyChange = { },
            onEditClick = { },
            onBackClick = { }
        )
    }
}
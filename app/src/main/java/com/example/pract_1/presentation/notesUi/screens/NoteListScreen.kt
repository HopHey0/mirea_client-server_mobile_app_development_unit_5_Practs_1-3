package com.example.pract_1.presentation.notesUi.screens

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pract_1.R
import com.example.pract_1.domain.noteDomain.model.NoteItem
import com.example.pract_1.presentation.notesUi.viewModels.ListNotesUIState
import com.example.pract_1.presentation.notesUi.viewModels.NoteItemCard

@Composable
fun NoteListScreen(
    uiState: ListNotesUIState,
    onNoteClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    onLongTap: (String) -> Unit,
    onDeleteNote: (String) -> Unit,
    onSwipeLeft: () -> Unit
){
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit){
                detectHorizontalDragGestures (
                    onDragEnd = {
                        if (uiState.totalDrag < -300f) {
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
        NoteList(
            uiState,
            onNoteClick,
            onLongTap,
            onDeleteNote,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun NoteList(
    uiState: ListNotesUIState,
    onNoteClick: (String) -> Unit,
    onLongTap: (String) -> Unit,
    onDeleteNote: (String) -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.notes.isEmpty()) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .padding(vertical = 10.dp),
                    text = stringResource(R.string.emptyNoteListHint),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            //Spacer(modifier = Modifier.weight(0.6f))
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .weight(0.7f)
            ) {
                items(
                    items = uiState.notes,
                    key = {noteCard -> noteCard.note.header }
                ) { item ->
                    NoteListItem(
                        item,
                        onNoteClick,
                        onLongTap,
                        onDeleteNote
                    )
                }
            }
        }
    }
}

@Composable
fun NoteListItem(
    item: NoteItemCard,
    onNoteClick: (String) -> Unit,
    onLongTap: (String) -> Unit,
    onDeleteNote: (String) -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .pointerInput(Unit){
                detectTapGestures(
                    onLongPress = { onLongTap(item.note.header) },
                    onTap = { onNoteClick(item.note.header) }
                )
            },

    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(
                text = item.note.header,
                fontWeight = FontWeight.Bold
            )
            Box {
                DropdownMenu(
                    expanded = item.isExpanded,
                    onDismissRequest = { onLongTap(item.note.header) }
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.delete_item)) },
                        leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                        onClick = { onDeleteNote(item.note.header) }
                    )
                }
            }

            Text(
                text = item.note.body,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = item.note.date,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true, device = "id:pixel_5")
fun NoteNotEmptyListScreenPreview(){
    MaterialTheme{
        NoteListScreen(
            uiState = ListNotesUIState(
                listOf(
                    NoteItemCard(NoteItem("Sample1", "Sample1", "Sample1"), false),
                    NoteItemCard(NoteItem("Sample2", "Sample2", "Sample2"), true),
                        NoteItemCard(NoteItem("Sample3", "Sample3", "Sample3"), false)
                )
            ),
            { },
            { },
            { },
            { },
            { }
        )
    }
}

@Composable
@Preview(showSystemUi = true, device = "id:pixel_5")
fun NoteEmptyListScreenPreview(){
    MaterialTheme{
        NoteListScreen(
            uiState = ListNotesUIState(
                listOf()
            ),
            { },
            { },
            { },
            { },
            { }
        )
    }
}
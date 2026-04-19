package com.example.pract_1.presentation.notesUi.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pract_1.domain.noteDomain.model.NoteItem
import com.example.pract_1.domain.noteDomain.useCases.DeleteNoteUseCase
import com.example.pract_1.domain.noteDomain.useCases.GetNotesUseCase
import com.example.pract_1.domain.noteDomain.useCases.ReadNoteUseCase
import com.example.pract_1.domain.noteDomain.useCases.WriteInNoteUseCase
import com.example.pract_1.presentation.notesUi.constants.UIConstants
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CreateNoteUIState(
    var header: String = "",
    var body: String = "",
    var isHeaderOutOfConstr: Boolean = false,
    var isBodyOutOfConstr: Boolean = false
)

data class ListNotesUIState(
    var notes: List<NoteItemCard> = emptyList(),
    var totalDrag: Float = 0f
)

data class NoteItemCard(
    val note: NoteItem,
    val isExpanded: Boolean = false,
)

class NoteManipulationViewModel(
    private val writeInNoteUseCase: WriteInNoteUseCase,
    private val readNoteUseCase: ReadNoteUseCase,
    private val getNotesUseCase: GetNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
): ViewModel() {
    private val _createUiState = MutableStateFlow(CreateNoteUIState())
    val createUiState = _createUiState.asStateFlow()

    private val _listUiState = MutableStateFlow(ListNotesUIState())
    val listUiState = _listUiState.asStateFlow()

    var noteManipulationJob: Job? = null

    init {
        noteManipulationJob?.cancel()
        noteManipulationJob = viewModelScope.launch {
            getNotesUseCase.invoke().onSuccess { notes ->
                _listUiState.update {uiState -> uiState.copy(notes = notes.map { NoteItemCard(it, false) }) }
            }
        }
    }

    fun writeInFile() {
        noteManipulationJob?.cancel()

        val header = _createUiState.value.header
        val content = _createUiState.value.body

        if (content.isBlank()) {
            return
        }

        val date = SimpleDateFormat("dd.MM.yyyy_HH:mm", Locale.getDefault()).format(Date())

        val fileName = if (header.endsWith(".txt") && header.contains("_")) {
            header
        } else {
            "${date}_${header}.txt"
        }

        val existingNote = _listUiState.value.notes.find {
            it.note.header == fileName
        }

        _listUiState.update { uiState ->
            val updatedNotes = if (existingNote != null) {
                uiState.notes.map { noteCard ->
                    if (noteCard.note.header == fileName) {
                        noteCard.copy(
                            note = noteCard.note.copy(
                                body = content,
                                date = date
                            )
                        )
                    } else {
                        noteCard
                    }
                }
            } else {
                uiState.notes + NoteItemCard(
                    note = NoteItem(
                        header = fileName,
                        body = content,
                        date = date
                    ),
                    isExpanded = false
                )
            }

            uiState.copy(notes = updatedNotes)
        }

        clearFields()

        noteManipulationJob = viewModelScope.launch {
            writeInNoteUseCase.invoke(fileName, content)
        }
    }

    fun clearFields(){
        _createUiState.update { uiState ->
            uiState.copy(
                header = "",
                body = "",
                isHeaderOutOfConstr = false,
                isBodyOutOfConstr = false
            )
        }
    }


    fun readTheFile(fileName: String){
        noteManipulationJob?.cancel()
        Log.v("HERE", fileName)
        noteManipulationJob = viewModelScope.launch {
            readNoteUseCase.invoke(fileName).onSuccess { content ->
                _createUiState.update { uiState ->
                    uiState.copy(
                        header = fileName,
                        body = content
                    )
                }
            }
        }
    }

    fun deleteFile(fileName: String){
        noteManipulationJob?.cancel()
        noteManipulationJob = viewModelScope.launch {
            deleteNoteUseCase.invoke(fileName).onSuccess { isDeleted ->
                _listUiState.update { uiState ->
                    uiState.copy(
                        notes = uiState.notes
                            .filter { it.note.header != fileName }
                            .map { it.copy(isExpanded = false) }
                    )
                }
            }
        }
    }

    fun onLongTap(fileName: String){
        _listUiState.update { uiState ->
            uiState.copy(
                notes = uiState.notes.map { if (it.note.header == fileName) it.copy(isExpanded = !it.isExpanded) else it }
            )
        }
    }

    fun onHeaderChange(newText: String){
        if (newText.length > UIConstants.HEADER_CONSTRAINT){
            _createUiState.update { it.copy(isHeaderOutOfConstr = true) }
        } else {
            _createUiState.update { it.copy(header = newText, isHeaderOutOfConstr = false) }
        }
    }

    fun onBodyChange(newText: String){
        if (newText.length > UIConstants.BODY_CONSTRAINT){
            _createUiState.update { it.copy(isBodyOutOfConstr = true) }
        } else {
            _createUiState.update { it.copy(body = newText, isBodyOutOfConstr = false) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        noteManipulationJob = null
    }
}
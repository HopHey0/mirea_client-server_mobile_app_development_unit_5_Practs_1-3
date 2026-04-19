package com.example.pract_1.navigation

sealed class Screens(val route: String) {
    object NoteList : Screens("notes")

    object NoteEdit : Screens("noteEdit/{fileName}") {
        fun createRoute(fileName: String) = "noteEdit/$fileName"
    }

    object PhotoList : Screens("photos")
}

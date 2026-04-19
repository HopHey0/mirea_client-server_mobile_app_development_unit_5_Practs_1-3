package com.example.pract_1.di

import android.app.Application
import com.example.pract_1.data.notesDataWork.InternalFilesManipulations
import com.example.pract_1.data.notesDataWork.NoteFileRepositoryImpl
import com.example.pract_1.data.photoDataWork.ExternalFilesManipulations
import com.example.pract_1.data.photoDataWork.PhotoFileRepositoryImpl
import com.example.pract_1.domain.noteDomain.NoteFileRepository
import com.example.pract_1.domain.noteDomain.useCases.DeleteNoteUseCase
import com.example.pract_1.domain.noteDomain.useCases.GetNotesUseCase
import com.example.pract_1.domain.noteDomain.useCases.ReadNoteUseCase
import com.example.pract_1.domain.noteDomain.useCases.WriteInNoteUseCase
import com.example.pract_1.domain.photoDomain.PhotoFileRepository
import com.example.pract_1.domain.photoDomain.useCases.DeletePhotoUseCase
import com.example.pract_1.domain.photoDomain.useCases.LoadPhotosUseCase
import com.example.pract_1.domain.photoDomain.useCases.SavePhotoUseCase
import com.example.pract_1.presentation.notesUi.viewModels.NoteManipulationViewModel
import com.example.pract_1.presentation.photosUi.viewModels.PhotoManipulationViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


class App : Application() {
    val noteModule = module {
        single { InternalFilesManipulations(androidContext()) }
        single <NoteFileRepository>{ NoteFileRepositoryImpl(get()) }

        factory { GetNotesUseCase(get()) }
        factory { ReadNoteUseCase(get()) }
        factory { WriteInNoteUseCase(get()) }
        factory { DeleteNoteUseCase(get()) }

        viewModel { NoteManipulationViewModel(get(), get(), get(), get()) }
    }

    val photoModule = module {
        single { ExternalFilesManipulations(androidContext()) }
        single <PhotoFileRepository>{ PhotoFileRepositoryImpl(get()) }

        factory { LoadPhotosUseCase(get()) }
        factory { SavePhotoUseCase(get()) }
        factory { DeletePhotoUseCase(get()) }

        viewModel { PhotoManipulationViewModel(get(), get(), get()) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(noteModule, photoModule)
        }
    }
}

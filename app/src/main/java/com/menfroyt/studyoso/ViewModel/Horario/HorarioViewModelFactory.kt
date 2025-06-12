package com.menfroyt.studyoso.ViewModel.Horario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.menfroyt.studyoso.data.repositories.HorarioRepository

class HorarioViewModelFactory(private val repository: HorarioRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HorarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HorarioViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
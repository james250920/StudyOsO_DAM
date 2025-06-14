package com.menfroyt.studyoso.ViewModel.usuario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.menfroyt.studyoso.data.repositories.UsuarioRepository


class UsuarioViewModelFactory(private val repository: UsuarioRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsuarioViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
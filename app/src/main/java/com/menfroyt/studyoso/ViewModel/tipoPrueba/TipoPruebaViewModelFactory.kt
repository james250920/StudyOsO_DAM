package com.menfroyt.studyoso.ViewModel.tipoPrueba

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.menfroyt.studyoso.data.repositories.TipoPruebaRepository


class TipoPruebaViewModelFactory(private val repository: TipoPruebaRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TipoPruebaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TipoPruebaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
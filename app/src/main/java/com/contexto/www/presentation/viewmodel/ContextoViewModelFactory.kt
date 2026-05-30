package com.contexto.www.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.contexto.www.perception.aggregator.ContextAggregatorImpl

class ContextoViewModelFactory(
    private val aggregator: ContextAggregatorImpl
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContextoViewModel::class.java)) {
            return ContextoViewModel(aggregator) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

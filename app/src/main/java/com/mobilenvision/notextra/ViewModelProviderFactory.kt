package com.mobilenvision.notextra

import androidx.core.util.Supplier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Singleton

@Singleton
class ViewModelProviderFactory<T : ViewModel?>(
    private val viewModelClass: Class<T>,
    private val viewModelSupplier: Supplier<T>
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(viewModelClass)) {
            viewModelSupplier.get() as T
        } else {
            throw IllegalArgumentException("Unknown Class name " + viewModelClass.name)
        }
    }
}
package com.android.musicmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

internal class MapViewModelFactory(
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            MapViewModel(getApiService()) as T
        } else {
            throw IllegalArgumentException("MapViewModel Not Found")
        }
    }
}
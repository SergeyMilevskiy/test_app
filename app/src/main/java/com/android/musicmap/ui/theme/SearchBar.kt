package com.android.musicmap.ui.theme

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.musicmap.MapViewModel

@Composable
internal fun SearchBar(viewModel: MapViewModel) {

    TextField(
        value = viewModel.input,
        onValueChange = {
            viewModel.searchItems(it)
        },
        label = { Text("Search") },
        modifier = Modifier.fillMaxWidth()
    )
}
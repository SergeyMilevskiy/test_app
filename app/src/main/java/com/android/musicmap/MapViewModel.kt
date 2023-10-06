package com.android.musicmap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

const val CURRENT_YEAR = 2023

internal class MapViewModel(private val apiService: ApiService) : ViewModel() {

    var input by mutableStateOf("")
        private set

    val result: StateFlow<Pair<List<MarkerItem>, Int>> = snapshotFlow { input }
        .debounce(400L)
        .mapLatest { apiService.searchMusic(query = it).body() }
        .map { resp ->
            resp?.places?.filter { it.coordinates != null }?.map {
                MarkerItem(
                    lat = it.coordinates?.latitude?.toDouble() ?: 0.0,
                    lng = it.coordinates?.longitude?.toDouble() ?: 0.0,
                    lifeSpan = it.lifeSpan?.begin?.let { begin -> getLifespan(begin) }
                        ?: 0
                )
            }?.filter { it.lifeSpan > 0 } ?: emptyList()
        }.flatMapLatest { markers ->
            if (markers.isEmpty()) emptyFlow() else getTimer(markers.maxOf { mrk -> mrk.lifeSpan })
                .map { Pair(markers.filter { m -> m.lifeSpan > it }, it) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Pair(emptyList(), 0)
        )


    private fun getLifespan(date: String?): Int {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
        return try {
            val parsedDate = simpleDateFormat.parse(date)
            val year = Calendar.getInstance().apply { time = parsedDate }.get(Calendar.YEAR)
            CURRENT_YEAR - year
        } catch (exp: ParseException) {
            0
        }
    }

    fun searchItems(search: String) {
        input = search
    }

    private fun getTimer(maxTime: Int) = (0..maxTime)
        .asSequence()
        .asFlow()
        .onEach { delay(1_000) }
}
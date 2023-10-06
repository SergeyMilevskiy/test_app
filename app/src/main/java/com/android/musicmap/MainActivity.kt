package com.android.musicmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.android.musicmap.ui.theme.MusicMapTheme
import com.android.musicmap.ui.theme.SearchBar
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mapViewModel = ViewModelProvider(
            this,
            MapViewModelFactory()
        )[MapViewModel::class.java]//TODO: it can be inject via Dagger, Hilt or Koin

        setContent {
            MusicMapTheme {
                val items by mapViewModel.result.collectAsState()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val warsaw = LatLng(52.237049, 21.017532)
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(warsaw, 10f)
                    }
                    Box(modifier = Modifier.fillMaxSize()) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState
                        ) {
                            items.first.forEach {
                                CustomMarker(
                                    position = LatLng(it.lat, it.lng),
                                    title = "${it.lifeSpan}"
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 20.dp)
                        ) {
                            Row { SearchBar(mapViewModel) }
                            Row {
                                Text("Timer ${items.second}")
                                Text("Number of markers ${items.first.size}")

                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun CustomMarker(position: LatLng, title: String) {
    val markerState = rememberMarkerState(title, position)
    Marker(
        state = markerState,
        title = title
    )
    markerState.showInfoWindow()
}

package ru.kire.deviceonmap.states

import android.location.Location
import ru.kire.deviceonmap.data.entity.SavedMarker

sealed class MapState: BaseState {
    data class Success(val location: Location?): MapState()
    data class MarkerSaved(val savedMarker: SavedMarker): MapState()
    data class Markers(val markers: List<SavedMarker>): MapState()
}
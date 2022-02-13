package ru.kire.deviceonmap.states

import ru.kire.deviceonmap.data.entity.SavedMarker

sealed class MarkersState: BaseState {
    data class Markers(val markers: List<SavedMarker>): MarkersState()
    data class Save(val marker: SavedMarker): MarkersState()
    data class Delete(val markerId: Long): MarkersState()
}
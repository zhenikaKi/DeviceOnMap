package ru.kire.deviceonmap.windows.mapwindow

import android.content.Context
import android.location.Geocoder
import kotlinx.coroutines.launch
import ru.kire.deviceonmap.states.BaseState
import ru.kire.deviceonmap.states.MapState
import ru.kire.deviceonmap.windows.BaseViewModel

class MapViewModel(private val mapService: MapService): BaseViewModel<BaseState>() {

    /**
     * Получить текущее местоположение.
     * @param context контекст места, где проверяются разрешения.
     */
    fun getLocation(context: Context) {
        liveData.postValue(BaseState.Loading)
        coroutineScope.launch {
            mapService.getLocation(context) {
                liveData.postValue(MapState.Success(it)) }
        }
    }

    /** Сохранить новый маркер */
    fun saveMarker(latitude: Double, longitude: Double, address: String?) {
        liveData.postValue(BaseState.Loading)
        coroutineScope.launch {
            liveData.postValue(
                MapState.MarkerSaved(mapService.saveMarker(latitude, longitude, address)))
        }
    }

    /** Получить сохраненные маркеры */
    fun getMarkers(){
        liveData.postValue(BaseState.Loading)
        coroutineScope.launch {
            liveData.postValue(MapState.Markers(mapService.getMarkers()))
        }
    }

    /**
     * Получить адрес по координатам.
     * @param context контекст места, где проверяются разрешения.
     * @param latitude широта маркера.
     * @param longitude долгота маркера.
     * @return адрес или null при ошибке
     */
    fun getAddress(context: Context, latitude: Double, longitude: Double): String? {
        return try {
            val geocoder = Geocoder(context)
            geocoder.getFromLocation(latitude, longitude, 1)
                .firstOrNull()
                ?.getAddressLine(0)
        } catch (e: Exception) {
            null
        }
    }

    /** Очистить состояние */
    fun clearState() {
        liveData.postValue(BaseState.Null)
    }
}
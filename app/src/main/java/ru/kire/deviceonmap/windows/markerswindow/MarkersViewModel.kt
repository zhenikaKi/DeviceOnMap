package ru.kire.deviceonmap.windows.markerswindow

import kotlinx.coroutines.launch
import ru.kire.deviceonmap.data.db.repositories.MainRepository
import ru.kire.deviceonmap.data.entity.SavedMarker
import ru.kire.deviceonmap.states.BaseState
import ru.kire.deviceonmap.states.MarkersState
import ru.kire.deviceonmap.windows.BaseViewModel

class MarkersViewModel(private val repository: MainRepository) : BaseViewModel<BaseState>() {
    /** Получить список сохраненных маркеров */
    fun getMarkers() {
        liveData.postValue(BaseState.Loading)
        coroutineScope.launch { liveData.postValue(MarkersState.Markers(repository.get())) }
    }


    /** Удалить маркер */
    fun delete(markerId: Long) {
        coroutineScope.launch {
            repository.delete(markerId)
            liveData.postValue(MarkersState.Delete(markerId))
        }
    }

    /** Сохранить маркер  в базе */
    fun saveMarker(savedMarker: SavedMarker) {
        coroutineScope.launch {
            repository.save(savedMarker)
            liveData.postValue(MarkersState.Save(savedMarker))
        }
    }

    /** Очистить состояние */
    fun clearState() {
        liveData.postValue(BaseState.Null)
    }
}
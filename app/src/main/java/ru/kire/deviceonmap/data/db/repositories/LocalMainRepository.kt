package ru.kire.deviceonmap.data.db.repositories

import ru.kire.deviceonmap.data.db.AppDB
import ru.kire.deviceonmap.data.entity.SavedMarker

class LocalMainRepository(private val db: AppDB): MainRepository {
    /** Сохранить маркер */
    override suspend fun save(marker: SavedMarker): SavedMarker {
        return if (marker.markerId == null) {
            val markerId = db.savedMarkerDao().insert(marker)
            db.savedMarkerDao().getMarker(markerId)
        } else {
            db.savedMarkerDao().update(marker)
            db.savedMarkerDao().getMarker(marker.markerId)
        }
    }

    /** Получит ьсписок маркеров */
    override suspend fun get(): List<SavedMarker> = db.savedMarkerDao().getMarkers()

    /** Удалить маркер */
    override suspend fun delete(markerId: Long) {
        db.savedMarkerDao().delete(markerId)
    }
}
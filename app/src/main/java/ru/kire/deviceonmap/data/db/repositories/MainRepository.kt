package ru.kire.deviceonmap.data.db.repositories

import ru.kire.deviceonmap.data.entity.SavedMarker

/** Интерфейс работы с маркерами карты */
interface MainRepository {
    /** Сохранить маркер */
    suspend fun save(marker: SavedMarker): SavedMarker

    /** Получить список маркеров */
    suspend fun get(): List<SavedMarker>

    /** Удалить маркер */
    suspend fun delete(markerId: Long)
}
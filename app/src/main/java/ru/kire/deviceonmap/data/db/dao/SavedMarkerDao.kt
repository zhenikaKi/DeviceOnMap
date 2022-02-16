package ru.kire.deviceonmap.data.db.dao

import androidx.room.*
import ru.kire.deviceonmap.data.db.DB
import ru.kire.deviceonmap.data.entity.SavedMarker

@Dao
interface SavedMarkerDao {
    /** Сохранить маркер */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(marker: SavedMarker): Long

    /** Обновить маркер */
    @Update
    suspend fun update(chart: SavedMarker)

    /** Найти маркер по id */
    @Query("select * from ${DB.TABLE_SAVED_MARKERS} where ${DB.MARKER_ID} = :markerId")
    suspend fun getMarker(markerId: Long): SavedMarker

    /** Получить список сохраненных маркеров */
    @Query("select * from ${DB.TABLE_SAVED_MARKERS}")
    suspend fun getMarkers(): List<SavedMarker>

    /** Удалить маркер */
    @Query("delete from ${DB.TABLE_SAVED_MARKERS} where ${DB.MARKER_ID} = :markerId")
    suspend fun delete(markerId: Long)
}
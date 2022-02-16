package ru.kire.deviceonmap.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.kire.deviceonmap.data.db.dao.SavedMarkerDao
import ru.kire.deviceonmap.data.entity.SavedMarker

@Database(
    entities = [
        SavedMarker::class
    ],
    version = DB.VERSION,
    exportSchema = true)
abstract class AppDB: RoomDatabase() {
    abstract fun savedMarkerDao(): SavedMarkerDao
}
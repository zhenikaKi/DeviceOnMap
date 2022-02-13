package ru.kire.deviceonmap.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.kire.deviceonmap.data.db.DB

/** Сохраненный маркер карты */
@Entity(tableName = DB.TABLE_SAVED_MARKERS)
data class SavedMarker(
    /** ID маркера */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DB.MARKER_ID)
    val markerId: Long? = null,

    /** Широта маркера */
    @ColumnInfo(name = DB.LATITUDE)
    val latitude: Double,

    /** Долгота маркера */
    @ColumnInfo(name = DB.LONGITUDE)
    val longitude: Double,

    /** Адрес маркера */
    @ColumnInfo(name = DB.ADDRESS)
    val address: String? = null,

    /** Заголовок маркера */
    @ColumnInfo(name = DB.TITLE)
    var title: String? = null,

    /** Описание маркера */
    @ColumnInfo(name = DB.DESCRIPTION)
    var description: String? = null
)

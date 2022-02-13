package ru.kire.deviceonmap.data.db

//константы по базе данных
object DB {
    //база данных
    const val VERSION = 1
    const val NAME = "app.db"

    //таблицы
    const val TABLE_SAVED_MARKERS = "saved_markers"

    //столбцы
    const val MARKER_ID = "marker_id"
    const val LATITUDE = "latitude"
    const val LONGITUDE = "longitude"
    const val ADDRESS = "address"
    const val TITLE = "title"
    const val DESCRIPTION = "description"
}
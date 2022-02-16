package ru.kire.deviceonmap.windows.markerswindow

import ru.kire.deviceonmap.data.entity.SavedMarker

/** Интерефейс событий элемента списка маркеров */
interface OnMarkerItemClickListener {
    /** Редактирование  маркера */
    fun edit(savedMarker: SavedMarker)

    /** Удаление  маркера */
    fun delete(markerId: Long)
}
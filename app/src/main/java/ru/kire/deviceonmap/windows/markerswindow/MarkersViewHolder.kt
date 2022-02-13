package ru.kire.deviceonmap.windows.markerswindow

import androidx.recyclerview.widget.RecyclerView
import ru.kire.deviceonmap.R
import ru.kire.deviceonmap.data.entity.SavedMarker
import ru.kire.deviceonmap.databinding.ItemMarkersBinding

class MarkersViewHolder(private val binding: ItemMarkersBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(data: SavedMarker, onMarkerItemClickListener: OnMarkerItemClickListener) {
        with (binding) {
            address.text = data.address
            title.text = data.title
            description.text = data.description
            point.text = String.format(root.context.getString(
                R.string.app_marker_clicked_info,
                data.latitude,
                data.longitude))

            //обработка нажатий
            data.markerId?.let { id ->
                iconEdit.setOnClickListener { onMarkerItemClickListener.edit(data) }
                iconDelete.setOnClickListener { onMarkerItemClickListener.delete(id) }
            }
        }
    }
}
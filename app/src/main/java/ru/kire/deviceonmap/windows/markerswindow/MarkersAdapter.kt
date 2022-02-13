package ru.kire.deviceonmap.windows.markerswindow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.kire.deviceonmap.data.entity.SavedMarker
import ru.kire.deviceonmap.databinding.ItemMarkersBinding

/** Адаптер списка сохраненных маркеров */
class MarkersAdapter(private val onMarkerItemClickListener: OnMarkerItemClickListener):
    RecyclerView.Adapter<MarkersViewHolder>() {

    private var markers: ArrayList<SavedMarker> = arrayListOf()

    /** Задать список маркеров */
    fun setData(l: List<SavedMarker>) {
        markers.clear()
        markers.addAll(l)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMarkersBinding.inflate(inflater, parent, false)
        return MarkersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarkersViewHolder, position: Int) {
        holder.bind(markers[position], onMarkerItemClickListener)
    }

    override fun getItemCount(): Int {
        return markers.size
    }

    fun update(marker: SavedMarker) {
        for (ind in markers.indices) {
            if (markers[ind].markerId == marker.markerId) {
                updateItem(ind, marker)
                break
            }
        }
    }

    /** Удалить маркер из списка */
    fun delete(markerId: Long) {
        for (ind in markers.indices) {
            if (markers[ind].markerId == markerId) {
                removeItem(ind)
                break
            }
        }
    }

    /** Удалить позицию с маркером */
    private fun removeItem(pos: Int) {
        markers.removeAt(pos)
        notifyItemRemoved(pos)
    }

    private fun updateItem(ind: Int, marker: SavedMarker) {
        markers[ind] = marker
        notifyItemChanged(ind)
    }
}
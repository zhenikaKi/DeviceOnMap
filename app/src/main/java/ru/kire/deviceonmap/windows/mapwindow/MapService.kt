package ru.kire.deviceonmap.windows.mapwindow

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import ru.kire.deviceonmap.data.db.repositories.MainRepository
import ru.kire.deviceonmap.data.entity.SavedMarker

class MapService(private val repository: MainRepository) {

    companion object {
        /**
         * Проверить есть ли разрешение на получение текущего местоположения.
         * @param context контекст места, где проверяются разрешения.
         * @return true - разрешения есть, false - разрешений нет.
         */
        fun isPermissionExists(context: Context): Boolean {
            val granted = PackageManager.PERMISSION_GRANTED
            return ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == granted
                    && ActivityCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) == granted
        }
    }

    /**
     * Получить текущее местоположение.
     * @param context контекст места, где проверяются разрешения.
     * @param locationListener событие, которое нужно выполнить при получении местоположения.
     */
    @SuppressLint("MissingPermission")
    fun getLocation(context: Context, locationListener: (location: Location?) -> Unit) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        if (isPermissionExists(context)) {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location -> locationListener(location) }
        }
    }

    /**
     * Сохранить новый маркер.
     * @param latitude широта маркера.
     * @param longitude долгота маркера.
     * @param address адрес координаты.
     */
    suspend fun saveMarker(latitude: Double, longitude: Double, address: String?): SavedMarker {
        val savedMarker = SavedMarker(latitude = latitude, longitude = longitude, address = address)
        return repository.save(savedMarker)
    }

    /** Получить сохраненные маркеры */
    suspend fun getMarkers(): List<SavedMarker> = repository.get()
}
package ru.kire.deviceonmap.windows.mapwindow

import android.Manifest
import android.app.AlertDialog
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.Router
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
import ru.kire.deviceonmap.AppConstants
import ru.kire.deviceonmap.R
import ru.kire.deviceonmap.data.entity.SavedMarker
import ru.kire.deviceonmap.databinding.MapFragmentBinding
import ru.kire.deviceonmap.di.Scopes
import ru.kire.deviceonmap.extensions.getBooleanPreference
import ru.kire.deviceonmap.extensions.setBooleanPreference
import ru.kire.deviceonmap.navigation.MarkersScreen
import ru.kire.deviceonmap.states.BaseState
import ru.kire.deviceonmap.states.MapState

class MapFragment: Fragment() {

    private val scope = KoinJavaComponent.getKoin().createScope<MapFragment>()
    private val router: Router = scope.get(qualifier = named(Scopes.ROUTER))
    private var _binding: MapFragmentBinding? = null
    private val binding
        get() = _binding

    private val viewModel: MapViewModel = scope.get(qualifier = named(Scopes.MAP_VIEW_MODEL))
    private val map: SupportMapFragment by lazy {
        childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    }
    private var allMarkers: List<Marker> = listOf()
    private var lastClickedMarker: Marker? = null

    /** Запрос разрешения на получения текущего местоположения */
    private val requestAccessLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                //разрешения получены, получаем местоположение
                (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) ->
                    viewModel.getLocation(requireContext())

                //пользователь еще не отключил окно с иформацией о том, что он может дать разрешения вручную
                !requireContext().getBooleanPreference(AppConstants.NOT_SHOW_DIALOG_PERMISSION) -> {
                    showDialogLocationPermission(
                        R.string.location_permission_disable,
                        R.string.location_permission_no_show_again) {
                        requireContext().setBooleanPreference(
                            AppConstants.NOT_SHOW_DIALOG_PERMISSION,
                            true)
                    }
                }

                //пользователь навсегда запретил использовать местоположение
                else -> { }
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = MapFragmentBinding.inflate(inflater, container, false)
        viewModel.getLiveData().observe(viewLifecycleOwner) { renderData(it) }

        //навешиваем обработчики
        setClickListener()


        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        map.getMapAsync {
            it.clear()
        }

        Log.d(AppConstants.TAG_LOG, "onResume")

        //удаляем ранее добавленные маркеры
        allMarkers.forEach { it.remove() }

        //проверяем разрешение на получение текущей позиции
        checkLocationPermission()

        //загружаем сохраненные маркеры
        viewModel.getMarkers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /** Навешать обработчки кликов */
    private fun setClickListener() {
        //обработчик клика по карте
        map.getMapAsync {
            it.setOnMapClickListener { position ->
                //удаляем предыдущий маркер
                lastClickedMarker?.remove()
                binding?.buttonSave?.isVisible = false
                val markerOption = getMarkerOptions(position)
                addMarkerOnMap(markerOption, clickPosition = true)
            }
        }

        //обработчик сохранения маркера
        binding?.buttonSave?.setOnClickListener {
            lastClickedMarker?.let { marker ->
                val address = viewModel.getAddress(
                    requireContext(),
                    marker.position.latitude,
                    marker.position.longitude)
                viewModel.saveMarker(marker.position.latitude, marker.position.longitude, address)
            }
        }

        //обработчик кнопки перехода к списку маркеров
        binding?.buttonMarker?.setOnClickListener { router.navigateTo(MarkersScreen()) }
    }

    /**
     * Обработать событие от viewModel.
     * @param state полученное состояние.
     */
    private fun renderData(state: BaseState?) {
        Log.d(AppConstants.TAG_LOG, "renderData state = $state")

        when (state) {
            //загрузка информации
            BaseState.Loading -> loadingViewVisible(true)

            //получение местоположения
            is MapState.Success -> {
                addMarkerOnMap(getMarkerOptions(state.location), currentPosition = true)
                loadingViewVisible(false)
            }

            //получение сохраненных маркеров
            is MapState.Markers -> {
                Log.d(AppConstants.TAG_LOG, "state.markers = ${state.markers}")
                state.markers.forEach { marker -> addMarkerOnMap(getMarkerOptions(marker)) }
                binding?.buttonMarker?.isVisible = state.markers.isNotEmpty()
                loadingViewVisible(false)
            }

            //сохранения адреса
            is MapState.MarkerSaved -> {
                Toast.makeText(requireContext(), R.string.app_marker_save_success, Toast.LENGTH_LONG)
                    .show()
                lastClickedMarker?.remove()
                addMarkerOnMap(getMarkerOptions(state.savedMarker))
                binding?.buttonMarker?.isVisible = true
                loadingViewVisible(false)
            }

            else -> { }
        }

        //сделаем пустое уведомление, чтобы при возврате с предыдущего экрана
        //повторно не отрабатывало состояние выше
        //viewModel.clearState()
    }

    /**
     * Сформировать параметры маркера текущего местоположения.
     * @param location координаты текущего местоположения.
     */
    private fun getMarkerOptions(location: Location?): MarkerOptions? {
        location?.let {
            val markerPosition = LatLng(it.latitude, it.longitude)
            return MarkerOptions()
                .position(markerPosition)
                .title(getString(R.string.marker_current_position))
                .snippet(getDescription(it.latitude, it.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_current_position))
        }

        return null
    }

    /**
     * Сформировать параметры маркера, сохраненного в базе.
     * @param marker сохраненный маркер.
     */
    private fun getMarkerOptions(marker: SavedMarker): MarkerOptions {
        val markerPosition = LatLng(marker.latitude, marker.longitude)
        val title = marker.title ?: getString(R.string.marker_current_position)
        return MarkerOptions()
            .position(markerPosition)
            .title(title)
            .snippet(getDescription(marker.latitude, marker.longitude))
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.saved_marker))
    }

    /**
     * Сформировать параметры маркера, выбранного на карте.
     * @param position координаты на карте.
     */
    private fun getMarkerOptions(position: LatLng): MarkerOptions {
        val markerPosition = LatLng(position.latitude, position.longitude)
        return MarkerOptions()
            .position(markerPosition)
            .title(getString(R.string.app_marker_clicked))
            .snippet(getDescription(position.latitude, position.longitude))
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.clicked_marker))
    }

    /**
     * Добавить на карту маркер.
     * @param markerOptions параметры паркера.
     * @param currentPosition true - маркер текущей позиции, false - сохраненный пользователем маркер.
     * @param clickPosition true - маркер нажатой точки на карте, false - какой-то другой маркер
     */
    private fun addMarkerOnMap(markerOptions: MarkerOptions?,
                               currentPosition: Boolean = false,
                               clickPosition: Boolean = false) {
        markerOptions?.let {
            map.getMapAsync { googleMap ->
                val marker = googleMap.addMarker(it)

                if (currentPosition) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(it.position))
                }

                if (clickPosition) {
                    lastClickedMarker = marker
                    binding?.buttonSave?.isVisible = true
                }
            }
        }
    }

    /** Показать или скрыть progressbar */
    private fun loadingViewVisible(loading: Boolean) {
        binding?.let {
            it.progressBar.isVisible = loading
        }
    }

    /** Проверить есть ли разрешение на получение текущего местоположения */
    private fun checkLocationPermission() {
        when {
            //получаем текущее местоположение
            MapService.isPermissionExists(requireContext()) -> {
                Log.d(AppConstants.TAG_LOG, "checkLocationPermission isPermissionExists")
                viewModel.getLocation(requireContext())
            }

            //пользователь не давал разрешения, рассказываем для чего нужно разрешение
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                    && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                Log.d(AppConstants.TAG_LOG, "checkLocationPermission shouldShowRequestPermissionRationale")
                showDialogLocationPermission(
                    R.string.location_permission_text,
                    R.string.location_permission_success) { requestLocationPermission() }
            }

            //еще не запрашивали разрешение, запрашиваем
            else -> {
                Log.d(AppConstants.TAG_LOG, "checkLocationPermission else")
                requestLocationPermission() }
        }
    }

    /** Запросить разрешение на получение текущего местоположения */
    private fun requestLocationPermission() {
        requestAccessLocationPermission.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    /**
     * Показать окно с информацией для чего нужно разрешение на получения местоположения.
     * @param textResourceId идентификатр строкового ресурса с описанием.
     * @param buttonPositiveResourceId идентификатр строкового ресурса для положительной кнопки.
     * @param positiveEvent событие, которое нужно выполнить при нажатии на положительную кнопку.
     */
    private fun showDialogLocationPermission(textResourceId: Int,
                                             buttonPositiveResourceId: Int,
                                             positiveEvent: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.location_permission_title)
            .setMessage(textResourceId)
            .setPositiveButton(buttonPositiveResourceId) { _, _ -> positiveEvent() }
            .setNegativeButton(R.string.location_permission_close) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun getDescription(latitude: Double, longitude: Double): String {
        return String.format(getString(
            R.string.app_marker_clicked_info,
            latitude,
            longitude))
    }
}
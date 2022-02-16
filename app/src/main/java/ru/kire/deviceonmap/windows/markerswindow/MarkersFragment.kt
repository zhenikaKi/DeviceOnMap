package ru.kire.deviceonmap.windows.markerswindow

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.terrakok.cicerone.Router
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
import ru.kire.deviceonmap.R
import ru.kire.deviceonmap.data.entity.SavedMarker
import ru.kire.deviceonmap.databinding.MarkersFragmentBinding
import ru.kire.deviceonmap.di.Scopes
import ru.kire.deviceonmap.states.BaseState
import ru.kire.deviceonmap.states.MarkersState

class MarkersFragment: Fragment() {

    private val scope = KoinJavaComponent.getKoin().createScope<MarkersFragment>()
    private val router: Router = scope.get(qualifier = named(Scopes.ROUTER))
    private var _binding: MarkersFragmentBinding? = null
    private val binding
        get() = _binding

    private val viewModel: MarkersViewModel = scope.get(qualifier = named(Scopes.MARKERS_VIEW_MODEL))

    private val adapter = MarkersAdapter(object: OnMarkerItemClickListener {
        /** Редактирование  маркера */
        override fun edit(savedMarker: SavedMarker) {
            openDialogEdit(savedMarker)
        }

        /** Удаление  маркера */
        override fun delete(markerId: Long) {
            viewModel.delete(markerId)
        }
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = MarkersFragmentBinding.inflate(inflater, container, false)
        viewModel.getLiveData().observe(viewLifecycleOwner) { renderData(it) }

        //загружаем сохраненные маркеры
        viewModel.getMarkers()

        binding?.markersList?.adapter = adapter
        binding?.markersList?.itemAnimator = DefaultItemAnimator()
        binding?.markersList?.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    /**
     * Обработать событие от viewModel.
     * @param state полученное состояние.
     */
    private fun renderData(state: BaseState?) {
        when (state) {
            //загрузка информации
            BaseState.Loading -> loadingViewVisible(true)

            //получение сохраненных маркеров
            is MarkersState.Markers -> {
                adapter.setData(state.markers)
                loadingViewVisible(false)
            }

            //удаление маркера
            is MarkersState.Delete -> { adapter.delete(state.markerId) }

            //сохранение маркера
            is MarkersState.Save -> adapter.update(state.marker)
        }

        //сделаем пустое уведомление, чтобы при возврате с предыдущего экрана
        //повторно не отрабатывало состояние выше
        viewModel.clearState()
    }


    /** Показать или скрыть progressbar */
    private fun loadingViewVisible(loading: Boolean) {
        binding?.let {
            it.progressBar.isVisible = loading
            it.markersList.isVisible = !loading
        }
    }

    /** Открыть окно редактирования маркера */
    private fun openDialogEdit(savedMarker: SavedMarker) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val viewDialog = LayoutInflater.from(context).inflate(R.layout.item_marker_edit, null)

        viewDialog.findViewById<TextView>(R.id.address).text = savedMarker.address
        viewDialog.findViewById<EditText>(R.id.edit_title).setText(savedMarker.title)
        viewDialog.findViewById<EditText>(R.id.edit_description).setText(savedMarker.description)

        builder
            .setView(viewDialog)
            .setCancelable(false)
            .setTitle(R.string.marker_edit_title)
            .setPositiveButton(R.string.marker_edit_save) { _, _ -> applyDialog(savedMarker, viewDialog) }
            .setNegativeButton(R.string.marker_edit_cancel) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    /** Обработка сохранения маркера в окне редактирования */
    private fun applyDialog(savedMarker: SavedMarker, view: View?) {
        savedMarker.title = view?.findViewById<EditText>(R.id.edit_title)?.text.toString()
        savedMarker.description = view?.findViewById<EditText>(R.id.edit_description)?.text.toString()
        viewModel.saveMarker(savedMarker)
    }
}
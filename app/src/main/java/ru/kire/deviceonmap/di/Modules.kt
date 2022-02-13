package ru.kire.deviceonmap.di

import androidx.room.Room
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.kire.deviceonmap.MainActivity
import ru.kire.deviceonmap.data.db.AppDB
import ru.kire.deviceonmap.data.db.DB
import ru.kire.deviceonmap.data.db.repositories.LocalMainRepository
import ru.kire.deviceonmap.data.db.repositories.MainRepository
import ru.kire.deviceonmap.windows.mapwindow.MapFragment
import ru.kire.deviceonmap.windows.mapwindow.MapService
import ru.kire.deviceonmap.windows.mapwindow.MapViewModel
import ru.kire.deviceonmap.windows.markerswindow.MarkersFragment
import ru.kire.deviceonmap.windows.markerswindow.MarkersViewModel

object Modules {
    //модуль, содержимое которого должно быть во всем приложении
    val application = module {
        //база данных
        single<AppDB>(qualifier = named(Scopes.DB)) {
            Room.databaseBuilder(get(), AppDB::class.java, DB.NAME)
                .build()
        }

        //навигация
        single<Cicerone<Router>>(qualifier = named(Scopes.CICERONE)) {
            Cicerone.create(Router())
        }
        single<NavigatorHolder>(qualifier = named(Scopes.NAVIGATOR)) {
            get<Cicerone<Router>>(qualifier = named(Scopes.CICERONE)).getNavigatorHolder()
        }
        single<Router>(qualifier = named(Scopes.ROUTER)) {
            get<Cicerone<Router>>(qualifier = named(Scopes.CICERONE)).router
        }

        //работа с базой данных
        single<MainRepository>(qualifier = named(Scopes.MAIN_REPOSITORY)) {
            LocalMainRepository(get(qualifier = named(Scopes.DB)))
        }
    }

    //модуль основной активити
    val appActivity = module {
        scope<MainActivity> {
        }
    }

    //модуль карты
    val mapWindow = module {
        scope<MapFragment> {
            viewModel(qualifier = named(Scopes.MAP_VIEW_MODEL)) {
                MapViewModel(get(qualifier = named(Scopes.MAP_SERVICE)))
            }

            scoped<MapService>(qualifier = named(Scopes.MAP_SERVICE)) {
                MapService(get(qualifier = named(Scopes.MAIN_REPOSITORY)))
            }
        }
    }

    //модуль списка сохраненных маркеров
    val markersWindow = module {
        scope<MarkersFragment> {
            viewModel(qualifier = named(Scopes.MARKERS_VIEW_MODEL)) {
                MarkersViewModel(get(qualifier = named(Scopes.MAIN_REPOSITORY)))
            }
        }
    }
}

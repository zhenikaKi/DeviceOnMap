package ru.kire.deviceonmap.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.kire.deviceonmap.windows.markerswindow.MarkersFragment

class MarkersScreen(): FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment = MarkersFragment()
}
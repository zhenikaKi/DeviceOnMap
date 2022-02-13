package ru.kire.deviceonmap.extensions

import android.content.Context
import ru.kire.deviceonmap.AppConstants

/**
 * Получить логическое значение из настроек.
 * @param preferenceName имя настройки, из которой нужно получить значение.
 * @return сохраненное значение из настроек или false.
 */
fun Context.getBooleanPreference(preferenceName: String): Boolean {
    val sharedPreferences = this.getSharedPreferences(AppConstants.PREFERENCES_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean(preferenceName, false)
}

/**
 * Сохранить логическое значение в настройки.
 * @param preferenceName имя настройки, в которую нужно сохранить значение.
 * @param value значение, которое нужно сохранить.
 */
fun Context.setBooleanPreference(preferenceName: String, value: Boolean) {
    val editor = this.getSharedPreferences(AppConstants.PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
    editor.putBoolean(preferenceName, value)
    editor.apply()
}
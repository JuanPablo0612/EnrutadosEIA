package com.juanpablo0612.carpool.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect fun createDataStore(context: Any? = null): DataStore<Preferences>

internal const val PREFERENCES_FILE_NAME = "carpool_prefs"

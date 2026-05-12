package com.juanpablo0612.carpool.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toOkioPath
import java.io.File

actual fun createDataStore(context: Any?): DataStore<Preferences> {
    val appContext = context as Context
    val file = File(appContext.filesDir, "datastore/$PREFERENCES_FILE_NAME.preferences_pb")
    file.parentFile?.mkdirs()
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { file.toOkioPath() }
    )
}

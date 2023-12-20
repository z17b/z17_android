package cu.z17.preferences

import android.content.Context

class Z17Preferences(
    private val context: Context,
): PreferencesRepositoryImpl(context, DataStoreObj.dataStore(context))
package cu.z17.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.security.crypto.MasterKey
import cu.z17.preferences.encription.Encryption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.IOException


class PreferencesRepositoryImpl(
    private val storage: DataStore<Preferences>,
    private val context: Context,
) {

    private val TAG = this.javaClass.simpleName

    @SuppressLint("HardwareIds")
    private fun createMasterKey(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val spec = KeyGenParameterSpec.Builder(
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
                .build()

            val masterKey = MasterKey.Builder(context)
                .setKeyGenParameterSpec(spec)
                .build().toString()

            masterKey
        } else {
            try {
                val androidId = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
                androidId
            } catch (e: Exception) {
                "android_todus_key"
            }
        }
    }

    private val salt = "android-preferences"
    private val iv = ByteArray(16)
    private val encryption: Encryption = Encryption.getDefault(createMasterKey(context), salt, iv)

    private val errors = MutableStateFlow(0)
    val errorFlow = errors.asStateFlow()

    private inline fun <reified T> Flow<Preferences>.secureMap(
        crossinline fetchValue: (value: Preferences) -> String?, default: T,
    ): Flow<T> {
        return map {
            try {
                val value = fetchValue(it)
                if (value != null) {
                    val decryptedValue = encryption.decrypt(value)
                    Json { encodeDefaults = true }.decodeFromString(serializer(), decryptedValue)
                } else {
                    default
                }
            } catch (e: Exception) {
                errors.value += 1
                default
            }
        }
    }

    private suspend inline fun <reified T> DataStore<Preferences>.secureEdit(
        value: T, crossinline editStore: (MutablePreferences, String) -> Unit,
    ) {
        edit {
            val encryptedValue = encryption.encryptOrNull(Json.encodeToString(serializer(), value))
            editStore.invoke(it, encryptedValue)
        }
    }

    suspend fun resetAll() {
        storage.edit {
            it.clear()
        }
    }

    /**
     * String
     */
    fun flowValueOf(keyName: String, default: String = ""): Flow<String> {
        val key = stringPreferencesKey(keyName)

        return storage.data.catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                Log.e(TAG, it.toString())
                emit(emptyPreferences())
            }
        }.secureMap({
            it[key]
        }, default)
    }

    suspend fun store(keyName: String, value: String): Boolean {
        val key = stringPreferencesKey(keyName)

        storage.secureEdit(value) { prefs, encryptedValue ->
            prefs[key] = encryptedValue
        }
        return true
    }

    suspend fun retrieve(keyName: String, default: String = ""): String {
        val key = stringPreferencesKey(keyName)
        val value = storage.data.catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                Log.e(TAG, it.toString())
                emit(emptyPreferences())
            }
        }.secureMap({
            it[key]
        }, default).first()

        return value
    }

    /**
     * Int
     */
    fun flowValueOf(keyName: String, default: Int = 0): Flow<Int> {
        val key = stringPreferencesKey(keyName)

        return storage.data.catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                Log.e(TAG, it.toString())
                emit(emptyPreferences())
            }
        }.secureMap({
            it[key]
        }, default)
    }

    suspend fun store(keyName: String, value: Int): Boolean {
        val key = stringPreferencesKey(keyName)

        storage.secureEdit(value) { prefs, encryptedValue ->
            prefs[key] = encryptedValue
        }
        return true
    }

    suspend fun retrieve(keyName: String, default: Int = 0): Int {
        val key = stringPreferencesKey(keyName)
        val value = storage.data.catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                Log.e(TAG, it.toString())
                emit(emptyPreferences())
            }
        }.secureMap({
            it[key]
        }, default).first()

        return value
    }

    /**
     * Double
     */
    fun flowValueOf(keyName: String, default: Double = 0.0): Flow<Double> {
        val key = stringPreferencesKey(keyName)

        return storage.data.catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                Log.e(TAG, it.toString())
                emit(emptyPreferences())
            }
        }.secureMap({
            it[key]
        }, default)
    }

    suspend fun store(keyName: String, value: Double): Boolean {
        val key = stringPreferencesKey(keyName)

        storage.secureEdit(value) { prefs, encryptedValue ->
            prefs[key] = encryptedValue
        }
        return true
    }

    suspend fun retrieve(keyName: String, default: Double = 0.0): Double {
        val key = stringPreferencesKey(keyName)
        val value = storage.data.catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                Log.e(TAG, it.toString())
                emit(emptyPreferences())
            }
        }.secureMap({
            it[key]
        }, default).first()

        return value
    }

    /**
     * LONG
     */
    fun flowValueOf(keyName: String, default: Long = 0L): Flow<Long> {
        val key = stringPreferencesKey(keyName)

        return storage.data.catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                Log.e(TAG, it.toString())
                emit(emptyPreferences())
            }
        }.secureMap({
            it[key]
        }, default)
    }

    suspend fun store(keyName: String, value: Long): Boolean {
        val key = stringPreferencesKey(keyName)

        storage.secureEdit(value) { prefs, encryptedValue ->
            prefs[key] = encryptedValue
        }
        return true
    }

    suspend fun retrieve(keyName: String, default: Long = 0L): Long {
        val key = stringPreferencesKey(keyName)
        val value = storage.data.catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                Log.e(TAG, it.toString())
                emit(emptyPreferences())
            }
        }.secureMap({
            it[key]
        }, default).first()

        return value
    }

    /**
     * Boolean
     */
    fun flowValueOf(keyName: String, default: Boolean = false): Flow<Boolean> {
        val key = stringPreferencesKey(keyName)

        return storage.data.catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                Log.e(TAG, it.toString())
                emit(emptyPreferences())
            }
        }.secureMap({
            it[key]
        }, default)
    }

    suspend fun store(keyName: String, value: Boolean): Boolean {
        val key = stringPreferencesKey(keyName)

        storage.secureEdit(value) { prefs, encryptedValue ->
            prefs[key] = encryptedValue
        }
        return true
    }

    suspend fun retrieve(keyName: String, default: Boolean = false): Boolean {
        val key = stringPreferencesKey(keyName)
        val value = storage.data.catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                Log.e(TAG, it.toString())
                emit(emptyPreferences())
            }
        }.secureMap({
            it[key]
        }, default).first()

        return value
    }
}
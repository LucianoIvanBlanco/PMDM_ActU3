package com.utad.ideasapp.data_store

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("com.utad.ideasapp")

object DataStoreManager {

    private const val userKey = "user_name"
    private const val userLoggedKey = "user_logged"
    private const val passwordKey = "password"

    suspend fun saveUser(context: Context, userName: String, password: String) {
        val userNameKey = stringPreferencesKey(userKey)
        val passwordKey = stringPreferencesKey(passwordKey)

        context.dataStore.edit { editor ->
            editor[userNameKey] = userName
            editor[passwordKey] = password
        }
    }

    suspend fun getUser(context: Context): Flow<String> {
        val userNameKey = stringPreferencesKey(userKey)
        return context.dataStore.data.map { editor ->
            editor[userNameKey] ?: "Es nulo"
        }
    }

    suspend fun getPassword(context: Context): Flow<String> {

        val passwordKey = stringPreferencesKey(passwordKey)

        return context.dataStore.data.map { editor ->
            editor[passwordKey] ?: "Es nula la contraseña"
        }

    }

    suspend fun setUserLogged(context: Context) {
        Log.d("DataStoreManager", "SetUserLogged called")
        val userLogged = booleanPreferencesKey(userLoggedKey)
        context.dataStore.edit { editor ->
            editor[userLogged] = true
        }
    }

    suspend fun getIsUserLogged(context: Context): Flow<Boolean> {
        val userLogged = booleanPreferencesKey(userLoggedKey)
        return context.dataStore.data.map { editor ->
            editor[userLogged] ?: false
        }
    }

    suspend fun clearUser(context: Context) {
        Log.d("DataStoreManager", "ClearUser called")
        context.dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(userKey))
            preferences.remove(stringPreferencesKey(passwordKey))
            preferences[booleanPreferencesKey(userLoggedKey)] = false
        }
    }

    suspend fun logoutUser(context: Context) {
        Log.d("DataStoreManager", "LogoutUser called")
        val userLogged = booleanPreferencesKey(userLoggedKey)
        context.dataStore.edit { editor ->
            editor[userLogged] = false
        }
    }


}

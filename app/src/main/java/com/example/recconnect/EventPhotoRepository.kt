package com.example.recconnect

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.FileOutputStream

val Context.eventPhotoDataStore by preferencesDataStore(
    name = "event_photos"
)

class EventPhotoRepository(
    private val context: Context
) {

    suspend fun savePhoto(
        eventId: String,
        sourceUri: Uri
    ): String? {

        return try {

            val fileName = "event_$eventId.jpg"
            val file = File(context.filesDir, fileName)

            context.contentResolver.openInputStream(sourceUri)?.use { input ->

                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            // Store as key vale pair with event id as key
            context.eventPhotoDataStore.edit { preferences ->
                preferences[stringPreferencesKey(eventId)] = fileName
            }

            fileName

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getPhotoFile(eventId: String): File? {

        val preferences = context.eventPhotoDataStore.data.first()

        val fileName =
            preferences[stringPreferencesKey(eventId)]

        if (fileName == null) {
            return null
        }

        val file = File(context.filesDir, fileName)

        return if (file.exists()) {
            file
        } else {
            null
        }
    }

    suspend fun deletePhoto(eventId: String) {

        val preferences = context.eventPhotoDataStore.data.first()

        val fileName =
            preferences[stringPreferencesKey(eventId)]

        if (fileName != null) {

            val file = File(context.filesDir, fileName)

            if (file.exists()) {
                file.delete()
            }
        }

        context.eventPhotoDataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(eventId))
        }
    }
}
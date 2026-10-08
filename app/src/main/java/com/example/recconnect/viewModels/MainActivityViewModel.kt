package com.example.recconnect.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recconnect.DatabaseRepository
import com.example.recconnect.Firestore
import com.example.recconnect.models.EventData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

private const val TAG = "RecConnect.MainActivityViewModel"
class MainActivityViewModel : ViewModel() {
    private val database: DatabaseRepository = Firestore()

    val events: StateFlow<List<EventData>> = flow {
        emit(database.getAllEvents())
    }
        .catch { e -> Log.e(TAG, "Failed to load events", e) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0),
            initialValue = emptyList()
        )



}
package com.example.recconnect.models

import com.google.firebase.firestore.DocumentId

data class UserData(
    @DocumentId val id: String,
    val email: String,
    var events: List<String>,
    val name: String
)
package com.example.recconnect

import com.google.firebase.firestore.DocumentId

data class UserData(
    @DocumentId val id: String,
    val email: String,
    var events: List<String>,
    val name: String
)
package com.example.recconnect.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference

data class UserData(
    @DocumentId val id: String,
    val email: String,
    var events: List<DocumentReference>,
    val name: String
)
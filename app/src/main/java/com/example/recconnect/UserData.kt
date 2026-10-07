package com.example.recconnect

import com.google.firebase.firestore.DocumentId

data class UserData(
    @DocumentId val id: String = "",
    val name: String = "",
    val email: String = "",
    val events: List<String> = emptyList()
) {
    fun isOwedBy(uid: String?) = uid != null && uid == id
}


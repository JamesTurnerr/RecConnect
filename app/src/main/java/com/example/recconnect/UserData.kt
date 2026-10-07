package com.example.recconnect

data class UserData(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val events: List<String> = emptyList()
) {
    fun isOwedBy(uid: String?) = uid != null && uid == id
}


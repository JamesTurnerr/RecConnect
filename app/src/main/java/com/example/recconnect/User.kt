package com.example.recconnect

data class User(
    val email: String,
    var events: List<String>,
    val name: String
)
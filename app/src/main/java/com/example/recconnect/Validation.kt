package com.example.recconnect

/** validation rules */
object Validators {

    private val EMAIL_REGEX =
        Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")

    fun name(value: String, label: String): String? =
        if (value.isBlank()) "Enter your $label" else null

    fun email(value: String): String? {
        val v = value.trim()
        return when {
            v.isEmpty() -> "Enter your email"
            !v.contains("@") -> "Email needs an @, e.g. name@example.com"
            !EMAIL_REGEX.matches(v) -> "Enter a valid email, e.g. name@example.com"
            else -> null
        }
    }

    /** Rules for choosing a new password */
    fun password(value: String): String? = when {
        value.isEmpty() -> "Enter a password"
        value.length < 8 -> "Use at least 8 characters"
        value.none { it.isLetter() } || value.none { it.isDigit() } ->
            "Include at least one letter and one number"
        else -> null
    }

    /** Login only checks that something was typed Firebase decides if it's correct. */
    fun loginPassword(value: String): String? =
        if (value.isEmpty()) "Enter your password" else null

    fun confirmPassword(password: String, confirm: String): String? = when {
        confirm.isEmpty() -> "Re-enter your password"
        confirm != password -> "Passwords don't match"
        else -> null
    }
}
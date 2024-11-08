package com.example.androidfitnesstracker.Membership

enum class MembershipType(val value: String) {
    USER("User"),
    TRAINER("Trainer"),
    GUEST("Guest");

    companion object {
        fun fromString(value: String): MembershipType? {
            return entries.find { it.value.equals(value, ignoreCase = true) }
        }
    }
}
package com.example.androidfitnesstracker.Membership

enum class SubscriptionStatus(val value: String) {
    FREE("Free"),
    PREMIUM("Premium");

    companion object {
        fun fromString(value: String): SubscriptionStatus? {
            return entries.find { it.value.equals(value, ignoreCase = true) }
        }
    }
}
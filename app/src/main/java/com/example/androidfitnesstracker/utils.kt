package com.example.androidfitnesstracker

fun getRandomSearchTerm(): String {
    val searchTerms = listOf(
        "fitness equipment", "fitness", "dumbbells", "exercise",
        "workout", "fitness tracker", "home gym"
    )
    return searchTerms.random()  // Picks a random term
}

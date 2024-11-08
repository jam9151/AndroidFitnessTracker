package com.example.androidfitnesstracker.Membership

import android.content.Context
import androidx.annotation.DrawableRes
import com.example.androidfitnesstracker.R

data class Advertisement(
    @DrawableRes val imageResId: Int,
    val description: String,
    val price: String
)

fun getRandomAdvertisement(context: Context): Advertisement {
    // Define available advertisement resources
    val adverts = listOf(
        Pair(R.drawable.advert_1, R.raw.advert_1),
        Pair(R.drawable.advert_2, R.raw.advert_2),
        Pair(R.drawable.advert_3, R.raw.advert_3),
        Pair(R.drawable.advert_4, R.raw.advert_4),
        Pair(R.drawable.advert_5, R.raw.advert_5)
    )

    // Pick a random advertisement
    val (imageResId, textResId) = adverts.random()

    // Load the description and price from the text file
    val inputStream = context.resources.openRawResource(textResId)
    val lines = inputStream.bufferedReader().readLines()

    // Assume first line is description, second line is price
    val description = lines.getOrNull(0) ?: "Description not available"
    val price = lines.getOrNull(1) ?: "0.00"

    return Advertisement(imageResId, description, price)
}

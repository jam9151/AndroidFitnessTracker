package com.example.androidfitnesstracker.Workout

import android.content.Context
import com.example.androidfitnesstracker.User.UserDatabaseHelper

data class DietPlan(
    val id: Int,
    val name: String,
    val description: String,
    val calories: Int,
    val protein: Double,
    val fats: Double,
    val carbs: Double,
    val ingredients: List<String> = emptyList(),
    val isCustom: Boolean = false // Derived from the database
)

class DietManager(context: Context) {
    private val dbHelper = UserDatabaseHelper.getInstance(context)

    // Get a specific diet plan by ID (handles pre-set and custom diets)
    fun getDietById(dietId: Int): DietPlan? {
        return dbHelper.getDietById(dietId)
    }

    // Fetch all diet plans (pre-set and custom combined)
    fun getDietPlans(): List<DietPlan> {
        // Fetch pre-set diets explicitly
        val presetDiets =
            (1..6).mapNotNull { dbHelper.getDietById(it) }.map { it.copy(isCustom = false) }

        // Fetch custom diets
        val customDiets = dbHelper.getCustomDietPlans()

        // Combine both lists
        return presetDiets + customDiets
    }

    // Create a custom diet plan
    fun createCustomDiet(
        age: Int,
        targetWeight: Int,
        workoutType: String,
        targetCalories: Int,
        ingredients: List<String>
    ): Int {
        val protein = targetWeight * 1.2 // Approx. calculation based on weight
        val fats = targetCalories * 0.3 / 9 // Approx. 30% fats in calories, divided by 9 for grams
        val carbs = (targetCalories - (protein * 4 + fats * 9)) / 4 // Remaining calories as carbs

        val customDiet = DietPlan(
            id = 0, // Temporary ID; the database assigns the actual ID
            name = "Custom Diet",
            description = "User-defined plan for $workoutType",
            calories = targetCalories,
            protein = protein,
            fats = fats,
            carbs = carbs,
            ingredients = ingredients,
            isCustom = true
        )

        return dbHelper.insertCustomDiet(customDiet).toInt()
    }

    // Delete a custom diet plan
    fun deleteCustomDiet(dietId: Int) {
        dbHelper.deleteCustomDiet(dietId)
    }
}

package com.example.androidfitnesstracker.Meal

import android.util.Log
import com.example.androidfitnesstracker.R

data class DefaultMeal(
    val name: String,
    val description: String,
    var coverImage: Int? = null, // File path or URI to cover image
    val calories: Int,
    val instructions: List<MealStep> = emptyList() // Steps for each workout
)


val defaultMeals = listOf(

    DefaultMeal(
        name = "Grilled Chicken Salad",
        description = "A healthy salad with grilled chicken, mixed greens, and a light vinaigrette.",
        coverImage = R.drawable.grilled_chicken,
        calories = 350,
        instructions = listOf(
            MealStep(1, "Prepare the mixed greens in a large bowl.", null),
            MealStep(2, "Grill the chicken breasts until fully cooked and slice into strips.", null),
            MealStep(3, "Add cherry tomatoes, cucumber slices, and shredded carrots to the greens.", null),
            MealStep(4, "Top with grilled chicken strips and drizzle with vinaigrette.", null),
            MealStep(5, "Toss the salad gently and serve immediately.", null)
        )
    ),

    DefaultMeal(
        name = "Pasta Primavera",
        description = "A classic Italian dish with pasta and fresh vegetables in a light sauce.",
        coverImage = R.drawable.pasta_primavera,
        calories = 450,
        instructions = listOf(
            MealStep(1, "Cook pasta according to package instructions and set aside.", null),
            MealStep(2, "In a large pan, sauté garlic and onions in olive oil until fragrant.", null),
            MealStep(3, "Add diced bell peppers, zucchini, and cherry tomatoes. Sauté until tender.", null),
            MealStep(4, "Mix in the cooked pasta and toss with the vegetables.", null),
            MealStep(5, "Season with salt, pepper, and grated Parmesan cheese before serving.", null)
        )
    ),

    DefaultMeal(
        name = "Avocado Toast",
        description = "A simple and nutritious meal with smashed avocado on toasted bread.",
        coverImage = R.drawable.avacado_toast,
        calories = 250,
        instructions = listOf(
            MealStep(1, "Toast slices of whole-grain bread until golden brown.", null),
            MealStep(2, "Cut avocados in half, remove the pit, and scoop out the flesh.", null),
            MealStep(3, "Mash the avocado in a bowl and season with salt, pepper, and lime juice.", null),
            MealStep(4, "Spread the avocado mixture evenly onto the toasted bread.", null),
            MealStep(5, "Top with optional ingredients like sliced tomatoes, radishes, or a poached egg.", null)
        )
    ),

    DefaultMeal(
        name = "Smoothie Bowl",
        description = "A refreshing smoothie bowl topped with fruits, granola, and seeds.",
        coverImage = R.drawable.smoothie_bowl,
        calories = 300,
        instructions = listOf(
            MealStep(1, "Blend frozen berries, a banana, and almond milk until smooth.", null),
            MealStep(2, "Pour the smoothie mixture into a bowl.", null),
            MealStep(3, "Top with sliced fresh fruits like kiwi, banana, and strawberries.", null),
            MealStep(4, "Sprinkle granola, chia seeds, and shredded coconut on top.", null),
            MealStep(5, "Serve immediately with a spoon and enjoy.", null)
        )
    ),

    DefaultMeal(
        name = "Vegetable Stir-Fry",
        description = "A quick and colorful vegetable stir-fry served with steamed rice.",
        coverImage = R.drawable.vegetable_stirfry,
        calories = 400,
        instructions = listOf(
            MealStep(1, "Cook rice according to package instructions and set aside.", null),
            MealStep(2, "In a wok or large pan, heat sesame oil over medium-high heat.", null),
            MealStep(3, "Add garlic and ginger, and stir-fry for 30 seconds.", null),
            MealStep(4, "Add chopped vegetables like broccoli, bell peppers, and snap peas. Stir-fry until tender-crisp.", null),
            MealStep(5, "Mix in soy sauce, a splash of rice vinegar, and a pinch of sugar. Serve over steamed rice.", null)
        )
    ),

    DefaultMeal(
        name = "Quinoa Buddha Bowl",
        description = "A balanced meal with quinoa, roasted vegetables, and a creamy tahini dressing.",
        coverImage = R.drawable.quinoa_bowl, // Replace with your drawable resource
        calories = 450,
        instructions = listOf(
            MealStep(1, "Cook quinoa according to package instructions and set aside.", null),
            MealStep(2, "Roast sweet potatoes, chickpeas, and broccoli in olive oil and spices.", null),
            MealStep(3, "Arrange the quinoa, roasted vegetables, and greens in a bowl.", null),
            MealStep(4, "Drizzle with tahini dressing and sprinkle with sesame seeds.", null),
            MealStep(5, "Serve immediately as a complete meal.", null)
        )
    ),

    DefaultMeal(
        name = "Veggie Wrap",
        description = "A quick and easy wrap with fresh vegetables and hummus.",
        coverImage = R.drawable.veggie_wrap,
        calories = 300,
        instructions = listOf(
            MealStep(1, "Spread a generous amount of hummus onto a whole-wheat tortilla.", null),
            MealStep(2, "Layer fresh spinach, shredded carrots, and sliced cucumbers.", null),
            MealStep(3, "Add sliced avocado and a sprinkle of feta cheese, if desired.", null),
            MealStep(4, "Roll the tortilla tightly and slice in half.", null),
            MealStep(5, "Serve with a side of fresh fruit or a small salad.", null)
        )
    ),

    DefaultMeal(
        name = "Turkey and Cheese Panini",
        description = "A warm panini with turkey, cheese, and a touch of pesto.",
        coverImage = R.drawable.turkey_panini,
        calories = 400,
        instructions = listOf(
            MealStep(1, "Spread pesto on one side of two slices of sourdough bread.", null),
            MealStep(2, "Layer sliced turkey, cheese (such as provolone), and spinach.", null),
            MealStep(3, "Close the sandwich and press in a preheated panini press.", null),
            MealStep(4, "Grill until the bread is golden and the cheese is melted.", null),
            MealStep(5, "Slice in half and serve warm.", null)
        )
    ),

    DefaultMeal(
        name = "Stuffed Bell Peppers",
        description = "Bell peppers stuffed with a savory mix of rice, beans, and spices.",
        coverImage = R.drawable.stuffed_bell_peppers,
        calories = 380,
        instructions = listOf(
            MealStep(1, "Cut the tops off bell peppers and remove the seeds.", null),
            MealStep(2, "Cook rice and mix with black beans, diced tomatoes, and spices.", null),
            MealStep(3, "Stuff the bell peppers with the rice mixture.", null),
            MealStep(4, "Bake in a preheated oven at 375°F (190°C) for 25-30 minutes.", null),
            MealStep(5, "Serve warm, garnished with chopped cilantro or shredded cheese.", null)
        )
    ),

    DefaultMeal(
        name = "Overnight Oats",
        description = "A no-cook oatmeal recipe prepared the night before for a quick breakfast.",
        coverImage = R.drawable.overnight_oats,
        calories = 250,
        instructions = listOf(
            MealStep(1, "Combine oats, milk (or plant-based milk), and a pinch of cinnamon in a jar.", null),
            MealStep(2, "Add a layer of Greek yogurt and a drizzle of honey.", null),
            MealStep(3, "Top with fresh fruit such as blueberries, strawberries, or bananas.", null),
            MealStep(4, "Cover the jar and refrigerate overnight.", null),
            MealStep(5, "Stir before eating and enjoy cold or slightly warmed.", null)
        )
    ),
)

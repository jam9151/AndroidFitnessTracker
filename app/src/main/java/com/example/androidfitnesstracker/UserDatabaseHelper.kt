package com.example.androidfitnesstracker

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.Locale

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Main user table
        private const val DATABASE_NAME = "User.db"
        private const val DATABASE_VERSION = 12  // Incremented version for new schema
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password" // Hashed Password
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_FIRST_NAME = "first_name"
        private const val COLUMN_LAST_NAME = "last_name"
        private const val COLUMN_AGE = "age"
        private const val COLUMN_WEIGHT = "weight"
        private const val COLUMN_GENDER = "gender"
        private const val COLUMN_HEIGHT = "height"
        private const val COLUMN_DIET = "diet"
        private const val COLUMN_PROFILE_PICTURE = "profile_picture"
        private const val COLUMN_MEMBERSHIP_TYPE = "membership_type"
        private const val COLUMN_SUBSCRIPTION_STATUS = "subscription_status"

        // Activity tracking table
        private const val TABLE_USER_ACTIVITY = "user_activity"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_TIMESTAMP = "timestamp"
        private const val COLUMN_STEPS = "steps"
        private const val COLUMN_CALORIES = "calories"
        private const val COLUMN_DISTANCE = "distance"

        //Workouts Tables
        private const val TABLE_WORKOUTS = "workouts"
        private const val COLUMN_WORKOUT_ID = "id"
        private const val COLUMN_WORKOUT_NAME = "name"
        private const val COLUMN_WORKOUT_DESCRIPTION = "description"
        private const val COLUMN_WORKOUT_COVER_IMAGE = "cover_image"
        private const val COLUMN_WORKOUT_CALORIES = "calories"
        private const val COLUMN_WORKOUT_DURATION = "duration"
        private const val COLUMN_WORKOUT_DISTANCE = "distance"
        private const val COLUMN_WORKOUT_IS_CUSTOM = "is_custom"

        // Exercise steps table
        private const val TABLE_EXERCISE_STEPS = "exercise_steps"
        private const val COLUMN_STEP_ID = "id"
        private const val COLUMN_STEP_WORKOUT_ID = "workout_id"
        private const val COLUMN_STEP_NUMBER = "step_number"
        private const val COLUMN_STEP_DESCRIPTION = "description"
        private const val COLUMN_STEP_IMAGE = "image"


        // Create Singleton instance
        @Volatile
        private var instance: UserDatabaseHelper? = null
        fun getInstance(context: Context): UserDatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: UserDatabaseHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create the main user table with updated schema
        val createUserTable = """
    CREATE TABLE $TABLE_USERS (
        $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_USERNAME TEXT,
        $COLUMN_PASSWORD TEXT,
        $COLUMN_EMAIL TEXT,
        $COLUMN_FIRST_NAME TEXT,
        $COLUMN_LAST_NAME TEXT,
        $COLUMN_MEMBERSHIP_TYPE TEXT,
        $COLUMN_SUBSCRIPTION_STATUS TEXT
    )
"""
        db.execSQL(createUserTable)

        // Create the user activity table
        val createUserActivityTable = """
        CREATE TABLE $TABLE_USER_ACTIVITY (
            $COLUMN_USER_ID INTEGER,
            $COLUMN_TIMESTAMP INTEGER,
            $COLUMN_STEPS INTEGER,
            $COLUMN_CALORIES REAL,
            $COLUMN_DISTANCE REAL,
            FOREIGN KEY ($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID)
        )
    """
        db.execSQL(createUserActivityTable)

        // Create the workouts table
        val createWorkoutsTable = """
        CREATE TABLE $TABLE_WORKOUTS (
            $COLUMN_WORKOUT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_WORKOUT_NAME TEXT NOT NULL,
            $COLUMN_WORKOUT_DESCRIPTION TEXT,
            $COLUMN_WORKOUT_COVER_IMAGE INTEGER,        -- Drawable Cover Image
            $COLUMN_WORKOUT_CALORIES INTEGER,        -- Estimated calories burned
            $COLUMN_WORKOUT_DURATION INTEGER,        -- Duration in minutes
            $COLUMN_WORKOUT_DISTANCE REAL,           -- Distance covered, in miles or kilometers
            $COLUMN_WORKOUT_IS_CUSTOM INTEGER DEFAULT 0 -- 1 for custom workouts, 0 for predefined
        )
    """
        db.execSQL(createWorkoutsTable)

        // Create the exercise_steps table
        val createExerciseStepsTable = """
        CREATE TABLE $TABLE_EXERCISE_STEPS (
            $COLUMN_STEP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_STEP_WORKOUT_ID INTEGER NOT NULL,      -- Foreign key linking to workouts table
            $COLUMN_STEP_NUMBER INTEGER NOT NULL,          -- Order of the step in the workout
            $COLUMN_STEP_DESCRIPTION TEXT NOT NULL,        -- Description of the step
            $COLUMN_STEP_IMAGE INTEGER,                       -- drawable image
            FOREIGN KEY ($COLUMN_STEP_WORKOUT_ID) REFERENCES $TABLE_WORKOUTS($COLUMN_WORKOUT_ID) ON DELETE CASCADE
        )
    """
        db.execSQL(createExerciseStepsTable)


        // Insert the default Guest user with MembershipType "Guest" and SubscriptionStatus "Free"
        val insertGuestUser = """
        INSERT INTO $TABLE_USERS (
            $COLUMN_USERNAME, $COLUMN_FIRST_NAME, $COLUMN_LAST_NAME, 
            $COLUMN_MEMBERSHIP_TYPE, $COLUMN_SUBSCRIPTION_STATUS
        ) VALUES (
            'Guest', 'Guest', '', '${MembershipType.GUEST.value}', '${SubscriptionStatus.FREE.value}'
        )
    """
        db.execSQL(insertGuestUser)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER_ACTIVITY") // Drop other tables if needed
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXERCISE_STEPS")
        onCreate(db)  // Recreate with updated schema
    }


    fun addUser(username: String, password: String, email: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_EMAIL, email)
            put(COLUMN_SUBSCRIPTION_STATUS, SubscriptionStatus.FREE.value)
        }
        return db.insert(TABLE_USERS, null, values)
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun isUsernameTaken(username: String): Boolean {
        val db = readableDatabase
        val cursor =
            db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?", arrayOf(username))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun isEmailTaken(email: String): Boolean {
        val db = readableDatabase
        val cursor =
            db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ?", arrayOf(email))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getEmailByUsername(username: String): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_EMAIL FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?",
            arrayOf(username)
        )

        val email =
            if (cursor.moveToFirst()) cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)) else null
        cursor.close()
        return email  // Returns the email if found, otherwise null
    }

    fun insertActivityRecord(
        userId: Int,
        timestamp: Long,
        steps: Int,
        calories: Float,
        distance: Float
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_TIMESTAMP, timestamp)
            put(COLUMN_STEPS, steps)
            put(COLUMN_CALORIES, calories)
            put(COLUMN_DISTANCE, distance)
        }
        return db.insert(TABLE_USER_ACTIVITY, null, values)
    }

    fun getDailySummary(userId: Int, date: Long): DailySummary {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM($COLUMN_STEPS), SUM($COLUMN_CALORIES), SUM($COLUMN_DISTANCE) " +
                    "FROM $TABLE_USER_ACTIVITY " +
                    "WHERE $COLUMN_USER_ID = ? AND date($COLUMN_TIMESTAMP/1000, 'unixepoch') = date(?/1000, 'unixepoch')",
            arrayOf(userId.toString(), date.toString())
        )

        var summary = DailySummary(steps = 0, calories = 0f, distance = 0f)
        if (cursor.moveToFirst()) {
            summary = DailySummary(
                steps = cursor.getInt(0),
                calories = cursor.getFloat(1),
                distance = cursor.getFloat(2)
            )
        }
        cursor.close()
        return summary
    }

    fun getUserIdByUsername(username: String): Int? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id FROM users WHERE username = ?", arrayOf(username))
        val userId =
            if (cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndexOrThrow("id")) else null
        cursor.close()
        return userId
    }

    fun getFirstName(userId: Int): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_FIRST_NAME FROM $TABLE_USERS WHERE $COLUMN_ID = ?",
            arrayOf(userId.toString())
        )
        val firstName = if (cursor.moveToFirst()) cursor.getString(
            cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)
        ) else null
        cursor.close()
        return firstName
    }

    // Set First Name
    fun setFirstName(userId: Int, firstName: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FIRST_NAME, firstName)
        }
        db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
    }

    // Get Last Name
    fun getLastName(userId: Int): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_LAST_NAME FROM $TABLE_USERS WHERE $COLUMN_ID = ?",
            arrayOf(userId.toString())
        )
        val lastName =
            if (cursor.moveToFirst()) cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)) else null
        cursor.close()
        return lastName
    }

    // Set Last Name
    fun setLastName(userId: Int, lastName: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LAST_NAME, lastName)
        }
        db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
    }

    fun getFullName(userId: Int): String {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_FIRST_NAME, $COLUMN_LAST_NAME FROM $TABLE_USERS WHERE $COLUMN_ID = ?",
            arrayOf(userId.toString())
        )
        val fullName = if (cursor.moveToFirst()) {
            "${cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME))} ${
                cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)
                )
            }"
        } else {
            "Guest"  // Default in case the user isn't found
        }
        cursor.close()
        return fullName
    }

    // Get Age
    fun getAge(userId: Int): Int? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_AGE FROM $TABLE_USERS WHERE $COLUMN_ID = ?",
            arrayOf(userId.toString())
        )
        val age =
            if (cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)) else null
        cursor.close()
        return age
    }

    // Set Age
    fun setAge(userId: Int, age: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AGE, age)
        }
        db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
    }

    // Get Weight
    fun getWeight(userId: Int): Float? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_WEIGHT FROM $TABLE_USERS WHERE $COLUMN_ID = ?",
            arrayOf(userId.toString())
        )
        val weight =
            if (cursor.moveToFirst()) cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)) else null
        cursor.close()
        return weight
    }

    // Set Weight
    fun setWeight(userId: Int, weight: Float) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WEIGHT, weight)
        }
        db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
    }

    // Get Gender
    fun getGender(userId: Int): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_GENDER FROM $TABLE_USERS WHERE $COLUMN_ID = ?",
            arrayOf(userId.toString())
        )
        val gender =
            if (cursor.moveToFirst()) cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)) else null
        cursor.close()
        return gender
    }

    // Set Gender
    fun setGender(userId: Int, gender: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_GENDER, gender)
        }
        db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
    }

    // Get Height
    fun getHeight(userId: Int): Float? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_HEIGHT FROM $TABLE_USERS WHERE $COLUMN_ID = ?",
            arrayOf(userId.toString())
        )
        val height =
            if (cursor.moveToFirst()) cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)) else null
        cursor.close()
        return height
    }

    // Set Height
    fun setHeight(userId: Int, height: Float) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_HEIGHT, height)
        }
        db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
    }

    // Get Diet
    fun getDiet(userId: Int): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_DIET FROM $TABLE_USERS WHERE $COLUMN_ID = ?",
            arrayOf(userId.toString())
        )
        val diet =
            if (cursor.moveToFirst()) cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIET)) else null
        cursor.close()
        return diet
    }

    // Set Diet
    fun setDiet(userId: Int, diet: String?) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DIET, diet)
        }
        db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
    }

    // Get Profile Picture Path
    fun getProfilePicturePath(userId: Int): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_PROFILE_PICTURE FROM $TABLE_USERS WHERE $COLUMN_ID = ?",
            arrayOf(userId.toString())
        )
        val profilePicture = if (cursor.moveToFirst()) cursor.getString(
            cursor.getColumnIndexOrThrow(COLUMN_PROFILE_PICTURE)
        ) else null
        cursor.close()
        return profilePicture
    }

    // Set Profile Picture Path
    fun setProfilePicturePath(userId: Int, profilePicturePath: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PROFILE_PICTURE, profilePicturePath)
        }
        db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
    }

    // Get Membership Type
    fun getMembershipType(userId: Int): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_MEMBERSHIP_TYPE FROM $TABLE_USERS WHERE $COLUMN_ID = ?",
            arrayOf(userId.toString())
        )
        val membershipType = if (cursor.moveToFirst()) cursor.getString(
            cursor.getColumnIndexOrThrow(COLUMN_MEMBERSHIP_TYPE)
        ) else null
        cursor.close()
        return membershipType
    }

    // Set Membership Type
    fun setMembershipType(userId: Int, membershipType: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MEMBERSHIP_TYPE, membershipType)
        }
        db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
    }

    // Get Subscription Status
    fun getSubscriptionStatus(userId: Int): SubscriptionStatus? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_SUBSCRIPTION_STATUS FROM $TABLE_USERS WHERE $COLUMN_ID = ?",
            arrayOf(userId.toString())
        )
        val subscriptionStatus = if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBSCRIPTION_STATUS))
        } else null
        cursor.close()
        return subscriptionStatus?.let { SubscriptionStatus.valueOf(it.uppercase(Locale.ROOT)) }
    }


    // Set Subscription Status
    fun setSubscriptionStatus(userId: Int, subscriptionStatus: SubscriptionStatus) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SUBSCRIPTION_STATUS, subscriptionStatus.value)
        }
        db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
    }

    fun insertWorkout(
        name: String,
        description: String,
        coverImage: Int,
        calories: Int,
        duration: Int,
        distance: Float,
        isCustom: Boolean
    ): Long {
        Log.d("DatabaseInsert", "Inserting workout: $name, coverImageResId: $coverImage") // Log before insertion
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WORKOUT_NAME, name)
            put(COLUMN_WORKOUT_DESCRIPTION, description)
            put(COLUMN_WORKOUT_COVER_IMAGE, coverImage)
            put(COLUMN_WORKOUT_CALORIES, calories)
            put(COLUMN_WORKOUT_DURATION, duration)
            put(COLUMN_WORKOUT_DISTANCE, distance)
            put(COLUMN_WORKOUT_IS_CUSTOM, if (isCustom) 1 else 0)
        }

        return db.insert(TABLE_WORKOUTS, null, values)
    }

    fun getAllWorkouts(): List<Workout> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_WORKOUTS", null)
        val workouts = mutableListOf<Workout>()

        while (cursor.moveToNext()) {
            val workout = Workout(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_NAME)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_DESCRIPTION)),
                //val coverImage =
                //    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_COVER_IMAGE))
                coverImage = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_COVER_IMAGE)), // Set image only if it's a valid ID
                calories = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_CALORIES)),
                duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_DURATION)),
                distance = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_DISTANCE)),
                isCustom = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_IS_CUSTOM)) == 1,
            )
            Log.d("DatabaseRetrieve", "Retrieved workout: ${workout.name}, coverImage: ${workout.coverImage}") // Log after retrieval
            workouts.add(workout)
        }
        cursor.close()
        return workouts
    }

    fun getExerciseSteps(workoutId: Int): List<ExerciseStep> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_EXERCISE_STEPS WHERE $COLUMN_STEP_WORKOUT_ID = ? ORDER BY $COLUMN_STEP_NUMBER",
            arrayOf(workoutId.toString())
        )
        val steps = mutableListOf<ExerciseStep>()

        while (cursor.moveToNext()) {
            val stepNumber = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STEP_NUMBER))
            val description =
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STEP_DESCRIPTION))
            val image = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STEP_IMAGE)).takeIf { it != 0 } // Set image only if it's a valid ID

            steps.add(ExerciseStep(stepNumber, description, image))
        }
        cursor.close()
        return steps
    }

    private fun insertWorkoutIfNotExists(
        name: String,
        description: String,
        coverImage: Int,
        calories: Int,
        duration: Int,
        distance: Float
    ): Long? {
        val db = writableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_WORKOUT_ID FROM $TABLE_WORKOUTS WHERE $COLUMN_WORKOUT_NAME = ?",
            arrayOf(name)
        )
        val exists = cursor.moveToFirst()
        cursor.close()

        return if (!exists) {
            insertWorkout(
                name = name,
                description = description,
                coverImage = coverImage,
                calories = calories,
                duration = duration,
                distance = distance,
                isCustom = false
            )
        } else {
            null // Return null if the workout already exists
        }
    }

    fun insertExerciseStep(workoutId: Long, stepNumber: Int, description: String, image: Int?) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STEP_WORKOUT_ID, workoutId)  // Link the step to the workout using COLUMN_STEP_WORKOUT_ID
            put(COLUMN_STEP_NUMBER, stepNumber)
            put(COLUMN_STEP_DESCRIPTION, description)
            image?.let { put(COLUMN_STEP_IMAGE, it) }
        }
        db.insert(TABLE_EXERCISE_STEPS, null, values)
    }


    fun initializeBasicExercises() {
        defaultExercises.forEach { exercise ->
            val workoutId = exercise.imageResId?.let {
                insertWorkoutIfNotExists(
                    name = exercise.name,
                    description = exercise.description,
                    coverImage = it,
                    calories = exercise.calories,
                    duration = exercise.duration,
                    distance = exercise.distance
                )
            }

            // Insert steps if the workout was just created
            if (workoutId != null) {
                exercise.steps.forEach { step ->
                    insertExerciseStep(
                        workoutId = workoutId,
                        stepNumber = step.stepNumber,
                        description = step.description,
                        image = step.image
                    )
                }
            }
        }
    }
}


package com.example.androidfitnesstracker.User

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.androidfitnesstracker.Workout.ExerciseStep
import com.example.androidfitnesstracker.Membership.MembershipType
import com.example.androidfitnesstracker.Membership.SubscriptionStatus
import com.example.androidfitnesstracker.Workout.Workout
import com.example.androidfitnesstracker.Workout.DailySummary
import com.example.androidfitnesstracker.Workout.defaultExercises
import java.util.Calendar
import java.util.Locale

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Main user table
        private const val DATABASE_NAME = "User.db"
        private const val DATABASE_VERSION = 15  // Incremented version for new schema
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
        private const val COLUMN_GOAL_CALORIES = "goal_calories"
        private const val COLUMN_GOAL_WEIGHT = "goal_weight"
        private const val COLUMN_GOAL_STEPS = "goal_steps"
        private const val COLUMN_GOAL_DISTANCE = "goal_distance"


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
        private const val COLUMN_WORKOUT_STEPS = "physical_steps"

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
        $COLUMN_SUBSCRIPTION_STATUS TEXT,
        $COLUMN_GOAL_CALORIES INTEGER DEFAULT 2000,   -- New goal fields
        $COLUMN_GOAL_WEIGHT INTEGER DEFAULT 0,
        $COLUMN_GOAL_STEPS INTEGER DEFAULT 2000,
        $COLUMN_GOAL_DISTANCE REAL DEFAULT 0
    )
"""
        db.execSQL(createUserTable)

        // Create the user activity table
        val createUserActivityTable = """
        CREATE TABLE $TABLE_USER_ACTIVITY (
            $COLUMN_USER_ID INTEGER,
            $COLUMN_TIMESTAMP INTEGER,
            $COLUMN_STEPS INTEGER,
            $COLUMN_CALORIES INTEGER,
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
            $COLUMN_WORKOUT_IS_CUSTOM INTEGER DEFAULT 0, -- 1 for custom workouts, 0 for predefined
            $COLUMN_WORKOUT_STEPS INTEGER
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
        calories: Int,
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
        Log.d("DatabaseInsert", "Inserting activity record: steps=$steps, calories=$calories, distance=$distance")
        return db.insert(TABLE_USER_ACTIVITY, null, values)
    }

    fun getDailySummary(userId: Int): DailySummary {
        val db = readableDatabase

        val todayTimestamp = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val endOfDayTimestamp = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        // Retrieve user goals for calculation
        val goalCalories = getGoalCalories(userId).takeIf { it > 0 } ?: 2000  // Default to 2000 if goal is 0
        val goalSteps = getGoalSteps(userId).takeIf { it > 0 } ?: 2000        // Default to 2000 if goal is 0
        val goalDistance = getGoalDistance(userId).takeIf { it > 0 } ?: 5f    // Default to 5 km if goal is 0

        // Fetch the total steps, calories, and distance for the specific day
        val cursor = db.rawQuery(
            """
        SELECT SUM($COLUMN_STEPS), SUM($COLUMN_CALORIES), SUM($COLUMN_DISTANCE) 
        FROM $TABLE_USER_ACTIVITY 
        WHERE $COLUMN_USER_ID = ? 
        AND $COLUMN_TIMESTAMP BETWEEN ? AND ?
        """,
            arrayOf(userId.toString(), todayTimestamp.toString(), endOfDayTimestamp.toString())
        )

        var summary = DailySummary(
            steps = 0,
            calories = 0f,
            distance = 0f,
            caloriesPercentage = 0f,
            stepsPercentage = 0f,
            distancePercentage = 0f
        )

        // Calculate percentages if there’s data
        if (cursor.moveToFirst()) {
            val steps = cursor.getInt(0)
            val calories = cursor.getFloat(1)
            val distance = cursor.getFloat(2)

            summary = DailySummary(
                steps = steps,
                calories = calories,
                distance = distance,
                caloriesPercentage = (calories / goalCalories) * 100,
                stepsPercentage = (steps.toFloat() / goalSteps) * 100,
                distancePercentage = (distance / goalDistance) * 100
            )
        }
        cursor.close()
        Log.d("DailySummary", "Retrieved daily summary: steps=${summary.steps}, calories=${summary.calories}, distance=${summary.distance}")
        Log.d("TimestampCheck", "Today’s timestamp (midnight): $todayTimestamp")


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
        Log.d("SubscriptionCheck", "UserID: $userId")
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

    // Get and Set Goal Calories
    fun getGoalCalories(userId: Int): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_GOAL_CALORIES FROM $TABLE_USERS WHERE $COLUMN_ID = ?", arrayOf(userId.toString()))
        val goalCalories = if (cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndexOrThrow(
            COLUMN_GOAL_CALORIES
        )) else 2000
        cursor.close()
        return goalCalories
    }

    fun setGoalCalories(userId: Int, goalCalories: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_GOAL_CALORIES, goalCalories)
        }
        db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
    }

    // Get and Set Goal Weight
    fun getGoalWeight(userId: Int): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_GOAL_WEIGHT FROM $TABLE_USERS WHERE $COLUMN_ID = ?", arrayOf(userId.toString()))
        val goalWeight = if (cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndexOrThrow(
            COLUMN_GOAL_WEIGHT
        )) else 0
        cursor.close()
        return goalWeight
    }

    fun setGoalWeight(userId: Int, goalWeight: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_GOAL_WEIGHT, goalWeight)
        }
        db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
    }

    // Get and Set Goal Steps
    fun getGoalSteps(userId: Int): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_GOAL_STEPS FROM $TABLE_USERS WHERE $COLUMN_ID = ?", arrayOf(userId.toString()))
        val goalSteps = if (cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndexOrThrow(
            COLUMN_GOAL_STEPS
        )) else 2000
        cursor.close()
        return goalSteps
    }

    fun setGoalSteps(userId: Int, goalSteps: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_GOAL_STEPS, goalSteps)
        }
        db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
    }

    // Get and Set Goal Distance
    fun getGoalDistance(userId: Int): Float {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_GOAL_DISTANCE FROM $TABLE_USERS WHERE $COLUMN_ID = ?", arrayOf(userId.toString()))
        val goalDistance = if (cursor.moveToFirst()) cursor.getFloat(cursor.getColumnIndexOrThrow(
            COLUMN_GOAL_DISTANCE
        )) else 0f
        cursor.close()
        return goalDistance
    }

    fun setGoalDistance(userId: Int, goalDistance: Float) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_GOAL_DISTANCE, goalDistance)
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
        steps: Int,
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
            put(COLUMN_WORKOUT_STEPS, steps)
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
                description = cursor.getString(cursor.getColumnIndexOrThrow(
                    COLUMN_WORKOUT_DESCRIPTION
                )),
                coverImage = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_COVER_IMAGE)),
                calories = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_CALORIES)),
                duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_DURATION)),
                distance = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_DISTANCE)),
                steps = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_STEPS)),
                instructions = getExerciseSteps(cursor.getInt(cursor.getColumnIndexOrThrow(
                    COLUMN_WORKOUT_ID
                ))),
                isCustom = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_IS_CUSTOM)) == 1,
            )
            //Log.d("DatabaseRetrieve", "Retrieved workout: ${workout.name}, coverImage: ${workout.coverImage}") // Log after retrieval
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
        distance: Float,
        steps: Int,
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
                steps = steps,
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
                    distance = exercise.distance,
                    steps = exercise.steps
                )
            }

            // Insert steps if the workout was just created
            if (workoutId != null) {
                exercise.instructions.forEach { step ->
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


    fun debugLogActivityTimestamps(userId: Int) {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_TIMESTAMP, $COLUMN_STEPS, $COLUMN_CALORIES, $COLUMN_DISTANCE FROM $TABLE_USER_ACTIVITY WHERE $COLUMN_USER_ID = ?",
            arrayOf(userId.toString())
        )

        Log.d("ActivityTimestampDebug", "Logging all activity records for user $userId:")

        while (cursor.moveToNext()) {
            val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
            val steps = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STEPS))
            val calories = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_CALORIES))
            val distance = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_DISTANCE))

            // Log the raw timestamp and the values associated with it
            Log.d("ActivityTimestampDebug", "Timestamp: $timestamp, Steps: $steps, Calories: $calories, Distance: $distance")
        }

        cursor.close()
    }
}


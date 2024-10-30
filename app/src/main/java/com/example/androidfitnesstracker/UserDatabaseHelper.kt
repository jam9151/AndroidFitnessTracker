package com.example.androidfitnesstracker

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.Locale

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Main user table
        private const val DATABASE_NAME = "User.db"
        private const val DATABASE_VERSION = 8  // Incremented version for new schema
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

        // Create Singleton instance
        @Volatile private var instance: UserDatabaseHelper? = null
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
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?", arrayOf(username))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun isEmailTaken(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ?", arrayOf(email))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getEmailByUsername(username: String): String? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_EMAIL FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?", arrayOf(username))

        val email = if (cursor.moveToFirst()) cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)) else null
        cursor.close()
        return email  // Returns the email if found, otherwise null
    }

    fun insertActivityRecord(userId: Int, timestamp: Long, steps: Int, calories: Float, distance: Float): Long {
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
        val userId = if (cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndexOrThrow("id")) else null
        cursor.close()
        return userId
    }

    fun getFirstName(userId: Int): String? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_FIRST_NAME FROM $TABLE_USERS WHERE $COLUMN_ID = ?", arrayOf(userId.toString()))
        val firstName = if (cursor.moveToFirst()) cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)) else null
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
        val cursor = db.rawQuery("SELECT $COLUMN_LAST_NAME FROM $TABLE_USERS WHERE $COLUMN_ID = ?", arrayOf(userId.toString()))
        val lastName = if (cursor.moveToFirst()) cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)) else null
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
        val cursor = db.rawQuery("SELECT $COLUMN_FIRST_NAME, $COLUMN_LAST_NAME FROM $TABLE_USERS WHERE $COLUMN_ID = ?", arrayOf(userId.toString()))
        val fullName = if (cursor.moveToFirst()) {
            "${cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME))} ${cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME))}"
        } else {
            "Guest"  // Default in case the user isn't found
        }
        cursor.close()
        return fullName
    }

    // Get Age
    fun getAge(userId: Int): Int? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_AGE FROM $TABLE_USERS WHERE $COLUMN_ID = ?", arrayOf(userId.toString()))
        val age = if (cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)) else null
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
        val cursor = db.rawQuery("SELECT $COLUMN_WEIGHT FROM $TABLE_USERS WHERE $COLUMN_ID = ?", arrayOf(userId.toString()))
        val weight = if (cursor.moveToFirst()) cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)) else null
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
        val cursor = db.rawQuery("SELECT $COLUMN_GENDER FROM $TABLE_USERS WHERE $COLUMN_ID = ?", arrayOf(userId.toString()))
        val gender = if (cursor.moveToFirst()) cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)) else null
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
        val cursor = db.rawQuery("SELECT $COLUMN_HEIGHT FROM $TABLE_USERS WHERE $COLUMN_ID = ?", arrayOf(userId.toString()))
        val height = if (cursor.moveToFirst()) cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)) else null
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
        val cursor = db.rawQuery("SELECT $COLUMN_DIET FROM $TABLE_USERS WHERE $COLUMN_ID = ?", arrayOf(userId.toString()))
        val diet = if (cursor.moveToFirst()) cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIET)) else null
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
        val cursor = db.rawQuery("SELECT $COLUMN_PROFILE_PICTURE FROM $TABLE_USERS WHERE $COLUMN_ID = ?", arrayOf(userId.toString()))
        val profilePicture = if (cursor.moveToFirst()) cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_PICTURE)) else null
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
        val cursor = db.rawQuery("SELECT $COLUMN_MEMBERSHIP_TYPE FROM $TABLE_USERS WHERE $COLUMN_ID = ?", arrayOf(userId.toString()))
        val membershipType = if (cursor.moveToFirst()) cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEMBERSHIP_TYPE)) else null
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
        val cursor = db.rawQuery("SELECT $COLUMN_SUBSCRIPTION_STATUS FROM $TABLE_USERS WHERE $COLUMN_ID = ?", arrayOf(userId.toString()))
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

}

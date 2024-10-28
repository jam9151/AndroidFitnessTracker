package com.example.androidfitnesstracker

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        //main user table
        private const val DATABASE_NAME = "User.db"
        private const val DATABASE_VERSION = 4
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_EMAIL = "email"

        // activity tracking
        private const val TABLE_USER_ACTIVITY = "user_activity"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_TIMESTAMP = "timestamp"
        private const val COLUMN_STEPS = "steps"
        private const val COLUMN_CALORIES = "calories"
        private const val COLUMN_DISTANCE = "distance"



        //Create a Singleton so SQL doesn't get confused
        @Volatile private var instance: UserDatabaseHelper? = null

        // Singleton instance
        fun getInstance(context: Context): UserDatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: UserDatabaseHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {

        //main user table
        val createTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_EMAIL TEXT
            )"""
        db.execSQL(createTable)

        // user activity
        val createUserActivityTable = """
            CREATE TABLE $TABLE_USER_ACTIVITY (
                $COLUMN_USER_ID INTEGER,
                $COLUMN_TIMESTAMP INTEGER,
                $COLUMN_STEPS INTEGER,
                $COLUMN_CALORIES REAL,
                $COLUMN_DISTANCE REAL,
                FOREIGN KEY ($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID)
            )"""
        db.execSQL(createUserActivityTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER_ACTIVITY") // Drop user_activity table
        onCreate(db)
        //as this is written, it re-creates the table when the database is upgraded
        //you have to modify this function when you upgrade the database
    }

    fun addUser(username: String, password: String, email: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_EMAIL, email)
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
}

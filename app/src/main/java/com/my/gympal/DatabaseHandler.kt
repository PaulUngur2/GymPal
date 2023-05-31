package com.my.gympal

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "exercise_data.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "exercises"
        private const val TABLE_NAME_DAY = "day"
        private const val COLUMN_REPS = "reps"
        private const val COLUMN_WEIGHT = "weight"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_TABLE_NAME = "table_name"
        private const val COLUMN_TARGET = "target"
        private const val COLUMN_CURRENT = "current"
        private const val COLUMN_ID = "id"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery =
            "CREATE TABLE $TABLE_NAME ($COLUMN_NAME TEXT, $COLUMN_DESCRIPTION TEXT, $COLUMN_TABLE_NAME TEXT)"

        db?.execSQL(createTableQuery)
        val exercises = listOf(
            Exercise(
                "bench_press",
                "Bench Press",
                "Lie on a bench and lift a barbell up and down above your chest."
            ),
            Exercise(
                "dead_lift",
                "Dead-lift",
                "Lift a barbell from the ground to your hips, standing up straight in the process."
            ),
            Exercise(
                "squat",
                "Squat",
                "Hold a barbell on your shoulders and lower your hips down and back, then stand up again."
            ),
            Exercise(
                "overhead_press",
                "Overhead Press",
                "Lift a barbell above your head from shoulder level, then lower it back down."
            ),
            Exercise(
                "bent_over_rows",
                "Bent-Over Rows",
                "Hold a barbell at arm's length and pull it up towards your chest, keeping your back straight."
            ),
            Exercise(
                "barbell_curls",
                "Barbell Curls",
                "Lift a barbell from waist level to shoulder level, keeping your elbows close to your body."
            ),
            Exercise(
                "triceps_extensions",
                "Triceps Extensions",
                "Hold a dumbbell in both hands and lift it above your head, then lower it behind your neck."
            ),
            Exercise(
                "lateral_raises",
                "Lateral Raises",
                "Hold dumbbells at your sides and lift them up to shoulder level, keeping your elbows straight."
            ),
            Exercise(
                "leg_press",
                "Leg Press",
                "Push a weighted platform away from you with your feet, then bring it back towards you."
            ),
            Exercise(
                "calf_raises",
                "Calf Raises",
                "Stand on your toes with weights on your shoulders, then lower your heels back down."
            )
        )

        val contentValues = ContentValues()
        exercises.forEach { exercise ->
            contentValues.clear()
            contentValues.put(COLUMN_NAME, exercise.name)
            contentValues.put(COLUMN_DESCRIPTION, exercise.description)
            contentValues.put(COLUMN_TABLE_NAME, exercise.nameDB)
            db?.insert(TABLE_NAME, null, contentValues)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Not needed for this app
    }

    fun insertSet(reps: Int, weight: Int, tableName: String) {
        val values = ContentValues()
        values.put(COLUMN_REPS, reps)
        values.put(COLUMN_WEIGHT, weight)

        val db = writableDatabase
        db.insert(tableName, null, values)
        db.close()
    }

    fun createTableIfNotExists(tableName: String) {
        val db = writableDatabase
        val query =
            "CREATE TABLE IF NOT EXISTS $tableName ($COLUMN_REPS INTEGER, $COLUMN_WEIGHT INTEGER)"
        db.execSQL(query)
    }

    fun newExercise(name: String, description: String, tableName: String) {
        val db = writableDatabase
        val query =
            "INSERT INTO $TABLE_NAME ($COLUMN_NAME, $COLUMN_DESCRIPTION, $COLUMN_TABLE_NAME) VALUES ('$name', '$description', '$tableName')"
        db.execSQL(query)
    }

    @SuppressLint("Range")
    fun getExerciseInfo(exerciseName: String): Exercise {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME = '$exerciseName'"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()
        val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
        val description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
        val tableName = cursor.getString(cursor.getColumnIndex(COLUMN_TABLE_NAME))
        cursor.close()
        return Exercise(tableName, name, description)
    }

    @SuppressLint("Range")
    fun getExercisesInfo(): MutableList<Exercise> {
        val exercises: MutableList<Exercise> = mutableListOf()

        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
            val description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
            val tableName = cursor.getString(cursor.getColumnIndex(COLUMN_TABLE_NAME))
            val exercise = Exercise(tableName, name, description)
            exercises.add(exercise)
        }

        cursor.close()
        return exercises
    }


    fun deleteExercise(selectedExercise: String) {
        val db = writableDatabase
        val query = "DELETE FROM $TABLE_NAME WHERE $COLUMN_NAME = '$selectedExercise'"
        db.execSQL(query)
    }

    fun renameExercise(oldName: String, newName: String, newDescription: String) {
        val db = writableDatabase
        val queryBuilder = StringBuilder("UPDATE $TABLE_NAME SET ")

        if (newName.isNotEmpty()) {
            queryBuilder.append("$COLUMN_NAME = '$newName'")
        }

        if (newDescription.isNotEmpty()) {
            if (newName.isNotEmpty()) {
                queryBuilder.append(", ")
            }
            queryBuilder.append("$COLUMN_DESCRIPTION = '$newDescription'")
        }

        queryBuilder.append(" WHERE $COLUMN_NAME = '$oldName'")
        db.execSQL(queryBuilder.toString())
    }

    @SuppressLint("Range")
    fun getSetFromTable(tableName: String): MutableList<String> {
        val db = readableDatabase
        val query = "SELECT * FROM $tableName"
        val cursor = db.rawQuery(query, null)

        val setList: MutableList<String> = mutableListOf()

        cursor.use {
            while (it.moveToNext()) {
                val reps = it.getInt(it.getColumnIndex(COLUMN_REPS))
                val weight = it.getInt(it.getColumnIndex(COLUMN_WEIGHT))
                val setString = "Reps: $reps Weight: $weight"
                setList.add(setString)
            }
        }

        return setList
    }

    fun createNewDayRow(target: Int) {
        val db = writableDatabase
        val createTableQuery =
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME_DAY ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TARGET INTEGER, $COLUMN_CURRENT INTEGER)"
        db.execSQL(createTableQuery)

        val insertRowQuery =
            "INSERT INTO $TABLE_NAME_DAY ($COLUMN_TARGET, $COLUMN_CURRENT) VALUES ($target, 0)"
        db.execSQL(insertRowQuery)
    }

    @SuppressLint("Range")
    fun getDays(): List<String> {
        val db = readableDatabase
        val query = "SELECT $COLUMN_ID FROM $TABLE_NAME_DAY"
        val cursor = db.rawQuery(query, null)

        val daysList = mutableListOf<String>()

        cursor.use {
            while (cursor.moveToNext()) {
                val day = cursor.getString(cursor.getColumnIndex(COLUMN_ID))
                daysList.add("Day $day")
            }
        }

        return daysList
    }

    @SuppressLint("Range")
    fun getTargetCurrentFromDay(day: Int): Pair<Int, Int> {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME_DAY WHERE $COLUMN_ID = $day"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()
        val target = cursor.getInt(cursor.getColumnIndex(COLUMN_TARGET))
        val current = cursor.getInt(cursor.getColumnIndex(COLUMN_CURRENT))
        cursor.close()
        return Pair(target, current)
    }

    fun updateCurrentDay(day: Int, newCurrent: Int): Int {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_CURRENT, newCurrent)
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(day.toString())
        db.update(TABLE_NAME_DAY, values, whereClause, whereArgs)

        return newCurrent
    }

    fun deleteDay(day: Int) {
        val db = writableDatabase
        val query = "DELETE FROM $TABLE_NAME_DAY WHERE $COLUMN_ID = $day"
        db.execSQL(query)
    }

    @SuppressLint("Range")
    fun getLastDay(): Int {
        val db = readableDatabase
        val query = "SELECT $COLUMN_ID FROM $TABLE_NAME_DAY ORDER BY $COLUMN_ID DESC LIMIT 1"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()
        val lastDay = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
        cursor.close()
        return lastDay
    }


}
package com.my.gympal

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class ExerciseDetailActivity : AppCompatActivity() {

    companion object {
        private var weight = 0
        private var reps = 0
        private lateinit var sharedPreferences: SharedPreferences
        private const val REP_COUNT_KEY = "rep_count"
        private const val WEIGHT_KEY = "weight"
        private lateinit var databaseHandler: DatabaseHandler
    }


    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_detail)

        databaseHandler = DatabaseHandler(this)

        val minusWeightButton = findViewById<Button>(R.id.minus_weight_button)
        val plusWeightButton = findViewById<Button>(R.id.plus_weight_button)
        val minusRepsButton = findViewById<Button>(R.id.minus_sets_button)
        val plusRepsButton = findViewById<Button>(R.id.plus_sets_button)
        val weightEditText: EditText = findViewById(R.id.weight_edit_text)
        val repsEditText: EditText = findViewById(R.id.sets_edit_text)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        weight = sharedPreferences.getInt(WEIGHT_KEY, 0)
        weightEditText.setText(weight.toString())

        reps = sharedPreferences.getInt(REP_COUNT_KEY, 0)
        repsEditText.setText(reps.toString())

        minusWeightButton.setOnClickListener {
            decreaseValue(weight, weightEditText) { updatedWeight ->
                weight = updatedWeight
                saveWeight(weight)
            }
        }

        minusRepsButton.setOnClickListener {
            decreaseValue(reps, repsEditText) { updatedReps ->
                reps = updatedReps
                saveReps(reps)
            }
        }

        plusWeightButton.setOnClickListener {
            updateValue(weight, weightEditText) { updatedWeight ->
                weight = updatedWeight
                saveWeight(weight)
            }
        }

        plusRepsButton.setOnClickListener {
            updateValue(reps, repsEditText) { updatedReps ->
                reps = updatedReps
                saveReps(reps)
            }
        }


        val listView = findViewById<ListView>(R.id.sets_list_view)
        val setList = mutableListOf<String>()

        val setAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, setList)
        listView.adapter = setAdapter

        val exercise =
            databaseHandler.getExerciseInfo(intent.getStringExtra("exercise_name").toString())

        val exerciseName = exercise.name
        val tableName = exercise.nameDB
        val exerciseDescription = exercise.description
        var sets = databaseHandler.getSetFromTable(tableName)
        var reversedSetList = sets.reversed()
        reversedSetList.let { setList.addAll(it) }



        findViewById<TextView>(R.id.exercise_name).text = exerciseName
        findViewById<TextView>(R.id.exercise_description).text = exerciseDescription

        val setButton = findViewById<Button>(R.id.add_set_button)
        setButton.setOnClickListener {
            databaseHandler.createTableIfNotExists(tableName)

            saveSet(weight, reps, tableName)

            setList.clear()
            sets = databaseHandler.getSetFromTable(tableName)
            reversedSetList = sets.reversed()
            reversedSetList.let { setList.addAll(it) }

            setAdapter.notifyDataSetChanged()
        }
    }

    private fun updateValue(value: Int, editText: EditText, saveAction: (Int) -> Unit) {
        val updatedValue = value + 1
        editText.setText(updatedValue.toString())
        saveAction(updatedValue)
    }

    private fun decreaseValue(value: Int, editText: EditText, saveAction: (Int) -> Unit) {
        if (value > 0) {
            val updatedValue = value - 1
            editText.setText(updatedValue.toString())
            saveAction(updatedValue)
        }
    }

    private fun saveWeight(weight: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(WEIGHT_KEY, weight)
        editor.apply()
    }

    private fun saveReps(reps: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(REP_COUNT_KEY, reps)
        editor.apply()
    }

    private fun saveSet(reps: Int, weight: Int, tableName: String) {
        databaseHandler.insertSet(reps, weight, tableName)
    }
}

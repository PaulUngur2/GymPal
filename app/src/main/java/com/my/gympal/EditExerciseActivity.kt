package com.my.gympal

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditExerciseActivity : AppCompatActivity() {

    companion object{
        private lateinit var databaseHandler: DatabaseHandler
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_exercise)

        databaseHandler = DatabaseHandler(this)
        val exerciseName = findViewById<TextView>(R.id.exercise_name)
        val editButton = findViewById<Button>(R.id.edit_exercise_button)
        val newName = findViewById<TextView>(R.id.exercise_new_name_input)
        val newDescription = findViewById<TextView>(R.id.exercise_new_description_input)

        exerciseName.text = intent.getStringExtra("exercise_name")

        editButton.setOnClickListener {
            if(newName.text.toString().isBlank() && newDescription.text.toString().isBlank()){
                Toast.makeText(this, "Please enter a new name", Toast.LENGTH_SHORT).show()
            } else {
                databaseHandler.renameExercise(
                    exerciseName.text.toString(),
                    newName.text.toString(),
                    newDescription.text.toString()
                )
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
}

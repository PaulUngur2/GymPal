package com.my.gympal

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NewExerciseActivity : AppCompatActivity() {

    private lateinit var databaseHandler: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_exercise)

        databaseHandler = DatabaseHandler(this)

        val nameEditText = findViewById<EditText>(R.id.exercise_name_input)
        val descriptionEditText = findViewById<EditText>(R.id.exercise_description_input)


        val saveButton = findViewById<Button>(R.id.add_exercise_button)
        saveButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val description = descriptionEditText.text.toString()

            if (name.isBlank() || description.isBlank()) {
                Toast.makeText(this, "Please enter a name and description.", Toast.LENGTH_SHORT).show()
            } else {
                databaseHandler.newExercise(name, description, convertToDBName(name))
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun convertToDBName(name: String): String {
        return name.lowercase().replace(" ", "_").replace("-", "_")
    }

}

package com.my.gympal

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NewCountingCaloriesActivity : AppCompatActivity() {

    companion object {
        private lateinit var databaseHandler: DatabaseHandler
    }

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_counting_calories)

        databaseHandler = DatabaseHandler(this)
        val day: Int = intent.getIntExtra("day", 1)
        val targetCalories = findViewById<EditText>(R.id.edit_text_target_calories)
        val saveButton = findViewById<Button>(R.id.button_save)
        val textViewCurrent = findViewById<TextView>(R.id.text_view_current_day)

        textViewCurrent.text = "Day $day"

        saveButton.setOnClickListener {
            databaseHandler.createNewDayRow(targetCalories.text.toString().toInt())
            val intent = Intent(this, CountingCaloriesActivity::class.java)
            intent.putExtra("day", day)
            startActivity(intent)
        }
    }
}
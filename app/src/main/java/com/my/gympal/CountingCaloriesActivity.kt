package com.my.gympal

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CountingCaloriesActivity : AppCompatActivity() {

    companion object {
        private lateinit var databaseHandler: DatabaseHandler
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counting_calories)

        databaseHandler = DatabaseHandler(this)

        val day: Int = intent.getIntExtra("day", 1)

        var (target, current) = databaseHandler.getTargetCurrentFromDay(day)

        val textTargetCalories = findViewById<TextView>(R.id.text_target_calories)
        val textCurrentCalories = findViewById<TextView>(R.id.text_current_calories)
        val textDayTitle = findViewById<TextView>(R.id.text_day_title)
        val button = findViewById<Button>(R.id.button_add_calories)
        val calories = findViewById<EditText>(R.id.edit_extra_calories)

        textTargetCalories.text = target.toString()
        textCurrentCalories.text = current.toString()
        textDayTitle.text = "Day $day"

        button.setOnClickListener {
            val caloriesToAdd = calories.text.toString().toInt()
            val updatedCurrent = databaseHandler.updateCurrentDay(day, current + caloriesToAdd)
            current = updatedCurrent
            updateCurrentCalories(day)
        }

    }

    private fun updateCurrentCalories(day: Int) {
        val textCurrentCalories = findViewById<TextView>(R.id.text_current_calories)
        val (_, current) = databaseHandler.getTargetCurrentFromDay(day)
        textCurrentCalories.text = current.toString()
    }

}

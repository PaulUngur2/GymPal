package com.my.gympal

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    companion object {
        private const val CONTEXT_MENU_DELETE = 1
        private const val CONTEXT_MENU_EDIT = 2
        private lateinit var databaseHandler: DatabaseHandler
        private lateinit var newExerciseLauncher: ActivityResultLauncher<Intent>
        private var exerciseNames = mutableListOf<Exercise>()
    }

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseHandler = DatabaseHandler(this)

        val spinner = findViewById<Spinner>(R.id.title_spinner)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                when (spinner.getItemAtPosition(position) as String) {
                    "Calorie Helper" -> {
                        val calorieIntent = Intent(this@MainActivity, CalorieActivity::class.java)
                        startActivity(calorieIntent)
                    }

                    "Sets Helper" -> {
                        // No action required
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action required
            }
        }

        val listView = findViewById<ListView>(R.id.exercise_list)

        refreshExerciseList()

        newExerciseLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    refreshExerciseList()
                }
            }

        registerForContextMenu(listView)

        listView.setOnItemClickListener { parent, view, position, id ->
            val exerciseName = exerciseNames[position]
            val intent = Intent(this, ExerciseDetailActivity::class.java)
            intent.putExtra("exercise_name", exerciseName.name)
            databaseHandler.createTableIfNotExists(exerciseName.nameDB)
            startActivity(intent)
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab_add_exercise)
        fab.setImageResource(R.drawable.add)
        fab.setOnClickListener {
            val intent = Intent(this, NewExerciseActivity::class.java)
            newExerciseLauncher.launch(intent)
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        if (v?.id == R.id.exercise_list) {
            menu?.setHeaderTitle("Options")
            menu?.add(Menu.NONE, CONTEXT_MENU_DELETE, Menu.NONE, "Delete")
            menu?.add(Menu.NONE, CONTEXT_MENU_EDIT, Menu.NONE, "Edit")
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val position = info.position

        return when (item.itemId) {
            CONTEXT_MENU_DELETE -> {
                val selectedExercise = exerciseNames[position].name
                databaseHandler.deleteExercise(selectedExercise)
                exerciseNames.clear()
                refreshExerciseList()
                true
            }

            CONTEXT_MENU_EDIT -> {
                val selectedExercise = exerciseNames[position].name
                val intent = Intent(this, EditExerciseActivity::class.java)
                intent.putExtra("exercise_name", selectedExercise)
                newExerciseLauncher.launch(intent)
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    private fun refreshExerciseList() {
        exerciseNames = databaseHandler.getExercisesInfo()
        val listView = findViewById<ListView>(R.id.exercise_list)
        listView.adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, exerciseNames.map { it.name })
    }
}


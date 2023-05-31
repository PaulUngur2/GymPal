package com.my.gympal

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

class CalorieActivity : AppCompatActivity() {

    companion object {
        private const val CONTEXT_MENU_DELETE = 1
        private lateinit var databaseHandler: DatabaseHandler
        private lateinit var newDayLauncher: ActivityResultLauncher<Intent>
        private var days = mutableListOf<String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calorie)

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
                        // No action required
                    }

                    "Sets Helper" -> {
                        val setsIntent = Intent(this@CalorieActivity, MainActivity::class.java)
                        startActivity(setsIntent)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action required
            }
        }
        val listView = findViewById<ListView>(R.id.days_list)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, days)
        listView.adapter = adapter

        refreshDaysList(adapter)


        newDayLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    refreshDaysList(adapter)
                }
            }

        registerForContextMenu(listView)

        listView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, CountingCaloriesActivity::class.java)
            intent.putExtra("day", days[position].replace("\\D+".toRegex(), "").toIntOrNull())
            startActivity(intent)
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab_add_day)
        fab.setImageResource(R.drawable.add)
        fab.setOnClickListener {
            refreshDaysList(adapter)
            val intent = Intent(this, NewCountingCaloriesActivity::class.java)
            intent.putExtra("day", databaseHandler.getLastDay() + 1)
            newDayLauncher.launch(intent)
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        if (v?.id == R.id.days_list) {
            menu?.setHeaderTitle("Options")
            menu?.add(Menu.NONE, CONTEXT_MENU_DELETE, Menu.NONE, "Delete")
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val position = info.position
        val listView = findViewById<ListView>(R.id.days_list)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, days)
        listView.adapter = adapter

        return when (item.itemId) {
            CONTEXT_MENU_DELETE -> {
                val selectedDay = days[position]
                val dayNumber = selectedDay.replace("\\D+".toRegex(), "").toIntOrNull()
                if (dayNumber != null) {
                    databaseHandler.deleteDay(dayNumber)
                }
                refreshDaysList(adapter)
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    private fun refreshDaysList(adapter: ArrayAdapter<*>) {
        days.clear()
        days.addAll(databaseHandler.getDays())
        adapter.notifyDataSetChanged()
    }

}
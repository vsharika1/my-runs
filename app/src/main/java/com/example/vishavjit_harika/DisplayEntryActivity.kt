package com.example.vishavjit_harika

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DisplayEntryActivity : AppCompatActivity() {
    private lateinit var viewModel: ExerciseEntryViewModel
    private lateinit var database: ExerciseEntryDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository
    private var entryKey: Long = -1
    private lateinit var entry: ExerciseEntry
    private lateinit var deleteButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_entry)

        entryKey = GlobalKey.selectedEntryKey

        database = ExerciseEntryDatabase.getInstance(application)
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)
        val viewModelFactory = ExerciseEntryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[ExerciseEntryViewModel::class.java]

        if (entryKey != -1L) {
            lifecycleScope.launch {
                entry = repository.get(entryKey)
                populateEntryData(entry)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.delete_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_button -> {
                entryKey = GlobalKey.selectedEntryKey
                lifecycleScope.launch {
                    repository.delete(entryKey)
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun populateEntryData(entry: ExerciseEntry) {
        val inputTypeEditText = findViewById<EditText>(R.id.input_type_editTextText)
        val activityTypeEditText = findViewById<EditText>(R.id.activity_type_editTextText)
        val dateAndTimeEditText = findViewById<EditText>(R.id.date_and_time_editTextText)
        val durationEditText = findViewById<EditText>(R.id.display_duration_editTextText)
        val distanceEditText = findViewById<EditText>(R.id.display_distance_editTextText)
        val caloriesEditText = findViewById<EditText>(R.id.editTextText6)
        val heartRateEditText = findViewById<EditText>(R.id.display_heart_rate_editTextText)

        val inputType: String = displayInputType(entry.inputType)
        val activityType: String = displayActivityType(entry.activityType)

        inputTypeEditText.setText(inputType)
        activityTypeEditText.setText(activityType)
        dateAndTimeEditText.setText(formatDate(entry.dateTime))
        durationEditText.setText(entry.duration.toString())
        distanceEditText.setText(entry.distance.toString())
        caloriesEditText.setText(entry.calorie.toString())
        heartRateEditText.setText(entry.heartRate.toString())
    }

    private fun formatDate(calendar: Calendar): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return formatter.format(calendar.time)
    }

    private fun displayInputType(entryInput: Int): String {
        var selection: String = ""
        when (entryInput) {
            1 -> selection = "Manual Entry"
            2 -> selection = "GPS"
            3 -> selection = "Automatic"
        }
        return selection
    }

    private fun displayActivityType(entryActivity: Int): String {
        var selection: String = ""
        when (entryActivity) {
            1 -> selection = "Running"
            2 -> selection = "Walking"
            3 -> selection = "Standing"
            4 -> selection = "Cycling"
            5 -> selection = "Hiking"
            6 -> selection = "Downhill Skiing"
            7 -> selection = "Cross-Country Skiing"
            8 -> selection = "Snowboarding"
            9 -> selection = "Skating"
            10 -> selection = "Swimming"
            11 -> selection = "Mountain Biking"
            12 -> selection = "Wheelchair"
            13 -> selection = "Elliptical"
            14 -> selection = "Other"
        }
        return selection
    }
}
package com.example.vishavjit_harika

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.ListView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import java.text.ParseException
import java.util.Calendar
import java.util.Locale

/**
 * Resources used:
 * 1. LayoutKotlin Demo provided by professor
 * 2. Dialog Demo provided by professor
 */

class ManualEntryActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private val workoutStatistics = arrayOf(
        "Date", "Time", "Duration", "Distance", "Calories", "Heart Rate", "Comment"
    )
    private lateinit var myListView: ListView
    private val calendar = Calendar.getInstance()

    val inputData = mutableMapOf<String, Any?>()

    private lateinit var database: ExerciseEntryDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_entry)

        myListView = this.findViewById(R.id.myListView)

        database = ExerciseEntryDatabase.getInstance(this)
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)

        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1, workoutStatistics
        )
        myListView.adapter = arrayAdapter
        myListView.setOnItemClickListener() { parent: AdapterView<*>, view: View, position: Int, id: Long ->
            when (position) {
                0 -> {
                    val datePickerDialog = DatePickerDialog(
                        this,
                        { _, year, monthOfYear, dayOfMonth ->
                            val selectedDate = Calendar.getInstance()
                            selectedDate.set(year, monthOfYear, dayOfMonth)
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val dateString = dateFormat.format(selectedDate.time)
                            ManualDataEntry.date = dateString
                            Log.i("Date", ManualDataEntry.date)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    datePickerDialog.show()
                }

                1 -> {
                    val timePickerDialog = TimePickerDialog(
                        this,
                        { _, hourOfDay, minute ->
                            val selectedTime = Calendar.getInstance()
                            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            selectedTime.set(Calendar.MINUTE, minute)
                            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                            val timeString = timeFormat.format(selectedTime.time)
                            ManualDataEntry.time = timeString
                            Log.i("Time", ManualDataEntry.time)
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    )
                    timePickerDialog.show()
                }

                2 -> {
                    val myDialog = MyDialog()
                    val bundle = Bundle()
                    bundle.putInt(MyDialog.DIALOG_KEY, MyDialog.DURATION_DIALOG)
                    myDialog.arguments = bundle
                    myDialog.show(supportFragmentManager, "duration dialog")
                }

                3 -> {
                    val myDialog = MyDialog()
                    val bundle = Bundle()
                    bundle.putInt(MyDialog.DIALOG_KEY, MyDialog.DISTANCE_DIALOG)
                    myDialog.arguments = bundle
                    myDialog.show(supportFragmentManager, "distance dialog")
                }

                4 -> {
                    val myDialog = MyDialog()
                    val bundle = Bundle()
                    bundle.putInt(MyDialog.DIALOG_KEY, MyDialog.CALORIES_DIALOG)
                    myDialog.arguments = bundle
                    myDialog.show(supportFragmentManager, "calories dialog")
                }

                5 -> {
                    val myDialog = MyDialog()
                    val bundle = Bundle()
                    bundle.putInt(MyDialog.DIALOG_KEY, MyDialog.HEART_RATE_DIALOG)
                    myDialog.arguments = bundle
                    myDialog.show(supportFragmentManager, "heart rate dialog")
                }

                6 -> {
                    val myDialog = MyDialog()
                    val bundle = Bundle()
                    bundle.putInt(MyDialog.DIALOG_KEY, MyDialog.COMMENT_DIALOG)
                    myDialog.arguments = bundle
                    myDialog.show(supportFragmentManager, "comment dialog")
                }
            }
        }

        val saveButton: Button = findViewById(R.id.save_button)
        val cancelButton: Button = findViewById(R.id.cancel_button)

        saveButton.setOnClickListener() {
            lifecycleScope.launch {
                val dateTime: Calendar =
                    dateTimeConvertor(ManualDataEntry.date, ManualDataEntry.time)
                val entry = ExerciseEntry(
                    inputType = ManualDataEntry.inputActivityType,
                    activityType = ManualDataEntry.activityType,
                    dateTime = dateTime,
                    duration = ManualDataEntry.duration,
                    distance = ManualDataEntry.distance,
                    avgPace = 0.0,
                    avgSpeed = 0.0,
                    calorie = ManualDataEntry.calories,
                    climb = 0.0,
                    heartRate = ManualDataEntry.heartRate,
                    comment = ManualDataEntry.comment,
                    locationList = ArrayList<LatLng>()
                )
                repository.insert(entry)
                finish()
                Log.i("Entry", entry.toString())
            }
        }
        cancelButton.setOnClickListener() {
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Entry discarded.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val inputDate = "${year.toString()} / ${month + 1} / $dayOfMonth"
        Log.i("date", inputDate);
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val inputTime = "$hourOfDay : $minute"
        Log.i("time", inputTime)
    }

    private fun dateTimeConvertor(date: String, time: String): Calendar {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val dateTime = Calendar.getInstance()

        try {
            val parsedDate = dateFormat.parse("$date $time")
            parsedDate?.let {
                dateTime.time = it
            }
        } catch (e: ParseException) {
            Log.e("ManualEntryActivity", "Parsing datetime error", e)
        }

        return dateTime
    }
}
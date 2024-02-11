package com.example.vishavjit_harika

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment

/**
 * Resources used:
 * 1. https://stackoverflow.com/questions/55684917/the-spinner-doesnt-work-in-my-kotlin-fragment
 * 2. https://developer.android.com/develop/ui/views/components/spinner
 */

class FragmentStart : Fragment(), AdapterView.OnItemSelectedListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val inf = inflater.inflate(R.layout.fragment_start, container, false)

        val inputSpinner: Spinner = inf.findViewById(R.id.input_spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            inf.context,
            R.array.workout_input,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            inputSpinner.adapter = adapter
        }

        val activitySpinner: Spinner = inf.findViewById(R.id.activity_spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            inf.context,
            R.array.activity_input,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            activitySpinner.adapter = adapter
        }

        val startWorkoutButton: Button = inf.findViewById(R.id.startWorkout_button)
        startWorkoutButton.setOnClickListener() {
            val itemSelected: String = inputSpinner.selectedItem.toString()
            ManualDataEntry.inputActivityType = selectedInputActivity(itemSelected)
            val activitySelected: String = activitySpinner.selectedItem.toString()
            ManualDataEntry.activityType = selectedActivity(activitySelected)

            if (itemSelected == "Manual Entry") {
                val intent: Intent = Intent(inf.context, ManualEntryActivity::class.java)
                startActivity(intent)
            }
            else if (itemSelected == "GPS" || itemSelected == "Automatic") {
                val intent: Intent = Intent(inf.context, MapsActivity::class.java)
                startActivity(intent)
            }
        }

        return inf
    }

    private fun selectedInputActivity(itemSelected: String): Int {
        var selection: Int = -1
        when (itemSelected) {
            "Manual Entry" -> selection = 1
            "GPS" -> selection = 2
            "Automatic" -> selection = 3
        }
        return selection
    }

    private fun selectedActivity(activitySelected: String): Int {
        var selection: Int = -1
        when (activitySelected) {
            "Running" -> selection = 1
            "Walking" -> selection = 2
            "Standing" -> selection = 3
            "Cycling" -> selection = 4
            "Hiking" -> selection = 5
            "Downhill Skiing" -> selection = 6
            "Cross-Country Skiing" -> selection = 7
            "Snowboarding" -> selection = 8
            "Skating" -> selection = 9
            "Swimming" -> selection = 10
            "Mountain Biking" -> selection = 11
            "Wheelchair" -> selection = 12
            "Elliptical" -> selection = 13
            "Other" -> selection = 14
        }
        return selection
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item is selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback.
    }
}
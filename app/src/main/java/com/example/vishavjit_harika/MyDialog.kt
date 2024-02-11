package com.example.vishavjit_harika

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class MyDialog : DialogFragment(), DialogInterface.OnClickListener{
    companion object{
        const val DIALOG_KEY = "dialog"
        const val DURATION_DIALOG = 1
        const val DISTANCE_DIALOG = 2
        const val CALORIES_DIALOG = 3
        const val HEART_RATE_DIALOG = 4
        const val COMMENT_DIALOG = 5
        const val UNIT_PREFERENCE_DIALOG = 6
        const val COMMENTS_SETTINGS_DIALOG = 7
        const val PICK_PICTURE_DIALOG = 8
    }

    lateinit var openCameraButton: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var ret: Dialog
        val bundle = arguments
        when (bundle?.getInt(DIALOG_KEY)) {
            DURATION_DIALOG -> {
                val builder = AlertDialog.Builder(requireActivity())
                val view: View = requireActivity().layoutInflater.inflate(R.layout.fragment_duration_dialog,
                    null)
                builder.setView(view)
                builder.setTitle("Duration")
                builder.setPositiveButton("OK") {_, _ ->
                    val durationEditText: EditText = view.findViewById(R.id.duration_editTextNumberDecimal2)
                    try {
                        val duration = durationEditText.text.toString().toDouble()
                        ManualDataEntry.duration = duration
                        Log.i("Duration", ManualDataEntry.duration.toString())
                    } catch (_: NumberFormatException) {}
                }
                builder.setNegativeButton("CANCEL") {dialog, _ ->
                    dialog.cancel()
                }
                ret = builder.create()
            }
            DISTANCE_DIALOG -> {
                val builder = AlertDialog.Builder(requireActivity())
                val view: View = requireActivity().layoutInflater.inflate(R.layout.fragment_distance_dialog,
                    null)
                builder.setView(view)
                builder.setTitle("Distance")
                builder.setPositiveButton("OK") {_, _ ->
                    val distanceEditText: EditText = view.findViewById(R.id.distance_editTextNumberDecimal2)
                    try {
                        val distance = distanceEditText.text.toString().toDouble()
                        ManualDataEntry.distance = distance
                        Log.i("Distance", ManualDataEntry.distance.toString())
                    } catch (_: NumberFormatException) {}
                }
                builder.setNegativeButton("CANCEL") {dialog, _ ->
                    dialog.cancel()
                }
                ret = builder.create()
            }
            CALORIES_DIALOG -> {
                val builder = AlertDialog.Builder(requireActivity())
                val view: View = requireActivity().layoutInflater.inflate(R.layout.fragment_calories_dialog,
                    null)
                builder.setView(view)
                builder.setTitle("Calories")
                builder.setPositiveButton("OK") {_, _ ->
                    val caloriesEditText: EditText = view.findViewById(R.id.calories_editTextNumberDecimal2)
                    try {
                        val calories = caloriesEditText.text.toString().toDouble()
                        ManualDataEntry.calories = calories
                        Log.i("Calories", ManualDataEntry.calories.toString())
                    } catch (_: NumberFormatException) {}
                }
                builder.setNegativeButton("CANCEL") {dialog, _ ->
                    dialog.cancel()
                }
                ret = builder.create()
            }
            HEART_RATE_DIALOG -> {
                val builder = AlertDialog.Builder(requireActivity())
                val view: View = requireActivity().layoutInflater.inflate(R.layout.fragment_heart_rate_dialog,
                    null)
                builder.setView(view)
                builder.setTitle("Heart Rate")
                builder.setPositiveButton("OK") {_, _ ->
                    val heartRateEditText: EditText = view.findViewById(R.id.heart_rate_editTextNumberDecimal2)
                    try {
                        val heartRate = heartRateEditText.text.toString().toDouble()
                        ManualDataEntry.heartRate = heartRate
                        Log.i("Heart Rate", ManualDataEntry.heartRate.toString())
                    } catch (_: NumberFormatException) {}
                }
                builder.setNegativeButton("CANCEL") {dialog, _ ->
                    dialog.cancel()
                }
                ret = builder.create()
            }
            COMMENT_DIALOG -> {
                val builder = AlertDialog.Builder(requireActivity())
                val view: View = requireActivity().layoutInflater.inflate(R.layout.fragment_comment_dialog,
                    null)
                builder.setView(view)
                builder.setTitle("Comment")
                builder.setPositiveButton("OK") {_, _ ->
                    val commentEditText: EditText = view.findViewById(R.id.comment_editTextText)
                    val comment = commentEditText.text.toString()
                    ManualDataEntry.comment = comment
                    Log.i("Comment", ManualDataEntry.comment)
                }
                builder.setNegativeButton("CANCEL") {dialog, _ ->
                    dialog.cancel()
                }
                ret = builder.create()
            }
            UNIT_PREFERENCE_DIALOG -> {
                val builder = AlertDialog.Builder(requireActivity())
                val view: View = requireActivity().layoutInflater.inflate(R.layout.fragment_unit_preference_dialog,
                    null)
                builder.setView(view)
                builder.setTitle("Unit Preference")
                builder.setNegativeButton("CANCEL", this)
                ret = builder.create()
            }
            COMMENTS_SETTINGS_DIALOG -> {
                val builder = AlertDialog.Builder(requireActivity())
                val view: View = requireActivity().layoutInflater.inflate(R.layout.fragment_comments_settings_dialog,
                    null)
                builder.setView(view)
                builder.setTitle("Comments")
                builder.setPositiveButton("OK", this)
                builder.setNegativeButton("CANCEL", this)
                ret = builder.create()
            }
            PICK_PICTURE_DIALOG -> {
                val builder = AlertDialog.Builder(requireActivity())
                val view: View = requireActivity().layoutInflater.inflate(R.layout.fragment_pick_profile_picture_dialog,
                    null)
                builder.setView(view)
                builder.setTitle("Pick Profile Picture")
                ret = builder.create()
                openCameraButton = view.findViewById(R.id.open_camera_button)
            }
        }
        return ret
    }

    override fun onClick(dialog: DialogInterface, item: Int) {
        if (item == DialogInterface.BUTTON_POSITIVE) {
            Toast.makeText(activity, "ok clicked", Toast.LENGTH_LONG).show()
        } else if (item == DialogInterface.BUTTON_NEGATIVE) {
            Toast.makeText(activity, "cancel clicked", Toast.LENGTH_LONG).show()
        }
    }
}
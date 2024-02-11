package com.example.vishavjit_harika

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HistoryListViewAdapter(private val context: Context, private var entryList: List<ExerciseEntry>) : BaseAdapter() {
    override fun getCount(): Int = entryList.size

    override fun getItem(position: Int): Any = entryList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val container: ViewsContainer
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.exercise_entry_list_display, parent, false)
            container = ViewsContainer(view)
            view.tag = container
        } else {
            view = convertView
            container = convertView.tag as ViewsContainer
        }

        val entry = getItem(position) as ExerciseEntry
        container.entryTypeTextView.text = displayInputType(entry.inputType)
        container.exerciseTextView.text = displayActivityType(entry.activityType)
        container.dateTimeTextView.text = formatDate(entry.dateTime)
        container.distanceTextView.text = String.format("%.2f", entry.distance)
        container.durationTextView.text = String.format("%.2f", entry.duration)

        return view
    }

    fun replace(newEntryList: List<ExerciseEntry>) {
        entryList = newEntryList
        notifyDataSetChanged()
    }

    private class ViewsContainer(view: View) {
        val entryTypeTextView: TextView = view.findViewById(R.id.entry_type_textView)
        val exerciseTextView: TextView = view.findViewById(R.id.exercise_textView)
        val dateTimeTextView: TextView = view.findViewById(R.id.dateTime_textView)
        val distanceTextView: TextView = view.findViewById(R.id.distance_textView)
        val durationTextView: TextView = view.findViewById(R.id.duration_textView)
    }

    fun formatDate(calendar: Calendar): String {
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
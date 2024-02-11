package com.example.vishavjit_harika

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class FragmentHistory : Fragment() {
    private lateinit var adapter: HistoryListViewAdapter
    private lateinit var viewModel: ExerciseEntryViewModel
    private lateinit var listView: ListView

    private lateinit var database: ExerciseEntryDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_history, container, false)

        database = ExerciseEntryDatabase.getInstance(requireActivity())
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)

        listView = view.findViewById(R.id.exercise_entry_list)
        viewModel = ViewModelProvider(requireActivity(), ExerciseEntryViewModelFactory(repository))[ExerciseEntryViewModel::class.java]
        adapter = HistoryListViewAdapter(requireContext(), listOf())

        viewModel.allEntries.observe(viewLifecycleOwner, Observer { entries ->
            adapter.replace(entries)
            adapter.notifyDataSetChanged()
        })

        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedEntry = adapter.getItem(position) as ExerciseEntry
            GlobalKey.selectedEntryKey = selectedEntry.id
            Log.i("key", GlobalKey.selectedEntryKey.toString())
            if (selectedEntry.inputType == 1) {
                val intent = Intent(requireContext(), DisplayEntryActivity::class.java)
                intent.putExtra("entry_id", selectedEntry.id)
                startActivity(intent)
            } else {
                val intent = Intent(requireContext(), DisplayEntryMapsActivity::class.java)
                intent.putExtra("entry_id", selectedEntry.id)
                startActivity(intent)
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        viewModel.allEntries.observe(viewLifecycleOwner, Observer { entries ->
            adapter.replace(entries)
            adapter.notifyDataSetChanged()
        })
    }
}
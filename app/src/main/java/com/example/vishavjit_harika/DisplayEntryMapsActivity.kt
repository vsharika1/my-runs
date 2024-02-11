package com.example.vishavjit_harika

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.vishavjit_harika.databinding.ActivityDisplayEntryMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.launch

class DisplayEntryMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityDisplayEntryMapsBinding

    private lateinit var viewModel: ExerciseEntryViewModel
    private lateinit var database: ExerciseEntryDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository
    private var entryKey: Long = -1
    private lateinit var entry: ExerciseEntry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDisplayEntryMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (this::entry.isInitialized && entry.locationList.isNotEmpty()) {
            val polylineOptions = PolylineOptions().apply {
                width(5f)
                color(Color.RED)
                addAll(entry.locationList)
            }
            mMap.addPolyline(polylineOptions)

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(entry.locationList.first(), 15f))

            mMap.addMarker(MarkerOptions().position(entry.locationList.first()).title("Start"))
            mMap.addMarker(MarkerOptions().position(entry.locationList.last()).title("End"))
        } else {
            val sydney = LatLng(-34.0, 151.0)
            mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun populateEntryData(entry: ExerciseEntry) {
        val activityTypeText = binding.typeTextView
        val avgSpeedText = binding.avgSpeedTextView
        val climbText = binding.climbTextView
        val caloriesText = binding.caloriesTextView
        val distanceText = binding.distanceTextView

        val activityType: String = displayActivityType(entry.activityType)

        activityTypeText.text = "Type: $activityType"
        avgSpeedText.text = "Avg Speed: ${String.format("%.2f", entry.avgSpeed)} m/s"
        climbText.text = "Climb: ${String.format("%.2f", entry.climb)} m"
        caloriesText.text = "Calories: ${String.format("%.2f", entry.calorie)}"
        distanceText.text = "Distance: ${String.format("%.2f", entry.distance)} m"

        drawPathOnMap(entry.locationList)
    }

    private fun drawPathOnMap(locationList: ArrayList<LatLng>) {
        if (locationList.isNotEmpty()) {
            val polylineOptions = PolylineOptions().apply {
                width(5f)
                color(Color.RED)
                addAll(locationList)
            }
            mMap.addPolyline(polylineOptions)

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationList.first(), 15f))

            mMap.addMarker(MarkerOptions().position(locationList.first()).title("Start"))
            mMap.addMarker(MarkerOptions().position(locationList.last()).title("End"))
        }
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
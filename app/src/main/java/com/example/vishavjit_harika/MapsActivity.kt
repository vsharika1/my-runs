package com.example.vishavjit_harika

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.vishavjit_harika.databinding.ActivityMapsBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.launch
import java.util.Calendar

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val binding by lazy { ActivityMapsBinding.inflate(layoutInflater) }
    private lateinit var mMap: GoogleMap
    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private lateinit var locationRequest: LocationRequest
    private val locationList = ArrayList<LatLng>()

    private lateinit var database: ExerciseEntryDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository

    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    private lateinit var startTime: Calendar

    private lateinit var typeTextView: TextView
    private lateinit var avgSpeedTextView: TextView
    private lateinit var currentSpeedTextView: TextView
    private lateinit var climbTextView: TextView
    private lateinit var calorieTextView: TextView
    private lateinit var distanceTextView: TextView

    private var lastUpdateTime: Long = 0

    private var totalDistance = 0.0
    private var totalDuration = 0.0

    private val userWeightInKg = 70

    private var lastLocation: Location? = null
    private var entryKey: Long = -1

    private lateinit var activityTypeReceiver: BroadcastReceiver

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        startTime = Calendar.getInstance()

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)

        database = ExerciseEntryDatabase.getInstance(this)
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)

        if (hasLocationPermission()) {
            startTrackingService()
        } else {
            requestLocationPermission()
        }
        createLocationRequest()

        typeTextView = binding.typeTextView
        avgSpeedTextView = binding.avgSpeedTextView
        currentSpeedTextView = binding.currentSpeedTextView
        climbTextView = binding.climbTextView
        calorieTextView = binding.calorieTextView
        distanceTextView = binding.distanceTextView

        if (ManualDataEntry.inputActivityType == 3) {
            activityTypeReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val activityType = intent.getIntExtra("activityType", -1)
                    ManualDataEntry.activityType = activityType
                }
            }
            val filter = IntentFilter("com.example.ACTION_ACTIVITY_TYPE")
            LocalBroadcastManager.getInstance(this).registerReceiver(activityTypeReceiver, filter)
        }

        saveButton = findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            val endTime = Calendar.getInstance()
            val durationInMinutes = (endTime.timeInMillis - startTime.timeInMillis) / 1000 / 60.0
            lifecycleScope.launch {
                val entry = ExerciseEntry(
                    inputType = ManualDataEntry.inputActivityType,
                    activityType = ManualDataEntry.activityType,
                    dateTime = startTime,
                    duration = durationInMinutes,
                    distance = calculateDistance(),
                    avgPace = calculateAveragePace(durationInMinutes, calculateDistance()),
                    avgSpeed = calculateAverageSpeed(durationInMinutes, calculateDistance()),
                    calorie = calculateCalories(calculateDistance()),
                    climb = calculateClimb(),
                    heartRate = 0.0,
                    comment = "",
                    locationList = locationList
                )
                repository.insert(entry)
                finish()
                Log.i("Entry", entry.toString())
            }
        }

        cancelButton = findViewById(R.id.cancelButton)

        cancelButton.setOnClickListener {
            finish()
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
                Log.i("Key", entryKey.toString())
                lifecycleScope.launch {
                    repository.delete(entryKey)
                    finish()
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTrackingService()
        if (ManualDataEntry.inputActivityType == 3) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(activityTypeReceiver)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        updateLocationUI()
        getDeviceLocation()
        startLocationUpdates()
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult ?: return
            for (location in locationResult.locations) {
                val latLng = LatLng(location.latitude, location.longitude)
                locationList.add(latLng)
                drawPolyline()

                if (lastLocation != null) {
                    totalDistance += lastLocation!!.distanceTo(location) // distanceTo gives meters
                }
                lastLocation = location

                totalDuration =
                    (Calendar.getInstance().timeInMillis - startTime.timeInMillis) / 1000.0

                updateRealTimeData(location)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            }
        }
    }

    private fun updateRealTimeData(location: Location) {
        val currentSpeed = calculateCurrentSpeed(location)
        val avgSpeed = calculateCurrentAverageSpeed()
        val distance = calculateDistance()
        val calories = calculateCurrentCalories()
        val climb = calculateClimb()

        val activityType: String = displayActivityType(ManualDataEntry.activityType)

        typeTextView.text = "Type: ${activityType}"
        currentSpeedTextView.text = "Current Speed: ${format(currentSpeed)} m/s"
        avgSpeedTextView.text = "Avg Speed: ${format(avgSpeed)} m/s"
        distanceTextView.text = "Distance: ${format(distance)} m"
        calorieTextView.text = "Calories: ${format(calories)}"
        climbTextView.text = "Climb: ${format(climb)} m"
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


    private fun drawPolyline() {
        val polylineOptions = PolylineOptions()
            .addAll(locationList)
            .width(5f)
            .color(Color.RED)

        mMap.addPolyline(polylineOptions)
    }

    private fun updateLocationUI() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    val lastKnownLocation = task.result

                    val currentLocation =
                        LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))

                    mMap.addMarker(
                        MarkerOptions()
                            .position(currentLocation)
                            .title("Current Location")
                            .snippet("This is your last known location")
                    )

                } else {
                    showLocationNotFoundDialog()
                }
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    private fun showLocationNotFoundDialog() {
        AlertDialog.Builder(this)
            .setTitle("Location Not Found")
            .setMessage("Unable to find the current location. Please ensure your GPS is turned on and try again.")
            .setPositiveButton("OK", null)
            .show()
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if ((grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)
            ) {
                startTrackingService()
                updateLocationUI()
                getDeviceLocation()
            } else {
                showPermissionDeniedDialog()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Location permission is necessary to access your current location.")
            .setPositiveButton("Retry") { _, _ -> requestLocationPermission() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun calculateDistance(): Double {
        var totalDistance = 0.0
        for (i in 0 until locationList.size - 1) {
            val startPoint = locationList[i]
            val endPoint = locationList[i + 1]
            totalDistance += distanceBetween(startPoint, endPoint)
        }
        return totalDistance
    }

    private fun distanceBetween(startPoint: LatLng, endPoint: LatLng): Double {
        val results = FloatArray(1)
        Location.distanceBetween(
            startPoint.latitude,
            startPoint.longitude,
            endPoint.latitude,
            endPoint.longitude,
            results
        )
        return results[0].toDouble()
    }

    private fun calculateAveragePace(durationInMinutes: Double, distanceInMeters: Double): Double {
        return if (distanceInMeters > 0) durationInMinutes / (distanceInMeters / 1000) else 0.0
    }

    private fun calculateAverageSpeed(durationInMinutes: Double, distanceInMeters: Double): Double {
        return if (durationInMinutes > 0) (distanceInMeters / 1000) / (durationInMinutes / 60) else 0.0
    }

    private fun calculateCalories(distance: Double): Double {
        val distanceInKm = distance / 1000
        return userWeightInKg * distanceInKm
    }

    private fun calculateClimb(): Double {
        return 0.0
    }

    private fun startTrackingService() {
        val serviceIntent = Intent(this, TrackingService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopTrackingService() {
        val serviceIntent = Intent(this, TrackingService::class.java)
        stopService(serviceIntent)
    }

    private fun calculateCurrentSpeed(location: Location): Double {
        if (locationList.size < 2) {
            lastUpdateTime = System.currentTimeMillis()
            return 0.0
        }

        val lastLocation = locationList[locationList.size - 2]
        val newLocation = LatLng(location.latitude, location.longitude)

        val distance = distanceBetween(lastLocation, newLocation) // in meters
        val currentTime = System.currentTimeMillis()
        val timeElapsed = (currentTime - lastUpdateTime) / 1000.0 // in seconds

        lastUpdateTime = currentTime
        return if (timeElapsed > 0) distance / timeElapsed else 0.0 // speed in m/s
    }

    private fun calculateCurrentAverageSpeed(): Double {
        return if (totalDuration > 0) totalDistance / totalDuration else 0.0
    }

    private fun calculateCurrentCalories(): Double {
        val distanceInKm = totalDistance / 1000
        return userWeightInKg * distanceInKm
    }

    private fun format(value: Double): String {
        return String.format("%.2f", value)
    }
}
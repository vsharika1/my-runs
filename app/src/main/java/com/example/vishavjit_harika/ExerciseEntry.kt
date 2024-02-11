package com.example.vishavjit_harika

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "exercise_entry_table")
data class ExerciseEntry(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "input_type_column")
    var inputType: Int,                     // Manual, GPS or automatic

    @ColumnInfo(name = "activity_type_column")
    var activityType: Int,                  // Running, cycling etc.

    @ColumnInfo(name = "date_time_column")
    var dateTime: java.util.Calendar,                 // When does this entry happen

    @ColumnInfo(name = "duration_column")
    var duration: Double,                   // Exercise duration in seconds

    @ColumnInfo(name = "distance_column")
    var distance: Double,                   // Distance traveled. Either in meters or feet.

    @ColumnInfo(name = "avg_pace_column")
    var avgPace: Double,                    // Average pace

    @ColumnInfo(name = "avg_speed_column")
    var avgSpeed: Double,                   // Average speed

    @ColumnInfo(name = "calorie_column")
    var calorie: Double,                    // Calories burnt

    @ColumnInfo(name = "climb_column")
    var climb: Double,                      // Climb. Either in meters or feet.

    @ColumnInfo(name = "heart_rate_column")
    var heartRate: Double,                  // Heart rate

    @ColumnInfo(name = "comment_column")
    var comment: String,                    // Comments

    @ColumnInfo(name = "location_column")
    var locationList: ArrayList <LatLng>    // Location list
)

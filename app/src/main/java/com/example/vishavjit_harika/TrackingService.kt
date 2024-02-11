package com.example.vishavjit_harika

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.AsyncTask
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.concurrent.ArrayBlockingQueue

class TrackingService : Service(), SensorEventListener {
    private val NOTIFY_ID = 1
    private val CHANNEL_ID = "tracking_channel"
    private lateinit var notificationManager: NotificationManager
    private lateinit var stopReceiver: BroadcastReceiver

    private val mFeatLen = Globals.ACCELEROMETER_BLOCK_CAPACITY + 2
    private lateinit var mLabel: String
    private lateinit var mAccBuffer: ArrayBlockingQueue<Double>

    companion object {
        const val STOP_SERVICE_ACTION = "stop service action"
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        startForeground(NOTIFY_ID, getNotification())

        stopReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                stopSelf()
                notificationManager.cancel(NOTIFY_ID)
            }
        }

        val filter = IntentFilter().apply {
            addAction(STOP_SERVICE_ACTION)
        }
        registerReceiver(stopReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(stopReceiver)
        notificationManager.cancel(NOTIFY_ID)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getNotification(): Notification {
        val stopIntent = Intent(this, TrackingService::class.java).apply {
            action = STOP_SERVICE_ACTION
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tracking Service")
            .setContentText("The app is tracking your location.")
            .setSmallIcon(R.drawable.location_on_24px) // Make sure this resource exists
            .addAction(
                R.drawable.cancel_24px,
                "Stop",
                stopPendingIntent
            )
            .build()
    }

    inner class OnSensorChangedTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg arg0: Void?): Void? {
            var blockSize = 0
            val fft = FFT(Globals.ACCELEROMETER_BLOCK_CAPACITY)
            val accBlock = DoubleArray(Globals.ACCELEROMETER_BLOCK_CAPACITY)
            val im = DoubleArray(Globals.ACCELEROMETER_BLOCK_CAPACITY)
            var max = Double.MIN_VALUE
            var featuresArrayList = ArrayList<Double>(Globals.ACCELEROMETER_BLOCK_CAPACITY)

            while (true) {
                try {
                    if (isCancelled() == true) {
                        return null
                    }

                    accBlock[blockSize++] = mAccBuffer.take().toDouble()
                    if (blockSize == Globals.ACCELEROMETER_BLOCK_CAPACITY) {
                        blockSize = 0

                        max = .0
                        for (`val` in accBlock) {
                            if (max < `val`) {
                                max = `val`
                            }
                        }
                        fft.fft(accBlock, im)
                        for (i in accBlock.indices) {
                            val mag = Math.sqrt(
                                accBlock[i] * accBlock[i] + im[i]
                                        * im[i]
                            )
                            im[i] = .0
                            featuresArrayList.add(mag)
                        }

                        val inputType = WekaClassifier.classify(featuresArrayList.toArray()).toInt()

                        when (inputType) {
                            Globals.ACTIVITY_ID_STANDING -> ManualDataEntry.activityType = 3
                            Globals.ACTIVITY_ID_WALKING -> ManualDataEntry.activityType = 2
                            Globals.ACTIVITY_ID_RUNNING -> ManualDataEntry.activityType = 1
                            Globals.ACTIVITY_ID_OTHER -> ManualDataEntry.activityType = 14
                        }
                        val intent = Intent("com.example.ACTION_ACTIVITY_TYPE")
                        intent.putExtra("activityType", ManualDataEntry.activityType)
                        LocalBroadcastManager.getInstance(this@TrackingService).sendBroadcast(intent)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val m = Math.sqrt(
                (event.values[0] * event.values[0] + event.values[1] * event.values[1] + (event.values[2]
                        * event.values[2])).toDouble()
            )
            try {
                mAccBuffer.add(m)
            } catch (e: IllegalStateException) {

                val newBuf = ArrayBlockingQueue<Double>(mAccBuffer.size * 2)
                mAccBuffer.drainTo(newBuf)
                mAccBuffer = newBuf
                mAccBuffer.add(m)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

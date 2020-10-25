package com.sizeofanton.mappeddatabaseandroid.data.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sizeofanton.mappeddatabaseandroid.data.local.EncryptedPreferences
import timber.log.Timber
import kotlin.concurrent.fixedRateTimer

class CurrentLocationService : Service() {

    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var broadcastManager: LocalBroadcastManager
    private var saveCounter = 0

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        locationClient = LocationServices.getFusedLocationProviderClient(this)
        broadcastManager = LocalBroadcastManager.getInstance(this)
        fixedRateTimer("getCurrentLocation", false, 0, 1000) {
            getCurrentLocation()
        }
        return START_NOT_STICKY

    }


    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        locationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                Timber.d("CURRENT LOCATION: lat ${location.latitude} long ${location.longitude}")
                Intent("ACTION_CURRENT_LOCATION").apply {
                    putExtra("LATITUDE", location.latitude)
                    putExtra("LONGITUDE", location.longitude)
                }.also { broadcastManager.sendBroadcast(it) }
                saveCounter++
                if (saveCounter % 100 == 0) EncryptedPreferences.getInstance(
                    this
                ).apply {
                    putFloat("LATITUDE", location.latitude.toFloat())
                    putFloat("LONGITUDE", location.longitude.toFloat())
                }
            }
        }
    }
}

package com.arorashivoy.wifistrength

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Screen2Activity : AppCompatActivity() {
    private lateinit var wifiManager: WifiManager
    private lateinit var logs: TextView

    private val LOCATION_A = "Location A"
    private val LOCATION_B = "Location B"
    private val LOCATION_C = "Location C"

    private var currentLocation = ""
    private var rssiMatrix = mutableMapOf<String, MutableList<Int>>()

    private val scanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@Screen2Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
                return
            }
            val results = wifiManager.scanResults
            collectRSSI(results)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen2)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        logs = findViewById(R.id.logs)

        findViewById<Button>(R.id.locationA).setOnClickListener { startLogging(LOCATION_A) }
        findViewById<Button>(R.id.locationB).setOnClickListener { startLogging(LOCATION_B) }
        findViewById<Button>(R.id.locationC).setOnClickListener { startLogging(LOCATION_C) }
        findViewById<Button>(R.id.compareLocations).setOnClickListener { compareLocations() }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        registerReceiver(scanReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
    }

    private fun startLogging(location: String) {
        currentLocation = location
        rssiMatrix.putIfAbsent(location, mutableListOf())
        rssiMatrix[location]?.clear()

        scanAndLog()
    }

    private fun scanAndLog() {
        wifiManager.startScan()
    }

    private fun collectRSSI(results: List<ScanResult>) {
        val rssiValues = rssiMatrix[currentLocation] ?: return

        for (result in results) {
            rssiValues.add(result.level)

            if (rssiValues.size >= 100) {
                updateLogs()
                return
            }
        }

        // If not enough yet, scan again
        if (rssiValues.size < 100) {
            wifiManager.startScan()
        }
    }

    private fun updateLogs() {
        val builder = StringBuilder()
        builder.append("RSSI values for $currentLocation (Total: ${rssiMatrix[currentLocation]?.size}):\n")
        rssiMatrix[currentLocation]?.forEachIndexed { index, value ->
            builder.append("[$index]: $value dBm\n")
        }
        logs.text = builder.toString()
    }

    private fun compareLocations() {
        var bestLocation = ""
        var bestAverage = Float.NEGATIVE_INFINITY

        rssiMatrix.forEach { (location, list) ->
            if (list.isNotEmpty()) {
                val average = list.average().toFloat()
                if (average > bestAverage) {
                    bestAverage = average
                    bestLocation = location
                }
            }
        }

        Toast.makeText(this, "Best WiFi Signal: $bestLocation (Avg: $bestAverage dBm)", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(scanReceiver)
    }
}
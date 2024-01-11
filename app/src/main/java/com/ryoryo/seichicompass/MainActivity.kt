package com.ryoryo.seichicompass

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.ryoryo.seichicompass.databinding.ActivityMainBinding
import com.ryoryo.seichicompass.lib.Constants
import com.ryoryo.seichicompass.lib.Permissions

/**
 * References for Location & Permission Grants
 * - https://drawtrail.net/osmdroid-gps/
 *
 * References for Navigation
 * - https://qiita.com/tktktks10/items/7df56b4795d907a4cd31
 * - https://swallow-incubate.com/archives/blog/20200902/
 * - https://developer.android.com/guide/navigation?hl=ja#principles
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var _fusedLocationClient: FusedLocationProviderClient? = null
    val fusedLocationClient get() = _fusedLocationClient
    val locationRequest get() = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) {
        if (it.containsValue(false)) {
            Toast.makeText(this, R.string.unable_to_activate_app, Toast.LENGTH_LONG).show()
        } else {
            setupLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (locationEnabled()) {
            setupLocation()
        } else if (shouldShowRequestPermissionRationale(Permissions.COARSE_LOCATION)
            || shouldShowRequestPermissionRationale(Permissions.FINE_LOCATION)) {
            val alert = AlertDialog.Builder(this)
            alert.setMessage(R.string.rationale_location)
                 .setPositiveButton("OK") { _, _ ->
                     requestPermission.launch(Permissions.LOCATIONS)
                 }
                .setNegativeButton("NO") { _, _ ->
                    Toast.makeText(this, R.string.unable_to_activate_app, Toast.LENGTH_LONG).show()
                }
            alert.create()
            alert.show()
        } else {
            requestPermission.launch(Permissions.LOCATIONS)
        }
    }

    fun locationEnabled(): Boolean {
        val coarse = checkSelfPermission(Permissions.COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val fine = checkSelfPermission(Permissions.FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return coarse && fine
    }

    private fun setupLocation() {
        _fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }
}
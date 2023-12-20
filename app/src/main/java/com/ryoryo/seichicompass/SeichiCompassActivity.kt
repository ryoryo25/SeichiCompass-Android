package com.ryoryo.seichicompass

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.ryoryo.seichicompass.databinding.ActivitySeichiCompassBinding
import com.ryoryo.seichicompass.lib.Constants
import com.ryoryo.seichicompass.model.Coordinate
import com.ryoryo.seichicompass.model.SeichiInfo
import com.ryoryo.seichicompass.model.SeichiInfoHelper

class SeichiCompassActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SeichiCompass"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySeichiCompassBinding

    private var _seichiInfo: SeichiInfo? = null
    val seichiInfo get() = _seichiInfo!!

    private val permissionCoarse = Manifest.permission.ACCESS_COARSE_LOCATION
    private val permissionFine = Manifest.permission.ACCESS_FINE_LOCATION
    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) {
        if (it.containsValue(false)) {
            Snackbar.make(binding.root, "位置情報", Snackbar.LENGTH_LONG).show()
        } else {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivitySeichiCompassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        _seichiInfo = SeichiInfoHelper.bundleToSeichiInfo(intent.getBundleExtra(Constants.EXTRA_NAME_SEICHI_INFO))

        if (!locationEnabled()) {
            requestPermission.launch(arrayOf(permissionCoarse, permissionFine))
        }
    }

    fun locationEnabled(): Boolean {
        val coarse = checkSelfPermission(permissionCoarse) == PackageManager.PERMISSION_GRANTED
        val fine = checkSelfPermission(permissionFine) == PackageManager.PERMISSION_GRANTED
        return coarse && fine
    }

    override fun onBackPressed() {
        finish()
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }
}
package com.ryoryo.seichicompass

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.OnSuccessListener
import com.ryoryo.seichicompass.databinding.FragmentCompassBinding
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class CompassFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentCompassBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sensorManager: SensorManager

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var locationEnabled = false
    private var bearingToSeichi = 0.0F
    private lateinit var destinationLocation: Location

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCompassBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        binding.seichiDistance.text = getString(R.string.seichi_distance, "-1.0")

        binding.forceCompleteButton.setOnClickListener {
            findNavController().navigate(R.id.action_Compass_to_SeichiComplete)
        }

        if ((requireActivity() as SeichiCompassActivity).locationEnabled()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            locationEnabled = true

            locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationUpdate(locationResult.lastLocation)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (locationEnabled) {
            startLocationUpdates()
            destinationLocation = Location(null)
            val parent = activity as SeichiCompassActivity
            destinationLocation.latitude = parent.seichiInfo.coordinate.latitude
            destinationLocation.longitude = parent.seichiInfo.coordinate.longitude
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            sensorManager.registerListener(
                this,
                magneticField,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(this)
        stopLocationUpdates()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
            } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
            }
        }

        updateOrientationAngles()
        val direction = orientationAngles[0] * 180 / Math.PI
        val grad = orientationAngles[1] * 180 / Math.PI
        val rotationX = -grad.toFloat()
        val scaleY = (0.3 * (1 - abs(orientationAngles[1])) + 0.5).toFloat() // scale 0.5-0.8 by angle

        binding.seichiCompass.rotation = (bearingToSeichi - direction).toFloat()
        binding.seichiCompass.rotationX = rotationX
        binding.seichiCompass.scaleY = scaleY
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )

        SensorManager.getOrientation(rotationMatrix, orientationAngles)
    }

    private fun locationUpdate(location: Location?) {
        location?.let {
            Log.i(SeichiCompassActivity.TAG, (activity as SeichiCompassActivity).seichiInfo.toString())
            Log.i(SeichiCompassActivity.TAG, "Currrent: $it")
            Log.i(SeichiCompassActivity.TAG, "Destination: $destinationLocation")

            val bearing = it.bearingTo(destinationLocation)
            bearingToSeichi = bearing

            val distance = it.distanceTo(destinationLocation)
            binding.seichiDistance.text = getString(R.string.seichi_distance, distance.roundToInt().toString())

            if (distance <= 13) {
                findNavController().navigate(R.id.action_Compass_to_SeichiComplete)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
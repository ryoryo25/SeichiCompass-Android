package com.ryoryo.seichicompass.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.ryoryo.seichicompass.MainActivity
import com.ryoryo.seichicompass.R
import com.ryoryo.seichicompass.databinding.FragmentCompassBinding
import com.ryoryo.seichicompass.lib.Constants
import com.ryoryo.seichicompass.model.SeichiInfoHelper
import java.util.Collections
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class CompassFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentCompassBinding? = null
    private val binding get() = _binding!!

    private lateinit var sensorManager: SensorManager

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private val historySize = 10
    private var nextPos = 0
    private val anglesHistory = arrayOf(
        FloatArray(historySize), FloatArray(historySize), FloatArray(historySize))

    private var bearingToSeichi = 0.0F
    private lateinit var destinationLocation: Location
    private val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationUpdate(locationResult.lastLocation)
                }
            }

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
            findNavController().navigate(R.id.action_Compass_to_SeichiComplete, arguments)
        }
    }

    override fun onResume() {
        super.onResume()

        val seichi = SeichiInfoHelper.bundleToSeichiInfo(arguments)

        if (activity is MainActivity && (activity as MainActivity).locationEnabled()) {
            startLocationUpdates()
            destinationLocation = Location(null)
            destinationLocation.latitude = seichi.coordinate.latitude
            destinationLocation.longitude = seichi.coordinate.longitude
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
        // clear history
        nextPos = 0
        anglesHistory.forEach {
            it.fill(0.0F)
        }
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
        val averageAngles = averageOrientationAngles()
        val direction = averageAngles[0] * 180 / Math.PI
        val grad = averageAngles[1] * 180 / Math.PI
        val rotationX = -grad.toFloat()
        val scaleY = (0.3 * (1 - abs(averageAngles[1])) + 0.5).toFloat() // scale 0.5-0.8 by angle

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

    private fun averageOrientationAngles(): FloatArray {
        val result = FloatArray(orientationAngles.size)
        anglesHistory.forEachIndexed { i, a ->
            a[nextPos] = orientationAngles[i] // update
            result[i] = a.average().toFloat() // average
        }
        nextPos++
        if (nextPos >= historySize) {
            nextPos = 0
        }
        return result
    }

    private fun locationUpdate(location: Location?) {
        location?.let {
//            Log.i(Constants.APP_TAG, (activity as SeichiCompassActivity).seichiInfo.toString())
            Log.i(Constants.APP_TAG, "Currrent: $it")
            Log.i(Constants.APP_TAG, "Destination: $destinationLocation")

            val bearing = it.bearingTo(destinationLocation)
            bearingToSeichi = bearing

            val distance = it.distanceTo(destinationLocation)
            binding.seichiDistance.text = getString(R.string.seichi_distance, distance.roundToInt().toString())

            if (distance <= 15) {
                findNavController().navigate(R.id.action_Compass_to_SeichiComplete, arguments)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (activity is MainActivity) {
            val parent = activity as MainActivity
            parent.fusedLocationClient?.requestLocationUpdates(
                parent.locationRequest,
                locationCallback,
                Looper.getMainLooper())
        }
    }

    private fun stopLocationUpdates() {
        if (activity is MainActivity) {
            (activity as MainActivity).fusedLocationClient?.removeLocationUpdates(locationCallback)
        }
    }
}
package com.ryoryo.seichicompass.fragment

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.ryoryo.seichicompass.MainActivity
import com.ryoryo.seichicompass.databinding.FragmentSeichiCompleteBinding
import com.ryoryo.seichicompass.model.SeichiInfoHelper

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SeichiCompleteFragment : Fragment() {

    private var _binding: FragmentSeichiCompleteBinding? = null
    private val binding get() = _binding!!

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationUpdate(locationResult.lastLocation)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSeichiCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val seichi = SeichiInfoHelper.bundleToSeichiInfo(arguments)
        binding.titleText.text = seichi.title
        binding.descriptionText.text = seichi.description
        binding.infoSourceText.text = seichi.infoSource
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity && (activity as MainActivity).locationEnabled()) {
            startLocationUpdates()
        }
        val seichi = SeichiInfoHelper.bundleToSeichiInfo(arguments)
        binding.titleText.text = seichi.title
        binding.descriptionText.text = seichi.description
        binding.infoSourceText.text = seichi.infoSource
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun locationUpdate(location: Location?) {
        location?.let {
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
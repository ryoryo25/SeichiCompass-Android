package com.ryoryo.seichicompass.fragment

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.ryoryo.seichicompass.MainActivity
import com.ryoryo.seichicompass.R
import com.ryoryo.seichicompass.databinding.FragmentSeichiSelectBinding
import com.ryoryo.seichicompass.model.SeichiInfo
import com.ryoryo.seichicompass.model.SeichiInfoAdapter
import com.ryoryo.seichicompass.model.SeichiInfoHelper
import com.ryoryo.seichicompass.network.SeichiInfoApi
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 *
 * References for RecyclerView
 * - https://qiita.com/tkmd35/items/0602ef84aa098e1e4f63
 * - https://qiita.com/tkmd35/items/b49070560abbfdfff3d1
 * - https://developer.android.com/guide/topics/ui/layout/recyclerview?hl=ja
 */
class SeichiSelectFragment : Fragment() {

    private var _binding: FragmentSeichiSelectBinding? = null
    private val binding get() = _binding!!

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationUpdate(locationResult.lastLocation)
        }
    }

    private val emptyList = mutableListOf<SeichiInfo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSeichiSelectBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.seichiList.layoutManager = LinearLayoutManager(activity)
        binding.seichiList.adapter = SeichiInfoAdapter(emptyList).also { a ->
            a.setListItemClickListener {
                findNavController().navigate(
                    R.id.action_SeichiSelect_to_Compass,
                    SeichiInfoHelper.seichiInfoToBundle(it))
            }
        }

        binding.seichiAddButton.setOnClickListener {
            findNavController().navigate(R.id.action_SeichiSelect_to_Add)
        }
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity && (activity as MainActivity).locationEnabled()) {
            startLocationUpdates()
        }

        lifecycleScope.launch {
            binding.selectLoading.loadingOverlay.visibility = View.VISIBLE
            val seichiList = try {
                SeichiInfoApi.retrofitService.getSeichis()
            } catch (e: Exception) {
                emptyList
            } as MutableList // catch (e: IOException) {
//                emptyList
//            } catch (e: HttpException) {
//                emptyList
//            }
            binding.seichiList.adapter = SeichiInfoAdapter(seichiList).also { adapter ->
                adapter.setListItemClickListener {
                    findNavController().navigate(
                        R.id.action_SeichiSelect_to_Compass,
                        SeichiInfoHelper.seichiInfoToBundle(it))
                }
            }
            binding.selectLoading.loadingOverlay.visibility = View.GONE
        }
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
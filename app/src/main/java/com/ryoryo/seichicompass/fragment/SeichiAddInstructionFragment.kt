package com.ryoryo.seichicompass.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ryoryo.seichicompass.R
import com.ryoryo.seichicompass.databinding.FragmentSeichiAddInstructionBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SeichiAddInstructionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SeichiAddInstructionFragment : Fragment() {

    private var _binding: FragmentSeichiAddInstructionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSeichiAddInstructionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.googleMapButton.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).also {
                it.setPackage("com.google.android.apps.maps")
            }
            mapIntent.resolveActivity(requireActivity().packageManager)?.let {
                startActivity(mapIntent)
                findNavController().popBackStack() // back to select
//                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
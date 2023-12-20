package com.ryoryo.seichicompass

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ryoryo.seichicompass.databinding.FragmentSeichiCompleteBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SeichiCompleteFragment : Fragment() {

    private var _binding: FragmentSeichiCompleteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSeichiCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = activity as SeichiCompassActivity
        binding.titleText.text = parent.seichiInfo.title
        binding.descriptionText.text = parent.seichiInfo.description
        binding.infoSourceText.text = parent.seichiInfo.infoSource
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
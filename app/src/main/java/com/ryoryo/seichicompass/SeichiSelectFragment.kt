package com.ryoryo.seichicompass

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ryoryo.seichicompass.databinding.FragmentSeichiSelectBinding
import com.ryoryo.seichicompass.lib.Constants
import com.ryoryo.seichicompass.model.Coordinate
import com.ryoryo.seichicompass.model.SeichiInfo
import com.ryoryo.seichicompass.model.SeichiInfoHelper

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class SeichiSelectFragment : Fragment() {

    private var _binding: FragmentSeichiSelectBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val seichiList = arrayListOf<SeichiInfo>(
        SeichiInfo(
            1,
            "西11号館",
            "ピクトラボがあるところ",
            "https://www.pict-lab.uec.ac.jp/",
            Coordinate(35.658150273845465, 139.54084536886313)
        ),
        SeichiInfo(
            2,
            "ska cafe",
            "河田陽菜のひなさんぽ2で訪れていた店",
            "https://youtu.be/NjDByOrfIqw?t=550",
            Coordinate(35.661176258997365, 139.45755283547493)
        ),
        SeichiInfo(
            2,
            "多摩川サイクリングロード",
            "『3年目のデビュー』のラストに登場する河川敷",
            "https://twitter.com/chofukankou/status/1285437496608747521",
            Coordinate(35.63860994170252, 139.55117488267354)
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSeichiSelectBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.testButton.setOnClickListener {
            val startCompass = Intent(activity, SeichiCompassActivity::class.java)
            val info = seichiList[2]
            startCompass.putExtra(Constants.EXTRA_NAME_SEICHI_INFO, SeichiInfoHelper.seichiInfoToBundle(info))
            startActivity(startCompass)
        }

//        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.rwawrzyniak.flighthelper.presentation.details

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rwawrzyniak.flighthelper.R
import kotlinx.android.synthetic.main.fragment_details.button_second
import kotlinx.android.synthetic.main.fragment_details.summaryList

class DetailsFragment : Fragment(R.layout.fragment_details) {
    private val args: DetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_second.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        val flightSummary = with(args.FlightDetails) {
            listOf("Origi: $originName", "Destination: $destinationName", "Infants left: $infantsLeft", "Fare class: $fareClass", "Discount: $discountPercent%")
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(),
            android.R.layout.simple_list_item_1, android.R.id.text1, flightSummary)
        summaryList.adapter = adapter
    }
}
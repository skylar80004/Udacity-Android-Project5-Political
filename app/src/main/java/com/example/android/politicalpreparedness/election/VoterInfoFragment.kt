package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.data.VoterInfoNetworkRepository
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.util.showToast

class VoterInfoFragment : Fragment() {
    private var _binding: FragmentVoterInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: VoterInfoViewModel

    private var electionId = 0
    private var division: Division? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModelFactory = VoterInfoViewModelFactory(
            ElectionDatabase.getInstance(requireContext()).electionDao,
            VoterInfoNetworkRepository(
                CivicsApi.retrofitService
            )
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[VoterInfoViewModel::class.java]

        _binding = FragmentVoterInfoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel




        // TODO: Add ViewModel values and create ViewModel
        // For example, you can observe ViewModel data here and set it to binding


        // TODO: Populate voter info -- hide views without provided data.
        // Implement logic to check if data is available and hide/show views accordingly

        // TODO: Handle loading of URLs
        // Set up click listeners for any URL buttons here

        // TODO: Handle save button UI state
        // Set the initial state of the save button based on ViewModel state

        // TODO: cont'd Handle save button clicks
//        binding.buttonSave.setOnClickListener {
//            // Logic to save the information
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the arguments passed from the navigation
        electionId = arguments?.getInt("arg_election_id") ?: -1
        division = arguments?.getParcelable<Division>("arg_division")

        viewModel.fetchVoterInfo(electionId = electionId)

        viewModel.showLoading.observe(viewLifecycleOwner) { showLoading ->
            if (showLoading) {
                binding.voterInfoLoading.visibility = View.VISIBLE
            } else {
                binding.voterInfoLoading.visibility = View.GONE
            }
        }

        viewModel.showErrorMessage.observe(viewLifecycleOwner) { message ->
            showToast(message = message)
        }
    }

    // TODO: Create method to load URL intents
    private fun loadUrl(url: String) {
        // Logic to load the URL, e.g., using an Intent
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding reference to avoid memory leaks
    }
}

package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
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
            VoterInfoNetworkRepository(
                CivicsApi.retrofitService
            ),
            ElectionDatabase.getInstance(requireContext()).electionDao
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[VoterInfoViewModel::class.java]

        _binding = FragmentVoterInfoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel


        // TODO: Add ViewModel values and create ViewModel
        // For example, you can observe ViewModel data here and set it to binding


        // TODO: Populate voter info -- hide views without provided data.
        // Implement logic to check if data is available and hide/show views accordingly

        // TODO: Handle save button UI state
        // Set the initial state of the save button based on ViewModel state

        // TODO: cont'd Handle save button clicks
//        binding.buttonSave.setOnClickListener {
//            // Logic to save the information
//        }

        binding.btnFollowElection.setOnClickListener {
            viewModel.onClickElectionButton()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the arguments passed from the navigation
        electionId = arguments?.getInt("arg_election_id") ?: -1
        division = arguments?.getParcelable<Division>("arg_division")


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

        viewModel.ballotInfoUrl.observe(viewLifecycleOwner) { url ->
            binding.stateBallot.apply {
                setOnClickListener {
                    loadUrl(url = url)
                }
                visibility = View.VISIBLE
            }
            binding.stateHeader.visibility = View.VISIBLE
        }

        viewModel.pollingLocationsURL.observe(viewLifecycleOwner) { url ->
            binding.stateLocations.apply {
                setOnClickListener {
                    loadUrl(url = url)
                }
                visibility = View.VISIBLE
            }
            binding.stateHeader.visibility = View.VISIBLE
        }

        viewModel.electionButtonState.observe(viewLifecycleOwner) { state ->
            binding.btnFollowElection.text = when (state) {
                FollowState.FOLLOWING -> getString(R.string.unfollow_election)
                FollowState.NOT_FOLLOWING -> getString(R.string.follow_election)
                else -> ""
            }
        }

        viewModel.correspondenceAddress.observe(viewLifecycleOwner) { correspondenceAddress ->
            binding.apply {
                addressGroup.visibility = View.VISIBLE
                address.text = correspondenceAddress
            }
        }

        viewModel.electionDay.observe(viewLifecycleOwner) { electionDay ->
            binding.electionDate.apply {
                text = electionDay
                visibility = View.VISIBLE
            }
        }

        viewModel.loadData(electionIdValue = electionId)
    }

    private fun loadUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding reference to avoid memory leaks
    }


}

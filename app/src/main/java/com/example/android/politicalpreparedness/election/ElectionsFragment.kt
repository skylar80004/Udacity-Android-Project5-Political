package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.data.ElectionsNetworkRepository
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.util.showToast

class ElectionsFragment : Fragment() {
    private var _binding: FragmentElectionBinding? = null
    private val binding get() = _binding!!

    private val upcomingElectionsAdapter = ElectionListAdapter(
        ElectionListener(
            clickListener = { electionId ->
                showToast("Upcoming Election clicked! Id: $electionId")
            }
        )
    )

    private val savedElectionsAdapter = ElectionListAdapter(
        ElectionListener(
            clickListener = { electionId ->
                showToast("Saved Election clicked! Id: $electionId")
            }
        )
    )

    private val viewModelFactory = ElectionsViewModelFactory(ElectionsNetworkRepository( CivicsApi.retrofitService))
    private lateinit var viewModel: ElectionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentElectionBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, viewModelFactory)[ElectionsViewModel::class.java]

        // TODO: Add ViewModel values and create ViewModel
        viewModel.upcomingElections.observe(viewLifecycleOwner) { elections ->
            upcomingElectionsAdapter.submitList(elections)
        }

        viewModel.savedElections.observe(viewLifecycleOwner) { savedElections ->
            savedElectionsAdapter.submitList(savedElections)
        }

        viewModel.showLoading.observe(viewLifecycleOwner) { showLoading ->
            if (showLoading) {
                binding.upcomingElectionsLoadingSpinner.visibility = View.VISIBLE
            } else {
                binding.upcomingElectionsLoadingSpinner.visibility = View.GONE
            }
        }

        // Set up RecyclerView for upcoming elections
        binding.recyclerUpcomingElections.apply {
            adapter = upcomingElectionsAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }

        // Set up RecyclerView for saved elections
        binding.recyclerSavedElections.apply {
            adapter = savedElectionsAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }

        // TODO: Link elections to voter info
        // Add code to link elections to voter info if necessary

        // TODO: Initiate recycler adapters
        // Initialize your RecyclerView adapters here

        // TODO: Populate recycler adapters
        // Load data into your adapters here

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding reference to avoid memory leaks
    }

    // TODO: Refresh adapters when fragment loads
    override fun onResume() {
        super.onResume()
        // Code to refresh adapters if needed
    }
}

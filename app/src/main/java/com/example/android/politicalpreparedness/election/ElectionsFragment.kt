package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.data.ElectionsNetworkRepository
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.CivicsApi

class ElectionsFragment : Fragment() {
    private var _binding: FragmentElectionBinding? = null
    private val binding get() = _binding!!

    private val upcomingElectionsAdapter = ElectionListAdapter(
        ElectionListener(
            clickListener = { election ->
                val action = ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                    election.id,
                    election.division
                )
                findNavController().navigate(action)
            }
        )
    )

    private val savedElectionsAdapter = ElectionListAdapter(
        ElectionListener(
            clickListener = { election ->
                val action = ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                    election.id,
                    election.division
                )
                findNavController().navigate(action)
            }
        )
    )

    private lateinit var viewModel: ElectionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentElectionBinding.inflate(inflater, container, false)

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

        val viewModelFactory = ElectionsViewModelFactory(
            ElectionsNetworkRepository(CivicsApi.retrofitService),
            ElectionDatabase.getInstance(requireContext()).electionDao
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[ElectionsViewModel::class.java]

        viewModel.upcomingElections.observe(viewLifecycleOwner) { elections ->
            upcomingElectionsAdapter.submitList(elections)
        }

        viewModel.savedElections.observe(viewLifecycleOwner) { savedElections ->
            savedElectionsAdapter.submitList(savedElections)
        }

        viewModel.showLoading.observe(viewLifecycleOwner) { showLoading ->
            if (showLoading) {
                binding.generalLoading.visibility = View.VISIBLE
            } else {
                binding.generalLoading.visibility = View.GONE
            }
        }

        return binding.root
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save RecyclerView scroll position for upcoming elections
        val upcomingElectionScrollPosition =
            (binding.recyclerUpcomingElections.layoutManager as LinearLayoutManager)
                .findFirstVisibleItemPosition()

        val savedElectionsScrollPosition =
            (binding.recyclerUpcomingElections.layoutManager as LinearLayoutManager)
                .findFirstVisibleItemPosition()

        outState.putInt(UPCOMING_ELECTIONS_SCROLL_POSITION, upcomingElectionScrollPosition)
        outState.putInt(SAVED_ELECTIONS_SCROLL_POSITION, savedElectionsScrollPosition)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.let { bundle ->
            val upcomingElectionScrollPosition =
                bundle.getInt(UPCOMING_ELECTIONS_SCROLL_POSITION, 0)
            binding.recyclerUpcomingElections.layoutManager?.scrollToPosition(
                upcomingElectionScrollPosition
            )

            val savedElectionScrollPosition = bundle.getInt(SAVED_ELECTIONS_SCROLL_POSITION, 0)
            binding.recyclerSavedElections.layoutManager?.scrollToPosition(
                savedElectionScrollPosition
            )
        }

        viewModel.loadUpcomingElections()
    }

    companion object {
        private const val UPCOMING_ELECTIONS_SCROLL_POSITION = "upcomingElectionsScrollPosition"
        private const val SAVED_ELECTIONS_SCROLL_POSITION = "savedElectionsScrollPosition"
    }
}

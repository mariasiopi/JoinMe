package com.example.joinme

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.room.Query
import kotlinx.coroutines.launch

class AvailableActivityFragment : Fragment(R.layout.fragment_available_activity) {

    private lateinit var adapter: ActivityAdapter
    private val viewModel: ActivityViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewAvailable)

        adapter = ActivityAdapter(mutableListOf(),
            false,
            onParticipateClick = { activity ->
                viewModel.participate(activityId = activity.id) },
            onDeleteClick = {})
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Ενημέρωση της λίστας με τα δεδομένα από το ViewModel
        viewModel.currentId.observe(viewLifecycleOwner){ id ->
            if(id != null && id > 0){
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.getAllActivities(id).asLiveData().observe(viewLifecycleOwner) { activities ->
                        if (viewModel.isSearching.value == false) { // Αν δε γίνεται αναζήτηση δείχνει τα τοπικά δεδομένα
                            adapter.updateData(activities)
                        }
                    }
                }
            }
        }

        //Παρακολούθηση της Firebase (Cloud)
        viewModel.searchResults.observe(viewLifecycleOwner) { cloudActivities ->
            if (viewModel.isSearching.value == true) {
                adapter.updateData(cloudActivities)
            }
        }

        viewModel.isSearching.observe(viewLifecycleOwner) { searching ->
            if (!searching) {
                // Αν σταμάτησε η αναζήτηση, ζήτα από τη Room την τρέχουσα λίστα
                val id = viewModel.currentId.value
                if (id != null) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.getAllActivities(id).collect { activities ->
                            adapter.updateData(activities)
                        }
                    }
                }
            }
        }

        //Αναζήτηση δραστηριότητας στο searchView
        val searchView = view.findViewById<SearchView>(R.id.searchView)

        // 1. Όταν πατιέται το "X" στη μπάρα
        searchView.setOnCloseListener {
            viewModel.clearSearch() // Επαναφέρει το isSearching σε false
            false
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            // Αναζήτηση με το πάτημα του Enter
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchActivitiesInCloud(it) }
                return true
            }


            override fun onQueryTextChange(query: String?): Boolean {
                if (!query.isNullOrEmpty()) { // Αναζήτηση καθώς πληκτρολογεί
                    viewModel.searchActivitiesInCloud(query)
                }
                if (query.isNullOrEmpty()) {
                    viewModel.clearSearch() // Επαναφορά όταν σβηστεί το κείμενο[cite: 1]
                }
                return true
            }
        })
    }
}
package com.example.joinme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.example.joinme.data.entities.Activity
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
                lifecycleScope.launch {
                    viewModel.getAllActivities(id).asLiveData().observe(viewLifecycleOwner) { activities ->
                        adapter.updateData(activities)
                    }
                }
            }
        }


    }
}
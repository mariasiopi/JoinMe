package com.example.joinme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.asLiveData

class MyActivitiesFragment : Fragment(R.layout.fragment_my_activities) {

    private lateinit var adapter: ActivityAdapter
    private val viewModel: ActivityViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewMy)

        adapter = ActivityAdapter(mutableListOf(),
            true,
            { },
            onDeleteClick = { activity -> viewModel.delete(activity) })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Ενημέρωση της λίστας με τα δεδομένα από το ViewModel
        viewModel.currentId.observe(viewLifecycleOwner){ id ->
            if(id != null && id > 0){
                viewModel.getMyActivities(id).asLiveData().observe(viewLifecycleOwner) { activities ->
                    adapter.updateData(activities)
                }
            }
        }
    }
}
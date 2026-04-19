package com.example.joinme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyActivitiesFragment : Fragment(R.layout.fragment_my_activities) {

    private lateinit var adapter: ActivityAdapter
    private val activityList = mutableListOf<ActivityModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewMy)

        adapter = ActivityAdapter(activityList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Εδώ θα φορτώνουμε τα δεδομένα (αργότερα από τη Room)
        loadData()
    }

    private fun loadData() {
        // Προσωρινά δεδομένα για δοκιμή
        activityList.add(ActivityModel("Ποδόσφαιρο 5x5", "20/04", "18:00", 5, 10, "Γήπεδα Α"))
        activityList.add(ActivityModel("Μπάσκετ", "21/04", "19:30", 2, 12, "Κλειστό Β"))
        adapter.notifyDataSetChanged()
    }
}
package com.example.joinme

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
class MainActivity : AppCompatActivity() {

    private val activityList = mutableListOf<ActivityModel>()
    private lateinit var adapter: ActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Ρύθμιση Toolbar και DrawerMenu
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawer_layout)
        val toggle = androidx.appcompat.app.ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val recyclerView = findViewById<RecyclerView>(R.id.activitiesRecyclerView)
        adapter = ActivityAdapter(activityList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //Προσθήκη δραστηριότητας
        val fab = findViewById<FloatingActionButton>(R.id.floatingActionButton)

        fab.setOnClickListener {
            // Δημιουργία AlertDialog
            val builder = AlertDialog.Builder(this)

            val dialogLayout = layoutInflater.inflate(R.layout.add_new_act, null)
            builder.setView(dialogLayout)

            val dialog = builder.create()

            // Βρίσκουμε τα στοιχεία
            val titleInput = dialogLayout.findViewById<EditText>(R.id.titleInput)
            val dateInput = dialogLayout.findViewById<EditText>(R.id.dateInput)
            val locationInput = dialogLayout.findViewById<EditText>(R.id.locationInput)
            val maxParticipantsInput = dialogLayout.findViewById<EditText>(R.id.maxParticipantsInput)
            val okBtn = dialogLayout.findViewById<Button>(R.id.okBtn)
            val cancelBtn = dialogLayout.findViewById<Button>(R.id.cancelBtn)

            // Τι γίνεται στο κλικ του "X"
            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }

            // Τι γίνεται στο κλικ του "✔"
            okBtn.setOnClickListener {
                val title = titleInput.text.toString()
                val date = dateInput.text.toString()
                val location = locationInput.text.toString()
                val maxPart = maxParticipantsInput.text.toString().toIntOrNull() ?: 0

                if (title.isNotEmpty()) {
                    // Δημιουργούμε το νέο αντικείμενο

                    val newActivity = ActivityModel(title, date, 0, maxPart, location)

                    // Προσθήκη στη λίστα και ενημέρωση του Adapter
                    activityList.add(newActivity)
                    adapter.notifyItemInserted(activityList.size - 1)

                    dialog.dismiss() // Κλείνουμε τη φούσκα
                } else {
                    titleInput.error = "Παρακαλώ βάλε έναν τίτλο"
                }
            }

            dialog.show()
        }


    }
}
package com.example.joinme

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.joinme.data.entities.Activity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var activityViewModel: ActivityViewModel
    private var currentId: Long = -1
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Εύρεση του NavHostFragment και του NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        //Ρύθμιση της Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Ρύθμιση DrawerMenu και του NavigationView
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        //Σύνδεση του NavController με το NavigationView (Μενού)
        navView.setupWithNavController(navController)

        //Ρύθμιση του AppBarConfiguration
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.LoginFragment, R.id.AvailableActFragment, R.id.MyActFragment),
            drawerLayout
        )

        //Σύνδεση της ActionBar με τον NavController
        setupActionBarWithNavController(navController, appBarConfiguration)

        activityViewModel = ViewModelProvider(this)[ActivityViewModel::class.java]

        activityViewModel.currentId.observe(this) { id ->
            if (id != null) {
                currentId = id // Ενημέρωση της μεταβλητής μόλις αλλάξει το ID στο Login
            }
        }
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
            val timeInput = dialogLayout.findViewById<EditText>(R.id.timeInput)
            val locationInput = dialogLayout.findViewById<EditText>(R.id.locationInput)
            val maxParticipantsInput =
                dialogLayout.findViewById<EditText>(R.id.maxParticipantsInput)
            val okBtn = dialogLayout.findViewById<Button>(R.id.okBtn)
            val cancelBtn = dialogLayout.findViewById<Button>(R.id.cancelBtn)

            //Ρύθμιση πεδίου ημερομηνίας και ώρας
            dateInput.setOnClickListener {
                val calendar = java.util.Calendar.getInstance()
                val datePicker = android.app.DatePickerDialog(
                    this,
                    { _, year, month, dayOfMonth ->
                        val selectedDate = "$dayOfMonth/${month + 1}/$year"
                        dateInput.setText(selectedDate)
                    },
                    calendar.get(java.util.Calendar.YEAR),
                    calendar.get(java.util.Calendar.MONTH),
                    calendar.get(java.util.Calendar.DAY_OF_MONTH)
                )
                datePicker.show()
            }

            timeInput.setOnClickListener {
                val calendar = java.util.Calendar.getInstance()
                val timePicker = android.app.TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                        timeInput.setText(selectedTime)
                    },
                    calendar.get(java.util.Calendar.HOUR_OF_DAY),
                    calendar.get(java.util.Calendar.MINUTE),
                    true // true για 24ωρη μορφή
                )
                timePicker.show()
            }

            // Τι γίνεται στο κλικ του "X"
            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }

            // Τι γίνεται στο κλικ του "✔"
            okBtn.setOnClickListener {
                val title = titleInput.text.toString()
                val date = dateInput.text.toString()
                val time = timeInput.text.toString()
                val location = locationInput.text.toString()
                val maxPart = maxParticipantsInput.text.toString().toIntOrNull() ?: 0

                if (title.isNotEmpty()) {
                    // Δημιουργούμε το νέο αντικείμενο
                    val activityEntity = Activity(
                        title = title,
                        date = date,
                        time = time,
                        location = location,
                        maxParticipants = maxPart,
                        creatorId = currentId
                    )
                    // Προσθήκη στη λίστα και ενημέρωση του Adapter
                    activityViewModel.insert(activityEntity)
                    dialog.dismiss() // Κλείνουμε τη φούσκα
                } else {
                    titleInput.error = "Παρακαλώ βάλε έναν τίτλο"
                }
            }
            dialog.show()
        }
    }

    // Επιτρέπει στο κουμπί "πάνω αριστερά" (hamburger icon) να ανοίγει το μενού
    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.LoginFragment, R.id.AvailableActFragment, R.id.MyActFragment),
            drawerLayout
        )
        return androidx.navigation.ui.NavigationUI.navigateUp(
            navController,
            appBarConfiguration
        )
                || super.onSupportNavigateUp()
    }
}
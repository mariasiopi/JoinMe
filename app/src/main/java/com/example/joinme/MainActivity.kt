package com.example.joinme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.joinme.data.entities.Activity
import com.google.android.material.color.DynamicColors
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import android.Manifest

class MainActivity : AppCompatActivity() {
    private lateinit var activityViewModel: ActivityViewModel
    private var currentId: Long = -1
    private lateinit var navController: NavController
    private var drawerLayout: DrawerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DynamicColors.applyToActivitiesIfAvailable(application)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Ρύθμιση DrawerMenu και του NavigationView
        drawerLayout = findViewById<View>(R.id.main_root) as? DrawerLayout
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Εύρεση του NavHostFragment και του NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val fab: FloatingActionButton? = findViewById(R.id.floatingActionButton)
            navView.setCheckedItem(destination.id)
            if (destination.id == R.id.LoginFragment) {
                // 1. Κλειδώνουμε την οθόνη σε Portrait μόνο για το Login
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                // 2. Κρύβουμε FAB και Toolbar
                fab?.visibility = View.GONE
                supportActionBar?.hide()
            } else {
                // 1. Επιτρέπουμε τη στροφή της οθόνης (Landscape) στις υπόλοιπες οθόνες
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

                // 2. Εμφανίζουμε FAB και Toolbar
                fab?.visibility = View.VISIBLE
                supportActionBar?.show()
            }
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            // Ορίζουμε ειδικές επιλογές για να μη "κολλάει" το Fragment
            val options = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setRestoreState(true)
                .build()

            when (menuItem.itemId) {
                R.id.AvailableActFragment -> {
                    navController.navigate(R.id.AvailableActFragment, null, options)
                }
                R.id.MyActFragment -> {
                    navController.navigate(R.id.MyActFragment, null, options)
                }
            }

            // Κλείσε το Drawer (αν υπάρχει, π.χ. σε Portrait)
            drawerLayout?.closeDrawers()
            true // Επιστρέφουμε true για να δείξουμε ότι το κλικ καταγράφηκε
        }

        //Ρύθμιση του AppBarConfiguration
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.AvailableActFragment, R.id.MyActFragment, R.id.LoginFragment),
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

        activityViewModel.showNotification.observe(this) { message ->
            message?.let {
                sendNotification(it)
                activityViewModel.doneShowingNotification() // Πολύ σημαντικό για να μην ξαναχτυπάει στο rotate
            }
        }
        //Προσθήκη δραστηριότητας
        val fab = findViewById<FloatingActionButton>(R.id.floatingActionButton)

        fab?.setOnClickListener {
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
        createNotificationChannel()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    // Επιτρέπει στο κουμπί "πάνω αριστερά" (hamburger icon) να ανοίγει το μενού
    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.AvailableActFragment, R.id.MyActFragment, R.id.LoginFragment),
            drawerLayout
        )
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun createNotificationChannel() {
        val name = "JoinMe Channel"
        val descriptionText = "Ειδοποιήσεις για συμμετοχή σε δραστηριότητες"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("JOIN_ME_NOTIF", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    fun sendNotification(message: String) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                // Αν δεν έχουμε άδεια, σταματάμε εδώ ή ζητάμε την άδεια
                return
            }
        }

        val builder = NotificationCompat.Builder(this, "JOIN_ME_NOTIF")
            .setSmallIcon(R.drawable.baseline_check_circle_outline_24) // Βάλε ένα δικό σου εικονίδιο
            .setContentTitle("JoinMe Επιτυχία!")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(this)) {
                notify(System.currentTimeMillis().toInt(), builder.build())
            }
        } catch (e: SecurityException) {
            Log.e("NotificationError", "Δεν υπάρχει άδεια για ειδοποιήσεις", e)
        }
    }
}
package com.example.joinme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.joinme.data.entities.Activity
import com.example.joinme.data.entities.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val activitiesDao = db.activitiesDao()

    // Ερώτημα 1: Όλες οι διαθέσιμες δραστηριότητες (χρήση Flow για αυτόματη ενημέρωση)
    val allActivities: Flow<List<Activity>> = activitiesDao.getOthersActivities(1)

    // Ερώτημα 2: Δραστηριότητες που έφτιαξε ο τρέχων χρήστης (για το MyActivitiesFragment)
    fun getMyActivities(userId: Int): Flow<List<Activity>> {
        return activitiesDao.getUsersActivities(userId)
    }

    // Συνάρτηση για προσθήκη (Insert) - θα καλείται από το FAB της MainActivity
    fun insert(activity: Activity) = viewModelScope.launch(Dispatchers.IO) {
        activitiesDao.insertActivity(activity)
    }
}
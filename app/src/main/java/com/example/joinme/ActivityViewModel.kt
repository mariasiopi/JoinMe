package com.example.joinme

import android.app.Application
import android.widget.EditText
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.joinme.data.entities.Activity
import com.example.joinme.data.entities.AppDatabase
import com.example.joinme.data.entities.User
import com.example.joinme.data.entities.UserActivityJoin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val activitiesDao = db.activitiesDao()
    private val userDao = db.userDao()
    private val _currentId = MutableLiveData<Long>()
    val currentId: LiveData<Long> get() = _currentId


    fun login(name: String, email: String) = viewModelScope.launch{
        val userId = userDao.insertUser(User(username = name, email = email))
        _currentId.value = userId
    }

    // Ερώτημα 1: Όλες οι διαθέσιμες δραστηριότητες (χρήση Flow για αυτόματη ενημέρωση)
    fun getAllActivities(currentId: Long): Flow<List<Activity>> {
        return activitiesDao.getOthersActivities(currentId)
    }

    // Ερώτημα 2: Δραστηριότητες που έφτιαξε ο τρέχων χρήστης (για το MyActivitiesFragment)
    fun getMyActivities(currentId: Long): Flow<List<Activity>> {
        return activitiesDao.getMyActivities(currentId)
    }

    // Συνάρτηση για προσθήκη (Insert) - θα καλείται από το FAB της MainActivity
    fun insert(activity: Activity) = viewModelScope.launch(Dispatchers.IO) {
        activitiesDao.insertActivity(activity)
    }

    fun delete(activity: Activity) = viewModelScope.launch {
        activitiesDao.deleteActivity(activity)
    }

    fun participate(activityId: Long) = viewModelScope.launch {
        val userId = _currentId.value
        if (userId != null && userId > 0) {
            viewModelScope.launch {
                val join = UserActivityJoin(participantId = userId, activityId = activityId)
                db.userActivityJoinDao().insertJoin(join)
            }
        }
    }
}
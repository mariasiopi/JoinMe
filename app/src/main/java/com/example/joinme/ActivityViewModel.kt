package com.example.joinme

import android.app.Application
import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore

class ActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val activitiesDao = db.activitiesDao()
    private val userDao = db.userDao()
    private val firestore = FirebaseFirestore.getInstance()
    private val _currentId = MutableLiveData<Long>()
    val currentId: LiveData<Long> get() = _currentId


    fun login(name: String, email: String) = viewModelScope.launch(Dispatchers.IO){
        userDao.insertUser(User(username = name, email = email))
        val userId = userDao.getUserIdByName(name)
        _currentId.postValue(userId)
    }

    // Ερώτημα 1: Όλες οι διαθέσιμες δραστηριότητες (χρήση Flow για αυτόματη ενημέρωση)
    fun getAllActivities(currentId: Long): Flow<List<Activity>> {
        return activitiesDao.getOthersActivities(currentId)
    }

    // Ερώτημα 2: Δραστηριότητες που έφτιαξε ο τρέχων χρήστης (για το MyActivitiesFragment)
    fun getMyActivities(currentId: Long): Flow<List<Activity>> {
        return activitiesDao.getMyActivities(currentId)
    }

    // Συνάρτηση για προσθήκη (Insert) - καλείται από το FAB της MainActivity
    private val _showNotification = MutableLiveData<String?>()
    val showNotification: LiveData<String?> get() = _showNotification
    fun doneShowingNotification() {
        _showNotification.value = null
    }
    fun insert(activity: Activity) = viewModelScope.launch(Dispatchers.IO) {
        activitiesDao.insertActivity(activity)
        firestore.collection("Activities")
            .add(activity)
            .addOnSuccessListener {
                // ΕΔΩ θα καλέσουμε το Notification μόλις επιβεβαιωθεί η εγγραφή!
                _showNotification.postValue("Η δραστηριότητα ${activity.title} δημιουργήθηκε!")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Σφάλμα κατά την εγγραφή", e)
            }
    }

    fun delete(activity: Activity) = viewModelScope.launch(Dispatchers.IO) {
        activitiesDao.deleteActivity(activity)
    }

    fun participate(activityId: Long) = viewModelScope.launch(Dispatchers.IO) {
        val userId = _currentId.value
        if (userId != null && userId > 0) {
                val join = UserActivityJoin(participantId = userId, activityId = activityId)
                db.userActivityJoinDao().insertJoin(join)
                db.activitiesDao().incParticipants(activityId)
        }
    }


    //-----------------------Firestore--------------------------------
    fun getParticipationsByUser(username: String) {
        firestore.collection("Participations")
            .whereEqualTo("participantName", username)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                }
            }
    }

    val searchResults = MutableLiveData<List<Activity>>()
    fun searchActivitiesInCloud(queryTitle: String) {
        firestore.collection("Activities")
            .orderBy("activityTitle")
            .startAt(queryTitle)
            .endAt(queryTitle + "\uf8ff")
            .get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Activity>()

                for (document in documents) {
                    val tempActivity = document.toObject(Activity::class.java)

                    // Δημιουργούμε το αντίγραφο με το σωστό ID
                    val finalActivity = tempActivity?.copy(
                        id = document.id.hashCode().toLong()
                    )

                    // Η προσθήκη πρέπει να γίνει ΕΔΩ μέσα, για κάθε έγγραφο!
                    finalActivity?.let { list.add(it) }
                }

                // Αφού τελειώσει το loop, δίνουμε όλη τη λίστα στο LiveData
                searchResults.value = list
            }
            .addOnFailureListener {
                searchResults.value = emptyList()
            }
    }
}
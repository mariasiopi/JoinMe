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
        val newUser = User(username = name, email = email)
        val generatedId = userDao.insertUser(newUser)
        _currentId.postValue(generatedId)

        val userForFirebase = User(userId = generatedId, username = name, email = email)
        firestore.collection("users")
            .document(generatedId.toString())
            .set(userForFirebase)
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
        val newId = activitiesDao.insertActivity(activity)

        val activityWithId = activity.copy(id = newId)

        firestore.collection("Activities")
            .document(newId.toString())
            .set(activityWithId)
            .addOnSuccessListener {
                _showNotification.postValue("Η δραστηριότητα ${activity.title} δημιουργήθηκε!")
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

            val participationData = hashMapOf(
                "activityId" to activityId,
                "participantId" to userId
            )
            firestore.collection("Participations")
                .add(participationData)

            firestore.collection("Activities")
                .document(activityId.toString()) // Χρησιμοποιούμε το ID ως όνομα εγγράφου
                .update("currentParticipants", com.google.firebase.firestore.FieldValue.increment(1))

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

    //Αναζήτηση δραστηριότητας στο Firebase
    val searchResults = MutableLiveData<List<Activity>>()
    val isSearching = MutableLiveData<Boolean>(false) //true αν γράφει στην αναζήτηση
    fun searchActivitiesInCloud(queryTitle: String) {
        if (queryTitle.isEmpty()) {
            clearSearch() //αν είναι αδειο σταματά την αναζήτηση
            return
        }

        isSearching.value = true
        firestore.collection("Activities")
            .orderBy("title")
            .startAt(queryTitle)
            .endAt(queryTitle + "\uf8ff") //μεχρι οποιαδηποτε λεξη
            .get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Activity>()

                for (document in documents) {
                    // Μετατροπή του Firebase εγγράφου σε αντικείμενο Activity
                    val tempActivity = document.toObject(Activity::class.java)
                    // Δημιουργούμε το αντίγραφο με το σωστό ID
                    val finalActivity = tempActivity?.copy(
                        id = document.id.toLongOrNull() ?: document.id.hashCode().toLong()
                    )
                    finalActivity?.let { list.add(it) }
                }
                searchResults.value = list // Ενημέρωση της λίστας αποτελεσμάτων
            }
            .addOnFailureListener {
                searchResults.value = emptyList()
            }
    }
    fun clearSearch() {
        isSearching.value = false
        searchResults.value = emptyList() // Καθαρίζουμε τα cloud αποτελέσματα
    }
}
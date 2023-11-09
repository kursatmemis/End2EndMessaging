package com.kursatmemis.end2endmessaging.repository.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kursatmemis.end2endmessaging.model.firebase_result.FirebaseOperationResult
import com.kursatmemis.end2endmessaging.model.database_model.UserData
import com.kursatmemis.end2endmessaging.model.database_model.Contact
import javax.inject.Inject

class FirebaseStoreRepository @Inject constructor(
    private val firestoreDatabase: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {

    private val _firebaseOperationResult = MutableLiveData<FirebaseOperationResult>()
    val firebaseOperationResult get() = _firebaseOperationResult

    private val _contactList = MutableLiveData<ArrayList<Contact>>()
    val contactList get() = _contactList

    fun saveUserDataToFirebaseStore(userData: UserData) {
        firestoreDatabase.collection("users").document(userData.phoneNumber!!)
            .set(userData)
            .addOnSuccessListener {
                _firebaseOperationResult.value = FirebaseOperationResult(true, null)
            }
            .addOnFailureListener {
                val errorMessage = it.message ?: "Unknown Error"
                _firebaseOperationResult.value = FirebaseOperationResult(true, errorMessage)
            }
    }

    // phoneNumber: Yeni eklenen kişi bu numaraya sahip kişinin rehberine eklenecek.
    fun saveNewPersonToFirebaseStore(contact: Contact, phoneNumber: String) {
        firestoreDatabase.collection("contact_list")
            .document(phoneNumber)
            .collection("contacts")
            .document(contact.phoneNumber!!)
            .set(contact).addOnSuccessListener {
                _firebaseOperationResult.value = FirebaseOperationResult(true, null)
            }
            .addOnFailureListener {
                val errorMessage = it.message ?: "Unknown Error"
                _firebaseOperationResult.value = FirebaseOperationResult(false, errorMessage)
            }

    }

    fun getContactListFromFirebaseStore() {
        val currentPhoneNumber = firebaseAuth.currentUser!!.phoneNumber!!

        firestoreDatabase.collection("contact_list")
            .document(currentPhoneNumber)
            .collection("contacts")
            .get()
            .addOnSuccessListener { result ->
                val contactList = ArrayList<Contact>()
                for (document in result.documents) {
                    val name = document.get("name").toString()
                    val surname = document.get("surname").toString()
                    val phoneNumber = document.get("phoneNumber").toString()
                    val contact = Contact(name, surname, phoneNumber)
                    contactList.add(contact)
                }
                _contactList.value = contactList
            }
            .addOnFailureListener {
                Log.w("mKm - firestore", it.message.toString())
            }
    }


}
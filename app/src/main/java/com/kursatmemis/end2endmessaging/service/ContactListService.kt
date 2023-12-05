package com.kursatmemis.end2endmessaging.service

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.Source
import com.kursatmemis.end2endmessaging.model.database_model.Contact
import com.kursatmemis.end2endmessaging.model.firebase_result.FirebaseOperationResult
import com.kursatmemis.end2endmessaging.util.FirestoreRefManager
import javax.inject.Inject

class ContactListService @Inject constructor(
    private val firestoreRefManager: FirestoreRefManager
) {

    private val _contactList = MutableLiveData<ArrayList<Contact>>()
    val contactList get() = _contactList

    private val _firebaseOperationResult = MutableLiveData<FirebaseOperationResult>()
    val firebaseOperationResult get() = _firebaseOperationResult

    private val _contact = MutableLiveData<Contact>()
    val contact get() = _contact

    // Parametre olarak belirtilen telefon numarasının rehberine contact'ı ekler.
    fun addNewContactToContactList(phoneNumber: String, contact: Contact) {
        val contactListRef = firestoreRefManager.getContactListRef(phoneNumber)
        Log.w("mKm - test - x", "geldi")
        contactListRef
            .document(contact.phoneNumber!!)
            .set(contact).addOnSuccessListener {
                _firebaseOperationResult.value = FirebaseOperationResult(true, null)
            }
            .addOnFailureListener {
                val errorMessage = it.message ?: "Unknown Error"
                _firebaseOperationResult.value = FirebaseOperationResult(false, errorMessage)
            }
    }

    fun getContactListFromFirestore(phoneNumber: String) {
        val contactListRef = firestoreRefManager.getContactListRef(phoneNumber)
        contactListRef
            .get()
            .addOnSuccessListener { result ->
                val contactList = ArrayList<Contact>()

                for (document in result.documents) {
                    val contact = document.toObject(Contact::class.java)
                    if (contact != null) {
                        contactList.add(contact)
                    }
                }
                _contactList.value = contactList
            }
            .addOnFailureListener {
                val errorMessage = it.message ?: "Unknown Error!"
                firebaseOperationResult.value = FirebaseOperationResult(false, errorMessage)
            }
    }

    fun getContact(
        senderPhoneNumber: String,
        receiverPhoneNumber: String,
        callback: (contact: Contact?) -> Unit
    ) {
        val contactListRef = firestoreRefManager.getContactListRef(senderPhoneNumber)
        contactListRef.document(receiverPhoneNumber).get().addOnSuccessListener {
            Log.w("mKm - test", receiverPhoneNumber)
            val contact = it.toObject(Contact::class.java)
            callback(contact)
        }
    }

}
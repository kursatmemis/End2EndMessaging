package com.kursatmemis.end2endmessaging.service

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.ktx.Firebase
import com.kursatmemis.end2endmessaging.model.Chat
import com.kursatmemis.end2endmessaging.repository.firebase.FirebaseStorageRepository
import com.kursatmemis.end2endmessaging.util.FirestoreRefManager
import javax.inject.Inject

class ChatListService @Inject constructor(
    private val firestoreRefManager: FirestoreRefManager,
    private val firebaseStorageRepository: FirebaseStorageRepository,
    private val contactListService: ContactListService
) {

    private val _chatList = MutableLiveData<ArrayList<Chat>>()

    val chatList get() = _chatList

    fun getChatList() {
        val chatListRef = firestoreRefManager.getChatRef(Firebase.auth.currentUser!!.phoneNumber!!)

        // Koleksiyon içindeki belgeleri "createdAt" alanına göre sırala ve dinleyici ekle
        chatListRef.orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Hata durumunda yapılacak işlemler
                    Log.w("mKm - chatList", "Veri alma başarısız", e)
                    return@addSnapshotListener
                }
                val list = ArrayList<Chat>()
                // Güncellenmiş belgeleri işle
                for (document in snapshot!!) {
                    val data = document.data
                    val createdAt = data["createdAt"] as Timestamp
                    val lastMessage = data["lastMessage"] as String
                    val phoneNumber = document.id
                    val chat = Chat(phoneNumber, createdAt, lastMessage)
                    list.add(chat)
                }

                // Güncellenmiş listeyi UI'ya yansıt
                _chatList.value = list
            }

    }

}


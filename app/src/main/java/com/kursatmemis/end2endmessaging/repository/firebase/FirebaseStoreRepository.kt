package com.kursatmemis.end2endmessaging.repository.firebase

import com.google.firebase.auth.FirebaseAuth
import com.kursatmemis.end2endmessaging.model.Chat
import com.kursatmemis.end2endmessaging.model.Message
import com.kursatmemis.end2endmessaging.model.database_model.UserData
import com.kursatmemis.end2endmessaging.model.database_model.Contact
import com.kursatmemis.end2endmessaging.service.ChatListService
import com.kursatmemis.end2endmessaging.service.ContactListService
import com.kursatmemis.end2endmessaging.service.MessageService
import com.kursatmemis.end2endmessaging.service.UserService
import javax.inject.Inject

class FirebaseStoreRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val messageService: MessageService,
    private val contactListService: ContactListService,
    private val userService: UserService,
    private val chatListService: ChatListService
) {

    val contactListServiceResult = contactListService.firebaseOperationResult
    val userServiceResult = userService.firebaseOperationResult
    val contactList = contactListService.contactList
    val messageList = messageService.messageList
    val messageServiceResult = messageService.messageListResult
    val chatList = chatListService.chatList

    fun saveUserDataToFirebaseStore(userData: UserData) {
        val phoneNumber = firebaseAuth.currentUser!!.phoneNumber!!
        userService.addUserToFirestore(phoneNumber, userData)
    }

    fun addNewContactToContactList(contact: Contact) {
        val phoneNumber = firebaseAuth.currentUser!!.phoneNumber!!
        contactListService.addNewContactToContactList(phoneNumber, contact)
    }

    fun getContactListFromFirebaseStore() {
        val phoneNumber = firebaseAuth.currentUser!!.phoneNumber!!
        contactListService.getContactListFromFirestore(phoneNumber)
    }

    fun saveMessageToFirestore(receiverPhoneNumber: String, message: Message) {
        val senderPhoneNumber = firebaseAuth.currentUser!!.phoneNumber!!
        messageService.saveMessageToFirestore(senderPhoneNumber, receiverPhoneNumber, message)
    }

    fun getMessageList(receiverPhoneNumber: String) {
        val senderPhoneNumber = firebaseAuth.currentUser!!.phoneNumber!!
        messageService.getMessageListBetweenUsers(senderPhoneNumber, receiverPhoneNumber)
    }

    fun getChatList() {
        chatListService.getChatList()
    }

    fun clear() {
        messageService.clear()
    }

}
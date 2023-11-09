package com.kursatmemis.end2endmessaging.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.kursatmemis.end2endmessaging.R
import com.kursatmemis.end2endmessaging.databinding.ItemPersonBinding
import com.kursatmemis.end2endmessaging.util.downloadFromUrl
import com.kursatmemis.end2endmessaging.util.buildProgressDrawable
import com.kursatmemis.end2endmessaging.model.database_model.Contact
import com.kursatmemis.end2endmessaging.repository.firebase.FirebaseStorageRepository
import javax.inject.Inject

class ContactListAdapter @Inject constructor(
    context: Context,
    private val contactList: ArrayList<Contact>,
    private val firebaseStorageRepository: FirebaseStorageRepository
) :
    ArrayAdapter<Contact>(context, R.layout.item_person, contactList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val binding: ItemPersonBinding

        if (convertView == null) {
            val layoutInflater = LayoutInflater.from(context)
            binding = ItemPersonBinding.inflate(layoutInflater, parent, false)
        } else {
            binding = ItemPersonBinding.bind(convertView)
        }

        val person = contactList[position]
        val fullName = "${person.name} ${person.surname}"
        binding.fullNameTextView.text = fullName

        firebaseStorageRepository.getProfilePictureUri(person.phoneNumber!!) { uri ->
            val imageUrl = uri.toString()
            binding.profileImageImageView.downloadFromUrl(
                imageUrl,
                buildProgressDrawable(context)
            )
        }

        return binding.root
    }

    fun updateAdapter(newContactList: ArrayList<Contact>) {
        clear()
        addAll(newContactList)
        notifyDataSetChanged()
    }

}
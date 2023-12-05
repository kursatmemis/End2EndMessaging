package com.kursatmemis.end2endmessaging.view.main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.kursatmemis.end2endmessaging.R
import com.kursatmemis.end2endmessaging.databinding.FragmentContactBinding
import com.kursatmemis.end2endmessaging.view.adapter.ContactListAdapter
import com.kursatmemis.end2endmessaging.view.BaseFragment
import com.kursatmemis.end2endmessaging.view.main.activity.ChatChannelActivity
import com.kursatmemis.end2endmessaging.viewmodel.main.ContactFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ContactFragment : BaseFragment<FragmentContactBinding>() {

    private val contactFragmentViewModel: ContactFragmentViewModel by viewModels()

    @Inject
    lateinit var contactListAdapter: ContactListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactFragmentViewModel.getContactListFromFirebaseStore()
    }

    override fun createBindingObject(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentContactBinding? {
        return FragmentContactBinding.inflate(inflater, container, false)
    }

    override fun setupUI() {

        binding.contactListView.adapter = contactListAdapter

        binding.contactListView.setOnItemClickListener { parent, view, position, id ->
            val contact = contactListAdapter.getContactList()[position]
            val messageTo = contact.phoneNumber!!
            goToChatChannelActivityWithReceiverPhoneNumber(messageTo)
            /*val directions = ContactFragmentDirections.actionPeopleFragmentToChannelFragment(
                    messageTo
                )
            Navigation.findNavController(binding.root).navigate(directions)
            */
        }

        binding.addNewPersonFab.setOnClickListener {
            navigateToAddPersonFragment()
        }

        observeLiveData()
    }

    private fun observeLiveData() {
        contactFragmentViewModel.contactList.observe(viewLifecycleOwner) {
            val newContactList = it
            contactListAdapter.updateAdapter(newContactList)
        }
    }

    private fun navigateToAddPersonFragment() {
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_peopleFragment_to_addNewPersonFragment)
    }

    private fun goToChatChannelActivityWithReceiverPhoneNumber(messageTo: String) {
        val intentToChatChannelActivity = Intent(requireActivity(), ChatChannelActivity::class.java)
        intentToChatChannelActivity.putExtra("receiverPhoneNumber", messageTo)
        startActivity(intentToChatChannelActivity)
    }

}


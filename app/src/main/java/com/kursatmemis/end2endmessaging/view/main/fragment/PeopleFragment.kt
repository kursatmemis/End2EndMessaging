package com.kursatmemis.end2endmessaging.view.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.kursatmemis.end2endmessaging.R
import com.kursatmemis.end2endmessaging.view.adapter.ContactListAdapter
import com.kursatmemis.end2endmessaging.databinding.FragmentPeopleBinding
import com.kursatmemis.end2endmessaging.view.BaseFragment
import com.kursatmemis.end2endmessaging.viewmodel.main.ContactFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PeopleFragment : BaseFragment<FragmentPeopleBinding>() {

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
    ): FragmentPeopleBinding? {
        return FragmentPeopleBinding.inflate(inflater, container, false)
    }

    override fun setupUI() {

        binding.peopleListView.adapter = contactListAdapter

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

}


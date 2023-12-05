package com.kursatmemis.end2endmessaging.view.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.kursatmemis.end2endmessaging.R
import com.kursatmemis.end2endmessaging.databinding.FragmentAddNewPersonBinding
import com.kursatmemis.end2endmessaging.util.areParamsEmpty
import com.kursatmemis.end2endmessaging.util.closeKeyboard
import com.kursatmemis.end2endmessaging.util.userfeedback.SnackBarAction
import com.kursatmemis.end2endmessaging.util.userfeedback.closeProgressBar
import com.kursatmemis.end2endmessaging.util.userfeedback.showProgressBar
import com.kursatmemis.end2endmessaging.util.userfeedback.showSnackBar
import com.kursatmemis.end2endmessaging.util.userfeedback.showToastMessage
import com.kursatmemis.end2endmessaging.model.database_model.Contact
import com.kursatmemis.end2endmessaging.view.BaseFragment
import com.kursatmemis.end2endmessaging.viewmodel.main.AddNewPersonViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNewContactFragment : BaseFragment<FragmentAddNewPersonBinding>() {

    private val addNewContactViewModel: AddNewPersonViewModel by viewModels()

    override fun createBindingObject(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddNewPersonBinding? {
        return FragmentAddNewPersonBinding.inflate(inflater, container, false)
    }

    override fun setupUI() {

        binding.countryCodePicker.registerCarrierNumberEditText(binding.phoneNumberEditText)

        binding.savePersonButton.setOnClickListener {
            closeKeyboard(requireActivity(), requireContext())
            val phoneNumber = binding.phoneNumberEditText.text.toString()
            val phoneNumberWithoutSpaces = phoneNumber.filter { !it.isWhitespace() }
            val isEmpty = areParamsEmpty(phoneNumberWithoutSpaces)
            if (isEmpty) {
                showSnackBar(
                    binding.root,
                    "Please enter a phone number!",
                    Snackbar.LENGTH_INDEFINITE,
                    SnackBarAction("OK") {
                    }
                )
            } else {
                showProgressBar(binding.progressBar3)
                val contact = getContact(phoneNumberWithoutSpaces)
                addNewContactViewModel.addNewContactToContactList(contact)
            }

        }

        observeLiveData()
    }

    private fun observeLiveData() {
        addNewContactViewModel.saveContactResult.observe(viewLifecycleOwner) {
            val isSaveUserSuccess = it.isSuccessful
            if (isSaveUserSuccess) {
                showToastMessage(
                    requireContext(),
                    "The person added!",
                    Toast.LENGTH_LONG
                )
                navigateToContactFragment()
            } else {
                val errorMessage = it.errorMessage!!
                showSnackBar(
                    binding.root,
                    errorMessage,
                    Snackbar.LENGTH_INDEFINITE,
                    SnackBarAction("OK") {})
            }
            closeProgressBar(binding.progressBar3)
        }
    }

    private fun navigateToContactFragment() {
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_addNewPersonFragment_to_peopleFragment)
    }

    private fun getContact(phoneNumberWithoutSpaces: String): Contact {
        val name = binding.nameEditText.text.toString()
        val surname = binding.surnameEditText.text.toString()
        val areaCode = binding.countryCodePicker.selectedCountryCode
        val fullPhoneNumber = "+$areaCode$phoneNumberWithoutSpaces"
        return Contact(name, surname, fullPhoneNumber)
    }

}
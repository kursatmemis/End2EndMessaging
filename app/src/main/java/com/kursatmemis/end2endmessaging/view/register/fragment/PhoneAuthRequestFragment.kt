package com.kursatmemis.end2endmessaging.view.register.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.kursatmemis.end2endmessaging.R
import com.kursatmemis.end2endmessaging.databinding.FragmentPhoneAuthRequestBinding
import com.kursatmemis.end2endmessaging.helper.areParamsEmpty
import com.kursatmemis.end2endmessaging.helper.closeKeyboard
import com.kursatmemis.end2endmessaging.helper.userfeedback.closeProgressBar
import com.kursatmemis.end2endmessaging.helper.userfeedback.showProgressBar
import com.kursatmemis.end2endmessaging.helper.userfeedback.showToastMessage
import com.kursatmemis.end2endmessaging.view.BaseFragment
import com.kursatmemis.end2endmessaging.viewmodel.authentication.PhoneAuthRequestViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhoneAuthRequestFragment : BaseFragment<FragmentPhoneAuthRequestBinding>() {

    private val phoneAuthRequestViewModel: PhoneAuthRequestViewModel by viewModels()
    private var fullPhoneNumber: String = ""

    override fun createBindingObject(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPhoneAuthRequestBinding? {
        return FragmentPhoneAuthRequestBinding.inflate(inflater, container, false)
    }

    override fun setupUI() {

        binding.countryCodePicker.registerCarrierNumberEditText(binding.phoneNumberEditText)

        binding.nextButton.setOnClickListener {
            closeKeyboard(requireActivity(), requireContext())
            val areaCode = binding.countryCodePicker.selectedCountryCode!!
            val phoneNumber = binding.phoneNumberEditText.text.toString()
            val isEmpty = areParamsEmpty(phoneNumber)
            if (isEmpty) {
                showToastMessage(requireContext(), "Please enter a phone number!")
            } else {
                showProgressBar(binding.progressBar)
                fullPhoneNumber = "+$areaCode$phoneNumber"
                phoneAuthRequestViewModel.verifyPhoneNumber(fullPhoneNumber, requireActivity())
            }
        }

        observeLiveData()
    }

    private fun observeLiveData() {

        phoneAuthRequestViewModel.verificationResult.observe(viewLifecycleOwner) {
            val isCompleted = it.isCompleted

            if (isCompleted) {
                // Doğrulama işlemi tamamlandı. Kod göndermeye gerek yok.
                val credential = it.credential!!
                phoneAuthRequestViewModel.signInWithPhoneAuthCredential(credential)
            } else {
                val isCodeSent = it.verificationId!= null || it.errorMessage == null
                if (isCodeSent) {
                    // Kod başarıyla gönderildi.
                    val verificationId = it.verificationId!!
                    navigateToCodeVerificationFragmentWithVerificationID(verificationId)
                } else {
                    // Kod gönderilmesinde bir problem oluştu.
                    val errorMessage = it.errorMessage!!
                    showToastMessage(requireContext(), errorMessage)
                }
            }
            closeProgressBar(binding.progressBar)
        }

        phoneAuthRequestViewModel.signInResult.observe(viewLifecycleOwner) {
            val isSignInSuccessful = it.isSignInSuccessful
            if (isSignInSuccessful) {
                // Telefon numarasını doğrulamak için kod göndermeye gerek kalmadı.
                navigateToProfileSetupFragment()
                requireActivity().finish()
            } else {
                val errorMessage = it.errorMessage!!
                showToastMessage(requireContext(), errorMessage)
            }
            closeProgressBar(binding.progressBar)
        }


    }

    private fun navigateToProfileSetupFragment() {
        Navigation.findNavController(binding.root).navigate(R.id.action_phoneAuthRequestFragment_to_profileSetupFragment)
    }

    private fun navigateToCodeVerificationFragmentWithVerificationID(
        verificationId: String
    ) {
        val directions =
            PhoneAuthRequestFragmentDirections.actionPhoneAuthRequestFragmentToCodeVerificationFragment(
                verificationId,
                fullPhoneNumber
            )
        Navigation.findNavController(binding.root).navigate(directions)
    }

}
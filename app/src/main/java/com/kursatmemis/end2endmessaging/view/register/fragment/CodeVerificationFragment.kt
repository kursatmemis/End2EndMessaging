package com.kursatmemis.end2endmessaging.view.register.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.kursatmemis.end2endmessaging.databinding.FragmentCodeVerificationBinding
import com.kursatmemis.end2endmessaging.util.areParamsEmpty
import com.kursatmemis.end2endmessaging.util.closeKeyboard
import com.kursatmemis.end2endmessaging.util.userfeedback.closeProgressBar
import com.kursatmemis.end2endmessaging.util.userfeedback.showProgressBar
import com.kursatmemis.end2endmessaging.util.userfeedback.showToastMessage
import com.kursatmemis.end2endmessaging.view.BaseFragment
import com.kursatmemis.end2endmessaging.viewmodel.authentication.CodeVerificationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CodeVerificationFragment : BaseFragment<FragmentCodeVerificationBinding>() {

    private val codeVerificationViewModel: CodeVerificationViewModel by viewModels()
    private lateinit var verificationId: String
    private lateinit var phoneNumber: String
    private var countDownTimer: CountDownTimer? = null
    private val backPressHandler = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            requireActivity().finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle: CodeVerificationFragmentArgs by navArgs()
        verificationId = bundle.verificationId
        phoneNumber = bundle.phoneNumber
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressHandler)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun createBindingObject(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCodeVerificationBinding? {
        return FragmentCodeVerificationBinding.inflate(inflater, container, false)
    }

    override fun setupUI() {

        startCountDownTimer()

        binding.resendCodeTextView.setOnClickListener {
            showProgressBar(binding.progressBar2)
            codeVerificationViewModel.verifyPhoneNumber(phoneNumber, requireActivity())
        }

        binding.verifyButton.setOnClickListener {
            closeKeyboard(requireActivity(), requireContext())
            val enteredCode = binding.codeEditText.text.toString()
            val isEmpty = areParamsEmpty(enteredCode)
            if (isEmpty) {
                showToastMessage(requireContext(), "Please fill the code you received!")
            } else if (enteredCode.length != 6) {
                showToastMessage(requireContext(), "Please enter the 6 digit code!")
            } else {
                showProgressBar(binding.progressBar2)
                codeVerificationViewModel.signInWithPhoneAuthCredential(verificationId, enteredCode)
            }
        }

        observeLiveData()

    }

    private fun startCountDownTimer() {
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.countDownTimerTextView.text = "Code Validity Period: $secondsRemaining"
            }

            override fun onFinish() {
                binding.countDownTimerTextView.visibility = View.INVISIBLE
                binding.resendCodeTextView.visibility = View.VISIBLE
            }

        }

        countDownTimer?.start()

    }

    private fun observeLiveData() {

        codeVerificationViewModel.verificationResult.observe(viewLifecycleOwner) {
            val isCodeSent = it.verificationId!= null || it.errorMessage == null
            if (isCodeSent) {
                // Kod başarıyla gönderildi.
                this.verificationId = it.verificationId!!
                startCountDownTimer()
                binding.resendCodeTextView.visibility = View.INVISIBLE
                binding.countDownTimerTextView.visibility = View.VISIBLE
                showToastMessage(requireContext(), "The code sent again!")
            } else {
                // Kod gönderilmesinde bir problem oluştu.
                val errorMessage = it.errorMessage!!
                showToastMessage(requireContext(), errorMessage)
            }
            closeProgressBar(binding.progressBar2)
        }

        codeVerificationViewModel.signInResult.observe(viewLifecycleOwner) {
            val isSignInSuccessful = it.isSuccessful
            if (isSignInSuccessful) {
                navigateToProfileSetupFragmentWithPhoneNumber()
            } else {
                val errorMessage = it.errorMessage!!
                showToastMessage(requireContext(), errorMessage)
            }
            closeProgressBar(binding.progressBar2)
        }

    }

    private fun navigateToProfileSetupFragmentWithPhoneNumber() {
        val directions =
            CodeVerificationFragmentDirections.actionCodeVerificationFragmentToProfileSetupFragment(
                phoneNumber
            )
        Navigation.findNavController(binding.root).navigate(directions)
    }

    override fun onDestroyView() {
        countDownTimer?.cancel()
        countDownTimer = null
        super.onDestroyView()
    }

}
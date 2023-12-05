package com.kursatmemis.end2endmessaging.view.main.fragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.kursatmemis.end2endmessaging.databinding.FragmentSettingsBinding
import com.kursatmemis.end2endmessaging.view.BaseFragment
import com.kursatmemis.end2endmessaging.view.register.activity.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    @Inject
    lateinit var auth: FirebaseAuth

    override fun createBindingObject(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding? {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun setupUI() {

        binding.logoutButton.setOnClickListener {
            auth.signOut()
            goToRegisterActivity()
            requireActivity().finish()
        }

    }

    private fun goToRegisterActivity() {
        val intentToRegisterAcitivty = Intent(requireActivity(), RegisterActivity::class.java)
        startActivity(intentToRegisterAcitivty)
    }

}
package com.kursatmemis.end2endmessaging.view.register.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.firebase.Timestamp
import com.kursatmemis.end2endmessaging.databinding.FragmentProfileSetupBinding
import com.kursatmemis.end2endmessaging.helper.ImagePickerFromGallery
import com.kursatmemis.end2endmessaging.helper.areParamsEmpty
import com.kursatmemis.end2endmessaging.helper.goToMainActivityAndFinishIt
import com.kursatmemis.end2endmessaging.helper.userfeedback.AlertDialogButton
import com.kursatmemis.end2endmessaging.helper.userfeedback.AlertDialogNegativeButton
import com.kursatmemis.end2endmessaging.helper.userfeedback.AlertDialogPositiveButton
import com.kursatmemis.end2endmessaging.helper.userfeedback.showAlertDialog
import com.kursatmemis.end2endmessaging.helper.userfeedback.showToastMessage
import com.kursatmemis.end2endmessaging.model.database.UserData
import com.kursatmemis.end2endmessaging.view.BaseFragment
import com.kursatmemis.end2endmessaging.viewmodel.authentication.ProfileSetupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileSetupFragment : BaseFragment<FragmentProfileSetupBinding>() {

    private val profileSetupViewModel: ProfileSetupViewModel by viewModels()
    private lateinit var imagePicker: ImagePickerFromGallery
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var phoneNumber: String
    private var imageUri: Uri? = null

    // Back tuşuna basıldığında tetiklenen işlemi yöneten bir geri çağırma
    private lateinit var backPressHandler: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle: ProfileSetupFragmentArgs by navArgs()
        phoneNumber = bundle.phoneNumber
        registerLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        backPressHandler = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val userData = getUserData()
                profileSetupViewModel.saveUserDataToFirebaseStore(userData)
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressHandler)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun createBindingObject(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileSetupBinding? {
        return FragmentProfileSetupBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagePicker = ImagePickerFromGallery(
            requireContext(),
            requireActivity(),
            binding.root,
            activityResultLauncher,
            permissionLauncher
        )
    }

    override fun setupUI() {

        // Profil Resmi Seçme
        binding.profileImage.setOnClickListener {
            selectImage()
        }

        // About Alanını Belirleme
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = binding.root.findViewById<RadioButton>(checkedId)
            val selectedText = selectedRadioButton.text.toString()
            binding.aboutEditText.setText(selectedText)
        }

        binding.saveButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val about = binding.aboutEditText.text.toString()
            val isEmpty = areParamsEmpty(name, about)

            if (isEmpty) {
                val title = "Empty Fields"
                val message =
                    "There are empty fields on your profile. Do you still want to continue?"
                val positiveButton = AlertDialogPositiveButton(
                    "Yes"
                ) { dialog, which ->
                    val userData = getUserData()
                    saveUserDataAndProfilePictureToFirebase(userData)
                }
                val negativeButton = AlertDialogNegativeButton(
                    "No"
                ) { dialog, which ->
                    val infoMessage = "It's cancelled!"
                    showToastMessage(requireContext(), infoMessage)
                }
                val alertDialogButton = AlertDialogButton(positiveButton, negativeButton)
                showAlertDialog(requireContext(), title, message, alertDialogButton)
            } else {
                // FirebaseStora'a kaydet.
                val userData = getUserData()
                saveUserDataAndProfilePictureToFirebase(userData)
            }
        }

        observeLiveData()
    }

    private fun observeLiveData() {

        profileSetupViewModel.uploadImageResult.observe(viewLifecycleOwner) {
            val isUploadSuccessful = it.isUploadSuccessful
            if (!isUploadSuccessful) {
                val errorMessage = it.errorMessage!!
                showToastMessage(requireContext(), errorMessage)
            } else if (profileSetupViewModel.saveUserDataResult.value != null && profileSetupViewModel.saveUserDataResult.value!!.isSaveUserSuccess) {
                goToMainActivityAndFinishIt(requireContext(), requireActivity())
            }
        }

        profileSetupViewModel.saveUserDataResult.observe(viewLifecycleOwner) {
            val isSaveUserSuccess = it.isSaveUserSuccess
            if (!isSaveUserSuccess) {
                val errorMessage = it.errorMessage!!
                showToastMessage(requireContext(), errorMessage)
            } else if (profileSetupViewModel.uploadImageResult.value != null && profileSetupViewModel.uploadImageResult.value!!.isUploadSuccessful) {
                goToMainActivityAndFinishIt(requireContext(), requireActivity())
            }
        }

    }

    private fun saveUserDataAndProfilePictureToFirebase(userData: UserData) {
        profileSetupViewModel.saveProfilePictureToFirebaseStorage(imageUri, phoneNumber)
        profileSetupViewModel.saveUserDataToFirebaseStore(userData)
    }

    private fun getUserData(): UserData {
        val id = phoneNumber
        val phoneNumber = phoneNumber
        val createdAt = Timestamp.now()
        val displayName = binding.nameEditText.text.toString()
        val description = binding.aboutEditText.text.toString()
        val lastSeen = Timestamp.now()
        val status = "online"
        val photoUrl = imageUri.toString()
        val publicKey = "publicKey"
        val token = "token"

        val userData = UserData(
            id,
            phoneNumber,
            createdAt,
            displayName,
            description,
            lastSeen,
            status,
            photoUrl,
            publicKey,
            token
        )

        return userData

    }

    private fun selectImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Sürümü 33'den büyük olan cihazlarda resim seç.
            imagePicker.selectImageForSDK33AndAbove()
        } else {
            // Sürümü 33'den küçük olan cihazlarda resim seç.
            imagePicker.selectImageForPreSDK33()
        }
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    imageUri = intentFromResult.data
                    binding.profileImage.setImageURI(imageUri)
                }
            }
        }

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                // Izin verilmiş.
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                // Izin verilmemiş.
                showToastMessage(requireContext(), "You have to give a permission.")
            }
        }

    }

}